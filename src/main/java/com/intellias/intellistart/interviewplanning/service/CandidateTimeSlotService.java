package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for candidate.
 */
@Service
@AllArgsConstructor
public class CandidateTimeSlotService {

  private UserRepository userRepository;
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  /**
   * Create time slot for Candidate.
   *
   * @param candidateId id of candidate
   * @param date        available date for time slot
   * @param from       start time of time slot
   * @param to         end time of time slot
   * @return candidate time slot
   */
  public CandidateTimeSlot createSlot(Long candidateId, LocalDate date,
      LocalTime from, LocalTime to) {
    return CandidateTimeSlot.builder()
        .from(from)
        .to(to)
        .date(date)
        .user(User.builder().id(candidateId).build())
        .build();
  }

}
