package com.intellias.intellistart.interviewplanning.repo;

import com.intellias.intellistart.interviewplanning.model.user.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for Candidate entity.
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

}
