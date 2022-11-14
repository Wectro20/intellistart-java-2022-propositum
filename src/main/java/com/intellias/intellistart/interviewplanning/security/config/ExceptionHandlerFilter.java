package com.intellias.intellistart.interviewplanning.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Entry point of all requests. Invokes before JWTRequestFilter and handles UserNotFoundException
 * from JWTRequestFilter.
 */
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  /**
   * Checks if the request has a valid JWT token. If it JwtRequestFilter throw UserNotFoundException
   * , it handles it send User error response and set status 400 bad request.
   *
   * @param request     every request.
   * @param response    every response
   * @param filterChain chains filters.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (UserNotFoundException e) {
      setErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), response);
    }
  }

  /**
   * Set status and error message to response and proceed it.
   *
   * @param status   response status.
   * @param message  error message.
   * @param response HttpResponse
   */
  private void setErrorResponse(HttpStatus status, String message, HttpServletResponse response)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status.value());

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode("invalid_jwt_token");
    errorResponse.setErrorMessage(message);

    ObjectMapper objectMapper = new ObjectMapper();
    String responseError = objectMapper.writeValueAsString(errorResponse);

    response.getOutputStream().print(responseError);
  }
}
