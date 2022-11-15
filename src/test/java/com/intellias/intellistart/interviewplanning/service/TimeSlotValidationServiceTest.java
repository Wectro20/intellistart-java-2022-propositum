package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeSlotValidationServiceTest {

  private static final int INTERVIEW_DURATION = 90;

  private static final LocalTime WORKING_HOUR_FROM = LocalTime.of(8, 0);
  private static final LocalTime WORKING_HOUR_TO = LocalTime.of(22, 0);

  private static final LocalTime NOT_ROUNDED_TIME = LocalTime.of(12, 2);

  private static final LocalTime NORMAL_TIME = LocalTime.of(12, 0);

  private TimeSlotValidationService timeSlotValidationService;

  @BeforeEach
  public void setUp() {
    timeSlotValidationService = new TimeSlotValidationService(INTERVIEW_DURATION, WORKING_HOUR_FROM,
        WORKING_HOUR_TO);
  }

  @Test
  public void validateTimeSlotBoundaries_When_FromIsNotRounded_Should_ThrowException() {
    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(NOT_ROUNDED_TIME, NORMAL_TIME));

    assertEquals("Minutes should be rounded to :00 or :30", exception.getMessage());
  }

  @Test
  public void validateTimeSlotBoundaries_When_ToIsNotRounded_Should_ThrowException() {
    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(NORMAL_TIME, NOT_ROUNDED_TIME));

    assertEquals("Minutes should be rounded to :00 or :30", exception.getMessage());
  }

  @Test
  public void validateTimeSlotBoundaries_When_FromIsAfterTo_Should_ThrowException() {
    LocalTime from = LocalTime.of(12, 30);
    LocalTime to = LocalTime.of(9, 30);

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(from, to));

    assertEquals("from is after to", exception.getMessage());
  }

  @Test
  public void validateTimeSlotBoundaries_When_DiffBetweenFromAndToIsShorterDuration_Should_ThrowException() {
    LocalTime from = LocalTime.of(11, 0);
    LocalTime to = LocalTime.of(12, 0);

    String expectedErrorMessage = "range cannot be shorter interview duration "
        + INTERVIEW_DURATION
        + " min.";

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(from, to));

    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  public void validateTimeSlotBoundaries_When_ToViolatesWorkingHours_Should_ThrowException() {
    LocalTime to = WORKING_HOUR_TO.plusHours(1);

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(NORMAL_TIME, to));

    String expectedErrorMessage =
        "Range violates working hours [" + WORKING_HOUR_FROM + " - " + WORKING_HOUR_TO + "]";

    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  public void validateTimeSlotBoundaries_When_FromViolatesWorkingHours_Should_ThrowException() {
    LocalTime from = WORKING_HOUR_FROM.minusHours(1);

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotValidationService.validateTimeSlotBoundaries(from, NORMAL_TIME));

    String expectedErrorMessage =
        "Range violates working hours [" + WORKING_HOUR_FROM + " - " + WORKING_HOUR_TO + "]";

    assertEquals(expectedErrorMessage, exception.getMessage());
  }


  @Test
  public void validateBookingTimeBoundariesInTimeSlots_Should_Successfully_Validate() {
    assertEquals(true, timeSlotValidationService.validateBookingTimeBoundariesInTimeSlots(
        generateBookingDto(),
        generateInterviewerSlot(),
        generateCandidateSlot()));
  }

  @Test
  public void validateBookingTimeBoundariesInTimeSlots_Should_Throw_ValidationException() {
    InterviewerTimeSlot interviewerTimeSlot = generateInterviewerSlot();
    interviewerTimeSlot.setTo(LocalTime.of(10, 30));
    interviewerTimeSlot.setFrom(LocalTime.of(8, 30));


    assertThrows(ValidationException.class,
        () -> timeSlotValidationService.validateBookingTimeBoundariesInTimeSlots(
        generateBookingDto(),
        interviewerTimeSlot,
        generateCandidateSlot()));
  }

  private static InterviewerTimeSlot generateInterviewerSlot() {
    return InterviewerTimeSlot.builder().from(LocalTime.of(10, 0)).to(LocalTime.of(11, 30))
        .dayOfWeek(InterviewDayOfWeek.MONDAY).weekNum(15).bookings(Collections.emptyList()).build();
  }

  private static CandidateTimeSlot generateCandidateSlot() {
    return CandidateTimeSlot.builder().date(LocalDate.of(2022, 10, 25)).from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30)).bookings(Collections.emptyList()).build();
  }

  private static BookingDto generateBookingDto() {
    return BookingDto.builder().startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(11, 30))
        .candidateTimeSlotId(1L).interviewerTimeSlotId(1L).subject("Interview")
        .description("Interview for candidate").build();
  }
}
