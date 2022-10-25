package com.intellias.intellistart.interviewplanning.exceptions;

import lombok.Getter;

/**
 * Exception for not already existed booking.
 */
@Getter
public class BookingValidationException extends RuntimeException {

  private String errorMessage;

  public BookingValidationException(String message, String errorMessage) {
    super(message);
    this.errorMessage = errorMessage;
  }
}
