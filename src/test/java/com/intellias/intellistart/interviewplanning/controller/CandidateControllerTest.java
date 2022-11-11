package com.intellias.intellistart.interviewplanning.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.security.config.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.service.CandidateTimeSlotService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class CandidateControllerTest {

  @MockBean
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  @Autowired
  private CandidateTimeSlotService candidateTimeSlotService;

  @Autowired
  private WebApplicationContext webappContext;

  @Autowired
  private JwtRequestFilter jwtRequestFilter;

  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  private static final String userEmail = "user@gmail.com";

  private static final String url = "/candidates/current/slots";

  private static CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
      .date(LocalDate.of(2022, 12, 16))
      .id(2L)
      .from(LocalTime.of(9, 0))
      .to(LocalTime.of(17, 0))
      .slotStatus(TimeSlotStatus.NEW)
      .email(userEmail)
      .build();

  private static CandidateTimeSlot updatedSlot = CandidateTimeSlot.builder()
      .date(LocalDate.of(2022, 12, 16))
      .id(2L)
      .from(LocalTime.of(10, 0))
      .to(LocalTime.of(17, 0))
      .slotStatus(TimeSlotStatus.NEW)
      .email(userEmail)
      .build();

  private static final String RIGHT_REQUEST = "{\n"
      + "\"from\": \"09:00\",\n"
      + "\"to\": \"17:00\",\n"
      + "\"date\": \"2022-12-16\"\n"
      + "}";

  private static final String UPDATE_REQUEST = "{\n"
      + "\"from\": \"10:00\",\n"
      + "\"to\": \"17:00\",\n"
      + "\"date\": \"2022-12-16\"\n"
      + "}";

  private static final String BAD_DAY_OF_WEEK_REQUEST = "{\n"
      + "\"from\": \"09:00\",\n"
      + "\"to\": \"17:00\",\n"
      + "\"date\": \"2022-12-10\"\n"
      + "}";

  private static final String BAD_FROM_BOUNDARIES_REQUEST = "{\n"
      + "\"from\": \"09:02\",\n"
      + "\"to\": \"17:00\",\n"
      + "\"date\": \"2022-12-16\"\n"
      + "}";

  private static final String BAD_FROM_TO_REQUEST = "{\n"
      + "\"from\": \"09:00\",\n"
      + "\"to\": \"10:00\",\n"
      + "\"date\": \"2022-12-16\"\n"
      + "}";

  private static final String RIGHT_RESPONSE = "{"
      + "\"id\":2,"
      + "\"from\":\"09:00\","
      + "\"to\":\"17:00\","
      + "\"date\":\"2022-12-16\""
      + "}";

  private static final String BAD_DAY_OF_WEEK_RESPONSE = "{"
      + "\"errorCode\":\"invalid_day_of_week\","
      + "\"errorMessage\":\"Possible values: [Mon, Tue, Wed, Thu, Fri]"
      + "\"}";

  private static final String BAD_FROM_BOUNDARIES_EXCEPTION = "{"
      + "\"errorCode\":\"invalid_boundaries\","
      + "\"errorMessage\":\"09:02; 17:00\""
      + "}";

  private static final String BAD_FROM_TO_BOUNDARIES_EXCEPTION = "{"
      + "\"errorCode\":\"invalid_boundaries\","
      + "\"errorMessage\":\"09:00; 10:00\""
      + "}";

  private static final String BAD_SLOT_IS_OVERLAPPING_EXCEPTION = "{"
      + "\"errorCode\":\"slot_is_overlapping\","
      + "\"errorMessage\":\"Slot is already exists, id of existing slot: 2\""
      + "}";

  @BeforeEach
  public void setUp() {
    // to allow all calls to /candidates/* without any authentication
    mockMvc = webAppContextSetup(this.webappContext)
        .addFilter(this.jwtRequestFilter, "/candidates**")
        .build();
  }

  @AfterEach
  public void prepare() {
    candidateTimeSlot = CandidateTimeSlot.builder()
        .date(LocalDate.of(2022, 12, 16))
        .id(2L)
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(17, 0))
        .slotStatus(TimeSlotStatus.NEW)
        .email(userEmail)
        .build();
  }

  @Test
  @WithUserDetails(userEmail)
  void createCandidateTimeSlot() throws Exception {
    Mockito.doReturn(candidateTimeSlot).when(candidateTimeSlotRepository)
        .save(ArgumentMatchers.any(CandidateTimeSlot.class));

    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .content(RIGHT_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().string(RIGHT_RESPONSE));

  }

  @Test
  @WithUserDetails(userEmail)
  void createCandidateTimeSlotWithInvalidDayOfWeekAndThrowException() throws Exception {
    candidateTimeSlot.setDate(LocalDate.of(2022, 12, 10));

    Mockito.doReturn(candidateTimeSlot).when(candidateTimeSlotRepository)
        .save(ArgumentMatchers.any(CandidateTimeSlot.class));

    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .content(BAD_DAY_OF_WEEK_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string(BAD_DAY_OF_WEEK_RESPONSE));
  }

  @Test
  @WithUserDetails(userEmail)
  void createCandidateTimeSlotWithInvalidFromBoundariesAndThrowException() throws Exception {
    candidateTimeSlot.setFrom(LocalTime.of(9, 2));

    Mockito.doReturn(candidateTimeSlot).when(candidateTimeSlotRepository)
        .save(ArgumentMatchers.any(CandidateTimeSlot.class));

    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .content(BAD_FROM_BOUNDARIES_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string(BAD_FROM_BOUNDARIES_EXCEPTION));
  }

  @Test
  @WithUserDetails(userEmail)
  void createCandidateTimeSlotWithIntervalLessThanMinAndThrowException() throws Exception {
    candidateTimeSlot.setTo(LocalTime.of(10, 0));

    Mockito.doReturn(candidateTimeSlot).when(candidateTimeSlotRepository)
        .save(ArgumentMatchers.any(CandidateTimeSlot.class));

    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .content(BAD_FROM_TO_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string(BAD_FROM_TO_BOUNDARIES_EXCEPTION));
  }

  @Test
  @WithUserDetails(userEmail)
  void createCandidateTimeSlotWhichOverlappingAndThrowException() throws Exception {
    Mockito.when(candidateTimeSlotService.createSlot(userEmail, candidateTimeSlot.getDate(),
            candidateTimeSlot.getFrom(), candidateTimeSlot.getTo()))
        .thenThrow(new SlotIsOverlappingException(candidateTimeSlot.getId()));

    this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .content(RIGHT_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andExpect(MockMvcResultMatchers.content().string(BAD_SLOT_IS_OVERLAPPING_EXCEPTION));
  }

  @Test
  @WithUserDetails(userEmail)
  void getCandidateSlotsAndDoPrint() throws Exception {
    List<CandidateTimeSlot> candidateTimeSlots = List.of(candidateTimeSlot, candidateTimeSlot,
        candidateTimeSlot);

    Mockito.doReturn(candidateTimeSlots).when(candidateTimeSlotRepository).findByEmail(userEmail);

    String timeSlots = objectMapper.writeValueAsString(candidateTimeSlots);

    this.mockMvc.perform(MockMvcRequestBuilders.get(url)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(timeSlots));
  }

  @Test
  @WithUserDetails(userEmail)
  void updateCandidateTimeSlotAndDoPrint() throws Exception {
    String response = objectMapper.writeValueAsString(updatedSlot);

    Mockito.doReturn(Optional.of(candidateTimeSlot)).when(candidateTimeSlotRepository)
        .findById(updatedSlot.getId());

    Mockito.doReturn(updatedSlot).when(candidateTimeSlotRepository).save(ArgumentMatchers.any(
        CandidateTimeSlot.class));

    this.mockMvc.perform(MockMvcRequestBuilders.put(url + "/" + candidateTimeSlot.getId())
            .content(UPDATE_REQUEST)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(response));
  }

  @Test
  @WithUserDetails(userEmail)
  void sendUpdateRequestWithInvalidSlotIdAndThrowException() throws Exception {
    Mockito.doReturn(Optional.empty()).when(candidateTimeSlotRepository).findById(
        updatedSlot.getId());

    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorCode("slot_not_found");
    errorResponse.setErrorMessage("slot was not found");

    this.mockMvc.perform(MockMvcRequestBuilders.put(url + "/" + updatedSlot.getId())
            .content(UPDATE_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
            MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(errorResponse)));
  }
}
