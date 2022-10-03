package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InterviewerServiceTest {

  @Autowired
  private InterviewerService interviewerService;


  @Test
  void interviewerSlotMainScenario() {
    var slot = interviewerService.createSlot();

    assertNotNull(slot);
  }

  @Test
  void candidateSlotMainScenario() {
    var slot = interviewerService.createSlot();

    assertNotNull(slot);
  }

  @Test
  void createInterviewerSlotWithParameters() {
    var slot = interviewerService.createSlot(
        DayOfWeek.FRIDAY,
        LocalTime.of(9, 0), // 09:00
        LocalTime.of(17, 0) // 17:00
    );

    assertNotNull(slot);
    assertEquals(DayOfWeek.FRIDAY, slot.getDay());
    assertEquals(LocalTime.of(9, 0), slot.getStart());
    assertEquals(LocalTime.of(17, 0), slot.getEnd());
  }
}
