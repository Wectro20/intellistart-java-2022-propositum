package com.intellias.intellistart.interviewplanning.security.authentication;

/**
 * Jwt access token, with whom user could use api.
 */
public class JwtResponse {

  private final String jwtAccessToken;

  public JwtResponse(String jwtAccessToken) {
    this.jwtAccessToken = jwtAccessToken;
  }

  public String getJwtAccessToken() {
    return jwtAccessToken;
  }
}
