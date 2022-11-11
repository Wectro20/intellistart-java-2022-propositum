package com.intellias.intellistart.interviewplanning.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class ExceptionHandlerFilterTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext webappContext;
  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private ExceptionHandlerFilter exceptionHandlerFilter;

  private MockMvc mockMvc;

  private static final String URL_GET_ME = "/me";

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webappContext)
        .addFilter(exceptionHandlerFilter)
        .addFilter(jwtRequestFilter)
        .build();
  }

  @Test
  void sendGetMeRequestWithInvalidAuthenticationTokenAndCatchException() throws Exception {

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode("invalid_jwt_token");
    errorResponse.setErrorMessage("Could not parse token");

    String response = objectMapper.writeValueAsString(errorResponse);

    this.mockMvc.perform(MockMvcRequestBuilders.get(URL_GET_ME)
            .header("Authorization", "Bearer qweqwrerqtretertfdgdfgfdgwgtrwttrwtetwrt")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string(response))
        .andDo(MockMvcResultHandlers.print());
  }
}