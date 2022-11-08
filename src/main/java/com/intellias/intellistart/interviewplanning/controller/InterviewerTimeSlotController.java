package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.security.SecurityUtil;
import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import com.intellias.intellistart.interviewplanning.service.InterviewerTimeSlotService;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotDto;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
   * @param interviewerTimeSlot request body of time slot
   * @return saved interviewer time slot
   */
  @PostMapping("/interviewers/slots")
  @PreAuthorize("hasAuthority('INTERVIEWER')")
  @ResponseStatus(HttpStatus.CREATED)
  public InterviewerTimeSlot createSlot(
      @RequestBody InterviewerTimeSlot interviewerTimeSlot) {
    return interviewerTimeSlotService.createSlot(SecurityUtil.getCurrentPrincipal().getEmail(),
        interviewerTimeSlot);
  }

  /**
   * Endpoint to update time slot for Interviewer.
   *
   * @param slotId              for which update
   * @param interviewerTimeSlot request body of time slot
   * @return updated interviewer time slot
   */
  @PostMapping("/interviewers/slots/{slotId}")
  @PreAuthorize("hasAuthority('INTERVIEWER')")
  public InterviewerTimeSlot updateSlot(@PathVariable Long slotId,
      @RequestBody InterviewerTimeSlot interviewerTimeSlot) {
    return interviewerTimeSlotService.updateSlot(SecurityUtil.getCurrentPrincipal().getEmail(),
        slotId, interviewerTimeSlot);
  }

  /**
   * Endpoint to get time slot for Interviewer.
   *
   * @param weekNum for which weekNum search slots
   * @return interviewer time slots
   */
  @GetMapping("/interviewers/slots")
  @PreAuthorize("hasAuthority('INTERVIEWER')")
  public List<InterviewerTimeSlotDto> getSlot(
      @RequestParam int weekNum) {
    return interviewerTimeSlotService.getTimeSlots(SecurityUtil.getCurrentPrincipal().getEmail(),
        weekNum);
  }

  /**
   * Endpoint to set booking limit Interviewer.
   *
   * @param interviewerId for which limit to set
   * @param bookingLimit  for define the value of limit
   * @return booking limit
   */
  @PostMapping("/interviewers/{interviewerId}/limit")
  @PreAuthorize("hasAuthority('INTERVIEWER')")
  public ResponseEntity<BookingLimit> setBookingLimit(@PathVariable Long interviewerId,
      @RequestParam Integer bookingLimit) {
    return new ResponseEntity<>(interviewerTimeSlotService
        .setBookingLimit(interviewerId, bookingLimit), HttpStatus.OK);
  }

}
