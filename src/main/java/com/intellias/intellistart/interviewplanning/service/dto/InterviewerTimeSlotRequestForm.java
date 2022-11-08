package com.intellias.intellistart.interviewplanning.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Request body for update interviewer slot.
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InterviewerTimeSlotRequestForm {
  private InterviewDayOfWeek dayOfWeek;
  private Integer weekNum;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime from;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime to;

  /**
  * Create interviewer time slot from request.
  *
  */
  public InterviewerTimeSlot getInterviewerTimeSlot() {
    return InterviewerTimeSlot.builder()
         .dayOfWeek(dayOfWeek)
         .weekNum(weekNum)
         .from(from)
         .to(to)
         .build();
  }
}
