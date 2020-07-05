package de.hsbhv.gamegateway.data.access.repos;

import de.hsbhv.gamegateway.data.entities.Match;
import de.hsbhv.gamegateway.data.entities.Participation;
import de.hsbhv.gamegateway.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

  List<Participation> findDistinctByUserEquals(User user);
  Participation findFirstByMatchEqualsAndUserEquals(Match match, User user);
}
