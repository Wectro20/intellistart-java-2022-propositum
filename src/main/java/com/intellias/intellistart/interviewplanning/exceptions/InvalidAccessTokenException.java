package com.intellias.intellistart.interviewplanning.exceptions;

/**
 * throws when user pass invalid or expired token.
 */
public class InvalidAccessTokenException extends RuntimeException {

  public InvalidAccessTokenException(String message) {
    super(message);
  }
}
