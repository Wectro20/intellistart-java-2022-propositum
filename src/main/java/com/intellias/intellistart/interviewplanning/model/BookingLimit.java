package com.intellias.intellistart.interviewplanning.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking Limit entity for max number of booking for interviewer.
 */
@Entity(name = "booking_limit")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingLimit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  private User user;
  @Column(name = "max_limit_per_week")
  private int maxLimitPerWeek;
}
