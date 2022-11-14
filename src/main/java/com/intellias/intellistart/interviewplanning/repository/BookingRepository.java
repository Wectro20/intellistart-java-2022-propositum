package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  void deleteAllByInterviewerTimeSlot_User(User user);

  List<Booking> findAllByInterviewerTimeSlotUser(User user);
}
