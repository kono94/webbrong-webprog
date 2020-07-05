package de.hsbremerhaven.pongservice.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsbremerhaven.pongservice.config.HandlerType;
import de.hsbremerhaven.pongservice.config.RestHandler;
import de.hsbremerhaven.pongservice.domain.Direction;
import de.hsbremerhaven.pongservice.domain.Match;
import de.hsbremerhaven.pongservice.domain.Player.Player;
import de.hsbremerhaven.pongservice.domain.IngameMessage;
import de.hsbremerhaven.pongservice.domain.ball.Position;
import de.hsbremerhaven.pongservice.domain.ball.Velocity;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PongEngine extends GameEngine{

    private int maxReachablePoints;
    public PongEngine(String roomID) {
        this.roomID = roomID;
        isRunning = true;
        defaultLifePoints = 10;
        maxReachablePoints = 10;
        defaultPoints = 0;
        positionMap = new HashMap<>();
        positionMap.put(0, Direction.LEFT);
        positionMap.put(1, Direction.RIGHT);
        positionMap.put(2, Direction.TOP);
        positionMap.put(3, Direction.BOTTOM);

        gameState.setPlayground(generateDefaultPlayground());
        gameState.setPlayers(new HashMap<>());
        gameState.setBall(generateDefaultBall());
        generateDefaultAllowedDrawingAreas();
    }

    @Override
    protected void processNextTick() {
        Position ballPosition = gameState.getBall().getPosition();
        Velocity ballVelocity = gameState.getBall().getVelocity();

        accelerateBall(ballVelocity);
        double potX = ballPosition.getX() + ballVelocity.getX() / TICK_RATE;
        double potY = ballPosition.getY() + ballVelocity.getY() / TICK_RATE;
       // System.out.println(potX + " " + ballPosition.getX());
        //System.out.println("potY " + potY + " " + ballPosition.getY());
        Direction borderHit = checkForBorderCollision(potX, potY);
        if (borderHit != null) {
            boolean lost = false;
            AtomicBoolean finishMatch = new AtomicBoolean(false);
            for(Player player: gameState.getPlayers().values()){
                if(player.getPosition() == borderHit){
                    lost = true;
                }
            }

            if(lost){
                gameState.getPlayers().forEach((username, player) -> {
                    if(player.getPosition() != borderHit){
                        player.setPoints(player.getPoints() + 1);
                        RestHandler.getInstance().addPointForPlayer(match.getMatchID(), username , player.getPoints());

                        if(player.getPoints() ==  maxReachablePoints){
                            finishMatch.set(true);
                        }
                    }
                });

                gameState.getBall().getPosition().setX(gameState.getPlayground().getWidth() / 2f);
                gameState.getBall().getPosition().setY(gameState.getPlayground().getHeight() / 2f);
                gameState.getBall().getVelocity().setSpeed(gameState.getBall().getVelocity().getDefaultSpeed());
                changeBallDirectionToLoserPaddle(gameState.getBall().getVelocity(), borderHit);
            }

            if(finishMatch.get()){
                match.setRunning(false);
                RestHandler.getInstance().endMatch(match.getMatchID());
            }

        } else {
            if (!checkForPaddleCollision(potX, potY)) {
                ballPosition.setX(potX);
                ballPosition.setY(potY);
            }
        }
        movePaddles();
    }

    @Override
    public void startNewMatch(WebSocketSession session){
        if(getPlayerCount() < 2 || match.isRunning() ||  !sessionToUsername.get(session).equals(gameState.getAdmin())){
            IngameMessage r = new IngameMessage();
            r.setMessage("Need 2 Players to start the match");
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        String[] stringArray = Arrays.copyOf(gameState.getPlayers().keySet().toArray(), gameState.getPlayers().keySet().toArray().length, String[].class);
        try {
            String matchID = RestHandler.getInstance().startMatch(HandlerType.PONG.toString(), String.join("," , stringArray));
            match = new Match();
            match.setMatchID(Integer.parseInt(matchID));
            gameState.getPlayers().values().forEach(e-> e.setPoints(0));
            match.setRunning(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
