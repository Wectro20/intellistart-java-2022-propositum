package com.intellias.intellistart.interviewplanning.service;

import static com.intellias.intellistart.interviewplanning.service.InterviewerTimeSlotService.buildBookingDto;

import com.intellias.intellistart.interviewplanning.model.Booking;
import com.intellias.intellistart.interviewplanning.model.InterviewDayOfWeek;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.service.dto.BookingDto;
import com.intellias.intellistart.interviewplanning.service.dto.DashboardDto;
import com.intellias.intellistart.interviewplanning.service.dto.DashboardDto.DashboardDay;
import com.intellias.intellistart.interviewplanning.service.dto.DashboardDto.TimeSlot;
import java.awt.print.Book;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * It is service to create Dashboard.
 */

@Service
@Slf4j
@AllArgsConstructor
public class DashboardService {

  private GetWeekNumberService weekService;
  private InterviewerTimeSlotRepository interviewerSlotRepository;
  private CandidateTimeSlotRepository candidateSlotRepository;

  /**
   * Get dashboard.
   *
   * @param weekNum for which to get dashboard.
   * @return dashboard
   */
  public DashboardDto getDashboard(int weekNum) {

    List<InterviewerTimeSlot> interviewersSlots = interviewerSlotRepository.findAllByWeekNum(
        weekNum);

    LocalDate startDateOfWeek = weekService.getDateForWeekNumAndDay(
        weekNum, TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    LocalDate endDateOfWeek = weekService.getDateForWeekNumAndDay(
        weekNum, TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

    log.info("Creating dashboard for weekNum {} and start week date {}, end date {}",
        weekNum, startDateOfWeek, endDateOfWeek);

    List<CandidateTimeSlot> candidatesSlots =
        candidateSlotRepository.findAllByDateBetween(startDateOfWeek, endDateOfWeek);

    List<DashboardDay> dashboardDays = new LinkedList<>();

    for (InterviewDayOfWeek day : InterviewDayOfWeek.values()) {
      DashboardDay dashboardDay =
          createDashboardDay(interviewersSlots, candidatesSlots, day, weekNum);
      dashboardDays.add(dashboardDay);
    }

    log.info("Successfully created dashboard for weekNum {}", weekNum);

    return new DashboardDto(dashboardDays);
  }

  private DashboardDay createDashboardDay(List<InterviewerTimeSlot> interviewersSlots,
      List<CandidateTimeSlot> candidatesSlots,
      InterviewDayOfWeek day,
      int weekNum) {
    log.info("Starting to create dashboard for day {} and weekNum {}", day, weekNum);

    DashboardDay dashboardDay = new DashboardDay();

    DayOfWeek dayOfWeek = InterviewDayOfWeek.convertToDayOfWeek(day);
    LocalDate dateOfDayForWeekNum = weekService.getDateForWeekNumAndDay(weekNum, dayOfWeek);

    log.info("For day of week {} and weekNum {} date is {}", dayOfWeek, weekNum,
        dateOfDayForWeekNum);

    dashboardDay.setDayOfWeek(day);
    dashboardDay.setDate(dateOfDayForWeekNum);

    List<InterviewerTimeSlot> interviewersSlotsPerDay = interviewersSlots.stream()
        .filter(slot -> slot.getDayOfWeek().equals(day))
        .collect(Collectors.toList());

    log.info("For dashboard day {} and weekNum {} found {} interviewers slots",
        day, weekNum, interviewersSlotsPerDay.size());

    Map<Long, BookingDto> bookingMap = interviewersSlotsPerDay.stream()
        .map(InterviewerTimeSlot::getBookings)
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(Booking::getId, booking -> buildBookingDto(booking)));

    log.info("For dashboard day {} and weekNum {} found {} bookings",
        day, weekNum, bookingMap.size());

    List<TimeSlot> mappedInterviewersTimeSlots = interviewersSlotsPerDay.stream()
        .map(this::mapInterviewerTimeSlotToTimeSlot)
        .collect(Collectors.toList());

    List<TimeSlot> candidatesSlotsPerDay = candidatesSlots.stream()
        .filter(slot -> slot.getDate().equals(dateOfDayForWeekNum))
        .map(this::mapCandidateTimeSlotToTimeSlot)
        .collect(Collectors.toList());

    log.info("For dashboard day {} and weekNum {} found {} candidates slots",
        day, weekNum, candidatesSlotsPerDay.size());

    dashboardDay.setInterviewerSlots(mappedInterviewersTimeSlots);
    dashboardDay.setCandidateSlots(candidatesSlotsPerDay);
    dashboardDay.setBookings(bookingMap);

    return dashboardDay;
  }

  private TimeSlot mapInterviewerTimeSlotToTimeSlot(InterviewerTimeSlot interviewerTimeSlot) {

    List<Long> bookingIds = interviewerTimeSlot.getBookings()
        .stream()
        .map(Booking::getId)
        .collect(Collectors.toList());

    return TimeSlot.builder()
        .id(interviewerTimeSlot.getId())
        .email(interviewerTimeSlot.getUser().getEmail())
        .from(interviewerTimeSlot.getFrom())
        .to(interviewerTimeSlot.getTo())
        .bookings(bookingIds)
        .build();
  }

  private TimeSlot mapCandidateTimeSlotToTimeSlot(CandidateTimeSlot candidateTimeSlot) {

    List<Long> bookingIds = candidateTimeSlot.getBookings()
        .stream()
        .map(Booking::getId)
        .collect(Collectors.toList());

    return TimeSlot.builder()
        .id(candidateTimeSlot.getId())
        .email(candidateTimeSlot.getEmail())
        .from(candidateTimeSlot.getFrom())
        .to(candidateTimeSlot.getTo())
        .bookings(bookingIds)
        .build();
  }

}
