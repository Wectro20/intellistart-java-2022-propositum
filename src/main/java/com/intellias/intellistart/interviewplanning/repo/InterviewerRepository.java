package com.intellias.intellistart.interviewplanning.repo;

import com.intellias.intellistart.interviewplanning.model.user.Interviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for Interviewer entity.
 */
@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, Long> {

}
