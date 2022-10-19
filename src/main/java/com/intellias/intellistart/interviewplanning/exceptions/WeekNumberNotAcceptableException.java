package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception for not valid week number.
 */

public class WeekNumberNotAcceptableException extends RuntimeException {

  public WeekNumberNotAcceptableException(Integer weekNumber) {
    super("Possible week number is: " + weekNumber);
  }
}
