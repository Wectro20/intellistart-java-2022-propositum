package com.intellias.intellistart.interviewplanning.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity for Spring JPA.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonView(Views.Public.class)
  private String email;
  @Enumerated(EnumType.STRING)
  private UserRole role;

  public User(String email, UserRole role) {
    this.email = email;
    this.role = role;
  }

  /**
   * User Role enum.
   */
  public enum UserRole {
    INTERVIEWER,
    COORDINATOR,
    CANDIDATE
  }
}
