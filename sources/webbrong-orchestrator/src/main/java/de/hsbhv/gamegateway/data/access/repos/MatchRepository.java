package de.hsbhv.gamegateway.data.access.repos;

import de.hsbhv.gamegateway.data.entities.Match;
import de.hsbhv.gamegateway.data.entities.MatchType;
import de.hsbhv.gamegateway.data.entities.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Integer> {
  List<Match> findDistinctByParticipationsInAndMatchTypeEquals(List<Participation> participations, MatchType matchType);
  List<Match> findDistinctByActiveIsTrue();
  List<Match> findDistinctByActiveIsTrueAndRoomIdEquals(int roomId);
}
