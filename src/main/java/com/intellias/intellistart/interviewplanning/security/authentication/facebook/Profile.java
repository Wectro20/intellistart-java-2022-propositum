package com.intellias.intellistart.interviewplanning.security.authentication.facebook;

import lombok.Data;

/**
 * User profile. Used for retrieving user info.
 */
@Data
public class Profile {

  private String name;
  private String email;
}
