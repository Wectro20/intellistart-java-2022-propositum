package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.security.authentication.AuthenticateService;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Facebook;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.FbAccessToken;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Profile;
import com.intellias.intellistart.interviewplanning.security.config.JwtTokenUtil;
import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import io.jsonwebtoken.lang.Assert;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class AuthenticateServiceTest {

  @MockBean
  UserDetailsService userDetailsService;
  @MockBean
  Facebook facebook;
  JwtTokenUtil jwtTokenUtil;
  AuthenticateService authenticateService;

  static String secret = "intellistartjava2022propositumintellistartjava2022propositumintellistartjava2022propositum";

  private static final FbAccessToken INTERVIEWER_FB_ACCESS_TOKEN_WITH_EMAIL =
      new FbAccessToken(
          "NwJG5PPbLZC3GcQrL1fZAvWgXzlUzZAaVF3cIEuACZAlyYPwlJuay81MZAaEKehyVS6ACZBqZABjwQNe93x8qYIER5daSjD3iBFz3Mv8KUqq3DhKGBBHRjBS7oLQyDzIJzuVYhvROD3HW1nklkxMLmNvZAmVNCIzfvPX658RCnRZAxRRjKfPkzYpZAbe31wZDZD");
  private static final FbAccessToken CANDIDATE_FB_ACCESS_TOKEN_WITH_EMAIL =
      new FbAccessToken(
          "qWheNYqeqvmUqhQTJ9BLaxW8PjLWbA0XIYIBGCgTg5UMcFqbZAYjprkr7Nh6wtBvLKj23xCFTvIYZA8BxBHAEnqIFv9XqeIAg2Fdl3aZBi4NtAbMLDVZASEdQVTPgdgzLFIzPKNdWNanplKleIDf1ZCIHx5tfYhLJGZBVcLR7QKDnEf2tPRhrSaKLzWqCQxWRLykj");
  private static final FbAccessToken FB_ACCESS_TOKEN_WITHOUT_EMAIL =
      new FbAccessToken(
          "LLyQOmbprxKWNwW3VETlLD0lNi20jLiy5MErGx4EzOifMe56dZA9WeKmeTToRQEwKcL6bxgj6tCXlHRtagEk7GAn4iXBpTlGGcE9rbCelUbSAYitBo7HQ92ni5DTDkux2qtb1KJdeZCLvZCLnFYNHZAfhEUSTjZBAoGubpK9p9JhflA1kLsZBg551aWUYsQZDZD");

  private final User interviewer = new User("rcpytlaiid_1667565185@tfbnw.net",
      UserRole.INTERVIEWER);
  private final User candidate = new User("cspdqbfhwp_1667564034@tfbnw.net", UserRole.CANDIDATE);

  private final Profile interviewerProfile = new Profile("Dan", interviewer.getEmail());
  private final Profile candidateProfile = new Profile("Dan", candidate.getEmail());
  private final Profile profileWithoutEmail = new Profile("Dan", null);
  private final UserDetails interviewerUserDetails = new SimpleUserPrincipal(interviewer);
  private final UserDetails candidateUserDetails = new SimpleUserPrincipal(candidate);


  @BeforeEach
  public void setUp() {
    jwtTokenUtil = new JwtTokenUtil();
    ReflectionTestUtils.setField(jwtTokenUtil, "secret", secret);
    authenticateService = new AuthenticateService(jwtTokenUtil, facebook, userDetailsService);
  }

  @Test
  void sendInterviewerFbAccessTokenAndRetrieveJwtResponse() {
    Mockito.when(facebook.getProfile(INTERVIEWER_FB_ACCESS_TOKEN_WITH_EMAIL.getToken()))
        .thenReturn(interviewerProfile);
    Mockito.when(userDetailsService.loadUserByUsername(interviewer.getEmail()))
        .thenReturn(interviewerUserDetails);

    var jwtResponse = authenticateService.authenticateUser(INTERVIEWER_FB_ACCESS_TOKEN_WITH_EMAIL);
    Assert.notNull(jwtResponse);
    Assert.notNull(jwtResponse.getJwtAccessToken());
    Assert.notNull(jwtResponse.getClaims());

    Assertions.assertEquals(jwtResponse.getClaims().getSubject(), interviewer.getEmail());

    ArrayList<LinkedHashMap<String, String>> role = (ArrayList<LinkedHashMap<String, String>>) jwtResponse.getClaims()
        .get("role");
    LinkedHashMap<String, String> authorities = role.get(0);
    Assertions.assertEquals(interviewer.getRole().toString(), authorities.get("authority"));
  }

  @Test
  void sendCandidateFbAccessTokenAndRetrieveJwtResponse() {
    Mockito.when(facebook.getProfile(CANDIDATE_FB_ACCESS_TOKEN_WITH_EMAIL.getToken()))
        .thenReturn(candidateProfile);
    Mockito.when(userDetailsService.loadUserByUsername(candidate.getEmail()))
        .thenReturn(candidateUserDetails);

    var jwtResponse = authenticateService.authenticateUser(CANDIDATE_FB_ACCESS_TOKEN_WITH_EMAIL);

    Assertions.assertEquals(jwtResponse.getClaims().getSubject(), candidate.getEmail());

    ArrayList<LinkedHashMap<String, String>> role = (ArrayList<LinkedHashMap<String, String>>) jwtResponse.getClaims()
        .get("role");
    LinkedHashMap<String, String> authorities = role.get(0);
    Assertions.assertEquals(candidate.getRole().toString(), authorities.get("authority"));

  }

  @Test
  void sendFbAccessTokenWithoutEmailAndCatchInvalidOauthAccessToken() {
    Mockito.when(facebook.getProfile(FB_ACCESS_TOKEN_WITHOUT_EMAIL.getToken()))
        .thenReturn(profileWithoutEmail);

    InvalidAccessTokenException invalidAccessTokenException = Assertions.assertThrows(
        InvalidAccessTokenException.class, () ->
            authenticateService.authenticateUser(FB_ACCESS_TOKEN_WITHOUT_EMAIL));

    Assert.notNull(invalidAccessTokenException);
    Assertions.assertEquals("User has not register email in Facebook",
        invalidAccessTokenException.getMessage());
  }
}
