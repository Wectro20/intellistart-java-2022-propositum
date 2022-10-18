package com.intellias.intellistart.interviewplanning.exceptions;

public class WeekNumberNotAcceptableException extends RuntimeException{

  public WeekNumberNotAcceptableException(Integer weekNumber) {
    super("Possible week number is: " + weekNumber);
  }
}
