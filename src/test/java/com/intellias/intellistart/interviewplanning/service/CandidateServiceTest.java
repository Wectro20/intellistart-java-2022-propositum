package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CandidateServiceTest {

  @Autowired
  private CandidateService candidateService;

  @Test
  void createCandidateSlotWithParameters() {
    var slot = candidateService.createSlot(
        LocalDate.of(2022, 4, 12),
        LocalTime.of(9, 0), // 09:00
        LocalTime.of(17, 0) // 17:00
    );

    assertNotNull(slot);
    assertEquals(LocalDate.of(2022, 4, 12), slot.getDate());
    assertEquals(LocalTime.of(9, 0), slot.getStart());
    assertEquals(LocalTime.of(17, 0), slot.getEnd());
  }

}
