package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.INVALID_BOUNDARIES;
import static com.intellias.intellistart.interviewplanning.exceptions.ApplicationExceptionHandler.SUBJECT_DESCRIPTION_NOT_VALID;

import com.intellias.intellistart.interviewplanning.exceptions.BookingIsAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.exceptions.ValidationException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
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

}
