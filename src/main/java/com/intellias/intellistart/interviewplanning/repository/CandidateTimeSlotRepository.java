package com.intellias.intellistart.interviewplanning.repository;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import java.util.List;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

  List<CandidateTimeSlot> findByUserId(Long id);

  List<InterviewerTimeSlot> findByEmail(String candidateEmail);
}
