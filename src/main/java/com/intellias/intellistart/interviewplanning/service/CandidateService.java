package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.user.Candidate;
import com.intellias.intellistart.interviewplanning.repo.CandidateRepository;
import com.intellias.intellistart.interviewplanning.repo.CandidateTimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for candidate.
 */
@Service
@AllArgsConstructor
public class CandidateService {

  private CandidateRepository candidateRepository;
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  public CandidateTimeSlot createSlot(Long candidateId, LocalDate date, LocalTime start, LocalTime end) {
    return CandidateTimeSlot.builder()
        .date(date)
        .start(start)
        .end(end)
        .candidate(Candidate.builder().id(candidateId).build())
        .build();
  }

}
