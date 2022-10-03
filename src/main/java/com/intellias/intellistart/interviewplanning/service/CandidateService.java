package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.model.slot.InterviewerTimeSlot;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class CandidateService {

    public CandidateTimeSlot createSlot() {
        return new CandidateTimeSlot();
    }

    public CandidateTimeSlot createSlot(LocalDate date, LocalTime start, LocalTime end) {
        return new CandidateTimeSlot(date, start, end);
    }
}
