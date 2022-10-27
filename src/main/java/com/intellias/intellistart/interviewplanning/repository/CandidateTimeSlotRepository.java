package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

  // TODO: Check if this functionality is required
  //List<CandidateTimeSlot> findByUserId(Long id);

  List<CandidateTimeSlot> findByEmail(String candidateEmail);

  List<CandidateTimeSlot> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
