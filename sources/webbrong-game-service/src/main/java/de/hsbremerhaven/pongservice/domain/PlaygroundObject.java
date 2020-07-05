package de.hsbremerhaven.pongservice.domain;

import java.awt.*;

public class PlaygroundObject {
  private Point pTopLeft;
  private Point pTopRight;
  private Point pBottomRight;
  private Point pBottomLeft;
  private String color;

  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }

  public Point getpTopLeft() {
    return pTopLeft;
  }

  public void setpTopLeft(Point pTopLeft) {
    this.pTopLeft = pTopLeft;
  }

  public Point getpTopRight() {
    return pTopRight;
  }

  public void setpTopRight(Point pTopRight) {
    this.pTopRight = pTopRight;
  }

  public Point getpBottomRight() {
    return pBottomRight;
  }

  public void setpBottomRight(Point pBottomRight) {
    this.pBottomRight = pBottomRight;
  }

  public Point getpBottomLeft() {
    return pBottomLeft;
  }

  public void setpBottomLeft(Point pBottomLeft) {
    this.pBottomLeft = pBottomLeft;
  }

  public Point getTopMiddle(){
     return new Point(Math.abs(pTopRight.x - pTopLeft.x), Math.abs(pTopRight.y - pTopLeft.y));
  }
  public Point getBottomMiddle(){
    return new Point(Math.abs(pBottomRight.x - pBottomLeft.x), Math.abs(pBottomRight.y - pBottomLeft.y));
  }

  public void moveAllPointsY(int value){
    pTopRight.y += value;
    pTopLeft.y += value;
    pBottomRight.y += value;
    pBottomLeft.y += value;
  }

  public void moveAllPointsX(int value){
    pTopRight.x += value;
    pTopLeft.x += value;
    pBottomRight.x += value;
    pBottomLeft.x += value;
  }

  public int getLowestX(){
    return Math.min(pTopLeft.x, pBottomLeft.x);
  }

  public int getHighestX(){
    return Math.max(pTopRight.x, pBottomRight.x);
  }

  public int getLowestY(){
    return Math.min(pTopRight.y, pTopLeft.y);

  }

  public int getHighestY(){
    return Math.max(pBottomRight.y, pBottomLeft.y);

  }
}
