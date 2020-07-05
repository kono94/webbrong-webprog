package de.hsbremerhaven.pongservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovementMessage {
    private Event event = Event.PADDLE_MOVEMENT;
    private Direction direction;
    private boolean isMoving;

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    @JsonProperty("isMoving")
    public boolean isMoving() {
        return isMoving;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
