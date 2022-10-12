package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.views.Views;
import com.intellias.intellistart.interviewplanning.service.CandidateTimeSlotService;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for creating time slots for candidate.
 */
@RestController
@RequestMapping(path = "/candidates", produces = "application/json")
public class CandidateController {

  private final CandidateTimeSlotService candidateService;

  @Autowired
  public CandidateController(CandidateTimeSlotService candidateService) {
    this.candidateService = candidateService;
  }

  @GetMapping
  ResponseEntity<CandidateTimeSlot> create() {
    CandidateTimeSlot build = CandidateTimeSlot.builder()
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(15, 0))
        .date(LocalDate.of(2020, 12, 12))
        .build();

    return new ResponseEntity<>(build, HttpStatus.CREATED);
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
}