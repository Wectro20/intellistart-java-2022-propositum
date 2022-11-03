package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * Invalid user passed exception.
 */
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(Long userId) {
    super("User with id " + userId + " already exists");
  }

}
