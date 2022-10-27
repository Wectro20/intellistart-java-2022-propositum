package com.intellias.intellistart.interviewplanning.service;

import static java.time.temporal.ChronoUnit.MINUTES;

import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekNumberNotAcceptableException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotDto;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for interviewer.
 */
@Service
@AllArgsConstructor
public class InterviewerTimeSlotService {

  @Value("${interview.duration_minutes}")
  private Integer interviewDuration;

  private UserRepository userRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  private GetWeekNumberService weekService;
  private TimeSlotValidationService timeSlotValidationService;

  /**
   * Create time slot for Interviewer.
   *
   * @param interviewerEmail    for which create slot
   * @param interviewerTimeSlot time slot which needs to create
   * @return saved interviewer time slot
   */
  public InterviewerTimeSlot createSlot(String interviewerEmail,
      InterviewerTimeSlot interviewerTimeSlot) {

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    InterviewDayOfWeek dayOfWeek = interviewerTimeSlot.getDayOfWeek();
    Integer weekNum = interviewerTimeSlot.getWeekNum();

    validateWeekNumber(weekNum);

    if (to != null) {
      timeSlotValidationService.validateTimeSlotBoundaries(from, to);
    }

    User user = userRepository.findByEmail(interviewerEmail)
        .orElseThrow(InterviewerNotFoundException::new);

    interviewerTimeSlotRepository.findAllByUserAndWeekNum(user, interviewerTimeSlot.getWeekNum())
        .stream()
        .filter(slot -> slot.getFrom().equals(from) && slot.getTo().equals(to))
        .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
        .findAny()
        .ifPresent(slot -> {
          throw new SlotIsOverlappingException(slot.getId());
        });

    interviewerTimeSlot.setUser(user);
    interviewerTimeSlot.setStatus(TimeSlotStatus.NEW);

    if (interviewerTimeSlot.getTo() == null) {
      interviewerTimeSlot.setTo(from.plusMinutes(interviewDuration));
    }

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }
  /**
   * Get time slot for Interviewer.
   *
   * @param interviewerEmail    for which get slot
   * @param weekNum for which week number get slots
   * @return found time slots
   */

  public List<InterviewerTimeSlotDto> getTimeSlots(String interviewerEmail, int weekNum) {
    int nextWeekNum = weekService.getNextWeekNumber().getWeekNum();
    int currentWeekNum = weekService.getCurrentWeekNumber().getWeekNum();

    if (weekNum != currentWeekNum && weekNum != nextWeekNum) {
      throw new WeekNumberNotAcceptableException(Arrays.asList(currentWeekNum, nextWeekNum));
    }

    User user = userRepository.findByEmail(interviewerEmail)
        .orElseThrow(InterviewerNotFoundException::new);

    return interviewerTimeSlotRepository.findAllByUserAndWeekNum(user, weekNum).stream()
        .map(slot -> InterviewerTimeSlotDto.builder()
            .id(slot.getId())
            .from(slot.getFrom())
            .to(slot.getTo())
            .dayOfWeek(slot.getDayOfWeek())
            .weekNum(slot.getWeekNum())
            .bookings(slot.getBookings().stream()
                .map(InterviewerTimeSlotService::buildBookingDto)
                .collect(Collectors.toList()))
                .build())
        .collect(Collectors.toList());
  }

  /**
   * Convert Booking entity to BookingDTO.
   *
   * @param booking Booking entity

   * @return BookingDTO
   */
  public static BookingDto buildBookingDto(Booking booking) {
    return BookingDto.builder()
        .id(booking.getId())
        .startTime(booking.getStartTime())
        .endTime(booking.getEndTime())
        .interviewerTimeSlotId(booking.getInterviewerTimeSlot().getId())
        .candidateTimeSlotId(booking.getCandidateTimeSlot().getId())
        .subject(booking.getSubject())
        .description(booking.getDescription())
        .build();
  }


  /**
   * Update time slot for Interviewer.
   *
   * @param interviewerEmail    for which update slot
   * @param slotId              for which update
   * @param interviewerTimeSlot request body of time slot
   * @return updated interviewer time slot
   */

  public InterviewerTimeSlot updateSlot(String interviewerEmail, Long slotId,
      InterviewerTimeSlot interviewerTimeSlot) {

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    InterviewDayOfWeek dayOfWeek = interviewerTimeSlot.getDayOfWeek();

    if (to != null && from != null && dayOfWeek != null) {
      timeSlotValidationService.validateTimeSlotBoundaries(from, to);
    }

    User user = userRepository.findByEmail(interviewerEmail)
        .orElseThrow(InterviewerNotFoundException::new);

    InterviewerTimeSlot outdatedInterviewerTimeSlot = interviewerTimeSlotRepository.findById(slotId)
        .orElseThrow(SlotNotFoundException::new);

    interviewerTimeSlot.setStatus(outdatedInterviewerTimeSlot.getStatus());
    interviewerTimeSlot.setId(slotId);
    interviewerTimeSlot.setUser(user);
    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  private void validateWeekNumber(Integer weekNum) {
    int nextWeekNum = weekService.getNextWeekNumber().getWeekNum();

    if (!weekNum.equals(nextWeekNum)) {
      throw new WeekNumberNotAcceptableException(Collections.singletonList(nextWeekNum));
    }
  }

}
