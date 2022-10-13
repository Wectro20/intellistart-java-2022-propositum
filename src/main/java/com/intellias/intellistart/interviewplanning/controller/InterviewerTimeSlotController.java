package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.service.InterviewerTimeSlotService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Interviewer time slots.
 */
@RestController
@AllArgsConstructor
public class InterviewerTimeSlotController {

  private InterviewerTimeSlotService interviewerTimeSlotService;

  /**
   * Endpoint to create time slot for Interviewer.
   *
   * @param interviewerEmail for which create slot
   * @param interviewerTimeSlot request body of time slot
   *
   * @return saved interviewer time slot
   */
  @PostMapping("/interviewers/{interviewerEmail}/slots")
  @ResponseStatus(HttpStatus.CREATED)
  public InterviewerTimeSlot createSlot(@PathVariable String interviewerEmail,
      @RequestBody InterviewerTimeSlot interviewerTimeSlot) {
    return interviewerTimeSlotService.createSlot(interviewerEmail, interviewerTimeSlot);
  }
}
