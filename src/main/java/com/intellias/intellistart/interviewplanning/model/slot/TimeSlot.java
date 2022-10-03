package com.intellias.intellistart.interviewplanning.model.slot;

import com.intellias.intellistart.interviewplanning.model.user.User;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class TimeSlot {

  private Long id;
  private LocalTime start;
  private LocalTime end;
  private User user;

  public TimeSlot(LocalTime start, LocalTime end) {
    this.start = start;
    this.end = end;
  }
}
