package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

}
