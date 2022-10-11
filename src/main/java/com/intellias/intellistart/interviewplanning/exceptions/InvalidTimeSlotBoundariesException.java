package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;

/**
 * Invalid Time Slot Boundaries Exception.
 */
@Getter
public class InvalidTimeSlotBoundariesException extends RuntimeException {

  public InvalidTimeSlotBoundariesException(String message) {
    super(message);
  }
}
