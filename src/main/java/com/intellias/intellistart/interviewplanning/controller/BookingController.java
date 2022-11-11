package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.service.BookingService;
import com.intellias.intellistart.interviewplanning.service.dto.BookingChangeRequestForm;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Booking interviews.
 */

@RestController
@AllArgsConstructor
public class BookingController {

  private BookingService bookingService;

  /**
   * Endpoint to create booking for interview.
   *
   * @param bookingDto request body of booking.
   *
   * @return saved booking.
   */

  @PostMapping("/bookings")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('COORDINATOR')")
  public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }

  /**
  * Endpoint to update booking for interview.
  *
  * @param bookingId id of booking
  * @param bookingChangeRequestForm request body of booking.
  *
  * @return updated booking.
  */

  @PostMapping("/bookings/{bookingId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasAuthority('COORDINATOR')")
  public BookingDto updateBooking(@RequestBody BookingChangeRequestForm bookingChangeRequestForm,
      @PathVariable Long bookingId) {
    return bookingService.updateBooking(bookingId, bookingChangeRequestForm);
  }

}