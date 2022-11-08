package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import com.intellias.intellistart.interviewplanning.security.config.JwtRequestFilter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class GetWeekNumberControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private ObjectMapper objectMapper;

  private static final String URL_CURRENT_WEEK = "/weeks/current";
  private static final String URL_NEXT_WEEK = "/weeks/next";

  private LocalDate dates;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    // to allow all calls to /candidates/* without any authentication
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(jwtRequestFilter, "/weeks**")
        .build();

    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int day = localDate.getDayOfMonth();
    int month = localDate.getMonthValue();
    int year = localDate.getYear();
    dates = LocalDate.of(year, month, day);
  }

  @Test
  void sendGetRequestToSeeCurrentWeekNumberAndGetStatusOk() throws Exception {
    WeekNumber weekNumber = new WeekNumber(dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR));

    String response = objectMapper.writeValueAsString(weekNumber);

    this.mockMvc.perform(MockMvcRequestBuilders.get(URL_CURRENT_WEEK)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(response))
        .andDo(MockMvcResultHandlers.print());
  }

  @Test
  void sendGetRequestToSeeNextWeekNumberAndGetStatusOk() throws Exception {
    WeekNumber nextWeekNumber = new WeekNumber(
        dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR) % 52 + 1);

    String response = objectMapper.writeValueAsString(nextWeekNumber);

    this.mockMvc.perform(MockMvcRequestBuilders.get(URL_NEXT_WEEK)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(response))
        .andDo(MockMvcResultHandlers.print());
  }
}