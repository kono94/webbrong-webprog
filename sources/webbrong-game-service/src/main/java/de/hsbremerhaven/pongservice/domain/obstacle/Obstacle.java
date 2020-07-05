package de.hsbremerhaven.pongservice.domain.obstacle;

import de.hsbremerhaven.pongservice.domain.PlaygroundObject;

public class Obstacle extends PlaygroundObject {
  private int hitPoints;

  public int getHitPoints() {
    return hitPoints;
  }

  public void setHitPoints(int hitPoints) {
    this.hitPoints = hitPoints;
  }
}
