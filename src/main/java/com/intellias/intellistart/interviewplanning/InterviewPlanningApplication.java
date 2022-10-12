package com.intellias.intellistart.interviewplanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application.
 */
@SpringBootApplication
@ComponentScan({
        "com.intellias.intellistart.interviewplanning.controller",
        "com.intellias.intellistart.interviewplanning.service",
        "com.intellias.intellistart.interviewplanning.repository",
        "com.intellias.intellistart.interviewplanning.exceptions"})
@EntityScan({"com.intellias.intellistart.interviewplanning.model"})
@EnableJpaRepositories("com.intellias.intellistart.interviewplanning.repository")

public class InterviewPlanningApplication {
    public static void main(String[] args) {
        SpringApplication.run(InterviewPlanningApplication.class, args);
    }
}
