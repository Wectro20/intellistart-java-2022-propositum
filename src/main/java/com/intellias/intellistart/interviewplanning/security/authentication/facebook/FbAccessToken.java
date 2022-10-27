package com.intellias.intellistart.interviewplanning.security.authentication.facebook;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User FaceBook access token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FbAccessToken {
  private String token;
}
