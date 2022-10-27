package com.intellias.intellistart.interviewplanning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellias.intellistart.interviewplanning.FileTestUtils;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.service.dto.DashboardDto;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

  private static final int TEST_WEEK_NUM = 44;

  @Mock
  private GetWeekNumberService weekService;
  @Mock
  private InterviewerTimeSlotRepository interviewerSlotRepository;
  @Mock
  private CandidateTimeSlotRepository candidateSlotRepository;
  private DashboardService dashboardService;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper()
        .disable(MapperFeature.USE_ANNOTATIONS)
        .registerModule(new JavaTimeModule());

    dashboardService =
        new DashboardService(weekService, interviewerSlotRepository, candidateSlotRepository
        );
  }

  @Test
  public void getDashboard_Should_SuccessCreateDashboard() throws JsonProcessingException {
    String json = FileTestUtils.readFile("dashboardTestInputs.json");

    LocalDate startDateOfWeek = LocalDate.of(2022, 10, 24);
    LocalDate endDateOfWeek = LocalDate.of(2022, 10, 28);
    LocalDate tuesdayDate = LocalDate.of(2022, 10, 25);
    LocalDate wednesdayDate = LocalDate.of(2022, 10, 26);
    LocalDate thursdayDate = LocalDate.of(2022, 10, 27);

    TemporalAdjuster dayMonday = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY);
    TemporalAdjuster dayFriday = TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY);

    try (MockedStatic<TemporalAdjusters> adjusters = Mockito.mockStatic(TemporalAdjusters.class)) {
      adjusters.when(() -> TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
          .thenReturn(dayMonday);
      adjusters.when(() -> TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
          .thenReturn(dayFriday);

      DashboardTestInputs dashboardTestInputs = objectMapper.readValue(json,
          DashboardTestInputs.class);

      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, DayOfWeek.MONDAY))
          .thenReturn(startDateOfWeek);
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, DayOfWeek.TUESDAY))
          .thenReturn(tuesdayDate);
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, DayOfWeek.WEDNESDAY))
          .thenReturn(wednesdayDate);
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, DayOfWeek.THURSDAY))
          .thenReturn(thursdayDate);
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, DayOfWeek.FRIDAY))
          .thenReturn(endDateOfWeek);

      Mockito.when(interviewerSlotRepository.findAllByWeekNum(TEST_WEEK_NUM))
          .thenReturn(dashboardTestInputs.getInterviewersTimeSlots());
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, dayMonday))
          .thenReturn(startDateOfWeek);
      Mockito.when(weekService.getDateForWeekNumAndDay(TEST_WEEK_NUM, dayFriday))
          .thenReturn(endDateOfWeek);
      Mockito.when(candidateSlotRepository.findAllByDateBetween(startDateOfWeek, endDateOfWeek))
          .thenReturn(dashboardTestInputs.getCandidatesTimeSlots());

      DashboardDto actualDashboard = dashboardService.getDashboard(TEST_WEEK_NUM);

      String jsonExpected = FileTestUtils.readFile("dashboardExpectedOutputs.json");

      DashboardDto expectedDashboard = objectMapper.readValue(jsonExpected,
          DashboardDto.class);

      Assertions.assertEquals(expectedDashboard, actualDashboard);
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class DashboardTestInputs {

    private List<InterviewerTimeSlot> interviewersTimeSlots;
    private List<CandidateTimeSlot> candidatesTimeSlots;

  }
}
