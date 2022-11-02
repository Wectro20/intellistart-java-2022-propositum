package com.intellias.intellistart.interviewplanning.security.authentication.facebook;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Component for sanding request to facebook and retrieve user info.
 */
@Component
public class Facebook {

  private static final String GRAPH_API_BASE_URL = "https://graph.facebook.com/v2.12";
  private static final String GRAPH_API_BASE_REQUEST = "/me?fields=email,name,id&access_token=";

  private final RestTemplate restTemplate;

  /**
   * Basic constructor.
   */
  @Autowired
  public Facebook(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Send request to facebook providing token and retrieve user`s email and name.
   *
   * @param accessToken user`s fb access token
   * @return Profile, which contains userEmail and name;
   */
  public Profile getProfile(String accessToken) {
    try {
      return restTemplate.getForObject(
          GRAPH_API_BASE_URL + GRAPH_API_BASE_REQUEST + accessToken, Profile.class);
    } catch (Exception e) {
      throw new InvalidAccessTokenException(
          "Invalid OAuth access token - Cannot parse access token");
    }
  }
}
