package de.hsbhv.gamegateway.data.rest.controller;

import de.hsbhv.gamegateway.data.access.handler.DataHandler;
import de.hsbhv.gamegateway.data.entities.Match;
import de.hsbhv.gamegateway.data.entities.MatchType;
import de.hsbhv.gamegateway.data.entities.Participation;
import de.hsbhv.gamegateway.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventReceiver {

  @Autowired
  DataHandler dataHandler;

  @GetMapping("/newMatch/{matchType}/{usernames}")
  public int newMatch(@PathVariable MatchType matchType, @PathVariable String[] usernames) {
    Match match = new Match();
    match.setActive(true);
    match.setMatchType(matchType);
    match.setStartDate(Calendar.getInstance());
    dataHandler.saveMatch(match);

    for (String username : usernames) {
      User user = dataHandler.getUser(username);
      if (user == null) {
        user = new User(username);
        dataHandler.saveUser(user);
      }
      Participation participation = new Participation();
      participation.setMatch(match);
      participation.setUser(user);
      dataHandler.saveParticipation(participation);
    }
    return match.getId();
  }

  @GetMapping("/matchFinished/{matchId}")
  public void matchFinished(@PathVariable int matchId) {
    Match match = dataHandler.getMatch(matchId);
    if (match != null) {
      match.setActive(false);
      match.setCompletionDate(Calendar.getInstance());
    }
    dataHandler.saveMatch(match);
  }

  @GetMapping("/matchCancelled/{matchId}")
  public void matchCancelled(@PathVariable("matchId") int matchId) {
    Match match = dataHandler.getMatch(matchId);
    if (match != null) {
      for (Participation participation : match.getParticipations())
        dataHandler.deleteParticipation(participation);

      dataHandler.deleteMatch(matchId);
    }
  }

  @GetMapping("/userScored/{matchId}/{username}/{points}")
  public void userScored(@PathVariable int matchId, @PathVariable String username, @PathVariable int points) {
    Match match = dataHandler.getMatch(matchId);
    User user = dataHandler.getUser(username);
    Participation participation = null;
    if (match != null && user != null)
      participation = dataHandler.getParticipation(match, user);
    if (participation != null) {
      participation.setPoints(points);
      dataHandler.saveParticipation(participation);
    }
  }

  @GetMapping("/userEnteredMatch/{matchId}/{username}")
  public void userEnteredMatch(@PathVariable int matchId, @PathVariable String username) {
    User user = dataHandler.getUser(username);
    if (user == null) {
      user = new User(username);
    }
    Match match = dataHandler.getMatch(matchId);
    if (match != null) {
      dataHandler.saveUser(user);
      Participation participation = new Participation(user, match);
      dataHandler.saveParticipation(participation);
    }
  }

  @GetMapping("/userLeftMatch/{matchId}/{username}")
  public void userLeftMatch(@PathVariable int matchId, @PathVariable String username) {
    User user = dataHandler.getUser(username);
    Match match = dataHandler.getMatch(matchId);
    if (match != null && user != null) {
      Participation participation = dataHandler.getParticipation(match, user);
      if (participation != null)
        dataHandler.deleteParticipation(participation);
    }
  }
}
