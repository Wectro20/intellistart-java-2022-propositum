package com.intellias.intellistart.interviewplanning.model;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking entity for Spring JPA.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  private InterviewerTimeSlot interviewerTimeSlot;
  @OneToOne
  private CandidateTimeSlot candidateTimeSlot;
  private LocalTime startTime;
  private LocalTime endTime;
  private String subject;
}

