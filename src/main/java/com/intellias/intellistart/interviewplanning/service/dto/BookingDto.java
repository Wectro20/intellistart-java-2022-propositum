package com.intellias.intellistart.interviewplanning.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It is DTO for booking entity.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

  @JsonProperty(access = Access.READ_ONLY)
  private Long id;
  private Long interviewerTimeSlotId;
  private Long candidateTimeSlotId;
  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("from")
  private LocalTime startTime;
  @JsonFormat(pattern = "HH:mm")
  @JsonProperty("to")
  private LocalTime endTime;
  private String subject;
  private String description;

}
