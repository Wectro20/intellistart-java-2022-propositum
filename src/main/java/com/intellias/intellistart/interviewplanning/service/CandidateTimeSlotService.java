package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.model.TimeSLotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
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
   * @param candidateEmail email of candidate
   * @param date        available date for time slot
   * @param start start time of time slot
   * @param end end time of time slot
   *
   * @return candidate time slot
   */
  public CandidateTimeSlot createSlot(String candidateEmail, LocalDate date,
      LocalTime start, LocalTime end) {

    if (date != null && start != null && end != null) {
      validateTimeSlot(date, start, end);
    }

    User candidate = validateCandidate(candidateEmail, date, start, end);

    return candidateTimeSlotRepository.save(CandidateTimeSlot.builder()
        .date(date)
        .start(start)
        .end(end)
        .sLotStatus(TimeSLotStatus.NEW)
        .user(candidate)
        .build());
  }

  /**
   * Validate time slot for Candidate.
   *
   * @param date           available date for time slot
   * @param start          start time of time slot
   * @param end            end time of time slot
   */
  private void validateTimeSlot(LocalDate date, LocalTime start, LocalTime end) {
    if (date.isBefore(LocalDate.now()) || date.getDayOfWeek().equals(DayOfWeek.SATURDAY)
        || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
      throw new InvalidDayOfWeekException(date.toString());
    }

    if (start.isAfter(LocalTime.of(22, 0)) || start.isBefore(LocalTime.of(8, 0))
        || end.isAfter(LocalTime.of(22, 0)) || end.isBefore(LocalTime.of(8, 0))
        || start.getMinute() % 30 != 0 || end.getMinute() % 30 != 0) {
      throw new InvalidTimeSlotBoundariesException(start + "; " + end);
    }

    if ((double) (end.getHour() * 60 + end.getMinute()) - (start.getHour() * 60 + start.getMinute())
        < 1.5) {
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
  private User validateCandidate(String candidateEmail, LocalDate date, LocalTime start,
      LocalTime end) {
    Optional<User> optionalCandidate = userRepository.findByEmail(candidateEmail);
    User candidate = optionalCandidate.orElseThrow(
        () -> new UserNotFoundException(candidateEmail));

    if (candidateTimeSlotRepository.findByUserId(candidate.getId()).stream()
        .anyMatch(candidateTimeSlot -> candidateTimeSlot.getDate().equals(date) &&
            candidateTimeSlot.getStart().equals(start) && candidateTimeSlot.getEnd().equals(end))) {
      throw new SlotIsOverlappingException(start + "; " + end);
    }

    return candidate;
  }
}
