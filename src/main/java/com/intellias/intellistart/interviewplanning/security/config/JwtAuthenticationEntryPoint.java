package com.intellias.intellistart.interviewplanning.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Rejects every unauthenticated request with an error code 401 sent back to the client.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * This is invoked when user tries to access a secured REST resource without supplying any
   * credentials.
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode("not_authorized");
    errorResponse.setErrorMessage("have not provide any credentials");

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ObjectMapper objectMapper = new ObjectMapper();
    response.getOutputStream().print(objectMapper.writeValueAsString(errorResponse));
  }
}
