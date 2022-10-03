package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class InterviewerService {

  public InterviewerTimeSlot createSlot() {
    return new InterviewerTimeSlot();
  }

  public InterviewerTimeSlot createSlot(DayOfWeek day, LocalTime start, LocalTime end) {
    return new InterviewerTimeSlot(day, start, end);
  }
}
