package com.intellias.intellistart.interviewplanning.model;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private InterviewerTimeSlot interviewerTimeSlot;
    private CandidateTimeSlot candidateTimeSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;
}

