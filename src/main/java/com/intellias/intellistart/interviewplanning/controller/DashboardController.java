package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.service.DashboardService;
import com.intellias.intellistart.interviewplanning.service.dto.DashboardDto;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for Dashboard.
 */

@RestController
@AllArgsConstructor
public class DashboardController {

  private DashboardService dashboardService;

  @GetMapping("/weeks/{weekNum}/dashboard")
  @PreAuthorize("hasAuthority('COORDINATOR')")
  public DashboardDto getDashboard(
      @PathVariable int weekNum) {
    return dashboardService.getDashboard(weekNum);
  }
}
