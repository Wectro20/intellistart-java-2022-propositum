package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Exception for already existed booking.
 */

public class BookingIsAlreadyExistsException extends RuntimeException {

  public BookingIsAlreadyExistsException(String subject) {
    super("booking is already exists for " + subject);
  }
}
