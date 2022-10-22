package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for candidate.
 */
@Service
@AllArgsConstructor
public class CandidateTimeSlotService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  /**
   * Create time slot for Candidate.
   *
   * @param candidateEmail email of candidate
   * @param date           available date for time slot
   * @param from           start time of time slot
   * @param to             end time of time slot
   * @return candidate time slot
   */
  // TODO: Refactor class, don't need User Validation anymore
  public CandidateTimeSlot createSlot(String candidateEmail, LocalDate date,
                                      LocalTime from, LocalTime to) {
    if (date != null && from != null && to != null) {
      validateTimeSlot(date, from, to);
    }

    //User candidate = validateCandidate(candidateEmail, date, from, to);

    return candidateTimeSlotRepository.save(CandidateTimeSlot.builder()
        .date(date)
        .from(from)
        .to(to)
        .slotStatus(TimeSlotStatus.NEW)
        .email(candidateEmail)
        .build());
  }

  public List<CandidateTimeSlot> getTimeSlots(String candidateEmail) {
    return candidateTimeSlotRepository.findByEmail(candidateEmail);
  }

  public CandidateTimeSlot updateSlot(LocalDate date,
                                      LocalTime from, LocalTime to) {
    if (date != null && from != null && to != null) {
      validateTimeSlot(date, from, to);
    }

    return candidateTimeSlotRepository.save(CandidateTimeSlot.builder()
        .date(date)
        .from(from)
        .to(to)
        .slotStatus(TimeSlotStatus.NEW)
        .build());
  }

  public CandidateTimeSlot getTimeSlotId(final Long id) {
    return candidateTimeSlotRepository.findById(id).orElse(null);
  }

  /**
   * Validate time slot for Candidate.
   *
   * @param date  available date for time slot
   * @param start start time of time slot
   * @param end   end time of time slot
   */
  private void validateTimeSlot(LocalDate date, LocalTime start, LocalTime end) {
    if (date.isBefore(LocalDate.now()) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)
        || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
      throw new InvalidDayOfWeekException(date.toString());
    }

    if (start.isAfter(LocalTime.of(22, 0)) || start.isBefore(LocalTime.of(8, 0))
        || end.isAfter(LocalTime.of(22, 0)) || end.isBefore(LocalTime.of(8, 0))
        || start.getMinute() % 30 != 0 || end.getMinute() % 30 != 0 || start.isAfter(end)) {
      throw new InvalidTimeSlotBoundariesException(start + "; " + end);
    }

    if (Duration.between(start, end).getSeconds() < 5400) {
      throw new InvalidTimeSlotBoundariesException(start + "; " + end);
    }
  }

  /**
   * Validate candidate for time slot.
   *
   * @param candidateEmail email of candidate
   * @param date           available date for time slot
   * @param start          start time of time slot
   * @param end            end time of time slot
   * @return candidate
   */
  // TODO: Check if this functionality is required
  /*
  private User validateCandidate(String candidateEmail, LocalDate date, LocalTime start,
      LocalTime end) {
    Optional<User> optionalCandidate = userRepository.findByEmail(candidateEmail);
    User candidate = optionalCandidate.orElseThrow(
        () -> new UserNotFoundException(candidateEmail));

    Optional<CandidateTimeSlot> overlappingSlot = candidateTimeSlotRepository.findByUserId(
            candidate.getId())
        .stream()
        .filter(candidateTimeSlot -> candidateTimeSlot.getDate().equals(date)
            &&
            candidateTimeSlot.getFrom().equals(start) && candidateTimeSlot.getTo().equals(end))
        .findAny();

    if (overlappingSlot.isPresent()) {
      throw new SlotIsOverlappingException(overlappingSlot.get().getId());
    }

    return candidate;
  }
   */
}
