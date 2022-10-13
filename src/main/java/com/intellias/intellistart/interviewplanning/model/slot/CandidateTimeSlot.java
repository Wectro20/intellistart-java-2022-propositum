package com.intellias.intellistart.interviewplanning.model.slot;

import com.intellias.intellistart.interviewplanning.model.User;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CandidateTimeSlot entity for Spring JPA.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateTimeSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "\"from\"")
  private LocalTime from;
  @Column(name = "\"to\"")
  private LocalTime to;
  private LocalDate date;
  @ManyToOne
  private User user;
}
