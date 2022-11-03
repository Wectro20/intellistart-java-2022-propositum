package com.intellias.intellistart.interviewplanning.security.authentication;

import io.jsonwebtoken.Claims;

/**
 * Jwt access token, with whom user could use api.
 */
public class JwtResponse {

  private final String jwtAccessToken;
  private final Claims claims;

  public JwtResponse(String jwtAccessToken, Claims claims) {
    this.jwtAccessToken = jwtAccessToken;
    this.claims = claims;
  }

  public String getJwtAccessToken() {
    return jwtAccessToken;
  }

  public Claims getClaims() {
    return claims;
  }
}
