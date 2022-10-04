package com.intellias.intellistart.interviewplanning.model.slot;

import com.intellias.intellistart.interviewplanning.model.user.Interviewer;
import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InterviewerTimeSlot entity for Spring JPA.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewerTimeSlot {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private LocalTime start;
  private LocalTime end;
  private DayOfWeek day;
  @ManyToOne
  private Interviewer interviewer;
}
