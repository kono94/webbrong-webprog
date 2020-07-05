package de.hsbhv.gamegateway.data.rest.messages;

public class UserStatistic {

  private UserGameStatistic pongStatistic;
  private UserGameStatistic breakoutStatistic;

  public UserGameStatistic getPongStatistic() {
    return pongStatistic;
  }

  public UserGameStatistic getBreakoutStatistic() {
    return breakoutStatistic;
  }

  public void setPongStatistic(UserGameStatistic pongStatistic) {
    this.pongStatistic = pongStatistic;
  }

  public void setBreakoutStatistic(UserGameStatistic breakoutStatistic) {
    this.breakoutStatistic = breakoutStatistic;
  }
}
