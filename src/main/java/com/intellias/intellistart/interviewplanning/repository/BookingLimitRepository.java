package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for BookingLimit entity.
 */

@Repository
public interface BookingLimitRepository extends JpaRepository<BookingLimit, Long> {
  Optional<BookingLimit> findByUser(User user);

  void deleteByUser(User user);
}
