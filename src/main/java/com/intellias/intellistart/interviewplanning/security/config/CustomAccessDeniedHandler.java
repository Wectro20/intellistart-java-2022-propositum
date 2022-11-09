package com.intellias.intellistart.interviewplanning.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Rejects every authenticated request without authorities and send an error code 403 sent back to
 * the client.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * This is invoked when user tries to access a secured REST resource without authorities.
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode("not_authorized");
    errorResponse.setErrorMessage("You are not authorized to use this functionality");

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    ObjectMapper objectMapper = new ObjectMapper();

    response.getOutputStream().print(objectMapper.writeValueAsString(errorResponse));
  }
}
