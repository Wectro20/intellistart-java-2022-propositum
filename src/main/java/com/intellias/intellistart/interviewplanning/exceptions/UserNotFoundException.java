package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Invalid user passed exception.
 */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

}
