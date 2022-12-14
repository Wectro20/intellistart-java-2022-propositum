package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidDayOfWeekException;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidTimeSlotBoundariesException;
import com.intellias.intellistart.interviewplanning.exceptions.SlotIsOverlappingException;
import com.intellias.intellistart.interviewplanning.model.TimeSlotStatus;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.model.slot.CandidateTimeSlot;
import com.intellias.intellistart.interviewplanning.repository.CandidateTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class CandidateTimeSlotServiceTest {

  @InjectMocks
  private CandidateTimeSlotService candidateTimeSlotService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private CandidateTimeSlotRepository candidateTimeSlotRepository;

  private static final String userEmail = "user@gmail.com";
  private static final User user = new User(1L, userEmail, UserRole.CANDIDATE);
  private static final CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
      .date(LocalDate.of(2022, 12, 16))
      .from(LocalTime.of(9, 0))
      .to(LocalTime.of(17, 0))
      .slotStatus(TimeSlotStatus.NEW)
      .email(userEmail)
      .build();

  @Test
  void createCandidateSlotWithParameters() {
    Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    Mockito.when(candidateTimeSlotRepository.save(ArgumentMatchers.any()))
        .thenReturn(candidateTimeSlot);

    candidateTimeSlot.setId(1L);

    var slot = candidateTimeSlotService.createSlot(
        userEmail,
        LocalDate.of(2022, 12, 16),
        LocalTime.of(9, 0), // 09:00
        LocalTime.of(17, 0) // 17:00
    );

    assertNotNull(slot);
    assertEquals(LocalDate.of(2022, 12, 16), slot.getDate());
    assertEquals(LocalTime.of(9, 0), slot.getFrom());
    assertEquals(LocalTime.of(17, 0), slot.getTo());
    assertEquals(TimeSlotStatus.NEW, slot.getSlotStatus());
  }

  @Test
  void createSlotWhenDayOfWeekIsWeekendAndThrowException() {
    Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    InvalidDayOfWeekException exception = assertThrows(
        InvalidDayOfWeekException.class, () -> candidateTimeSlotService.createSlot(userEmail,
            LocalDate.of(2022, 12, 10),
            LocalTime.of(17, 0), // 09:00
            LocalTime.of(9, 0) // 17:00
        ));

    assertEquals("2022-12-10", exception.getMessage());
  }

  @Test
  void createSlotWhenBoundariesIsNotRoundedAndThrowException() {
    Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class,
        () -> candidateTimeSlotService.createSlot(userEmail,
            LocalDate.of(2022, 12, 16),
            LocalTime.of(9, 2), // 09:00
            LocalTime.of(17, 0) // 17:00
        ));

    assertEquals("09:02; 17:00", exception.getMessage());
  }

  @Test
  void createSlotWhenFromIsAfterToAndThrowException() {
    Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    InvalidTimeSlotBoundariesException exception = assertThrows(
        InvalidTimeSlotBoundariesException.class,
        () -> candidateTimeSlotService.createSlot(userEmail,
            LocalDate.of(2022, 12, 16),
            LocalTime.of(17, 0), // 09:00
            LocalTime.of(9, 0) // 17:00
        ));

    assertEquals("17:00; 09:00", exception.getMessage());
  }


  @Test
  void createSlotWhichOverlapsAndThrowException() {
    CandidateTimeSlot candidateTimeSlot = CandidateTimeSlot.builder()
        .id(2L)
        .date(LocalDate.of(2022, 12, 16))
        .from(LocalTime.of(9, 0))
        .to(LocalTime.of(17, 0))
        .slotStatus(TimeSlotStatus.NEW)
        .email(userEmail)
        .build();

    Mockito.when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
    Mockito.when(candidateTimeSlotRepository.findByDateAndEmail(LocalDate.of(2022, 12, 16), user.getEmail()))
        .thenReturn((List.of(candidateTimeSlot)));

    // New slot is inside the existing one
    SlotIsOverlappingException exception = assertThrows(
            SlotIsOverlappingException.class, () -> candidateTimeSlotService.createSlot(userEmail,
                    LocalDate.of(2022, 12, 16),
                    LocalTime.of(10, 0), // 10:00
                    LocalTime.of(16, 0) // 16:00
            ));

    assertEquals("2", exception.getMessage().substring(exception.getMessage().length()-1));

    // New slot covers the existing one from both sides
    exception = assertThrows(
            SlotIsOverlappingException.class, () -> candidateTimeSlotService.createSlot(userEmail,
                    LocalDate.of(2022, 12, 16),
                    LocalTime.of(8, 0), // 08:00
                    LocalTime.of(18, 0) // 18:00
            ));

    assertEquals("2", exception.getMessage().substring(exception.getMessage().length()-1));

    // New slot covers the existing one from right side
    exception = assertThrows(
            SlotIsOverlappingException.class, () -> candidateTimeSlotService.createSlot(userEmail,
                    LocalDate.of(2022, 12, 16),
                    LocalTime.of(15, 0), // 15:00
                    LocalTime.of(20, 0) // 20:00
            ));

    assertEquals("2", exception.getMessage().substring(exception.getMessage().length()-1));
  }

}
