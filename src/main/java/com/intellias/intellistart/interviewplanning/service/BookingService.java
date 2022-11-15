package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.INVALID_BOUNDARIES;
import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.MAX_COUNT_OF_BOOKING;
import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.SUBJECT_DESCRIPTION_NOT_VALID;

import com.intellias.intellistart.interviewplanning.exceptions.BookingIsAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.exceptions.BookingNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * It is service for finally booking an interview.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BookingService {

  @Value("${booking.subject_length}")
  private Integer subjectLength;

  @Value("${booking.description_length}")
  private Integer descriptionLength;

  private BookingRepository bookingRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private CandidateTimeSlotRepository candidateTimeSlotRepository;
  private TimeSlotValidationService timeSlotValidationService;
  private BookingLimitRepository bookingLimitRepository;
  private GetWeekNumberService weekNumberService;

  /**
   * Create booking for interview.
   *
   * @param bookingDto booking which needs to create.
   * @return saved booking
   */
  public BookingDto createBooking(BookingDto bookingDto) {
    log.info("Create booking with details {}", bookingDto);

    InterviewerTimeSlot interviewerTimeSlot = interviewerTimeSlotRepository
        .findById(bookingDto.getInterviewerTimeSlotId())
        .orElseThrow(SlotNotFoundException::new);

    Optional<BookingLimit> interviewerLimit = bookingLimitRepository.findByUser(
        interviewerTimeSlot.getUser());

    List<Booking> currentInterviewerBookings = allInterviewerBookingsForCurrentWeek(
        interviewerTimeSlot);

    if (interviewerLimit.isPresent() && !currentInterviewerBookings.isEmpty()
        && interviewerLimit.get().getBookingLimit() == currentInterviewerBookings.size()) {
      log.error("Cannot set more booking for interviewer than max booking limit {}",
          interviewerLimit.get().getBookingLimit());

      throw new ValidationException(
          String.format("cannot set more bookings for interviewer than max limit:%d",
              interviewerLimit.get().getBookingLimit()), MAX_COUNT_OF_BOOKING);
    }

    CandidateTimeSlot candidateTimeSlot = candidateTimeSlotRepository
        .findById(bookingDto.getCandidateTimeSlotId())
        .orElseThrow(SlotNotFoundException::new);

    if (isBookingWithRangePresented(interviewerTimeSlot.getBookings(), bookingDto)) {
      log.error("Interviewer time slot with id {} already has booking with from/to",
          interviewerTimeSlot.getId());

      throw new BookingIsAlreadyExistsException("interviewer slot");
    }

    if (isBookingWithRangePresented(candidateTimeSlot.getBookings(), bookingDto)) {
      log.error("Candidate time slot with id {} already has booking with from/to",
          candidateTimeSlot.getId());

      throw new BookingIsAlreadyExistsException("candidate slot");
    }

    timeSlotValidationService.validateBookingTimeSlotBoundaries(bookingDto.getStartTime(),
        bookingDto.getEndTime());

    if (isTimeNotInInterviewerSlotRange(interviewerTimeSlot, bookingDto.getStartTime())
        || isTimeNotInInterviewerSlotRange(interviewerTimeSlot, bookingDto.getEndTime())) {
      log.error("From/to does not fit into interviewer slot range {} - {}",
          interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

      throw new ValidationException("from/to does not fit into interviewer time slot",
          INVALID_BOUNDARIES);
    }

    if (bookingDto.getSubject().length() > subjectLength) {
      throw new ValidationException("subject max length is " + subjectLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }

    if (bookingDto.getDescription().length() > descriptionLength) {
      throw new ValidationException(
          "description max length is " + descriptionLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }

    Booking booking = Booking.builder()
        .startTime(bookingDto.getStartTime())
        .endTime(bookingDto.getEndTime())
        .candidateTimeSlot(candidateTimeSlot)
        .interviewerTimeSlot(interviewerTimeSlot)
        .subject(bookingDto.getSubject())
        .description(bookingDto.getDescription())
        .build();

    bookingRepository.save(booking);

    bookingDto.setId(booking.getId());

    log.info("Booking successfully saved with id {}", booking.getId());

    return bookingDto;
  }

  private boolean isTimeNotInInterviewerSlotRange(InterviewerTimeSlot timeSlot, LocalTime target) {
    return target.isBefore(timeSlot.getFrom()) || target.isAfter(timeSlot.getTo());
  }

  private boolean isBookingWithRangePresented(List<Booking> bookings, BookingDto bookingDto) {
    return bookings.stream()
        .anyMatch(booking -> booking.getStartTime().equals(bookingDto.getStartTime())
            && booking.getEndTime().equals(bookingDto.getEndTime()));
  }

  private List<Booking> allInterviewerBookingsForCurrentWeek(
      InterviewerTimeSlot interviewerTimeSlot) {
    return
        bookingRepository.findAllByInterviewerTimeSlotUser(
                interviewerTimeSlot.getUser())
            .stream()
            .filter(
                timeSlot -> timeSlot.getInterviewerTimeSlot().getWeekNum().equals(weekNumberService
                    .getCurrentWeekNumber().getWeekNum())).collect(Collectors.toList());
  }

  /**
   * Create booking for interview.
   *
   * @param bookingId id of the booking which needs to update.
   * @param bookingDto the body of the booking which needs to update.
   *
   * @return saved booking
   */
  public BookingDto updateBooking(Long bookingId,
      BookingDto bookingDto) {
    log.info("Update booking with details {}", bookingDto);

    InterviewerTimeSlot interviewerTimeSlot = interviewerTimeSlotRepository
        .findById(bookingDto.getInterviewerTimeSlotId())
        .orElseThrow(SlotNotFoundException::new);

    Optional<BookingLimit> interviewerLimit = bookingLimitRepository.findByUser(
        interviewerTimeSlot.getUser());

    List<Booking> currentInterviewerBookings = allInterviewerBookingsForCurrentWeek(
        interviewerTimeSlot);

    if (interviewerLimit.isPresent() && !currentInterviewerBookings.isEmpty()
        && interviewerLimit.get().getBookingLimit() == currentInterviewerBookings.size()) {
      log.error("Cannot set more booking for interviewer than max booking limit {}",
          interviewerLimit.get().getBookingLimit());

      throw new ValidationException(
          String.format("cannot set more bookings for interviewer than max limit:%d",
              interviewerLimit.get().getBookingLimit()), MAX_COUNT_OF_BOOKING);
    }

    Booking outdatedBooking = bookingRepository
        .findById(bookingId)
        .orElseThrow(BookingNotFoundException::new);

    CandidateTimeSlot candidateTimeSlot = candidateTimeSlotRepository
        .findById(bookingDto.getCandidateTimeSlotId())
        .orElseThrow(SlotNotFoundException::new);

    timeSlotValidationService
        .validateBookingTimeBoundariesInTimeSlots(
            bookingDto,
            interviewerTimeSlot,
            candidateTimeSlot);

    timeSlotValidationService
        .validateBookingTimeSlotBoundaries(bookingDto.getStartTime(),
          bookingDto.getEndTime());

    validateBookingInInterviewerTimeSlot(bookingDto, candidateTimeSlot, interviewerTimeSlot,
        outdatedBooking);

    validateBookingInCandidateTimeSlot(bookingDto, candidateTimeSlot, outdatedBooking);

    validateDescriptionAndSubject(bookingDto.getDescription(), bookingDto.getSubject());


    outdatedBooking.setCandidateTimeSlot(candidateTimeSlot);
    outdatedBooking.setInterviewerTimeSlot(interviewerTimeSlot);
    outdatedBooking.setStartTime(bookingDto.getStartTime());
    outdatedBooking.setEndTime(bookingDto.getEndTime());
    outdatedBooking.setSubject(bookingDto.getSubject());
    outdatedBooking.setDescription(bookingDto.getDescription());
    bookingRepository.save(outdatedBooking);
    log.info("Booking successfully updated with id {}", bookingId);


    bookingDto.setId(bookingId);
    return bookingDto;
  }

  private void validateBookingInCandidateTimeSlot(
      BookingDto bookingDto,
      CandidateTimeSlot candidateTimeSlot,
      Booking outdatedBooking) {


    List<Booking> bookings = candidateTimeSlot.getBookings();

    bookings.remove(outdatedBooking);

    if (bookings.stream()
        .filter(booking -> booking.getCandidateTimeSlot().getDate()
            .equals(candidateTimeSlot.getDate()))
        .anyMatch(booking -> validateBookingTime(booking, bookingDto))) {
      throw new ValidationException(
          "already exist booking that intersect given from/to in candidate",
             INVALID_BOUNDARIES);
    }
  }

  private void validateBookingInInterviewerTimeSlot(
      BookingDto bookingDto,
      CandidateTimeSlot candidateTimeSlot,
      InterviewerTimeSlot interviewerTimeSlot,
      Booking outdatedBooking) {

    List<Booking> bookings = interviewerTimeSlot
        .getBookings();

    bookings.remove(outdatedBooking);

    if (bookings.stream()
        .filter(booking -> booking.getCandidateTimeSlot().getDate()
            .equals(candidateTimeSlot.getDate()))
        .anyMatch(booking -> validateBookingTime(booking, bookingDto))) {
      throw new ValidationException(
          "already exist booking that intersect given from/to in interviewer",
          INVALID_BOUNDARIES);
    }
  }

  private boolean validateBookingTime(Booking booking,
      BookingDto bookingDto) {
    LocalTime startTarget = bookingDto.getStartTime();
    LocalTime endTarget = bookingDto.getEndTime();

    return ((booking.getStartTime().isBefore(startTarget)
        || booking.getStartTime().equals(startTarget))
        && (booking.getEndTime().isAfter(startTarget)
        || booking.getEndTime().equals(startTarget)))
        || ((booking.getStartTime().isBefore(endTarget)
        || (booking.getStartTime().equals(endTarget)))
        && (booking.getEndTime().isAfter(endTarget)
        || booking.getEndTime().equals(endTarget)));
  }

  private void validateDescriptionAndSubject(String description, String subject) {
    if (subject.length() > subjectLength) {
      throw new ValidationException("subject max length is " + subjectLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }

    if (description.length() > descriptionLength) {
      throw new ValidationException(
          "description max length is " + descriptionLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }
  }
}




