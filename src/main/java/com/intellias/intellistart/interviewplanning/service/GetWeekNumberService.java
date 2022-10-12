package com.intellias.intellistart.interviewplanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;

@Service
@ApplicationScope
public class GetWeekNumberService {
    @Autowired
    public int getWeekNumber() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();

        LocalDate dates = LocalDate.of(year, month, day);
        return dates.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }
}
