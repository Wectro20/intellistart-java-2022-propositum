package com.intellias.intellistart.interviewplanning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import java.util.Arrays;
import lombok.AllArgsConstructor;

/**
 * Day of week for Interviewer time slot.
 */
@AllArgsConstructor
public enum DayOfWeek {
  MONDAY("Mon"),
  TUESDAY("Tue"),
  WEDNESDAY("Wed"),
  THURSDAY("Thu"),
  FRIDAY("Fri"),
  SATURDAY("Sat"),
  SUNDAY("Sun");

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
  public static DayOfWeek forValue(String dayOfWeek) {
    return Arrays.stream(DayOfWeek.values())
        .filter(day -> day.getValue().equals(dayOfWeek))
        .findAny()
        .orElseThrow(() -> new InvalidDayOfWeekException(dayOfWeek));
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
