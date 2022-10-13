package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for InterviewTimeSlot entity.
 */
@Repository
public interface InterviewerTimeSlotRepository extends JpaRepository<InterviewerTimeSlot, Integer> {

  Optional<InterviewerTimeSlot> findById(int id);

  Optional<InterviewerTimeSlot> findByUserAndWeekNum(User user, int weekNum);
}
