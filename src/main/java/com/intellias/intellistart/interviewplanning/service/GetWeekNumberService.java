package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * Service for getting current/next week number.
 */
@Service
@ApplicationScope
public class GetWeekNumberService {
  WeekNumber weekNumber = new WeekNumber();

  /**
   * Getting current week number.
   *
   * @return saved current weekNumber
   */
  public WeekNumber getCurrentWeekNumber() {
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int day = localDate.getDayOfMonth();
    int month = localDate.getMonthValue();
    int year = localDate.getYear();

    LocalDate dates = LocalDate.of(year, month, day);
    weekNumber.setWeekNum(dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
    return weekNumber;
  }

  /**
   * Getting next week number.
   *
   * @return saved next weekNumber
   */
  public WeekNumber getNextWeekNumber() {
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int day = localDate.getDayOfMonth();
    int month = localDate.getMonthValue();
    int year = localDate.getYear();
    LocalDate dates = LocalDate.of(year, month, day);
    if (dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + 1 > 52) {
      weekNumber.setWeekNum(1);
      return weekNumber;
    } else {
      weekNumber.setWeekNum(dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + 1);
      return weekNumber;
    }
  }
}
