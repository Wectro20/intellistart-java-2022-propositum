package com.intellias.intellistart.interviewplanning.model.slot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Builder(toBuilder = true)
public class InterviewerTimeSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "\"from\"")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  @Column(name = "\"to\"")
  private LocalTime to;
  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;
  private Integer weekNum;
  @Enumerated(EnumType.STRING)
  @JsonIgnore
  private TimeSlotStatus status;
  @ManyToOne
  @JsonIgnore
  private User user;
  @OneToMany(mappedBy = "interviewerTimeSlot")
  private List<Booking> bookings;
}
