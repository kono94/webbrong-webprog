package de.hsbhv.gamegateway.data.rest.controller;

import de.hsbhv.gamegateway.data.access.handler.DataHandler;
import de.hsbhv.gamegateway.data.entities.Match;
import de.hsbhv.gamegateway.data.entities.MatchType;
import de.hsbhv.gamegateway.data.entities.Participation;
import de.hsbhv.gamegateway.data.entities.User;
import de.hsbhv.gamegateway.data.rest.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
public class StatisticsProvider {

  @Autowired
  private DataHandler dataHandler;

  private long lastTimeMillis = Calendar.getInstance().getTimeInMillis();

  @GetMapping("/user/{username}")
  public UserStatistic getUserStats(@PathVariable("username") String username) {

    UserStatistic statistic = new UserStatistic();

    User user = dataHandler.getUser(username);

    if (user != null) {
      statistic.setBreakoutStatistic(createUserGameStatistic(user, MatchType.BREAKOUT));
      statistic.setPongStatistic(createUserGameStatistic(user, MatchType.PONG));
    }

    return statistic;
  }

  @GetMapping("/user")
  public List<UserStatistic> getAllUserStats() {
    List<UserStatistic> userStatistics = new LinkedList<>();
    List<User> users = dataHandler.getUserList();
    users.forEach(user -> userStatistics.add(getUserStats(user.getUsername())));

    return userStatistics;
  }

  @GetMapping("/scoreboard/{matchType}")
  public ScoreBoard getScoreBoard(@PathVariable MatchType matchType){
    ScoreBoard scoreBoard = new ScoreBoard();
    List<User> users = dataHandler.getUserList();
    for (User user : users){
      UserGameStatistic statistic = createUserGameStatistic(user, matchType);
      if (statistic != null)
        scoreBoard.addEntry(createUserGameStatistic(user, matchType));
    }
    return scoreBoard;
  }

  @GetMapping("/match")
  public List<MatchStatistic> getAllMatches() {
    List<Match> matches = dataHandler.getMatches();
    return createMatchStatisticList(matches);
  }

  @GetMapping("/match/active")
  public List<MatchStatistic> getAllActiveMatches() {
    List<Match> matches = dataHandler.getActiveMatches();
    return createMatchStatisticList(matches);
  }

  @GetMapping("/match/active/{roomId}")
  public MatchStatistic getActiveMatch(@PathVariable("roomId") int roomId) {
    List<Match> matches = dataHandler.getActiveMatches(roomId);
    if (matches.isEmpty())
      return null;
    else
      return createMatchStatistic(matches.get(0));
  }
  private UserGameStatistic createUserGameStatistic(User user, MatchType matchType){

    UserGameStatistic statistic = null;
    List<Participation> allParticipations = dataHandler.getParticipations(user);

    List<Match> matches = dataHandler.getMatches(allParticipations, matchType);

    List<Participation> participations = new LinkedList<>();
    for (Match match : matches){
      participations.add(dataHandler.getParticipation(match, user));
    }

    if (!matches.isEmpty()) {
      statistic = new UserGameStatistic();
      statistic.setUsername(user.getUsername());
      double totalTime =
        matches.stream().filter(e -> (e.getStartDate() != null && e.getCompletionDate() != null))
          .mapToDouble(e -> ((e.getCompletionDate().getTimeInMillis() - e.getStartDate().getTimeInMillis()) / (double) 60000)).sum()
          + matches.stream().filter(e -> (e.getStartDate() != null && e.getCompletionDate() == null))
          .mapToDouble(e -> ((Calendar.getInstance().getTimeInMillis() - e.getStartDate().getTimeInMillis()) / (double) 60000)).sum();
      statistic.setTotalGameMinutes(totalTime);
      statistic.setTotalMatchesCompleted(matches.size());

    OptionalDouble averageScore = participations.stream().mapToDouble(Participation::getPoints).average();
    if (averageScore.isPresent())
      statistic.setAverageScore(averageScore.getAsDouble());

    double totalScore = participations.stream().mapToDouble(Participation::getPoints).sum();
    statistic.setTotalScore(totalScore);

    List<Participation> matchWinner = new LinkedList<>();
    matches.forEach(match -> matchWinner.add(match.getParticipations().stream()
      .max(Comparator.comparing(Participation::getPoints)).orElseThrow(NoSuchElementException::new)));

    long userWins = matchWinner.stream().filter(participation -> participation.getUser().equals(user)).count();
    statistic.setWinRate(userWins / (double) matches.size());
  }
    return statistic;
  }

  private List<MatchStatistic> createMatchStatisticList(List<Match> matches){
    List<MatchStatistic> statistics = new LinkedList<>();

    for (Match match : matches) {
      MatchStatistic statistic = createMatchStatistic(match);
      statistics.add(statistic);
    }
    return statistics;
  }

  private MatchStatistic createMatchStatistic(Match match){
    MatchStatistic statistic = new MatchStatistic();
    statistic.setCompleted(!match.isActive());
    statistic.setStartDate(match.getStartDate());
    if (!match.isActive()) {
      statistic.setCompletionDate(match.getCompletionDate());
      statistic.setDuration((match.getCompletionDate().getTimeInMillis() - match.getStartDate().getTimeInMillis()) / (double) 60000);
    }else {
      statistic.setDuration((Calendar.getInstance().getTimeInMillis() - match.getStartDate().getTimeInMillis()) / (double) 60000);
    }
    int count = 1;

    for (Participation user :
      match.getParticipations().stream().sorted((e1, e2) -> Double.compare(e2.getPoints(), e1.getPoints())).collect(Collectors.toList())) {
      UserInfo userInfo = new UserInfo();
      userInfo.setTotalPoints(user.getPoints());
      userInfo.setUsername(user.getUser().getUsername());
      userInfo.setPlacement(count++);
      statistic.addParticipant(userInfo);
    }

    return statistic;
  }
}
