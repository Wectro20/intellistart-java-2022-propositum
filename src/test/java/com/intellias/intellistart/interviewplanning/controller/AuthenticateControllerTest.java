package com.intellias.intellistart.interviewplanning.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Facebook;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Profile;
import com.intellias.intellistart.interviewplanning.security.config.JwtRequestFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class AuthenticateControllerTest {

  @Autowired
  private WebApplicationContext webappContext;
  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  private MockMvc mockMvc;

  @MockBean
  private Facebook facebook;
  private static final String BASIC_LOGIN_URL = "/login";

  private static final String RIGHT_REQUEST = "{\n"
      + "    \"token\": \"kAz8WZBa9ZAPTXBq5P0L20WvpO3TgctBE7GZAQGDTD3nBgZCrL0gsXWEt1nG1poivj5RFwAd3Cnh5cnLZCgga7hU0nV8GLhGGt5EvOfx4yP9iH8HZB4iZAV3ec7ZBy4Y085UATY\"\n"
      + "}";
  private static final String INVALID_REQUEST = "{\n"
      + "    \"token\": \"VRTQwMOQSGrEBAGJSKAGUQc5ZB6o4dkAz8WZBa9ZAPTXBq5P0L20WvpO3TgctBE7GZAQGDTD3nBgZCrL0gsXWEt1nG1poivj5RFwAd3Cnh5cnLZCgga7hU0nV8GLhGGt5EvOfx4yP9iH8HZB4iZAV3ec7ZBy4Y085UATY\"\n"
      + "}";

  private static final Profile profileWithEmailAndName = new Profile("Denis", "denis@gmail.com");
  private static final Profile profileWithOutEmail = new Profile("Denis", null);

  @BeforeEach
  public void setUp() {
    // to allow all calls to /login without any authentication
    mockMvc = webAppContextSetup(this.webappContext)
        .addFilter(this.jwtRequestFilter, "/candidates**")
        .build();
  }

  @Test
  void sendFbAccessTokenAndRetrieveJwtTokenWithClaims() throws Exception {
    Mockito.when(facebook.getProfile(ArgumentMatchers.anyString()))
        .thenReturn(profileWithEmailAndName);

    this.mockMvc.perform(MockMvcRequestBuilders.post(BASIC_LOGIN_URL)
            .content(RIGHT_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void sendInvalidFbAccessTokenAndCatchInvalidAccessTokenException() throws Exception {
    Mockito.when(facebook.getProfile(ArgumentMatchers.anyString()))
        .thenThrow(InvalidAccessTokenException.class);

    this.mockMvc.perform(MockMvcRequestBuilders.post(BASIC_LOGIN_URL)
            .content(INVALID_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  void sendFbAccessTokenWithNotRegisteredEmailAndCatchInvalidAccessTokenException()
      throws Exception {

    Mockito.when(facebook.getProfile(ArgumentMatchers.anyString()))
        .thenReturn(profileWithOutEmail);

    this.mockMvc.perform(MockMvcRequestBuilders.post(BASIC_LOGIN_URL)
            .content(RIGHT_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

}
