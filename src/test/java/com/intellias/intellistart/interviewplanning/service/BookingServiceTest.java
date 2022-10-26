package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.BookingIsAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.exceptions.BookingValidationException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

  private static final int SUBJECT_LENGTH = 10;
  private static final int DESCRIPTION_LENGTH = 35;
  private static final BookingDto BOOKING_DTO = generateBookingDto();
  private static final InterviewerTimeSlot INTERVIEWER_TIME_SLOT = generateInterviewerSlot();
  private static final CandidateTimeSlot CANDIDATE_TIME_SLOT = generateCandidateSlot();
  private static final Booking BOOKING = generateBooking();

  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Mock
  private CandidateTimeSlotRepository candidateTimeSlotRepository;
  @Mock
  private TimeSlotValidationService timeSlotValidationService;

  @Captor
  private ArgumentCaptor<Booking> bookingArgumentCaptor;

  private BookingService bookingService;

  @BeforeEach
  public void setUp() {
    bookingService = new BookingService(
        SUBJECT_LENGTH, DESCRIPTION_LENGTH, bookingRepository, interviewerTimeSlotRepository,
        candidateTimeSlotRepository, timeSlotValidationService);
  }

  @Test
  public void createBooking_Should_SuccessfullyCreateBookingAndSave() {
    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(CANDIDATE_TIME_SLOT));

    bookingService.createBooking(BOOKING_DTO);

    Mockito.verify(bookingRepository, Mockito.times(1))
        .save(bookingArgumentCaptor.capture());
    Mockito.verify(timeSlotValidationService, Mockito.times(1))
        .validateBookingTimeSlotBoundaries(BOOKING_DTO.getStartTime(), BOOKING_DTO.getEndTime());

    Booking actualBooking = bookingArgumentCaptor.getValue();

    assertEquals(BOOKING.getStartTime(), actualBooking.getStartTime());
    assertEquals(BOOKING.getEndTime(), actualBooking.getEndTime());
    assertEquals(BOOKING.getCandidateTimeSlot(), actualBooking.getCandidateTimeSlot());
    assertEquals(BOOKING.getInterviewerTimeSlot(), actualBooking.getInterviewerTimeSlot());
    assertEquals(BOOKING.getSubject(), actualBooking.getSubject());
    assertEquals(BOOKING.getDescription(), actualBooking.getDescription());
  }

  @Test
  public void createBooking_When_InterviewerSlotNotFound_Should_ThrowException() {
    Mockito.when(interviewerTimeSlotRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(SlotNotFoundException.class,
        () -> bookingService.createBooking(BOOKING_DTO));
  }

  @Test
  public void createBooking_When_CandidateSlotNotFound_Should_ThrowException() {
    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(SlotNotFoundException.class,
        () -> bookingService.createBooking(BOOKING_DTO));
  }

  @Test
  public void createBooking_When_BookingIsExistForInterviewerSlot_Should_ThrowException() {
    InterviewerTimeSlot interviewerTimeSlot = generateInterviewerSlot();
    interviewerTimeSlot.setBookings(Collections.singletonList(BOOKING));

    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(interviewerTimeSlot));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(CANDIDATE_TIME_SLOT));


    BookingIsAlreadyExistsException exception = assertThrows(
        BookingIsAlreadyExistsException.class,
        () -> bookingService.createBooking(BOOKING_DTO));

    assertEquals("booking is already exists for interviewer slot", exception.getMessage());
  }

  @Test
  public void createBooking_When_BookingIsExistForCandidateSlot_Should_ThrowException() {
    CandidateTimeSlot candidateTimeSlot = generateCandidateSlot();
    candidateTimeSlot.setBookings(Collections.singletonList(BOOKING));

    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(candidateTimeSlot));

    BookingIsAlreadyExistsException exception = assertThrows(
        BookingIsAlreadyExistsException.class,
        () -> bookingService.createBooking(BOOKING_DTO));

    assertEquals("booking is already exists for candidate slot", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("outOfRangeTimeValues")
  public void createBooking_When_TimeIsNotInInterviewerSlotRange_Should_ThrowException(LocalTime from, LocalTime to) {
    BookingDto bookingDto = generateBookingDto();
    bookingDto.setStartTime(from);
    bookingDto.setEndTime(to);

    Mockito.when(interviewerTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(INTERVIEWER_TIME_SLOT));
    Mockito.when(candidateTimeSlotRepository.findById(1L))
        .thenReturn(Optional.of(CANDIDATE_TIME_SLOT));

    assertThrows(BookingValidationException.class,
        () -> bookingService.createBooking(bookingDto));

  }

  private static Stream<Arguments> outOfRangeTimeValues() {
    return Stream.of(
        Arguments.of(LocalTime.of(12, 0), LocalTime.of(13, 30)),
        Arguments.of(LocalTime.of(10, 30), LocalTime.of(12, 0)),
        Arguments.of(LocalTime.of(9, 30), LocalTime.of(11, 0))
    );
  }


  private static BookingDto generateBookingDto() {
    return BookingDto.builder()
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 30))
        .candidateTimeSlotId(1L)
        .interviewerTimeSlotId(1L)
        .subject("Interview")
        .description("Interview for candidate")
        .build();
  }

  private static InterviewerTimeSlot generateInterviewerSlot() {
    return InterviewerTimeSlot.builder()
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .dayOfWeek(DayOfWeek.MONDAY)
        .weekNum(15)
        .bookings(Collections.emptyList())
        .build();
  }

  private static CandidateTimeSlot generateCandidateSlot() {
    return CandidateTimeSlot.builder()
        .date(LocalDate.of(2022, 10, 25))
        .from(LocalTime.of(10, 0))
        .to(LocalTime.of(11, 30))
        .bookings(Collections.emptyList())
        .build();
  }

  private static Booking generateBooking() {
    return Booking.builder()
        .id(1L)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 30))
        .candidateTimeSlot(CANDIDATE_TIME_SLOT)
        .interviewerTimeSlot(INTERVIEWER_TIME_SLOT)
        .subject("Interview")
        .description("Interview for candidate")
        .build();
  }

}