package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.security.authentication.AuthenticateService;
import com.intellias.intellistart.interviewplanning.security.authentication.JwtResponse;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.FbAccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for authentication.
 */
@RestController
@RequestMapping(produces = "application/json")
public class AuthenticationController {

  private final AuthenticateService authenticateService;

  @Autowired
  public AuthenticationController(AuthenticateService authenticateService) {
    this.authenticateService = authenticateService;
  }

  /**
   * Authenticate user via facebook and return token.
   *
   * @param fbAccessToken request body of user`s access token.
   * @return jwt access token, with whom he could use api.
   */
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> authenticateUserAndReturnJwtToken(
      @RequestBody FbAccessToken fbAccessToken) {
    JwtResponse jwtResponse = authenticateService.authenticateUser(fbAccessToken);
    return ResponseEntity.ok(jwtResponse);
  }
}
