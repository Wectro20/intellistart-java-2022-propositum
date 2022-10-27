package com.intellias.intellistart.interviewplanning.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * It is DTO for dashboard.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDto {

  private List<DashboardDay> days;

  /**
   * It is data for each day in dashboard.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class DashboardDay {
    private LocalDate date;
    private InterviewDayOfWeek dayOfWeek;
    private List<TimeSlot> interviewerSlots;
    private List<TimeSlot> candidateSlots;
    private Map<Long, BookingDto> bookings;
  }

  /**
   * It is data for interviewer and candidate time slots.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class TimeSlot {
    private Long id;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime from;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime to;
    private String email;
    private List<Long> bookings;
  }

}
