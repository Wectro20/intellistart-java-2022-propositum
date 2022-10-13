package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception for invalid time slot boundaries.
 */
public class InvalidTimeSlotBoundariesException extends RuntimeException {

  public InvalidTimeSlotBoundariesException(String message) {
    super(message);
  }
}
