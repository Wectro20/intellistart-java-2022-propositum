package com.intellias.intellistart.interviewplanning.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.FileTestUtils;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import com.intellias.intellistart.interviewplanning.security.config.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.service.GetWeekNumberService;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
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
  @MockBean
  private BookingLimitRepository bookingLimitRepository;

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
  private GetWeekNumberService weekNumberService;

  @Autowired
  private JwtRequestFilter jwtRequestFilter;

  @Autowired
  private WebApplicationContext webappContext;

  @BeforeEach
  public void setUp() {
    // to allow all calls to /candidates/* without any authentication
    mockMvc = webAppContextSetup(this.webappContext)
        .addFilter(this.jwtRequestFilter, "/interviewers**")
        .build();

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
  }

  @Test
  @WithUserDetails(value = EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
  public void createSlot_Should_RespondWithSavedTimeSlot() throws Exception {
    String requestJson = FileTestUtils.readFile(CREATE_SLOT_REQ_PATH);

    InterviewerTimeSlot expectedSlot = objectMapper.readValue(requestJson,
        InterviewerTimeSlot.class);

    expectedSlot.setWeekNum(weekNumberService.getNextWeekNumber().getWeekNum());

    requestJson = objectMapper.writeValueAsString(expectedSlot);

    expectedSlot.setId(5L);
    expectedSlot.setStatus(TimeSlotStatus.NEW);
    expectedSlot.setUser(USER);

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, 23))
        .thenReturn(Collections.emptyList());
    Mockito.when(timeSlotRepository.save(Mockito.any())).thenReturn(expectedSlot);

    mockMvc.perform(post("/interviewers/slots")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().json(prepareResponseWithWeekNum(CREATE_SLOT_RESP_PATH)));

    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualSlot = timeSlotArgumentCaptor.getValue();
    actualSlot.setId(5L);

    Assertions.assertEquals(expectedSlot, actualSlot);
  }

  @Test
  @WithUserDetails(value = EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
  public void createSlot_When_ToIsNotPresented_Should_UpdateToAndRespondWithSavedTimeSlot()
      throws Exception {
    String requestJson = FileTestUtils.readFile(CREATE_SLOT_NO_TO_FIELD_REQ_PATH);

    InterviewerTimeSlot expectedSlot = objectMapper.readValue(requestJson,
        InterviewerTimeSlot.class);

    expectedSlot.setWeekNum(weekNumberService.getNextWeekNumber().getWeekNum());

    requestJson = objectMapper.writeValueAsString(expectedSlot);

    expectedSlot.setId(5L);
    expectedSlot.setTo(LocalTime.of(10, 30));
    expectedSlot.setStatus(TimeSlotStatus.NEW);
    expectedSlot.setUser(USER);

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, 23))
        .thenReturn(Collections.emptyList());

    Mockito.when(timeSlotRepository.save(Mockito.any())).thenReturn(expectedSlot);

    mockMvc.perform(post("/interviewers/slots")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().json(prepareResponseWithWeekNum(CREATE_SLOT_NO_TO_FIELD_RESP_PATH)));

    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualSlot = timeSlotArgumentCaptor.getValue();
    actualSlot.setId(5L);

    Assertions.assertEquals(expectedSlot, actualSlot);
  }

  @Test
  @WithUserDetails(value = EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
  public void createSlot_When_RangeIsShorterInterviewDuration_Should_ReturnErrorResponse()
      throws Exception {
    String requestJson = FileTestUtils.readFile(CREATE_SLOT_SHORT_RANGE_REQ_PATH);

    InterviewerTimeSlot requestSlot = objectMapper.readValue(requestJson,
        InterviewerTimeSlot.class);

    requestSlot.setWeekNum(weekNumberService.getNextWeekNumber().getWeekNum());

    requestJson = objectMapper.writeValueAsString(requestSlot);

    mockMvc.perform(post("/interviewers/slots")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().json(FileTestUtils.readFile(CREATE_SLOT_SHORT_RANGE_RESP_PATH)));
  }

  @Test
  @WithUserDetails(value = EMAIL, setupBefore = TestExecutionEvent.TEST_EXECUTION)
  public void setInterviewBookingLimit() throws Exception {
    Mockito.when(bookingLimitRepository.findByUserAndWeekNum(ArgumentMatchers.any(),
            ArgumentMatchers.anyInt()))
        .thenReturn(Optional.empty());

    BookingLimit bookingLimit = BookingLimit.builder()
        .id(1L)
        .bookingLimit(10)
        .user(USER)
        .build();

    String response = objectMapper.writeValueAsString(bookingLimit);

    Mockito.when(bookingLimitRepository.save(ArgumentMatchers.any()))
        .thenReturn(bookingLimit);

    this.mockMvc.perform(
            MockMvcRequestBuilders.post("/interviewers/" + EMAIL + "/limit?bookingLimit=10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(response))
        .andDo(MockMvcResultHandlers.print());
  }

  private String prepareResponseWithWeekNum(String filePath) throws JsonProcessingException {
    String responseJson = FileTestUtils.readFile(filePath);

    InterviewerTimeSlot responseSlot = objectMapper.readValue(responseJson,
        InterviewerTimeSlot.class);

    responseSlot.setWeekNum(weekNumberService.getNextWeekNumber().getWeekNum());

    return objectMapper.writeValueAsString(responseSlot);
  }
}