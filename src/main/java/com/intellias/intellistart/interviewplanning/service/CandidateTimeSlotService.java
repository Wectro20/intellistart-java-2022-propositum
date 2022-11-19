package com.intellias.intellistart.interviewplanning.service;


import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
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

  public CandidateTimeSlot createSlot(String candidateEmail, LocalDate date,
      LocalTime from, LocalTime to) {
    validateTimeSlot(date, from, to);
    validateSlotIsNotOverlapping(candidateEmail, date, from, to);
    return candidateTimeSlotRepository.save(CandidateTimeSlot.builder()
        .date(date)
        .from(from)
        .to(to)
        .slotStatus(TimeSlotStatus.NEW)
        .email(candidateEmail)
        .build());
  }

  /**
   * Get time slot for Candidate.
   *
   * @param candidateEmail email of candidate
   * @return candidate time slot
   */
  public List<CandidateTimeSlot> getTimeSlots(String candidateEmail) {
    return candidateTimeSlotRepository.findByEmail(candidateEmail);
  }

  /**
   * Get time slot for Candidate.
   *
   * @param id id of slot that has to be updated
   * @return updated candidate time slot
   */
  public CandidateTimeSlot updateSlot(long id, CandidateTimeSlot newSlotValue) {
    validateTimeSlot(newSlotValue.getDate(), newSlotValue.getFrom(), newSlotValue.getTo());
    CandidateTimeSlot candidateTimeSlot = candidateTimeSlotRepository
        .findById(id)
        .orElseThrow(SlotNotFoundException::new);
    validateSlotIsNotOverlapping(candidateTimeSlot.getEmail(), newSlotValue.getDate(),
            newSlotValue.getFrom(), newSlotValue.getTo());
    candidateTimeSlot.setDate(newSlotValue.getDate());
    candidateTimeSlot.setFrom(newSlotValue.getFrom());
    candidateTimeSlot.setTo(newSlotValue.getTo());
    return candidateTimeSlotRepository.save(candidateTimeSlot);
  }

  /**
   * Get time slot id for Candidate.
   *
   * @param id id of slot
   * @return found time slot by id
   */
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
    if (date == null || start == null || end == null) {
      throw new NoSuchElementException();
    }

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
   */
  private void validateSlotIsNotOverlapping(String candidateEmail,
                                            LocalDate date, LocalTime start, LocalTime end) {
    List<CandidateTimeSlot> slotList = candidateTimeSlotRepository
            .findByDateAndEmail(date, candidateEmail);

    Optional<CandidateTimeSlot> overlappingSlot = slotList
        .stream()
        .filter(slot -> (!(start.isAfter(slot.getTo()) || start.equals(slot.getTo())
                || end.isBefore(slot.getFrom()) || end.equals(slot.getFrom()))))
        .findAny();
    if (overlappingSlot.isPresent()) {
      throw new SlotIsOverlappingException(overlappingSlot.get().getId());
    }
  }
}

