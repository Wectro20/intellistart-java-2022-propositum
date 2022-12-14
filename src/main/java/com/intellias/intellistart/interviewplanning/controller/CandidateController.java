package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import com.intellias.intellistart.interviewplanning.service.CandidateTimeSlotService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  @PreAuthorize("hasAuthority('CANDIDATE')")
  public ResponseEntity<CandidateTimeSlot> createSlot(
      @RequestBody CandidateTimeSlot timeSlotRequest) throws RuntimeException {
    SimpleUserPrincipal principal = (SimpleUserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
    CandidateTimeSlot timeSlotResponse = candidateService.createSlot(principal.getEmail(),
        timeSlotRequest.getDate(),
        timeSlotRequest.getFrom(), timeSlotRequest.getTo());
    return new ResponseEntity<>(timeSlotResponse, HttpStatus.CREATED);
  }

  /**
   * Create time slot for Candidate.
   *
   * @return response entity for candidate`s time slot and Http.Status.OK
   */
  @GetMapping("/current/slots")
  @PreAuthorize("hasAuthority('CANDIDATE')")
  public ResponseEntity<List<CandidateTimeSlot>> getSlots() {
    LOGGER.info("Successfully gave candidate slots");
    SimpleUserPrincipal principal = (SimpleUserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
    return new ResponseEntity<>(candidateService.getTimeSlots(principal.getEmail()), HttpStatus.OK);
  }

  /**
   * Create time slot for Candidate.
   *
   * @param id id of candidate time slot to be changed
   * @param candidateTimeSlot request body of time slot
   * @return response entity for candidate`s time slot and Http.Status.OK
   */
  @PutMapping(path = "current/slots/{slotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('CANDIDATE')")
  public ResponseEntity<CandidateTimeSlot> updateSlot(
      @PathVariable("slotId") final long id,
      @RequestBody final CandidateTimeSlot candidateTimeSlot) {
    if (candidateService.getTimeSlotId(id) == null) {
      LOGGER.info("Can't update product without id - null value was passed instead of it");
      throw new SlotNotFoundException();
    }
    LOGGER.info("Updated candidate time slot with id " + id);
    return new ResponseEntity<>(candidateService.updateSlot(id, candidateTimeSlot), HttpStatus.OK);
  }
}