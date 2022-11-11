package com.intellias.intellistart.interviewplanning.security.config;

import com.intellias.intellistart.interviewplanning.model.User;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Entry point of JWT authentication process. Invokes every request.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final JwtTokenUtil jwtTokenUtil;

  @Autowired
  public JwtRequestFilter(JwtTokenUtil jwtTokenUtil) {
    this.jwtTokenUtil = jwtTokenUtil;
  }

  /**
   * Checks if the request has a valid JWT token. If it has a valid JWT Token then it sets the
   * Authentication in the context, to specify that the current user is authenticated.
   *
   * @param request     every request.
   * @param response    every response
   * @param filterChain chains filters.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    final String requestTokenHeader = request.getHeader("Authorization");

    String jwtToken;

    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {

      jwtToken = requestTokenHeader.substring(7);

      User user = jwtTokenUtil.parseTokenToUser(jwtToken);

      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = new SimpleUserPrincipal(user);
        if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails))) {
          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          usernamePasswordAuthenticationToken
              .setDetails(
                  new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
