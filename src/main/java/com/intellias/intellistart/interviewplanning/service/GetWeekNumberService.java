package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * Service for getting current/next week number.
 */
@Service
@ApplicationScope
public class GetWeekNumberService {
  Date date = new Date();
  LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  int day = localDate.getDayOfMonth();
  int month = localDate.getMonthValue();
  int year = localDate.getYear();
  LocalDate dates = LocalDate.of(year, month, day);

  /**
   * Getting current week number.
   *
   * @return saved current weekNumber
   */
  public WeekNumber getCurrentWeekNumber() {
    return new WeekNumber(dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
  }

  /**
   * Getting next week number.
   *
   * @return saved next weekNumber
   */
  public WeekNumber getNextWeekNumber() {
    return new WeekNumber(dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR) % 52 + 1);
  }


  /**
   * Getting date for week number and day of this week number.
   * E.g. weekNum 46 and MONDAY will produce 2022-10-24
   *
   * @return date for week and day
   */
  public LocalDate getDateForWeekNumAndDay(int weekNum, TemporalAdjuster adjuster) {
    return LocalDate.now()
        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNum)
        .with(adjuster);
  }
}
