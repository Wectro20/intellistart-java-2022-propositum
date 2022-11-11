package com.intellias.intellistart.interviewplanning.exceptions;

import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for handling exception in application.
 */
@ControllerAdvice
public class ApplicationExceptionHandler {
  private static final String INTERVIEWER_NOT_FOUND = "interviewer_not_found";
  private static final String USER_NOT_FOUND = "user_not_found";
  private static final String SLOT_IS_OVERLAPPING = "slot_is_overlapping";
  private static final String BOOKING_IS_OVERLAPPING = "booking_is_overlapping";
  public static final String INVALID_BOUNDARIES = "invalid_boundaries";
  public static final String SUBJECT_DESCRIPTION_NOT_VALID = "subject_or_description_not_valid";
  private static final String INVALID_DAY_OF_WEEK = "invalid_day_of_week";
  private static final String SLOT_NOT_FOUND = "slot_not_found";
  private static final String WEEK_NUMBER_NOT_ACCEPTABLE = "week_number_not_acceptable";
  private static final String USER_ALREADY_EXIST = "user_already_exist";
  public static final String EMAIL_NOT_VALID = "email_not_valid";
  private static final String INVALID_LIMIT = "invalid_limit";
  private static final String INVALID_ACCESS_TOKEN = "invalid_access_token";

  private static final String BOOKING_NOT_FOUND = "booking_not_found";

  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(WeekNumberNotAcceptableException.class)
  public ErrorResponse handleWeekNumberNotAcceptableException(WeekNumberNotAcceptableException e) {
    return new ErrorResponse(WEEK_NUMBER_NOT_ACCEPTABLE, e.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException e) {
    return new ErrorResponse(USER_ALREADY_EXIST, e.getMessage());
  }

  /**
   * Exception handler for InterviewerNotFoundException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(InterviewerNotFoundException.class)
  public ErrorResponse handleInterviewerNotFoundException() {
    return new ErrorResponse(INTERVIEWER_NOT_FOUND, "interviewer was not found");
  }

  /**
   * Exception handler for BookingNotFoundException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(BookingNotFoundException.class)
  public ErrorResponse handleBookingNotFoundException() {
    return new ErrorResponse(BOOKING_NOT_FOUND, "booking was not found");
  }


  /**
   * Exception handler for UserNotFoundException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
    return new ErrorResponse(USER_NOT_FOUND, e.getMessage());
  }

  /**
   * Exception handler for SlotNotFoundException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(SlotNotFoundException.class)
  public ErrorResponse handleSlotNotFoundException(SlotNotFoundException e) {
    return new ErrorResponse(SLOT_NOT_FOUND, "slot was not found");
  }

  /**
   * Exception handler for BookingIsAlreadyExistsException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(BookingIsAlreadyExistsException.class)
  public ErrorResponse handleBookingIsAlreadyExistsException(BookingIsAlreadyExistsException e) {
    return new ErrorResponse(BOOKING_IS_OVERLAPPING, e.getMessage());
  }

  /**
   * Exception handler for InvalidLimitException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidLimitException.class)
  public ErrorResponse handleInvalidLimitException(InvalidLimitException e) {
    return new ErrorResponse(INVALID_LIMIT, e.getMessage());
  }

  /**
   * Exception handler for SlotIsOverlappingException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(SlotIsOverlappingException.class)
  public ErrorResponse handleSlotIsOverlappingException(SlotIsOverlappingException e) {
    return new ErrorResponse(SLOT_IS_OVERLAPPING, e.getMessage());
  }

  /**
   * Exception handler for InvalidTimeSlotBoundariesException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidTimeSlotBoundariesException.class)
  public ErrorResponse handleInvalidTimeSlotBoundariesException(
      InvalidTimeSlotBoundariesException e) {
    return new ErrorResponse(INVALID_BOUNDARIES, e.getMessage());
  }

  /**
   * Exception handler for InvalidTimeSlotBoundariesException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ValidationException.class)
  public ErrorResponse handleBookingValidationException(ValidationException e) {
    return new ErrorResponse(e.getErrorMessage(), e.getMessage());
  }

  /**
   * Exception handler for InvalidDayOfWeekException.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidDayOfWeekException.class)
  public ErrorResponse handleInvalidDayOfWeekException() {
    String errorMessage = "Possible values: " + Arrays.stream(InterviewDayOfWeek.values())
        .map(InterviewDayOfWeek::getValue)
        .collect(Collectors.toList());
    return new ErrorResponse(INVALID_DAY_OF_WEEK, errorMessage);
  }

  /**
   * Exception handler for InvalidAccessToken.
   */
  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(InvalidAccessTokenException.class)
  public ErrorResponse handleInvalidAccessTokenException(
      InvalidAccessTokenException invalidAccessTokenException) {
    return new ErrorResponse(INVALID_ACCESS_TOKEN, invalidAccessTokenException.getMessage());
  }

  /**
   * Response error, if occurs.
   */
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class ErrorResponse {

    private String errorCode;
    private String errorMessage;
  }
}