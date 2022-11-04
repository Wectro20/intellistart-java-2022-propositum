package com.intellias.intellistart.interviewplanning.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.intellias.intellistart.interviewplanning.exceptions.UserAlreadyExistsException;
import com.intellias.intellistart.interviewplanning.model.User;
import com.intellias.intellistart.interviewplanning.model.User.UserRole;
import com.intellias.intellistart.interviewplanning.repository.BookingLimitRepository;
import com.intellias.intellistart.interviewplanning.repository.BookingRepository;
import com.intellias.intellistart.interviewplanning.repository.InterviewerTimeSlotRepository;
import com.intellias.intellistart.interviewplanning.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  private static final String EMAIL = "test@.com";
  private static final User INTERVIEWER = new User(1L, EMAIL, UserRole.INTERVIEWER);

  private static final User COORDINATOR = new User(1L, EMAIL, UserRole.COORDINATOR);

  private static final User INPUT_USER = User.builder().email(EMAIL).build();

  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingLimitRepository bookingLimitRepository;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private InterviewerTimeSlotRepository interviewerTimeSlotRepository;


  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    userService = new UserService(userRepository, bookingLimitRepository, bookingRepository,
        interviewerTimeSlotRepository);
  }

  @Test
  public void saveUserWithRole_When_UserAlreadyExist_Should_ThrowException() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(INTERVIEWER));
    assertThrows(UserAlreadyExistsException.class,
        () -> userService.saveUserWithRole(INPUT_USER, UserRole.INTERVIEWER));
  }

  @Test
  public void saveUserWithRole_ShouldSuccess() {
    Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    userService.saveUserWithRole(INPUT_USER, UserRole.INTERVIEWER);

    Mockito.verify(userRepository, Mockito.times(1))
        .save(userArgumentCaptor.capture());

    User actualUser = userArgumentCaptor.getValue();

    assertEquals(UserRole.INTERVIEWER, actualUser.getRole());
    assertEquals(EMAIL, actualUser.getEmail());
  }

  @Test
  public void deleteUserWithRole_When_RoleIsCoordinator_ShouldSuccessDelete() {
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(COORDINATOR));

    userService.deleteUserWithRole(1L, UserRole.COORDINATOR);

    Mockito.verify(userRepository, Mockito.times(1)).delete(COORDINATOR);
  }


  @Test
  public void deleteUserWithRole_When_RoleIsInterviewer_ShouldSuccessDelete() {
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(INTERVIEWER));

    userService.deleteUserWithRole(1L, UserRole.INTERVIEWER);

    Mockito.verify(bookingLimitRepository, Mockito.times(1))
        .deleteByUser(INTERVIEWER);
    Mockito.verify(bookingRepository, Mockito.times(1))
        .deleteAllByInterviewerTimeSlot_User(INTERVIEWER);
    Mockito.verify(interviewerTimeSlotRepository, Mockito.times(1))
        .deleteAllByUser(INTERVIEWER);

    Mockito.verify(userRepository, Mockito.times(1)).delete(INTERVIEWER);

  }


}
