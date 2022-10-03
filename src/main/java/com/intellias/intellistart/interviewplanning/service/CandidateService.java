package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {

  public CandidateTimeSlot createSlot() {
    return new CandidateTimeSlot();
  }

  public CandidateTimeSlot createSlot(LocalDate date, LocalTime start, LocalTime end) {
    return new CandidateTimeSlot(date, start, end);
  }

}
