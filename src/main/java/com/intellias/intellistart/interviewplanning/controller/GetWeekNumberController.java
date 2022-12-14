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

/**
 * Rest Controller for getting week number.
 */
@RestController
@RequestMapping
public class GetWeekNumberController {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetWeekNumberController.class);

  @Autowired
  private GetWeekNumberService getWeekNumberService;

  /**
   * Getting week number.
   *
   * @return response entity for current number of week and Http.Status.OK
   */
  @GetMapping(path = "/weeks/current")
  public ResponseEntity<WeekNumber> getCurrentWeekOfYear() {
    WeekNumber weekOfYear = getWeekNumberService.getCurrentWeekNumber();
    LOGGER.info("Successfully gave number of current week");
    return new ResponseEntity<>(weekOfYear, HttpStatus.OK);
  }

  /**
   * Getting week number.
   *
   * @return response entity for next number of week and Http.Status.OK
   */
  @GetMapping(path = "/weeks/next")
  public ResponseEntity<WeekNumber> getNextWeekOfYear() {
    WeekNumber weekOfYear = getWeekNumberService.getNextWeekNumber();
    LOGGER.info("Successfully gave number of next week");
    return new ResponseEntity<>(weekOfYear, HttpStatus.OK);
  }
}
