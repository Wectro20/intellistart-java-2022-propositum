package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekNumberNotAcceptableException;
import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotDto;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotRequestForm;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InterviewerTimeSlotServiceTest {

  private static final int INTERVIEW_DURATION = 90;

  private static final String EMAIL = "test@.com";

  private static InterviewerTimeSlot TIME_SLOT;

  private static InterviewerTimeSlotRequestForm TIME_SLOT_REQUEST_FORM;

  private static InterviewerTimeSlot TIME_SLOT_FOR_UPDATE;

  private static final Long SLOT_ID = 1L;


  private static final User USER = new User(1L, EMAIL, UserRole.INTERVIEWER);

  @Mock
  private InterviewerTimeSlotRepository timeSlotRepository;

  @Mock
  private BookingLimitRepository bookingLimitRepository;

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
    timeSlotService = new InterviewerTimeSlotService(INTERVIEW_DURATION, bookingLimitRepository,
        userRepository, timeSlotRepository, weekService, timeSlotValidationService);

    TIME_SLOT = InterviewerTimeSlot.builder()
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .dayOfWeek(InterviewDayOfWeek.MONDAY)
        .weekNum(15)
        .bookings(Collections.emptyList())
        .user(USER)
        .build();

    TIME_SLOT_REQUEST_FORM = InterviewerTimeSlotRequestForm.builder()
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .dayOfWeek(InterviewDayOfWeek.MONDAY)
        .weekNum(15)
        .build();

    TIME_SLOT_FOR_UPDATE = InterviewerTimeSlot.builder()
        .from(LocalTime.of(12, 0))
        .to(LocalTime.of(13, 30))
        .dayOfWeek(InterviewDayOfWeek.TUESDAY)
        .weekNum(15)
        .bookings(Collections.emptyList())
        .user(USER)
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
  public void updateSlot_Should_SuccessfullyUpdateSlot() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findById(SLOT_ID))
        .thenReturn(Optional.of(TIME_SLOT_FOR_UPDATE));
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(14));
    timeSlotService.updateSlot(EMAIL, SLOT_ID, TIME_SLOT_REQUEST_FORM, USER);

    Mockito.verify(timeSlotRepository, Mockito.times(1))
        .save(timeSlotArgumentCaptor.capture());

    InterviewerTimeSlot actualTimeSlot = timeSlotArgumentCaptor.getValue();

    assertEquals(TIME_SLOT.getFrom(), actualTimeSlot.getFrom());
    assertEquals(TIME_SLOT.getTo(), actualTimeSlot.getTo());
    assertEquals(TIME_SLOT.getWeekNum(), actualTimeSlot.getWeekNum());
    assertEquals(TIME_SLOT.getDayOfWeek(), actualTimeSlot.getDayOfWeek());
    assertEquals(TIME_SLOT.getStatus(), actualTimeSlot.getStatus());
    assertEquals(USER, actualTimeSlot.getUser());
  }


  @Test
  public void updateSlot_WhenSlotNotFound_Should_ThrowException() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Optional.empty());
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(14));

    assertThrows(SlotNotFoundException.class,
        () -> timeSlotService.updateSlot(EMAIL, SLOT_ID, TIME_SLOT_REQUEST_FORM, USER));
  }


  @Test
  public void updateSlot_WhenWeekNumberNotAcceptable_Should_ThrowException() {
    TIME_SLOT_REQUEST_FORM.setWeekNum(14);
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(weekService.getCurrentWeekNumber()).thenReturn(new WeekNumber(14));
    assertThrows(WeekNumberNotAcceptableException.class,
        () -> timeSlotService.updateSlot(EMAIL, SLOT_ID, TIME_SLOT_REQUEST_FORM, USER));
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

    InterviewerTimeSlotDto expectedSlot = InterviewerTimeSlotDto.builder()
        .id(TIME_SLOT.getId())
        .from(TIME_SLOT.getFrom())
        .to(TIME_SLOT.getTo())
        .dayOfWeek(TIME_SLOT.getDayOfWeek())
        .weekNum(TIME_SLOT.getWeekNum())
        .bookings(Collections.emptyList())
        .build();

    List<InterviewerTimeSlotDto> actualTimeSlots = timeSlotService.getTimeSlots(EMAIL, 15);

    assertEquals(Collections.singletonList(expectedSlot), actualTimeSlots);
  }

  @Test
  public void set_BookingLimit_For_Interviewer() {
    Mockito.when(weekService.getNextWeekNumber()).thenReturn(new WeekNumber(15));
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    Mockito.when(bookingLimitRepository.findByUserAndWeekNum(USER, 15))
        .thenReturn(Optional.empty());

    BookingLimit bookingLimit = BookingLimit.builder()
        .user(USER)
        .bookingLimit(10)
        .weekNum(15)
        .build();

    Mockito.doReturn(bookingLimit).when(bookingLimitRepository).save(ArgumentMatchers.any());

    BookingLimit actualLimit = timeSlotService.setBookingLimit(USER.getEmail(), 10);

    assertNotNull(actualLimit);
    Assertions.assertEquals(USER, bookingLimit.getUser());
  }
}
