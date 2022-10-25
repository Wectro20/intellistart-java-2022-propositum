package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import java.time.LocalTime;
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

}
