package de.hsbhv.gamegateway.data.rest.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"username","winRate","totalScore","averageScore","totalMatchesCompleted","totalGameMinutes"})
public class UserGameStatistic {

  private double winRate;
  private int totalMatchesCompleted;
  private double averageScore;
  private double totalScore;
  private double totalGameMinutes;
  private String username;

  public String getUsername() {
    return username;
  }

  public double getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(double totalScore) {
    this.totalScore = totalScore;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public double getWinRate() {
    return winRate;
  }

  public void setWinRate(double winRate) {
    this.winRate = winRate;
  }

  public int getTotalMatchesCompleted() {
    return totalMatchesCompleted;
  }

  public void setTotalMatchesCompleted(int totalMatchesCompleted) {
    this.totalMatchesCompleted = totalMatchesCompleted;
  }

  public double getAverageScore() {
    return averageScore;
  }

  public void setAverageScore(double averageScore) {
    this.averageScore = averageScore;
  }

  public double getTotalGameMinutes() {
    return totalGameMinutes;
  }

  public void setTotalGameMinutes(double totalGameMinutes) {
    this.totalGameMinutes = totalGameMinutes;
  }
}
