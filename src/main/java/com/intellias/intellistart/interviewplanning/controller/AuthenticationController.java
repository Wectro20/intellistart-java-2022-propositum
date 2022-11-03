package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import com.intellias.intellistart.interviewplanning.security.authentication.AuthenticateService;
import com.intellias.intellistart.interviewplanning.security.authentication.JwtResponse;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.FbAccessToken;
import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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


  /**
   * Return user email and role only for coordinator and interviewer. If user has not granted role
   * CustomAccessDeniedHandler called and send user error message with status forbidden.
   *
   * @return User info.
   */
  @JsonView({Views.Public.class})
  @GetMapping("/me")
  @PreAuthorize("hasAnyAuthority('COORDINATOR', 'INTERVIEWER')")
  public ResponseEntity<User> getUserInfo() {
    SimpleUserPrincipal principal = (SimpleUserPrincipal) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
    return ResponseEntity.ok(new User(principal.getEmail(), principal.getRole()));
  }
}
