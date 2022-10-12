package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.model.WeekNumber;
import com.intellias.intellistart.interviewplanning.service.GetWeekNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/weeks/current")
public class GetWeekNumberController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetWeekNumberController.class);

    @Autowired
    private GetWeekNumberService getWeekNumberService;

    @GetMapping(path = "/weeks/current")
    public ResponseEntity<WeekNumber> getCurrentWeekOfYear() {
        WeekNumber weekOfYear = getWeekNumberService.getCurrentWeekNumber();
        LOGGER.info("Successfully gave number of week " + weekOfYear);
        return new ResponseEntity<>(weekOfYear, HttpStatus.OK);
    }

    @GetMapping(path = "/weeks/next")
    public ResponseEntity<WeekNumber> getNextWeekOfYear() {
        WeekNumber weekOfYear = getWeekNumberService.getNextWeekNumber();
        LOGGER.info("Successfully gave number of week " + weekOfYear);
        return new ResponseEntity<>(weekOfYear, HttpStatus.OK);
    }
}
