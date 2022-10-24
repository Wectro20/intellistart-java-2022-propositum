package com.intellias.intellistart.interviewplanning.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It is DTO for booking entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(Include.NON_EMPTY)
public class InterviewerTimeSlotDto {

  private int id;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;
  private DayOfWeek dayOfWeek;
  private Integer weekNum;
  private List<BookingDto> bookings;

}
