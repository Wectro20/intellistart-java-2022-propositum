package com.intellias.intellistart.interviewplanning.security;

import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * It is security util.
 */
public class SecurityUtil {

  /**
   * Get current principal.
   *
   * @return authentication details for current user which call endpoint
   */

  public static SimpleUserPrincipal getCurrentPrincipal() {
    return (SimpleUserPrincipal) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }
}
