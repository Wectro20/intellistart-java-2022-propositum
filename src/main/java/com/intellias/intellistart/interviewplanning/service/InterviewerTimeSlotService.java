package com.intellias.intellistart.interviewplanning.service;


import com.intellias.intellistart.interviewplanning.exceptions.InterviewerNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidLimitException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.UserNotFoundException;
import com.intellias.intellistart.interviewplanning.exceptions.WeekNumberNotAcceptableException;
import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.BookingLimit;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotDto;
import com.intellias.intellistart.interviewplanning.service.dto.InterviewerTimeSlotRequestForm;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for interviewer.
 */
@Service
@AllArgsConstructor
public class InterviewerTimeSlotService {

  @Value("${interview.duration_minutes}")
  private Integer interviewDuration;

  @Autowired
  private BookingLimitRepository bookingLimitRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;
  @Autowired
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
   * @param interviewerTimeSlotRequestForm request body of time slot
   * @return updated interviewer time slot
   *
   * @throws SlotNotFoundException interviewer slot with given slotId not found
   * @throws InterviewerNotFoundException interviewer with given email not found
   * @throws WeekNumberNotAcceptableException incorrect week number
   */

  public InterviewerTimeSlot updateSlot(String interviewerEmail, Long slotId,
      InterviewerTimeSlotRequestForm interviewerTimeSlotRequestForm, User authUser) {
    InterviewerTimeSlot interviewerTimeSlot =
        interviewerTimeSlotRequestForm.getInterviewerTimeSlot();

    LocalTime from = interviewerTimeSlot.getFrom();
    LocalTime to = interviewerTimeSlot.getTo();
    InterviewDayOfWeek dayOfWeek = interviewerTimeSlot.getDayOfWeek();


    final User user = userRepository.findByEmail(interviewerEmail)
            .orElseThrow(InterviewerNotFoundException::new);

    validateWeekNumberForUpdate(interviewerTimeSlot.getWeekNum(),
        authUser.getRole());


    if (to != null && from != null && dayOfWeek != null) {
      timeSlotValidationService.validateTimeSlotBoundaries(from, to);
    }

    InterviewerTimeSlot outdatedInterviewerTimeSlot = interviewerTimeSlotRepository.findById(slotId)
        .orElseThrow(SlotNotFoundException::new);


    if (!interviewerEmail.equals(outdatedInterviewerTimeSlot.getUser().getEmail())) {
      throw new SlotNotFoundException();
    }

    interviewerTimeSlotRepository.findAllByUserAndWeekNum(user, interviewerTimeSlot.getWeekNum())
        .stream()
        .filter(slot -> !slot.getId().equals(slotId))
        .filter(slot -> slot.getDayOfWeek().equals(dayOfWeek))
        .filter(slot ->
            ((slot.getFrom().minusMinutes(30L).isBefore(from)
                && slot.getTo().isAfter(from))
              || (slot.getFrom().isBefore(to)
                && slot.getTo().plusMinutes(30L).isAfter(to))
              || (slot.getFrom().equals(from) && slot.getTo().equals(to))))
        .findAny()
        .ifPresent(slot -> {
          throw new SlotIsOverlappingException(slot.getId());
        });



    interviewerTimeSlot.setStatus(outdatedInterviewerTimeSlot.getStatus());
    interviewerTimeSlot.setId(slotId);
    interviewerTimeSlot.setUser(user);

    return interviewerTimeSlotRepository.save(interviewerTimeSlot);
  }

  /**
   * Get time slot for Interviewer.
   *
   * @param interviewerEmail for getting interviewer id
   * @param limitValue for setting booking limit
   * @return booking limit
   */
  public BookingLimit setBookingLimit(String interviewerEmail, Integer limitValue) {
    if (limitValue < 0) {
      throw new InvalidLimitException("Invalid limit");
    }
    int weekNum = weekService.getNextWeekNumber().getWeekNum();

    User interviewer = userRepository.findByEmail(interviewerEmail)
        .orElseThrow(() -> new UserNotFoundException("Invalid User email: " + interviewerEmail));

    Optional<BookingLimit> bookingLimitOptional = bookingLimitRepository
        .findByUserAndWeekNum(interviewer, weekNum);

    BookingLimit bookingLimit;
    if (bookingLimitOptional.isEmpty()) {
      bookingLimit = BookingLimit.builder()
          .bookingLimit(limitValue)
          .weekNum(weekNum)
          .user(interviewer)
          .build();
    } else {
      bookingLimit = bookingLimitOptional.get();
      bookingLimit.setBookingLimit(limitValue);
    }
    return bookingLimitRepository.save(bookingLimit);
  }

  private void validateWeekNumberForUpdate(Integer weekNum, User.UserRole userRole) {
    int nextWeekNum = weekService.getNextWeekNumber().getWeekNum();
    int currentWeekNum = weekService.getCurrentWeekNumber().getWeekNum();

    if (userRole == User.UserRole.INTERVIEWER && !weekNum.equals(nextWeekNum)) {
      throw new WeekNumberNotAcceptableException(Collections.singletonList(nextWeekNum));
    } else if (userRole == UserRole.COORDINATOR
        && (!weekNum.equals(currentWeekNum) && !weekNum.equals(nextWeekNum))) {
      throw new WeekNumberNotAcceptableException(Collections.singletonList(nextWeekNum));
    }
  }

  private void validateWeekNumber(Integer weekNum) {
    int nextWeekNum = weekService.getNextWeekNumber().getWeekNum();

    if (!weekNum.equals(nextWeekNum)) {
      throw new WeekNumberNotAcceptableException(Collections.singletonList(nextWeekNum));
    }
  }

}
