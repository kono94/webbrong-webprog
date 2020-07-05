package de.hsbhv.gamegateway.data.rest.messages;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MatchStatistic {
  private List<UserInfo> participants = new LinkedList<>();
  private double duration;
  private Calendar startDate;
  private Calendar completionDate;
  private boolean isCompleted;

  public void addParticipant(UserInfo userInfo){
    participants.add(userInfo);
  }

  public Calendar getStartDate() {
    return startDate;
  }

  public void setStartDate(Calendar startDate) {
    this.startDate = startDate;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean completed) {
    isCompleted = completed;
  }

  public List<UserInfo> getParticipants() {
    return participants;
  }

  public void setParticipants(List<UserInfo> participants) {
    this.participants = participants;
  }

  public double getDuration() {
    return duration;
  }

  public void setDuration(double duration) {
    this.duration = duration;
  }

  public Calendar getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(Calendar completionDate) {
    this.completionDate = completionDate;
  }
}
