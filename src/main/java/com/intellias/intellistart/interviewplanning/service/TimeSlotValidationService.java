package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.INVALID_BOUNDARIES;
import static java.time.temporal.ChronoUnit.MINUTES;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

/**
 * Service for time slot validation.
 */
@Component
@AllArgsConstructor
public class TimeSlotValidationService {

  @Value("${interview.duration_minutes}")
  private Integer interviewDuration;

  @Value("${working_hours.from}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursFrom;

  @Value("${working_hours.to}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursTo;

  /**
   * Validate booking time slot boundaries.
   *
   * @param from left boundary
   * @param to right boundary
   */
  public void validateBookingTimeSlotBoundaries(LocalTime from, LocalTime to) {
    validateTimeSlotBoundaries(from, to);
    if (MINUTES.between(from, to) != interviewDuration) {
      String message = "range should be equal interview duration " + interviewDuration + " min.";

      throw new InvalidTimeSlotBoundariesException(message);
    }
  }

  /**
   * Validate time slot boundaries.
   *
   * @param from left boundary
   * @param to right boundary
   */
  public void validateTimeSlotBoundaries(LocalTime from, LocalTime to) {
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

  /**
   * Validate time slot boundaries.
   *
   * @param bookingDto          Dto with time for validation
   * @param interviewerTimeSlot time slot in which time should cover the time of bookingDto
   * @param candidateTimeSlot   time slot in which time should cover the time of bookingDto
   */
  public boolean validateBookingTimeBoundariesInTimeSlots(BookingDto bookingDto,
      InterviewerTimeSlot interviewerTimeSlot,
      CandidateTimeSlot candidateTimeSlot) {

    if (isTimeNotInInterviewerSlotRange(interviewerTimeSlot,
        bookingDto.getStartTime())
        || isTimeNotInInterviewerSlotRange(interviewerTimeSlot,
        bookingDto.getEndTime())) {

      throw new ValidationException("from/to does not fit into interviewer time slot",
          INVALID_BOUNDARIES);
    }

    if (isTimeNotInCandidateSlotRange(candidateTimeSlot,
        bookingDto.getStartTime())
        || isTimeNotInCandidateSlotRange(candidateTimeSlot,
        bookingDto.getEndTime())) {

      throw new ValidationException("from/to does not fit into bounded candidate time slot",
          INVALID_BOUNDARIES);
    }

    return true;
  }

  private boolean isTimeNotInCandidateSlotRange(CandidateTimeSlot timeSlot, LocalTime target) {
    return target.isBefore(timeSlot.getFrom()) || target.isAfter(timeSlot.getTo());
  }

  private boolean isTimeNotInInterviewerSlotRange(InterviewerTimeSlot timeSlot, LocalTime target) {
    return target.isBefore(timeSlot.getFrom()) || target.isAfter(timeSlot.getTo());
  }
}
