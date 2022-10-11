package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;

@Getter
public class InvalidTimeSlotBoundariesException extends RuntimeException {

  public InvalidTimeSlotBoundariesException(String message) {
    super(message);
  }
}
