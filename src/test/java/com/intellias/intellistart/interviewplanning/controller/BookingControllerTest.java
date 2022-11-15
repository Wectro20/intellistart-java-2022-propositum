package com.intellias.intellistart.interviewplanning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.security.config.JwtRequestFilter;
import com.intellias.intellistart.interviewplanning.security.config.SimpleUserPrincipal;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class BookingControllerTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BookingRepository bookingRepository;
  @MockBean
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @MockBean
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  private MockMvc mockMvc;

  private static final BookingDto BOOKING_DTO = BookingDto.builder()
      .startTime(LocalTime.of(10, 0))
      .endTime(LocalTime.of(11, 30))
      .candidateTimeSlotId(1L)
      .interviewerTimeSlotId(1L)
      .subject("Interview")
      .description("Interview for candidate")
      .build();
  private static final InterviewerTimeSlot INTERVIEWER_TIME_SLOT = InterviewerTimeSlot.builder()
      .from(LocalTime.of(10, 0))
      .to(LocalTime.of(11, 30))
      .dayOfWeek(InterviewDayOfWeek.MONDAY)
      .weekNum(15)
      .bookings(Collections.emptyList())
      .build();
  private static final CandidateTimeSlot CANDIDATE_TIME_SLOT = CandidateTimeSlot.builder()
      .date(LocalDate.of(2022, 10, 25))
      .from(LocalTime.of(10, 0))
      .to(LocalTime.of(11, 30))
      .bookings(Collections.emptyList())
      .build();
  private static final Booking BOOKING = Booking.builder()
      .id(1L)
      .startTime(LocalTime.of(10, 0))
      .endTime(LocalTime.of(11, 30))
      .candidateTimeSlot(CANDIDATE_TIME_SLOT)
      .interviewerTimeSlot(INTERVIEWER_TIME_SLOT)
      .subject("Interview")
      .description("Interview for candidate")
      .build();

  private static final String POST_BOOKING_URL = "/bookings";
  private static final String userEmail = "user@gmail.com";
  private static final User user = new User(userEmail, UserRole.COORDINATOR);
  private static final UserDetails userDetails = new SimpleUserPrincipal(user);

  @BeforeEach
  public void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(jwtRequestFilter)
        .build();
  }

  @Test
  @WithMockUser(authorities = "COORDINATOR")
  void sendPostMethodToCreateBookingAndRetrieveStatusOk() throws Exception {
    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(CANDIDATE_TIME_SLOT));
    Mockito.when(bookingRepository.save(BOOKING))
        .thenReturn(BOOKING);

    String requestBooking = objectMapper.writeValueAsString(BOOKING_DTO);
    String responseBooking = objectMapper.writeValueAsString(BOOKING_DTO);
    this.mockMvc.perform(MockMvcRequestBuilders.post(POST_BOOKING_URL)
            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
            .content(requestBooking)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().string(responseBooking));
  }

  @Test
  @WithMockUser(authorities = "COORDINATOR")
  public void sendPostMethodToUpdateBookingAndRetrieveStatusOk() throws Exception {
    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(CANDIDATE_TIME_SLOT));
    Mockito.when(bookingRepository.findById(1L))
        .thenReturn(Optional.of(BOOKING));
    Mockito.when(bookingRepository.save(BOOKING))
        .thenReturn(BOOKING);
    String requestBooking = objectMapper.writeValueAsString(BOOKING_DTO);
    BOOKING_DTO.setId(1L);
    String responseBooking = objectMapper.writeValueAsString(BOOKING_DTO);
    this.mockMvc.perform(MockMvcRequestBuilders.post(POST_BOOKING_URL + "/1")
            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
            .content(requestBooking)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(responseBooking));
  }

}