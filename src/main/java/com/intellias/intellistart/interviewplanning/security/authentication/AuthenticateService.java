package com.intellias.intellistart.interviewplanning.security.authentication;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Facebook;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.FbAccessToken;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Profile;
import com.intellias.intellistart.interviewplanning.security.config.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service for authenticate user using facebook access token.
 */
@Service
public class AuthenticateService {

  private final JwtTokenUtil jwtTokenUtil;
  private final Facebook facebook;
  private final UserDetailsService userDetailsService;

  /**
   * Basic constructor.
   */
  @Autowired
  public AuthenticateService(JwtTokenUtil jwtTokenUtil, Facebook facebook,
      UserDetailsService userDetailsService) {
    this.jwtTokenUtil = jwtTokenUtil;
    this.facebook = facebook;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Authenticate user using facebook service.
   *
   * @param fbAccessToken user`s access token.
   * @return jwt access token.
   */
  public JwtResponse authenticateUser(FbAccessToken fbAccessToken) {
    Profile profile = facebook.getProfile(fbAccessToken.getToken());

    if (profile.getEmail() == null) {
      throw new InvalidAccessTokenException("User has not register email in Facebook");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(profile.getEmail());

    String jwtResponseToken = jwtTokenUtil.generateToken(userDetails);
    Claims userClaims = jwtTokenUtil.getAllClaimsFromToken(jwtResponseToken);

    return new JwtResponse(jwtResponseToken, userClaims);
  }
}
