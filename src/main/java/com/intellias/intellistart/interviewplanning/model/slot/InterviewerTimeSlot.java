package com.intellias.intellistart.interviewplanning.model.slot;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class InterviewerTimeSlot extends TimeSlot {
    private DayOfWeek day;

    public InterviewerTimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {
        super(start, end);
        this.day = day;
    }
}
