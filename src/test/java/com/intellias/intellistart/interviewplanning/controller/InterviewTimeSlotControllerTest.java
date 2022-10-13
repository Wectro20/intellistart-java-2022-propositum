package com.intellias.intellistart.interviewplanning.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.FileTestUtils;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
public class InterviewTimeSlotControllerTest {

  private static final String CREATE_SLOT_NO_TO_FIELD_REQ_PATH = "request/createInterviewerSlotNoToFieldRequest.json";
  private static final String CREATE_SLOT_REQ_PATH = "request/createInterviewerSlotRequest.json";
  private static final String CREATE_SLOT_NO_TO_FIELD_RESP_PATH = "response/createInterviewerSlotNoToFieldResponse.json";
  private static final String CREATE_SLOT_RESP_PATH = "response/createInterviewerSlotResponse.json";
  private static final String CREATE_SLOT_SHORT_RANGE_REQ_PATH = "request/createSlotShortRangeRequest.json";
  private static final String CREATE_SLOT_SHORT_RANGE_RESP_PATH = "response/createSlotShortRangeResponse.json";

  private static final String EMAIL = "test@com";
  private static final User USER = new User(1L, EMAIL, UserRole.INTERVIEWER);

  @MockBean
  private InterviewerTimeSlotRepository timeSlotRepository;

  @MockBean
  private UserRepository userRepository;

  @Captor
  private ArgumentCaptor<InterviewerTimeSlot> timeSlotArgumentCaptor;

  @Value("${interview.duration_minutes}")
  private Integer interviewDuration;

  @Value("${working_hours.from}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursFrom;

  @Value("${working_hours.to}")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime workingHoursTo;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FilterChainProxy springSecurityFilter;

  @Autowired
  private WebApplicationContext webappContext;

  @BeforeEach
  public void setUp() {
    // to allow all calls to /interviewers/* without any authentication
    mockMvc = webAppContextSetup(this.webappContext)
        .addFilter(this.springSecurityFilter, "/interviewers**")
        .build();
  }

  @Test
  public void createSlot_Should_RespondWithSavedTimeSlot() throws Exception {
    String requestJson = FileTestUtils.readFile(CREATE_SLOT_REQ_PATH);

    InterviewerTimeSlot expectedSlot = objectMapper.readValue(requestJson,
        InterviewerTimeSlot.class);
    expectedSlot.setId(5);
    expectedSlot.setStatus(TimeSlotStatus.NEW);
    expectedSlot.setUser(USER);

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, 23))
        .thenReturn(Collections.emptyList());
    Mockito.when(timeSlotRepository.save(Mockito.any())).thenReturn(expectedSlot);

    mockMvc.perform(post("/interviewers/" + EMAIL + "/slots")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().json(FileTestUtils.readFile(CREATE_SLOT_RESP_PATH)));

    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualSlot = timeSlotArgumentCaptor.getValue();
    actualSlot.setId(5);

    Assertions.assertEquals(expectedSlot, actualSlot);
  }

  @Test
  public void createSlot_When_ToIsNotPresented_Should_UpdateToAndRespondWithSavedTimeSlot()
      throws Exception {
    String requestJson = FileTestUtils.readFile(CREATE_SLOT_NO_TO_FIELD_REQ_PATH);

    InterviewerTimeSlot expectedSlot = objectMapper.readValue(requestJson,
        InterviewerTimeSlot.class);

    expectedSlot.setId(5);
    expectedSlot.setTo(LocalTime.of(10, 30));
    expectedSlot.setStatus(TimeSlotStatus.NEW);
    expectedSlot.setUser(USER);

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, 23))
        .thenReturn(Collections.emptyList());

    Mockito.when(timeSlotRepository.save(Mockito.any())).thenReturn(expectedSlot);

    mockMvc.perform(post("/interviewers/" + EMAIL + "/slots")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().json(FileTestUtils.readFile(CREATE_SLOT_NO_TO_FIELD_RESP_PATH)));

    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualSlot = timeSlotArgumentCaptor.getValue();
    actualSlot.setId(5);

    Assertions.assertEquals(expectedSlot, actualSlot);
  }

  @Test
  public void createSlot_When_RangeIsShorterInterviewDuration_Should_ReturnErrorResponse()
      throws Exception {

    mockMvc.perform(post("/interviewers/" + EMAIL + "/slots")
            .content(FileTestUtils.readFile(CREATE_SLOT_SHORT_RANGE_REQ_PATH))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().json(FileTestUtils.readFile(CREATE_SLOT_SHORT_RANGE_RESP_PATH)));
  }

}