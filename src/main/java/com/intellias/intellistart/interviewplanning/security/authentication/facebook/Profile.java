package com.intellias.intellistart.interviewplanning.security.authentication.facebook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User profile. Used for retrieving user info.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

  private String name;
  private String email;
}
