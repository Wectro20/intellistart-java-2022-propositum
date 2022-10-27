package com.intellias.intellistart.interviewplanning.security.config;

import com.intellias.intellistart.interviewplanning.model.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Simply store user information which is later encapsulated into Authentication object.
 */
public class SimpleUserPrincipal implements UserDetails {

  private final User user;

  public SimpleUserPrincipal(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().toString());
    return Collections.singleton(grantedAuthority);
  }

  @Override
  public String getPassword() {
    return "";
  }

  @Override
  public String getUsername() {
    return getEmail();
  }

  public String getEmail() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

