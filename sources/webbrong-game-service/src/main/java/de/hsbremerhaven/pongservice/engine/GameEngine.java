package de.hsbremerhaven.pongservice.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsbremerhaven.pongservice.config.GameEngineRunnable;
import de.hsbremerhaven.pongservice.config.RestHandler;
import de.hsbremerhaven.pongservice.domain.*;
import de.hsbremerhaven.pongservice.domain.Event;
import de.hsbremerhaven.pongservice.domain.Player.FourPoints;
import de.hsbremerhaven.pongservice.domain.Player.Movement;
import de.hsbremerhaven.pongservice.domain.Player.Paddle;
import de.hsbremerhaven.pongservice.domain.Player.Player;
import de.hsbremerhaven.pongservice.domain.ball.Ball;
import de.hsbremerhaven.pongservice.domain.ball.Position;
import de.hsbremerhaven.pongservice.domain.ball.Velocity;
import javassist.tools.web.Webserver;
import org.dyn4j.geometry.Vector2;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameEngine implements GameEngineRunnable {
    protected String roomID;
    protected GameState gameState;
    protected final int TICK_RATE = 60;
    protected boolean isRunning;
    protected Map<Integer, Direction> positionMap;
    protected String destinationPrefix;
    protected Player lastHitPlayer;
    protected int defaultLifePoints;
    protected int defaultPoints;
    protected Map<Integer, String> playerCountToColor;
    protected Map<Direction, FourPoints> directionToAllowedDrawingArea;
    protected Map<WebSocketSession, String> sessionToUsername;
    protected Set<WebSocketSession> spectators;
    protected Match match;

    public GameEngine() {
        gameState = new GameState();
        gameState.setSpectators(new HashSet<>());
        playerCountToColor = new HashMap<>();
        playerCountToColor.put(0, "#ff0022");
        playerCountToColor.put(1, "#aa3344");
        playerCountToColor.put(2, "#22BB33");
        playerCountToColor.put(3, "#00ff33");
        sessionToUsername = new ConcurrentHashMap<>();
        spectators = new HashSet<>();
        match = new Match();
        match.setRunning(false);
        match.setMatchID(-1);
    }

    public void addNewPlayer(WebSocketSession session) throws IOException {
        System.out.println(session.getId());
        String username = (String) session.getAttributes().get("username");
        if((Boolean) session.getAttributes().get("isSpectating") || match.isRunning()){
            if(getPlayerCount() == 0){
                RoomEntryErrorMessage r = new RoomEntryErrorMessage();
                r.setMessage("Cannot spectate empty room");
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
                session.close();
            }else{
                EventDummy eventDummy = new EventDummy();
                eventDummy.setEvent(Event.FORCED_TO_SPECTATE);
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(eventDummy)));
                gameState.getSpectators().add(username);
                spectators.add(session);
            }
            return;
        }

        if (getPlayerCount() >= 4) {
            RoomEntryErrorMessage r = new RoomEntryErrorMessage();
            r.setMessage("Room is full");
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
            session.close();
            return;
        }

        if (gameState.getPlayers().get(username) != null) {
            RoomEntryErrorMessage r = new RoomEntryErrorMessage();
            r.setMessage("Username already present in room");
            session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
            session.close();
            return;
        }

        if ((Boolean) session.getAttributes().get("customPaddle")) {
            gameState.getPlayers().put(username, generateCustomPaddlePlayer(username));
        } else {
            gameState.getPlayers().put(username, generateDefaultPlayer(username));
        }
        sessionToUsername.put(session, username);
    }

    public void removePlayer(WebSocketSession session) {
        if(sessionToUsername.get(session) != null){
            String username = sessionToUsername.get(session);
            gameState.getPlayers().remove(username);
            gameState.getSpectators().remove(username);
            sessionToUsername.remove(session);
            if(sessionToUsername.size() > 0 && username.equals(gameState.getAdmin())){
                gameState.setAdmin((String)gameState.getPlayers().keySet().toArray()[0]);
            }
        }
    }

    public void changePlayerMovement(WebSocketSession session, MovementMessage message) {
        if(sessionToUsername.get(session) == null){
            return;
        }

        Player player = gameState.getPlayers().get(sessionToUsername.get(session));
        // check if movement direction is allowed
        if (!player.getAllowedMovementDirections().contains(message.getDirection())) return;

        switch (message.getDirection()) {
            case TOP:
                player.getPaddle().getMovement().setUp(message.isMoving());
                break;
            case BOTTOM:
                player.getPaddle().getMovement().setDown(message.isMoving());
                break;
            case RIGHT:
                player.getPaddle().getMovement().setRight(message.isMoving());
                break;
            case LEFT:
                player.getPaddle().getMovement().setLeft(message.isMoving());
                break;
        }
    }

    public void stop() {
        isRunning = false;
        RestHandler.getInstance().cancelMatch(match.getMatchID());
        for(WebSocketSession ws: spectators){
            try {
                RoomEntryErrorMessage r = new RoomEntryErrorMessage();
                r.setMessage("Room closed");
                ws.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
                ws.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void processNextTick() {
        // specific engine defines behaviour
    }

    @Override
    public void run() {
        while (isRunning) {
            if(match.isRunning()){
                processNextTick();
            }
            for(WebSocketSession ws: sessionToUsername.keySet()){
                try {
                    if(ws.isOpen()){
                        ws.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(gameState)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(WebSocketSession ws: spectators){
                try {
                    if(ws.isOpen()){
                        ws.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(gameState)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000 / TICK_RATE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void accelerateBall(Velocity ballVelocity) {
        ballVelocity.setSpeed(ballVelocity.getSpeed() + ballVelocity.getDefaultSpeed() * (ballVelocity.getAcceleration() / TICK_RATE));
    }

    /**
     * @param potX
     * @param potY
     * @return Direction of the border that got hit or null if no border got hit
     */
    protected Direction checkForBorderCollision(double potX, double potY) {
        Playground playground = gameState.getPlayground();
        Ball ball = gameState.getBall();
        Position ballPosition = ball.getPosition();
        int ballRadius = ballPosition.getRadius();
        Velocity ballVelocity = ball.getVelocity();

        Direction borderHit = null;
        // left border
        if (potX - ballRadius <= 0) {
            ballPosition.setX(ballRadius);
            ballVelocity.setX(-ballVelocity.getX());
            borderHit = Direction.LEFT;
        }
        // right border
        else if (potX + ballRadius > playground.getWidth()) {
            ballPosition.setX(playground.getWidth() - ballRadius);
            ballVelocity.setX(-ballVelocity.getX());
            borderHit = Direction.RIGHT;
        }
        // bottom border
        else if (potY + ballRadius > playground.getHeight()) {
            ballPosition.setY(playground.getHeight() - ballRadius);
            ballVelocity.setY(-ballVelocity.getY());
            borderHit = Direction.BOTTOM;
        }
        // top border
        else if (potY - ballRadius < 0) {
            ballPosition.setY(ballRadius);
            ballVelocity.setY(-ballVelocity.getY());
            borderHit = Direction.TOP;
        }
        return borderHit;
    }

    protected boolean checkForPaddleCollision(double potX, double potY) {
        Ball ball = gameState.getBall();
        Position ballPosition = ball.getPosition();
        Velocity ballVelocity = ball.getVelocity();

        Vector2 normalVectorOfEdgeHit = null;
        Iterator it = gameState.getPlayers().values().iterator();
        // check for collisions with player paddles
        while (normalVectorOfEdgeHit == null && it.hasNext()) {
            Player player = (Player) it.next();
            Paddle paddle = player.getPaddle();
            normalVectorOfEdgeHit = checkInterceptions(ballPosition, potX, potY, paddle);

            if (normalVectorOfEdgeHit != null) {
                // remember the last player who hit the ball to calculate points for breakout
                lastHitPlayer = player;
                if(lastHitPlayer.isUsingCustomPaddle()){
                    resetCustomPaddlePosition(lastHitPlayer);
                }
                adjustBallVelocity(normalVectorOfEdgeHit, ballVelocity, ballPosition, player);
            }
        }
        return normalVectorOfEdgeHit != null;
    }

    public static Vector2 Reflect(Vector2 vector, Vector2 normal) {
        return vector.subtract(normal.multiply(normal.dot(vector)).multiply(2));
    }

    protected void adjustBallVelocity(Vector2 normalVectorOfEdgeHit, Velocity ballVelocity, Position ballPosition, Player player) {
        Vector2 ball = new Vector2(ballVelocity.getX(), ballVelocity.getY());
        Vector2 result = Reflect(ball, normalVectorOfEdgeHit);
        double x = Math.abs(result.x) / (Math.abs(result.x) + Math.abs(result.y));
        double y = 1 - x;
        if (result.x < 0) x *= -1;
        if (result.y < 0) y *= -1;

        if(player != null){
            Movement move = player.getPaddle().getMovement();
            if(move.isDown() || move.isLeft()){
                y += 0.2;
                x -= 0.2;
            }else if(move.isUp() || move.isRight()){
                y -= 0.2;
                x += 0.2;
            }
        }
        ballVelocity.setX(x * ballVelocity.getSpeed());
        ballVelocity.setY(y * ballVelocity.getSpeed());
        ballPosition.setX(ballPosition.getX() + ballVelocity.getX() / TICK_RATE);
        ballPosition.setY(ballPosition.getY() + ballVelocity.getY() / TICK_RATE);
    }

    protected void movePaddles() {
        // move player paddles based of their current movement direction
        for (Player player : gameState.getPlayers().values()) {
            Paddle paddle = player.getPaddle();

            int left = paddle.getLowestX();
            int right = paddle.getHighestX();
            int top = paddle.getLowestY();
            int bottom = paddle.getHighestY();

            int change = paddle.getVelocity() / TICK_RATE;

            if (paddle.getMovement().isUp() && top > 0) {
                paddle.moveAllPointsY(-change);
            }
            if (paddle.getMovement().isDown() && bottom < gameState.getPlayground().getHeight()) {
                paddle.moveAllPointsY(+change);
            }
            if (paddle.getMovement().isRight() && right < gameState.getPlayground().getWidth()) {
                paddle.moveAllPointsX(change);
            }
            if (paddle.getMovement().isLeft() && left > 0) {
                paddle.moveAllPointsX(-change);
            }
        }
    }

    /**
     * @param ballPosition
     * @param nextX
     * @param nextY
     * @param playgroundObject
     * @return normal vector of edge that just got hit
     */
    protected Vector2 checkInterceptions(Position ballPosition, double nextX, double nextY,
                                         PlaygroundObject playgroundObject) {
        Vector2 pt;

        Point topRight = playgroundObject.getpTopRight();
        Point topLeft = playgroundObject.getpTopLeft();
        Point bottomRight = playgroundObject.getpBottomRight();
        Point bottomLeft = playgroundObject.getpBottomLeft();

        pt = intercept2(ballPosition.getX(), ballPosition.getY(), nextX, nextY,
                topRight.x,
                topRight.y,
                bottomRight.x,
                bottomRight.y,
                Direction.RIGHT);

        if (pt == null) {
            pt = intercept2(ballPosition.getX(), ballPosition.getY(), nextX, nextY,
                    topLeft.x,
                    topLeft.y,
                    bottomLeft.x,
                    bottomLeft.y,
                    Direction.LEFT);

            if (pt == null) {

                pt = intercept2(ballPosition.getX(), ballPosition.getY(), nextX, nextY,
                        bottomLeft.x,
                        bottomLeft.y,
                        bottomRight.x,
                        bottomRight.y,
                        Direction.BOTTOM);
                if (pt == null) {
                    pt = intercept2(ballPosition.getX(), ballPosition.getY(), nextX, nextY,
                            topLeft.x,
                            topLeft.y,
                            topRight.x,
                            topRight.y,
                            Direction.TOP);
                }
            }
        }
        return pt;
    }


    private Vector2 getNormalizedPerpendicular(double x1, double y1, double x2, double y2){
        Vector2 normal = new Vector2();
        normal.x = x2 - x1;
        normal.y = y2 - y1;
        double temp = normal.x;
        normal.x = -normal.y;
        normal.y = temp;
        return normal.getNormalized();
    }

    protected Vector2 intercept2(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, Direction d) {
        boolean coll = Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
        if (coll) {
            return getNormalizedPerpendicular(x3,y3,x4,y4);
        } else {
            return null;
        }
    }

    protected Playground generateDefaultPlayground() {
        Playground playground = new Playground();
        playground.setHeight(900);
        playground.setWidth(1600);
        return playground;
    }

    protected Ball generateDefaultBall() {
        Ball ball = new Ball();
        ball.setColor("#0000ff");
        Position position = new Position();
        position.setX(300);
        position.setY(200);
        position.setRadius(10);
        ball.setPosition(position);

        Velocity velocity = new Velocity();
        velocity.setDefaultSpeed(500);
        velocity.setSpeed(velocity.getDefaultSpeed());
        velocity.setX(velocity.getSpeed() * 0.5);
        velocity.setY(velocity.getSpeed() * 0.5);
        velocity.setAcceleration(0.001f);

        ball.setVelocity(velocity);
        return ball;
    }

    protected Player generateCustomPaddlePlayer(String username) {
        Player player = new Player();
        player.setUsername(username);
        Paddle paddle = new Paddle();
        Movement movement = new Movement();
        movement.setDown(false);
        movement.setLeft(false);
        movement.setRight(false);
        movement.setUp(false);
        paddle.setMovement(movement);
        paddle.setVelocity(0);
        paddle.setColor(playerCountToColor.get(getPlayerCount()));
        resetCustomPaddlePosition(paddle);

        Set<Direction> allowedMovement = new HashSet<>();
        player.setAllowedMovementDirections(allowedMovement);
        player.setPosition(positionMap.get(gameState.getPlayers().size()));
        player.setPaddle(paddle);
        player.setPoints(0);
        player.setUsingCustomPaddle(true);
        player.setAllowedDrawingArea(directionToAllowedDrawingArea.get(player.getPosition()));
        return player;
    }

    protected Player generateDefaultPlayer(String username) {
        Player player = new Player();
        player.setUsername(username);
        Paddle paddle = new Paddle();
        Movement movement = new Movement();
        movement.setDown(false);
        movement.setLeft(false);
        movement.setRight(false);
        movement.setUp(false);
        paddle.setMovement(movement);
        paddle.setVelocity(300);

        paddle.setColor(playerCountToColor.get(getPlayerCount()));
        Point topLeft;
        int width;
        int height;
        Set<Direction> allowedMovement = new HashSet<>();
        int pWidth = gameState.getPlayground().getWidth();
        int pHeight = gameState.getPlayground().getHeight();
        int paddleLong = 100;
        int paddleShort = 20;
        int offset = 20;
        switch (positionMap.get(gameState.getPlayers().size())) {
            case TOP:
                width = paddleLong;
                height = paddleShort;
                topLeft = new Point(pWidth / 2 - width / 2, offset);
                allowedMovement.add(Direction.LEFT);
                allowedMovement.add(Direction.RIGHT);
                player.setPosition(Direction.TOP);
                break;
            case BOTTOM:
                width = paddleLong;
                height = paddleShort;
                topLeft = new Point(pWidth / 2 - width / 2, pHeight - height - offset);
                allowedMovement.add(Direction.LEFT);
                allowedMovement.add(Direction.RIGHT);
                player.setPosition(Direction.BOTTOM);
                break;
            case LEFT:
                width = paddleShort;
                height = paddleLong;
                topLeft = new Point(offset, pHeight / 2 - height / 2);
                allowedMovement.add(Direction.TOP);
                allowedMovement.add(Direction.BOTTOM);
                player.setPosition(Direction.LEFT);
                break;
            case RIGHT:
                width = paddleShort;
                height = paddleLong;
                topLeft = new Point(pWidth - paddleShort - offset, pHeight / 2 - height / 2);
                allowedMovement.add(Direction.TOP);
                allowedMovement.add(Direction.BOTTOM);
                player.setPosition(Direction.RIGHT);
                break;
            default:
                throw new RuntimeException("Invalid or missing direction while generating defaultPlayer");
        }
        paddle.setpTopLeft(topLeft);
        paddle.setpTopRight(new Point(topLeft.x + width, topLeft.y));
        paddle.setpBottomLeft(new Point(topLeft.x, topLeft.y + height));
        paddle.setpBottomRight(new Point(topLeft.x + width, topLeft.y + height));

        player.setPaddle(paddle);
        player.setAllowedMovementDirections(allowedMovement);
        player.setPoints(defaultPoints);
        player.setLifePoints(defaultLifePoints);
        player.setUsingCustomPaddle(false);
        return player;
    }

    public int getPlayerCount() {
        return gameState.getPlayers().size();
    }

    protected void randomBallDirection(Velocity ballVelocity) {
        double percentX = Math.random();
        double percentY = 1 - percentX;
        double direction = Math.random();
        if (direction < 0.25) {
            // bottom right
        } else if (direction < 0.5) {
            percentX *= -1;
        } else if (direction < 0.75) {
            percentY *= -1;
        } else {
            percentX *= -1;
            percentY *= -1;
        }

        ballVelocity.setX(ballVelocity.getSpeed() * percentX);
        ballVelocity.setY(ballVelocity.getSpeed() * percentY);
    }

    public void newCustomPaddle(WebSocketSession session, NewPaddleDrawn p){
        Paddle paddle = gameState.getPlayers().get(sessionToUsername.get(session)).getPaddle();
        int paddleWidth = 20;
        Vector2 norm = getNormalizedPerpendicular(p.getX1(), p.getY1(), p.getX2(), p.getY2());
        int xPlus = (int) (norm.x * paddleWidth/2);
        int yPlus = (int) (norm.y * paddleWidth/2);

        paddle.setpTopLeft(new Point(p.getX1() - xPlus, p.getY1() - yPlus));
        paddle.setpTopRight(new Point(p.getX1() + xPlus, p.getY1() + yPlus));
        paddle.setpBottomLeft(new Point(p.getX2() - xPlus, p.getY2() - yPlus));
        paddle.setpBottomRight(new Point(p.getX2() + xPlus, p.getY2() + yPlus));
    }

    protected void resetCustomPaddlePosition(Paddle paddle){
        paddle.setpTopLeft(new Point(0,0));
        paddle.setpTopRight(new Point(0,0));
        paddle.setpBottomLeft(new Point(0,0));
        paddle.setpBottomRight(new Point(0,0));
    }
    public void resetCustomPaddlePosition(WebSocketSession session){
        resetCustomPaddlePosition(gameState.getPlayers().get(sessionToUsername.get(session)).getPaddle());
    }
    public void resetCustomPaddlePosition(Player player){
        resetCustomPaddlePosition(player.getPaddle());
    }

    protected void changeBallDirectionToLoserPaddle(Velocity ballVelocity, Direction lastWallHit){
        int x = 1;
        int y = 1;
        switch (lastWallHit){
            case LEFT:
                x = -1;
                y = 0;
                break;
            case RIGHT:
                x = 1;
                y = 0;
                break;
            case TOP:
                x = 0;
                y = -1;
                break;
            case BOTTOM:
                x = 0;
                y = 1;
                break;
            default:
                break;
        }
        ballVelocity.setX(ballVelocity.getSpeed() * x);
        ballVelocity.setY(ballVelocity.getSpeed() * y);
    }

    protected void generateDefaultAllowedDrawingAreas(){
        int playgroundHeight = gameState.getPlayground().getHeight();
        int playgroundWidth = gameState.getPlayground().getWidth();

        directionToAllowedDrawingArea = new HashMap<>();
        int range = 400;
        FourPoints leftArea = new FourPoints();
        leftArea.setpTopLeft(new Point(0,0));
        leftArea.setpTopRight(new Point(range, 0));
        leftArea.setpBottomLeft(new Point(0, playgroundHeight));
        leftArea.setpBottomRight(new Point(range, playgroundHeight));
        directionToAllowedDrawingArea.put(Direction.LEFT, leftArea);

        FourPoints rightArea = new FourPoints();
        rightArea.setpTopLeft(new Point(playgroundWidth-range, 0));
        rightArea.setpTopRight(new Point(playgroundWidth, 0));
        rightArea.setpBottomLeft(new Point(playgroundWidth- range, playgroundHeight));
        rightArea.setpBottomRight(new Point(playgroundWidth, playgroundHeight));
        directionToAllowedDrawingArea.put(Direction.RIGHT, rightArea);

        FourPoints topArea = new FourPoints();
        topArea.setpTopLeft(new Point(0,0));
        topArea.setpTopRight(new Point(playgroundWidth, 0));
        topArea.setpBottomLeft(new Point(0, range));
        topArea.setpBottomRight(new Point(playgroundWidth, range));
        directionToAllowedDrawingArea.put(Direction.TOP, topArea);

        FourPoints bottomArea = new FourPoints();
        bottomArea.setpTopLeft(new Point(0,  playgroundHeight -range));
        bottomArea.setpTopRight(new Point(playgroundWidth, playgroundHeight - range));
        bottomArea.setpBottomLeft(new Point(0 ,playgroundHeight));
        bottomArea.setpBottomRight(new Point(playgroundWidth, playgroundHeight));
        directionToAllowedDrawingArea.put(Direction.BOTTOM, bottomArea);
    }

    public void removeSpectator(WebSocketSession session){
        gameState.getSpectators().remove(sessionToUsername.get(session));
        spectators.remove(session);
    }

    @Override
    public abstract void startNewMatch(WebSocketSession session);

    @Override
    public void setAdmin(String admin){
        gameState.setAdmin(admin);
    }
}
