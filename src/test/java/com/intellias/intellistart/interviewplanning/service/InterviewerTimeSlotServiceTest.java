package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.*;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InterviewerTimeSlotServiceTest {

  private static final int INTERVIEW_DURATION = 90;

  private static final LocalTime WORKING_HOUR_FROM = LocalTime.of(8, 0);
  private static final LocalTime WORKING_HOUR_TO = LocalTime.of(22, 0);

  private static final String EMAIL = "test@.com";

  private static InterviewerTimeSlot TIME_SLOT;


  private static final User USER = new User(1L, EMAIL, UserRole.INTERVIEWER);

  @Mock
  private InterviewerTimeSlotRepository timeSlotRepository;

  @Mock
  private UserRepository userRepository;

  @Captor
  private ArgumentCaptor<InterviewerTimeSlot> timeSlotArgumentCaptor;

  private InterviewerTimeSlotService timeSlotService;

  @BeforeEach
  public void setUp() {
    timeSlotService = new InterviewerTimeSlotService(INTERVIEW_DURATION, WORKING_HOUR_FROM,
        WORKING_HOUR_TO, userRepository, timeSlotRepository);

    TIME_SLOT = InterviewerTimeSlot.builder()
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(15)
        .build();
  }

  @Test
  public void createSlot_Should_SuccessfullyCreateSlotAndSave() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    timeSlotService.createSlot(EMAIL, TIME_SLOT);
    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualTimeSlot = timeSlotArgumentCaptor.getValue();

    assertEquals(TIME_SLOT.getFrom(), actualTimeSlot.getFrom());
    assertEquals(TIME_SLOT.getTo(), actualTimeSlot.getTo());
    assertEquals(TIME_SLOT.getWeekNum(), actualTimeSlot.getWeekNum());
    assertEquals(TIME_SLOT.getDayOfWeek(), actualTimeSlot.getDayOfWeek());
    assertEquals(TimeSlotStatus.NEW, actualTimeSlot.getStatus());
    assertEquals(USER, actualTimeSlot.getUser());
  }

  @Test
  public void createSlot_When_ToIsNotPresented_Should_CreateSlotWithToEqualInterviewDuration() {
    TIME_SLOT.setTo(null);
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    timeSlotService.createSlot(EMAIL, TIME_SLOT);
    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualTimeSlot = timeSlotArgumentCaptor.getValue();

    assertEquals(TIME_SLOT.getFrom(), actualTimeSlot.getFrom());
    assertEquals(TIME_SLOT.getFrom().plusMinutes(INTERVIEW_DURATION), actualTimeSlot.getTo());
    assertEquals(TIME_SLOT.getWeekNum(), actualTimeSlot.getWeekNum());
    assertEquals(TIME_SLOT.getDayOfWeek(), actualTimeSlot.getDayOfWeek());
    assertEquals(TimeSlotStatus.NEW, actualTimeSlot.getStatus());
    assertEquals(USER, actualTimeSlot.getUser());
  }

  @Test
  public void createSlot_When_FromIsNotRounded_Should_ThrowException() {
    TIME_SLOT.setFrom(LocalTime.of(10, 2));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    assertEquals("Minutes should be rounded to :00 or :30", exception.getMessage());
  }

  @Test
  public void createSlot_When_ToIsNotRounded_Should_ThrowException() {
    TIME_SLOT.setTo(LocalTime.of(11, 32));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    assertEquals("Minutes should be rounded to :00 or :30", exception.getMessage());
  }

  @Test
  public void createSlot_When_FromIsAfterTo_Should_ThrowException() {
    TIME_SLOT.setFrom(LocalTime.of(12, 30));
    TIME_SLOT.setTo(LocalTime.of(9, 30));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    assertEquals("from is after to", exception.getMessage());
  }

  @Test
  public void createSlot_When_DiffBetweenFromAndToIsShorterDuration_Should_ThrowException() {
    TIME_SLOT.setFrom(LocalTime.of(11, 0));
    TIME_SLOT.setTo(LocalTime.of(12, 0));

    String expectedErrorMessage = "range cannot be shorter interview duration "
        + INTERVIEW_DURATION
        + " min.";

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  public void createSlot_When_ToViolatesWorkingHours_Should_ThrowException() {
    TIME_SLOT.setTo(WORKING_HOUR_TO.plusHours(1));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    String expectedErrorMessage =
        "Range violates working hours [" + WORKING_HOUR_FROM + " - " + WORKING_HOUR_TO + "]";

    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  public void createSlot_When_FromViolatesWorkingHours_Should_ThrowException() {
    TIME_SLOT.setFrom(WORKING_HOUR_FROM.minusHours(1));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class, () ->
            timeSlotService.createSlot(EMAIL, TIME_SLOT));

    String expectedErrorMessage =
        "Range violates working hours [" + WORKING_HOUR_FROM + " - " + WORKING_HOUR_TO + "]";

    assertEquals(expectedErrorMessage, exception.getMessage());
  }

  @Test
  public void createSlot_When_UserByEmailNotFound_Should_ThrowException() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(InterviewerNotFoundException.class,
        () -> timeSlotService.createSlot(EMAIL, TIME_SLOT));
  }

  @Test
  public void createSlot_When_TimeSlotForUserAlreadyExist_Should_ThrowException() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, TIME_SLOT.getWeekNum()))
        .thenReturn((Collections.singletonList(TIME_SLOT)));

    assertThrows(SlotIsOverlappingException.class,
        () -> timeSlotService.createSlot(EMAIL, TIME_SLOT));
  }
}
