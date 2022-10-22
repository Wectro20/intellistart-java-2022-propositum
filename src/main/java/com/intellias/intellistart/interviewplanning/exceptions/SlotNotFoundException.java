package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception for interviewer slot not found.
 */
public class SlotNotFoundException extends RuntimeException {
  public SlotNotFoundException(String message) {
    super(message);
  }
}
