package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.model.user.Interviewer;
import com.intellias.intellistart.interviewplanning.repo.InterviewerRepository;
import com.intellias.intellistart.interviewplanning.repo.InterviewerTimeSlotRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for interviewer.
 */
@Service
@AllArgsConstructor
public class InterviewerService {

  private InterviewerRepository interviewerRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  public InterviewerTimeSlot createSlot(Long interviewId, DayOfWeek day, LocalTime start, LocalTime end) {
    return InterviewerTimeSlot.builder()
        .day(day)
        .start(start)
        .interviewer(Interviewer.builder().id(interviewId).build())
        .end(end)
        .build();
  }
}
