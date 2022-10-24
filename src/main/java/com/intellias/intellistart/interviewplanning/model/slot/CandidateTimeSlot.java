package com.intellias.intellistart.interviewplanning.model.slot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
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
  @JsonView(Views.Public.class)
  private Long id;

  @Column(name = "\"from\"")
  @JsonFormat(pattern = "HH:mm")
  @JsonView(Views.Public.class)
  private LocalTime from;

  @Column(name = "\"to\"")
  @JsonFormat(pattern = "HH:mm")
  @JsonView(Views.Public.class)
  private LocalTime to;

  @JsonView(Views.Public.class)
  private LocalDate date;

  @JsonView(Views.Internal.class)
  private TimeSlotStatus slotStatus;

  @JsonView(Views.Internal.class)
  private String email;

}
