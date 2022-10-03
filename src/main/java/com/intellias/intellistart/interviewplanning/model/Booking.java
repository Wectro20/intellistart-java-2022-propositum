package com.intellias.intellistart.interviewplanning.model;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import java.time.LocalTime;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
  private Long id;
  private InterviewerTimeSlot interviewerTimeSlot;
  private CandidateTimeSlot candidateTimeSlot;
  private LocalTime startTime;
  private LocalTime endTime;
  private String subject;
}

