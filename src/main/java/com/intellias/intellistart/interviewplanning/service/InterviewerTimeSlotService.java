package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for creating time slots for interviewer.
 */
@Service
@AllArgsConstructor
public class InterviewerTimeSlotService {

  private UserRepository userRepository;
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;

  /**
   * Create time slot for Interviewer.
   *
   * @param interviewId id of candidate
   * @param day        available day for time slot
   * @param start start time of time slot
   * @param end end time of time slot
   *
   * @return candidate time slot
   */
  public InterviewerTimeSlot createSlot(Long interviewId, DayOfWeek day,
                                        LocalTime start, LocalTime end) {
    return InterviewerTimeSlot.builder()
        .day(day)
        .start(start)
        .user(User.builder().id(interviewId).build())
        .end(end)
        .build();
  }
}
