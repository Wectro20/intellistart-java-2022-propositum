package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.service.BookingService;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
    return bookingService.createBooking(bookingDto);
  }
}