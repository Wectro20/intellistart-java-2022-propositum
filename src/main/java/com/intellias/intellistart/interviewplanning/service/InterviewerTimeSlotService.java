package com.intellias.intellistart.interviewplanning.service;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for interviewer.
 */
@Service
@AllArgsConstructor
public class InterviewerTimeSlotService {

  @Value("${interview.duration_minutes}")
  private Integer interviewDuration;

  @Value("${working_hours.from}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursFrom;

  @Value("${working_hours.to}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursTo;

  private UserRepository userRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  /**
   * Create time slot for Interviewer.
   *
   * @param interviewerEmail    for which create slot
   * @param interviewerTimeSlot time slot which needs to create
   * @return saved interviewer time slot
   */
  public InterviewerTimeSlot createSlot(String interviewerEmail,
      InterviewerTimeSlot interviewerTimeSlot) {

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    DayOfWeek dayOfWeek = interviewerTimeSlot.getDayOfWeek();

    if (to != null) {
      validateTimeSlotBoundaries(from, to);
    }

    User user = userRepository.findByEmail(interviewerEmail)
        .orElseThrow(InterviewerNotFoundException::new);

    interviewerTimeSlotRepository.findAllByUserAndWeekNum(user, interviewerTimeSlot.getWeekNum())
        .stream()
        .filter(slot -> slot.getFrom().equals(from) && slot.getTo().equals(to))
        .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
        .findAny()
        .ifPresent(slot -> {
          throw new SlotIsOverlappingException(slot.getId());
        });

    interviewerTimeSlot.setUser(user);
    interviewerTimeSlot.setStatus(TimeSlotStatus.NEW);

    if (interviewerTimeSlot.getTo() == null) {
      interviewerTimeSlot.setTo(from.plusMinutes(interviewDuration));
    }

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  private void validateTimeSlotBoundaries(LocalTime from, LocalTime to) {
    if (isNotRoundedTime(from) || isNotRoundedTime(to)) {
      throw new InvalidTimeSlotBoundariesException("Minutes should be rounded to :00 or :30");
    } else if (from.isAfter(to)) {
      throw new InvalidTimeSlotBoundariesException("from is after to");

    } else if (MINUTES.between(from, to) < interviewDuration) {
      String message = "range cannot be shorter interview duration " + interviewDuration + " min.";

      throw new InvalidTimeSlotBoundariesException(message);
    } else if (from.isBefore(workingHoursFrom) || to.isAfter(workingHoursTo)) {
      String message =
          "Range violates working hours [" + workingHoursFrom + " - " + workingHoursTo + "]";
      throw new InvalidTimeSlotBoundariesException(message);
    }
  }

  private boolean isNotRoundedTime(LocalTime time) {
    return !(time.getMinute() == 30 || time.getMinute() == 0);
  }

}
