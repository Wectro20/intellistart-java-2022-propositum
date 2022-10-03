package com.intellias.intellistart.interviewplanning.model.slot;

import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InterviewerTimeSlot extends TimeSlot {

  private DayOfWeek day;

  public InterviewerTimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {
    super(start, end);
    this.day = day;
  }
}
