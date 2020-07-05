package de.hsbremerhaven.pongservice.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsbremerhaven.pongservice.config.HandlerType;
import de.hsbremerhaven.pongservice.config.RestHandler;
import de.hsbremerhaven.pongservice.domain.*;
import de.hsbremerhaven.pongservice.domain.Event;
import de.hsbremerhaven.pongservice.domain.Player.Player;
import de.hsbremerhaven.pongservice.domain.ball.Ball;
import de.hsbremerhaven.pongservice.domain.ball.Position;
import de.hsbremerhaven.pongservice.domain.ball.Velocity;
import de.hsbremerhaven.pongservice.domain.obstacle.Obstacle;
import de.hsbremerhaven.pongservice.engine.GameEngine;
import org.dyn4j.geometry.Vector2;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BreakoutEngine extends GameEngine {

    private Map<Integer, String> obstacleHitpointsToColors;

    public BreakoutEngine(String roomID) {
        destinationPrefix = "/breakout-room/";
        defaultLifePoints = 10;
        defaultPoints = 0;
        this.roomID = roomID;
        isRunning = true;
        positionMap = new HashMap<>();
        positionMap.put(0, Direction.LEFT);
        positionMap.put(1, Direction.RIGHT);
        positionMap.put(2, Direction.TOP);
        positionMap.put(3, Direction.BOTTOM);

        gameState.setPlayground(generateDefaultPlayground());
        gameState.setPlayers(new HashMap<>());
        gameState.setBall(generateDefaultBall());
        gameState.setObstacles(new HashSet<>());
        obstacleHitpointsToColors = new HashMap<>();
        obstacleHitpointsToColors.put(1, "#f232ff");
        obstacleHitpointsToColors.put(2, "#82A232");
        obstacleHitpointsToColors.put(3, "#242424");

        int obsAreaWidth = gameState.getPlayground().getWidth() / 3;
        int obsAreaHeight = gameState.getPlayground().getHeight() / 3;
        int startX = gameState.getPlayground().getWidth() / 2 - obsAreaWidth/2;
        int startY = gameState.getPlayground().getHeight() / 2 - obsAreaHeight/2;

        for (int i = startX; i < startX + obsAreaWidth; i+=90) {
            for (int j = startY; j < startY + obsAreaHeight; j+=40) {
                Obstacle obstacle = new Obstacle();
                int width = 90;
                int height = 40;
                obstacle.setpTopLeft(new Point(i, j));
                obstacle.setpTopRight(new Point(i + width, j));
                obstacle.setpBottomLeft(new Point(i, j +height));
                obstacle.setpBottomRight(new Point(i + width, j +height));
                int defaultHitPoints = (int) (Math.random() * 3 + 1);
                obstacle.setHitPoints(defaultHitPoints);
                obstacle.setColor(obstacleHitpointsToColors.get(defaultHitPoints));
                gameState.getObstacles().add(obstacle);
            }
        }

        generateDefaultAllowedDrawingAreas();
    }

    @Override
    protected void processNextTick() {
        Position ballPosition = gameState.getBall().getPosition();
        Velocity ballVelocity = gameState.getBall().getVelocity();

        accelerateBall(ballVelocity);

        int potX = (int) (ballPosition.getX() + ballVelocity.getX() / TICK_RATE);
        int potY = (int) (ballPosition.getY() + ballVelocity.getY() / TICK_RATE);

        Direction borderHit = checkForBorderCollision(potX, potY);
        if (borderHit != null) {
            Player loser = null;
            for(Map.Entry<String, Player> entry : gameState.getPlayers().entrySet()){
                String username =  entry.getKey();
                Player player = (Player) entry.getValue();
                if(player.getPosition() == borderHit){
                    player.setLifePoints(player.getLifePoints() - 1);
                    if(player.getLifePoints() == 0){
                        sessionToUsername.forEach((ws, uname) -> {
                            if(uname.equals(username)){
                                removePlayer(ws);
                                spectators.add(ws);
                                IngameMessage r = new IngameMessage();
                                r.setMessage("You got no life points left! You are switched to spectators");
                                try {
                                    ws.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(r)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if(gameState.getPlayers().size() == 0){
                            match.setRunning(false);
                            RestHandler.getInstance().endMatch(match.getMatchID());
                        }else{
                          loser = (Player) gameState.getPlayers().values().toArray()[0];
                        }
                    }else{
                        loser = player;
                    }
                }
            }

            if(loser != null){
                int offsetX = 0;
                int offsetY = 0;
                switch (loser.getPosition()){
                    case TOP:
                        offsetY = 200;
                        break;
                    case BOTTOM:
                        offsetY = -200;
                        break;
                    case RIGHT:
                        offsetX = -200;
                        break;
                    case LEFT:
                        offsetX = 200;
                        break;
                }

                gameState.getBall().getPosition().setX((loser.getPaddle().getHighestX() + loser.getPaddle().getLowestX())/2f + offsetX);
                gameState.getBall().getPosition().setY((loser.getPaddle().getHighestY() + loser.getPaddle().getLowestY())/2f + offsetY);
                gameState.getBall().getVelocity().setSpeed(gameState.getBall().getVelocity().getDefaultSpeed());
                changeBallDirectionToLoserPaddle(gameState.getBall().getVelocity(), loser.getPosition());
            }
        }else if (!checkForPaddleCollision(potX, potY) && !checkForObstacleCollision(potX, potY)) {
            ballPosition.setX(potX);
            ballPosition.setY(potY);
        }
        movePaddles();
    }

    protected boolean checkForObstacleCollision(float potX, float potY) {
        Ball ball = gameState.getBall();
        Position ballPosition = ball.getPosition();
        Velocity ballVelocity = ball.getVelocity();

        Vector2 normalVectorOfHitEdge = null;
        if (gameState.getObstacles() != null) {
            Iterator it = gameState.getObstacles().iterator();
            while (normalVectorOfHitEdge == null && it.hasNext()) {
                Obstacle obstacle = (Obstacle) it.next();
                if (obstacle.getHitPoints() > 0) {
                    normalVectorOfHitEdge = checkInterceptions(ballPosition, potX, potY, obstacle);
                    if (normalVectorOfHitEdge != null) {
                        // if the ball hit something, adjust the direction the ball is flying
                        adjustBallVelocity(normalVectorOfHitEdge, ballVelocity, ballPosition, null);
                        int newHP = obstacle.getHitPoints() - 1;
                        obstacle.setHitPoints(newHP);
                        obstacle.setColor(obstacleHitpointsToColors.get(newHP));
                        if (lastHitPlayer != null) {
                            int gainingPoints;
                            if(obstacle.getHitPoints() == 0){
                                gameState.getObstacles().remove(obstacle);
                                gainingPoints = 2;
                            }else {
                                gainingPoints = 1;
                            }
                            lastHitPlayer.setPoints(lastHitPlayer.getPoints() + gainingPoints);
                            RestHandler.getInstance().addPointForPlayer(match.getMatchID(), lastHitPlayer.getUsername(), lastHitPlayer.getPoints());

                            if(gameState.getObstacles().size() == 0){
                                match.setRunning(false);
                                RestHandler.getInstance().endMatch(match.getMatchID());
                            }
                        }

                    }
                }
            }
        }

        return normalVectorOfHitEdge != null;
    }

    @Override
    public void startNewMatch(WebSocketSession session){
        if(getPlayerCount() < 1 ||  match.isRunning() || !sessionToUsername.get(session).equals(gameState.getAdmin())){
            //TODO SEND ERROR MESSAGE
            return;
        }
        try {
            String[] stringArray = Arrays.copyOf(gameState.getPlayers().keySet().toArray(), gameState.getPlayers().keySet().toArray().length, String[].class);
            String matchID = RestHandler.getInstance().startMatch(HandlerType.BREAKOUT.toString(),String.join(",", stringArray));
            match = new Match();
            match.setMatchID(Integer.parseInt(matchID));
            match.setRunning(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
