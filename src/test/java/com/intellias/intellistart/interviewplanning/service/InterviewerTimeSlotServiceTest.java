package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.*;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekNumberNotAcceptableException;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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

  private static final String EMAIL = "test@.com";

  private static InterviewerTimeSlot TIME_SLOT;


  private static final User USER = new User(1L, EMAIL, UserRole.INTERVIEWER);

  @Mock
  private InterviewerTimeSlotRepository timeSlotRepository;

  @Mock
  private UserRepository userRepository;
  @Mock
  private GetWeekNumberService weekService;
  @Mock
  private TimeSlotValidationService timeSlotValidationService;

  @Captor
  private ArgumentCaptor<InterviewerTimeSlot> timeSlotArgumentCaptor;

  private InterviewerTimeSlotService timeSlotService;

  @BeforeEach
  public void setUp() {
    timeSlotService = new InterviewerTimeSlotService(INTERVIEW_DURATION,
        userRepository, timeSlotRepository, weekService, timeSlotValidationService);

    TIME_SLOT = InterviewerTimeSlot.builder()
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(15)
        .build();

    Mockito.when(weekService.getNextWeekNumber()).thenReturn(new WeekNumber(15));
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

  @Test
  public void createSlot_When_WeekNumberIsNotValid_Should_ThrowException() {
    Mockito.when(weekService.getNextWeekNumber()).thenReturn(new WeekNumber(16));
    assertThrows(WeekNumberNotAcceptableException.class,
        () -> timeSlotService.createSlot(EMAIL, TIME_SLOT));

  }

  @Test
  public void getTimeSlots_When_WeekNumberIsNotCurrent_Should_ThrowException() {
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(15));

    assertThrows(WeekNumberNotAcceptableException.class,
        () -> timeSlotService.getTimeSlots(EMAIL, 14));
  }

  @Test
  public void getTimeSlots_When_WeekNumberIsNotNext_Should_ThrowException() {
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(15));
    Mockito.when(weekService.getNextWeekNumber()).thenReturn(new WeekNumber(16));

    assertThrows(WeekNumberNotAcceptableException.class,
        () -> timeSlotService.getTimeSlots(EMAIL, 18));
  }

  @Test
  public void getTimeSlots_When_UserNotFound_Should_ThrowException() {
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(18));

    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThrows(InterviewerNotFoundException.class,
        () -> timeSlotService.getTimeSlots(EMAIL, 18));
  }

  @Test
  public void getTimeSlots_Should_Success() {
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(15));
    Mockito.when(weekService.getNextWeekNumber()).thenReturn(new WeekNumber(16));
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));

    Mockito.when(timeSlotRepository.findAllByUserAndWeekNum(USER, 15)).thenReturn
        (Collections.singletonList(TIME_SLOT));

    List<InterviewerTimeSlot> actualTimeSlots = timeSlotService.getTimeSlots(EMAIL, 15);

    assertEquals(Collections.singletonList(TIME_SLOT), actualTimeSlots);
  }
}
