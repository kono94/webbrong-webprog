package de.hsbhv.gamegateway.data.rest.messages;

import java.util.LinkedList;
import java.util.List;

public class ScoreBoard {
  private String[] headers = {"Username","Winrate","Total score","Average score","Total matches completed"
    ,"Total minutes played"};
  private List<UserGameStatistic> data = new LinkedList<>();

  public void addEntry(UserGameStatistic statistic){
    data.add(statistic);
  }

  public String[] getHeaders() {
    return headers;
  }

  public void setHeaders(String[] headers) {
    this.headers = headers;
  }

  public List<UserGameStatistic> getData() {
    return data;
  }

  public void setData(List<UserGameStatistic> data) {
    this.data = data;
  }
}
