package com.intellias.intellistart.interviewplanning.exceptions;

import java.time.DayOfWeek;
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
 * Controller for handling exception in application
 * */
@ControllerAdvice
public class ApplicationExceptionHandler {

  private static final String CANDIDATE_NOT_FOUND = "candidate_not_found";
  private static final String SLOT_IS_OVERLAPPING = "slot_is_overlapping";
  private static final String INVALID_BOUNDARIES = "invalid_boundaries";
  private static final String INVALID_DAY_OF_WEEK = "invalid_day_of_week";

  @ResponseBody
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(UserNotFoundException.class)
  public ErrorResponse handleInterviewerNotFoundException(UserNotFoundException e) {
    return new ErrorResponse(CANDIDATE_NOT_FOUND, e.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(SlotIsOverlappingException.class)
  public ErrorResponse handleSlotIsOverlappingException(SlotIsOverlappingException e) {
    return new ErrorResponse(SLOT_IS_OVERLAPPING, e.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidTimeSlotBoundariesException.class)
  public ErrorResponse handleInvalidTimeSlotBoundariesException(
      InvalidTimeSlotBoundariesException e) {
    return new ErrorResponse(INVALID_BOUNDARIES, e.getMessage());
  }

  @ResponseBody
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidDayOfWeekException.class)
  public ErrorResponse handleInvalidDayOfWeekException() {
    String errorMessage = "Possible values: " + Arrays.stream(DayOfWeek.values())
        .collect(Collectors.toList());
    return new ErrorResponse(INVALID_DAY_OF_WEEK, errorMessage);
  }


  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class ErrorResponse {

    private String errorCode;
    private String errorMessage;
  }
}