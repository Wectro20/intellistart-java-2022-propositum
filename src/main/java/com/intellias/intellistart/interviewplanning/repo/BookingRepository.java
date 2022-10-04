package com.intellias.intellistart.interviewplanning.repo;

import com.intellias.intellistart.interviewplanning.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Its repository for Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

}
