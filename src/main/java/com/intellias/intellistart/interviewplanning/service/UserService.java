package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.EMAIL_NOT_VALID;

import com.intellias.intellistart.interviewplanning.exceptions.UserAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.util.List;
import java.util.regex.Pattern;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * It is service for interactions with users.
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {

  private static final String EMAIL_REGEX = "^(.+)@(\\S+)$";

  private UserRepository userRepository;
  private BookingLimitRepository bookingLimitRepository;
  private BookingRepository bookingRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  /**
   * Save user with role.
   *
   * @param user which is saving
   * @param role which role is granted to user
   * @return saved user with role
   */
  public User saveUserWithRole(User user, UserRole role) {
    if (isEmailNotValid(user.getEmail())) {
      throw new ValidationException("email is not valid", EMAIL_NOT_VALID);
    }

    userRepository.findByEmail(user.getEmail())
        .ifPresent(foundUser -> {
          throw new UserAlreadyExistsException(foundUser.getId());
        });

    user.setRole(role);

    return userRepository.save(user);
  }

  /**
   * Get all users by role.
   *
   * @param role which is used to find users
   * @return all users by role
   */
  public List<User> getUsersByRole(UserRole role) {
    return userRepository.findAllByRole(role);
  }

  /**
   * Delete users by role.
   *
   * @param id   to find user
   * @param role to filter user by role
   * @return deleted user
   */
  @Transactional
  public User deleteUserWithRole(long id, UserRole role) {
    User userToRemove = userRepository.findById(id)
        .filter(user -> user.getRole().equals(role))
        .orElseThrow(() -> new UserNotFoundException(
            String.format("user with id %s and role %s not found", id, role)));

    if (role.equals(UserRole.INTERVIEWER)) {

      log.info("Deleting all bookings and time slots related to the user with id {}", id);

      bookingLimitRepository.deleteByUser(userToRemove);
      bookingRepository.deleteAllByInterviewerTimeSlot_User(userToRemove);
      interviewerTimeSlotRepository.deleteAllByUser(userToRemove);

      log.info("All bookings and time slots related to the user with id {} was deleted", id);

    }
    log.info("Deleting user {}", userToRemove);

    userRepository.delete(userToRemove);

    return userToRemove;
  }

  private boolean isEmailNotValid(String email) {
    if (email == null) {
      return true;
    }

    return !Pattern.compile(EMAIL_REGEX)
        .matcher(email)
        .matches();
  }
}
