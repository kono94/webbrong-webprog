package de.hsbhv.gamegateway.data.entities;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Entity
public class Match {

  @GeneratedValue
  @Id
  private int id;
  private int roomId;
  //private int totalTime;
  @OneToMany(mappedBy = "match", cascade = CascadeType.ALL)
  private Set<Participation> participations;
  private MatchType matchType;
  private Calendar completionDate, startDate;
  private boolean active;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Calendar getStartDate() {
    return startDate;
  }

  public void setStartDate(Calendar startDate) {
    this.startDate = startDate;
  }

  public Calendar getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(Calendar completionDate) {
    this.completionDate = completionDate;
  }

//  public int getTotalTime() {
//    return totalTime;
//  }

  public Set<Participation> getParticipations() {
    return participations;
  }

  public MatchType getMatchType() {
    return matchType;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getRoomId() {
    return roomId;
  }

  public void setRoomId(int roomId) {
    this.roomId = roomId;
  }

//  public void setTotalTime(int totalTime) {
//    this.totalTime = totalTime;
//  }

  public void setParticipations(Set<Participation> participations) {
    this.participations = participations;
  }

  public void setMatchType(MatchType matchType) {
    this.matchType = matchType;
  }
}
