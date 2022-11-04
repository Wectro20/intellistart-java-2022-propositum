package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import com.intellias.intellistart.interviewplanning.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for interactions with users.
 */

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@PreAuthorize("hasAuthority('COORDINATOR')")
public class UserController {

  private UserService userService;

  @PostMapping("/interviewers")
  public User grantUserInterviewerRole(@RequestBody @JsonView({Views.Public.class}) User user) {
    return userService.saveUserWithRole(user, UserRole.INTERVIEWER);
  }

  @PostMapping("/coordinators")
  public User grantUserCoordinatorRole(@RequestBody @JsonView({Views.Public.class}) User user) {
    return userService.saveUserWithRole(user, UserRole.COORDINATOR);
  }

  @GetMapping("/interviewers")
  public List<User> getInterviewers() {
    return userService.getUsersByRole(UserRole.INTERVIEWER);
  }

  @GetMapping("/coordinators")
  public List<User> getCoordinators() {
    return userService.getUsersByRole(UserRole.COORDINATOR);
  }

  @DeleteMapping("/interviewers/{userId}")
  public User deleteInterviewer(@PathVariable long userId) {
    return userService.deleteUserWithRole(userId, UserRole.INTERVIEWER);
  }

  @DeleteMapping("/coordinators/{userId}")
  public User deleteCoordinator(@PathVariable long userId) {
    return userService.deleteUserWithRole(userId, UserRole.COORDINATOR);
  }
}
