package com.intellias.intellistart.interviewplanning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import java.time.DayOfWeek;
import java.util.Arrays;
import lombok.AllArgsConstructor;

/**
 * Day of week for Interviewer time slot.
 */
@AllArgsConstructor
public enum InterviewDayOfWeek {
  MONDAY("Mon"),
  TUESDAY("Tue"),
  WEDNESDAY("Wed"),
  THURSDAY("Thu"),
  FRIDAY("Fri");

  private final String value;

  /**
   * Convert string to DayOfWeek enum.
   * Used for automatic converting request field.
   *
   * @param dayOfWeek string value of enum
   *
   * @return converted DayOfWeek
   */

  @JsonCreator
  public static InterviewDayOfWeek forValue(String dayOfWeek) {
    return Arrays.stream(InterviewDayOfWeek.values())
        .filter(day -> day.getValue().equals(dayOfWeek))
        .findAny()
        .orElseThrow(() -> new InvalidDayOfWeekException(dayOfWeek));
  }

  /**
   * Convert InterviewDayOfWeek to DayOfWeek.
   *
   * @param interviewDayOfWeek to be converted
   * @return converted interviewDayOfWeek to DayOfWeek
   */

  public static DayOfWeek convertToDayOfWeek(InterviewDayOfWeek interviewDayOfWeek) {
    return Arrays.stream(DayOfWeek.values())
        .filter(day -> day.name().equals(interviewDayOfWeek.name()))
        .findAny()
        .orElseThrow(() -> new InvalidDayOfWeekException(interviewDayOfWeek.getValue()));
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
