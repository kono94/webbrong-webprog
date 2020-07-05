package de.hsbremerhaven.pongservice.domain.Player;

import de.hsbremerhaven.pongservice.domain.Direction;

import java.util.Set;

public class Player {
    private Paddle paddle;
    private String username;
    private int points;
    private Direction position;
    private Set<Direction> allowedMovementDirections;
    private boolean isUsingCustomPaddle;
    private FourPoints allowedDrawingArea;
    private boolean isSpectating;
    private int lifePoints;

    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Set<Direction> getAllowedMovementDirections() {
        return allowedMovementDirections;
    }

    public void setAllowedMovementDirections(Set<Direction> allowedMovementDirections) {
        this.allowedMovementDirections = allowedMovementDirections;
    }

    public Direction getPosition() {
        return position;
    }

    public void setPosition(Direction position) {
        this.position = position;
    }

    public boolean isUsingCustomPaddle() {
        return isUsingCustomPaddle;
    }

    public void setUsingCustomPaddle(boolean usingCustomPaddle) {
        isUsingCustomPaddle = usingCustomPaddle;
    }

    public FourPoints getAllowedDrawingArea() {
        return allowedDrawingArea;
    }

    public void setAllowedDrawingArea(FourPoints allowedDrawingArea) {
        this.allowedDrawingArea = allowedDrawingArea;
    }

    public boolean isSpectating() {
        return isSpectating;
    }

    public void setSpectating(boolean spectating) {
        isSpectating = spectating;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
