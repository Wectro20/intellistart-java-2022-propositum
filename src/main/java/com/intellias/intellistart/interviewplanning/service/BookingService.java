package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.INVALID_BOUNDARIES;
import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.SUBJECT_DESCRIPTION_NOT_VALID;

import com.intellias.intellistart.interviewplanning.exceptions.BookingIsAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.exceptions.BookingNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingChangeRequestForm;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import java.time.LocalTime;
import java.util.List;
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



  /**
   * Create booking for interview.
   *
   * @param bookingId id of the booking which needs to update.
   * @param bookingChangeRequestForm the body of the booking which needs to update.
   *
   * @return saved booking
   */
  public BookingDto updateBooking(Long bookingId,
      BookingChangeRequestForm bookingChangeRequestForm) {
    log.info("Update booking with details {}", bookingChangeRequestForm);

    Booking outdatedBooking = bookingRepository
        .findById(bookingId)
        .orElseThrow(BookingNotFoundException::new);

    InterviewerTimeSlot interviewerTimeSlot = outdatedBooking.getInterviewerTimeSlot();
    CandidateTimeSlot candidateTimeSlot = outdatedBooking.getCandidateTimeSlot();

    if (isTimeNotInInterviewerSlotRange(interviewerTimeSlot,
        bookingChangeRequestForm.getStartTime())
        || isTimeNotInInterviewerSlotRange(interviewerTimeSlot,
        bookingChangeRequestForm.getEndTime())) {

      log.error("From/to does not fit into interviewer slot range {} - {}",
          interviewerTimeSlot.getFrom(), interviewerTimeSlot.getTo());

      throw new ValidationException("from/to does not fit into interviewer time slot",
          INVALID_BOUNDARIES);
    } else if (isTimeNotInCandidateSlotRange(candidateTimeSlot,
        bookingChangeRequestForm.getStartTime())
        || isTimeNotInCandidateSlotRange(candidateTimeSlot,
        bookingChangeRequestForm.getEndTime())) {

      log.error("From/to does not fit into candidate slot range {} - {}",
          candidateTimeSlot.getFrom(), candidateTimeSlot.getTo());

      throw new ValidationException("from/to does not fit into bounded candidate time slot",
          INVALID_BOUNDARIES);
    }

    timeSlotValidationService
        .validateBookingTimeSlotBoundaries(bookingChangeRequestForm.getStartTime(),
          bookingChangeRequestForm.getEndTime());

    validateBookingInInterviewerTimeSlot(bookingChangeRequestForm,
        outdatedBooking);

    validateBookingInCandidateTimeSlot(bookingChangeRequestForm,
        outdatedBooking);

    if (bookingChangeRequestForm.getSubject().length() > subjectLength) {
      throw new ValidationException("subject max length is " + subjectLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }

    if (bookingChangeRequestForm.getDescription().length() > descriptionLength) {
      throw new ValidationException(
          "description max length is " + descriptionLength + " chars",
          SUBJECT_DESCRIPTION_NOT_VALID);
    }

    outdatedBooking.setStartTime(bookingChangeRequestForm.getStartTime());
    outdatedBooking.setEndTime(bookingChangeRequestForm.getEndTime());
    outdatedBooking.setSubject(bookingChangeRequestForm.getSubject());
    outdatedBooking.setDescription(bookingChangeRequestForm.getDescription());

    bookingRepository.save(outdatedBooking);

    log.info("Booking successfully updated with id {}", bookingId);

    return BookingDto.builder()
        .id(bookingId)
        .startTime(bookingChangeRequestForm.getStartTime())
        .endTime(bookingChangeRequestForm.getEndTime())
        .subject(bookingChangeRequestForm.getSubject())
        .description(bookingChangeRequestForm.getDescription())
        .candidateTimeSlotId(outdatedBooking.getCandidateTimeSlot().getId())
        .interviewerTimeSlotId(outdatedBooking.getInterviewerTimeSlot().getId())
        .build();
  }

  private void validateBookingInInterviewerTimeSlot(
      BookingChangeRequestForm bookingChangeRequestForm,
      Booking outdatedBooking) {


    List<Booking> bookings = outdatedBooking
        .getCandidateTimeSlot()
        .getBookings();

    bookings.remove(outdatedBooking);

    if (bookings.stream()
        .anyMatch(booking -> validateBookingTime(booking, bookingChangeRequestForm))) {
      throw new ValidationException(
          "already exist booking that intersect given from/to in candidate",
             INVALID_BOUNDARIES);
    }
  }

  private void validateBookingInCandidateTimeSlot(
      BookingChangeRequestForm bookingChangeRequestForm,
      Booking outdatedBooking) {

    List<Booking> bookings = outdatedBooking
        .getInterviewerTimeSlot()
        .getBookings();

    bookings.remove(outdatedBooking);

    if (bookings.stream()
        .anyMatch(booking -> validateBookingTime(booking, bookingChangeRequestForm))) {
      throw new ValidationException(
          "already exist booking that intersect given from/to in interviewer",
          INVALID_BOUNDARIES);
    }
  }

  private boolean validateBookingTime(Booking booking,
      BookingChangeRequestForm bookingChangeRequestForm) {
    LocalTime startTarget = bookingChangeRequestForm.getStartTime();
    LocalTime endTarget = bookingChangeRequestForm.getEndTime();

    return (booking.getStartTime().isBefore(startTarget)
        && booking.getEndTime().isAfter(startTarget))
        || (booking.getStartTime().isBefore(endTarget)
        && booking.getEndTime().isAfter(endTarget));
  }

  private boolean isTimeNotInCandidateSlotRange(CandidateTimeSlot timeSlot, LocalTime target) {
    return target.isBefore(timeSlot.getFrom()) || target.isAfter(timeSlot.getTo());
  }
}




