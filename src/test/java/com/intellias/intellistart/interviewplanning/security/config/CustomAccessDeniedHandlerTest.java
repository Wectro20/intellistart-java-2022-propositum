package com.intellias.intellistart.interviewplanning.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.ErrorResponse;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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
class CustomAccessDeniedHandlerTest {

  @Autowired
  private WebApplicationContext webappContext;
  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  @Autowired
  private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  private static final BookingDto BOOKING_DTO = BookingDto.builder()
      .startTime(LocalTime.of(10, 0))
      .endTime(LocalTime.of(11, 30))
      .candidateTimeSlotId(1L)
      .interviewerTimeSlotId(1L)
      .subject("Interview")
      .description("Interview for candidate")
      .build();

  private final User user = new User("your@gmail.com", UserRole.CANDIDATE);
  private final UserDetails userDetails = new SimpleUserPrincipal(user);
  private static final String URL_BOOKING = "/bookings";

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webappContext)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .addFilter(jwtRequestFilter)
        .build();
  }

  @Test
  @WithMockUser(authorities = "CANDIDATE")
  void sendRequestToBookingControllerWithUserCandidateAndThrowAccessDeniedException()
      throws Exception {

    String requestBooking = objectMapper.writeValueAsString(BOOKING_DTO);

    ErrorResponse error = new ErrorResponse();
    error.setErrorCode("not_authorized");
    error.setErrorMessage("You are not authorized to use this functionality");

    String errorResponse = objectMapper.writeValueAsString(error);

    this.mockMvc.perform(MockMvcRequestBuilders.post(URL_BOOKING)
            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
            .content(requestBooking)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isForbidden())
        .andExpect(MockMvcResultMatchers.content().string(errorResponse))
        .andDo(MockMvcResultHandlers.print());
  }

}