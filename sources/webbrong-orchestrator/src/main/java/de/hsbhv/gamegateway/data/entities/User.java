package de.hsbhv.gamegateway.data.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
  @GeneratedValue
  @Id
  private int id;
  private String username;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Participation> participations;

  public User(String username) {
    this.username = username;
  }

  public User() {
  }

  public String getUsername() {
    return username;
  }

  public Set<Participation> getParticipations() {
    return participations;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setParticipations(Set<Participation> participations) {
    this.participations = participations;
  }
}
