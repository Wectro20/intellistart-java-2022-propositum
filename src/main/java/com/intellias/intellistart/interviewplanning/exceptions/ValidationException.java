package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;

/**
 * Exception for not already existed booking.
 */
@Getter
public class ValidationException extends RuntimeException {

  private String errorMessage;

  public ValidationException(String message, String errorMessage) {
    super(message);
    this.errorMessage = errorMessage;
  }
}
