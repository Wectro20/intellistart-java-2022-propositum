package com.intellias.intellistart.interviewplanning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellias.intellistart.interviewplanning.controllers.CandidateController;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.service.CandidateTimeSlotService;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class CandidateControllerTest {

  @Autowired
  private CandidateController candidateController;
  @Autowired
  private CandidateTimeSlotService candidateTimeSlotService;

  @Test
  void createCandidateTimeSlot() {
    CandidateTimeSlot slot = candidateTimeSlotService.createSlot("user@gmail.com",
        LocalDate.of(2022, 04, 12),
        LocalTime.of(8, 30),
        LocalTime.of(10, 0));

    ResponseEntity<CandidateTimeSlot> slot1 = candidateController.createSlot(slot);

    assertNotNull(slot1);
  }

  @Test
  void createCandidateTimeSlotAndStatusIsCreated() {
    CandidateTimeSlot slot = candidateTimeSlotService.createSlot("user@gmail.com",
        LocalDate.of(2022, 04, 12),
        LocalTime.of(8, 30),
        LocalTime.of(10, 0));

    ResponseEntity<CandidateTimeSlot> slot1 = candidateController.createSlot(slot);

    assertEquals(HttpStatus.CREATED, slot1.getStatusCode());
  }
}
