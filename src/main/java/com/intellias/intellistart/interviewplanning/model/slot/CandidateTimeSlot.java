package com.intellias.intellistart.interviewplanning.model.slot;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CandidateTimeSlot extends TimeSlot {
    private LocalDate date;

    public CandidateTimeSlot(LocalDate date, LocalTime start, LocalTime end) {
        super(start, end);
        this.date = date;
    }
}
