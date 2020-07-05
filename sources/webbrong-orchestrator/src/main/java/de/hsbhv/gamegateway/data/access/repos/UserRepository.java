package de.hsbhv.gamegateway.data.access.repos;

import de.hsbhv.gamegateway.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
  User findFirstByUsername(String username);
}
