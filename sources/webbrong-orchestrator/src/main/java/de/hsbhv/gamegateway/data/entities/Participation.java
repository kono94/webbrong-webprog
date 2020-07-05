package de.hsbhv.gamegateway.data.entities;

import javax.persistence.*;

@Entity
public class Participation {
  @GeneratedValue
  @Id
  private int id;

  @ManyToOne
  @JoinColumn
  private User user;

  @ManyToOne
  @JoinColumn
  private Match match;

  private double points;

  public Participation(User user, Match match) {
    this.user = user;
    this.match = match;
  }

  public Participation() {
  }

  public User getUser() {
    return user;
  }

  public Match getMatch() {
    return match;
  }

  public double getPoints() {
    return points;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setMatch(Match match) {
    this.match = match;
  }

  public void setPoints(double points) {
    this.points = points;
  }
}
