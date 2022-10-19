package com.intellias.intellistart.interviewplanning.exceptions;

import java.util.List;

/**
 * Exception for not valid week number.
 */

public class WeekNumberNotAcceptableException extends RuntimeException {

  public WeekNumberNotAcceptableException(List<Integer> weeksNumbers) {
    super("Possible weeks numbers is: " + weeksNumbers);
  }
}
