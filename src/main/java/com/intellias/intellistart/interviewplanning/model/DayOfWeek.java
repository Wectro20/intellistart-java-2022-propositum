package com.intellias.intellistart.interviewplanning.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

  @JsonCreator
  public static DayOfWeek forValue(String dayOfWeek) {
    return Arrays.stream(DayOfWeek.values())
        .filter(day -> day.getValue().equals(dayOfWeek))
        .findAny()
        .orElseThrow(InvalidDayOfWeekException::new);
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
