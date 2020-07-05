package de.hsbhv.gamegateway.data.access.handler;

import de.hsbhv.gamegateway.data.access.repos.MatchRepository;
import de.hsbhv.gamegateway.data.access.repos.ParticipationRepository;
import de.hsbhv.gamegateway.data.access.repos.UserRepository;
import de.hsbhv.gamegateway.data.entities.Match;
import de.hsbhv.gamegateway.data.entities.MatchType;
import de.hsbhv.gamegateway.data.entities.Participation;
import de.hsbhv.gamegateway.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataHandler {
  @Autowired
  private MatchRepository matchRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ParticipationRepository participationRepository;

  public List<Match> getActiveMatches(){
    return matchRepository.findDistinctByActiveIsTrue();
  }

  public List<Match> getActiveMatches(int roomId){
    return matchRepository.findDistinctByActiveIsTrueAndRoomIdEquals(roomId);
  }

  public List<Match> getMatches(List<Participation> participations, MatchType matchType){
    return matchRepository.findDistinctByParticipationsInAndMatchTypeEquals(participations, matchType);
  }

  public List<Match> getMatches(){
    return matchRepository.findAll();
  }

  public Match getMatch(int id){
    Optional<Match> match = matchRepository.findById(id);
    return match.orElse(null);
  }

  public void deleteMatch(int matchId){
    matchRepository.deleteById(matchId);
  }

  public List<Participation> getParticipations(User user){
    return participationRepository.findDistinctByUserEquals(user);
  }

  public Participation getParticipation(Match match, User user){
    return participationRepository.findFirstByMatchEqualsAndUserEquals(match, user);
  }

  public void deleteParticipation(Participation participation){
    participationRepository.delete(participation);
  }

  public User getUser(String username){
    return userRepository.findFirstByUsername(username);
  }

  public List<User> getUserList(){
    return userRepository.findAll();
  }

  public void saveUser(User user){
    userRepository.save(user);
  }

  public void saveParticipation(Participation participation){
    participationRepository.save(participation);
  }

  public void saveMatch(Match match){
    matchRepository.save(match);
  }
}
