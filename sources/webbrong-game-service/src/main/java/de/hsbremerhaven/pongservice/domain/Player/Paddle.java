package de.hsbremerhaven.pongservice.domain.Player;

import de.hsbremerhaven.pongservice.domain.PlaygroundObject;

public class Paddle extends PlaygroundObject {

    private Movement movement;
    private int velocity;

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
