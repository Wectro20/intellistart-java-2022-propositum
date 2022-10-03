package com.intellias.intellistart.interviewplanning.repo;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for CandidateTimeSlot entity.
 */
@Repository
public interface CandidateTimeSlotRepository extends JpaRepository<CandidateTimeSlot, Long> {

}
