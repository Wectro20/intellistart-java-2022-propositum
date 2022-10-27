package com.intellias.intellistart.interviewplanning.security.config;

import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Used to retrieve user-related data.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public JwtUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Search for user in DB and if it presents return existing user or return new user with role
   * Candidate.
   *
   * @param userEmail field for which we will search for user.
   * @return simply store user information which is later encapsulated into Authentication object
   */
  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    Optional<User> optionalUser = userRepository.findByEmail(userEmail);
    User user = null;

    if (optionalUser.isEmpty()) {
      user = new User(userEmail, UserRole.CANDIDATE);
    } else {
      user = optionalUser.get();
    }

    return new SimpleUserPrincipal(user);
  }
}
