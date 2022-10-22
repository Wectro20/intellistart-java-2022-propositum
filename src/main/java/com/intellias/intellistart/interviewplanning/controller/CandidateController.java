package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import com.intellias.intellistart.interviewplanning.service.CandidateTimeSlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestController for creating time slots for candidate.
 */
@RestController
@RequestMapping(path = "/candidates", produces = "application/json")
public class CandidateController {
  private static final Logger LOGGER = LoggerFactory.getLogger(CandidateController.class);

  @Autowired
  private final CandidateTimeSlotService candidateService;

  @Autowired
  public CandidateController(CandidateTimeSlotService candidateService) {
    this.candidateService = candidateService;
  }

  /**
   * Create time slot for Candidate.
   *
   * @param timeSlotRequest request body of time slot
   * @return response entity for candidate`s time slot and Http.Status.Created
   */
  @JsonView({Views.Public.class})
  @PostMapping("current/slots")
  public ResponseEntity<CandidateTimeSlot> createSlot(
      @RequestBody CandidateTimeSlot timeSlotRequest) throws RuntimeException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentEmail = authentication.getName();

    CandidateTimeSlot timeSlotResponse = candidateService.createSlot(currentEmail,
        timeSlotRequest.getDate(),
        timeSlotRequest.getFrom(), timeSlotRequest.getTo());

    return new ResponseEntity<>(timeSlotResponse, HttpStatus.CREATED);
  }

  // TODO: set path to "/candidate/{candidateEmail}/slots" after integrating OAuth2
  @GetMapping("/{candidateEmail}/slots")
  public ResponseEntity<List<CandidateTimeSlot>> getSlots(@PathVariable String candidateEmail) {
    LOGGER.info("Successfully gave candidate slots");
    return new ResponseEntity<>(candidateService.getTimeSlots(candidateEmail), HttpStatus.OK);
  }

  @PutMapping(path = "current/slots/{slotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CandidateTimeSlot> updateSlot(@PathVariable("slotId") final long id,
                                                      @RequestBody final CandidateTimeSlot candidateTimeSlot) {
    if (candidateService.getTimeSlotId(id) == null) {
      LOGGER.info("Can't update product without id - null value was passed instead of it");
      throw new SlotNotFoundException();
    }
    return new ResponseEntity<>(candidateService.updateSlot(id, candidateTimeSlot), HttpStatus.OK);
  }
}