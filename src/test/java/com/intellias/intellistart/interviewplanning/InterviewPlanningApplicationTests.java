package com.intellias.intellistart.interviewplanning;

import com.intellias.intellistart.interviewplanning.service.CandidateService;
import com.intellias.intellistart.interviewplanning.service.InterviewerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class InterviewPlanningApplicationTests {

  @Autowired
  private InterviewerService interviewerService;

  @Autowired
  private CandidateService candidateService;

  @Test
  void interviewerSlotMainScenario() {
    var slot = interviewerService.createSlot();
    assertThat(slot).isNotNull();
  }

  @Test
  void candidateSlotMainScenario() {
    var slot = interviewerService.createSlot();
    assertThat(slot).isNotNull();
  }

  @Test
  void createInterviewerSlotWithParameters() {
    var slot = interviewerService.createSlot(
            DayOfWeek.FRIDAY,
            LocalTime.of(9, 0), // 09:00
            LocalTime.of(17, 0) // 17:00
    );
  }

  @Test
  void createCandidateSlotWithParameters() {
    var slot = candidateService.createSlot(
            LocalDate.of(2022, 4, 12),
            LocalTime.of(9, 0), // 09:00
            LocalTime.of(17, 0) // 17:00
    );
  }

  @Test
  void contextLoads() {
  }

}
