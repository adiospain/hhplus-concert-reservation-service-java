package io.hhplus.concert_reservation_service_java.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.business.service.UserServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.Optional;

class UserServiceTest {
  private final UserRepository userRepository = Mockito.mock(UserRepository.class);
  private final UserService service = new UserServiceImpl(userRepository);

  @Test
  @DisplayName("존재하는 유저 조회")
  void testGetUser_ExistingUser() {
    long userId = 1L;
    int point = 3000;
    User expectedUser = new User(userId, point);
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

    User result = service.getUser(userId);
    assertEquals(expectedUser, result);
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("존재하지 않는 유저 조회")
  void testGetUser_NonExistingUser() {
    long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(CustomException.class, () -> service.getUser(userId));
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("존재하는 유저 조회 - 비관적 락")
  void testGetUserWithLock_ExistingUser() {
    long userId = 1L;
    int point = 3000;
    User expectedUser = new User(userId, point);
    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(expectedUser));

    User result = service.getUserWithLock(userId);

    assertEquals(expectedUser, result);
    verify(userRepository).findByIdWithPessimisticLock(userId);
  }

  @Test
  @DisplayName("존재하지 않는 유저 조회 - 비관적 락")
  void testGetUserWithLock_NonExistingUser() {
    long userId = 1L;
    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.empty());

    assertThrows(CustomException.class, () -> service.getUserWithLock(userId));
    verify(userRepository).findByIdWithPessimisticLock(userId);
  }

  @Test
  @DisplayName("유저 포인트 조회")
  void testGetPoint() {
    long userId = 1L;
    int point = 3000;
    User user = new User(userId, point);
    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));

    int result = service.getPoint(userId);

    assertEquals(point, result);
    verify(userRepository).findByIdWithPessimisticLock(userId);
  }

  @Test
  @DisplayName("유저 포인트 충전")
  void testChargePoint() {
    long userId = 1L;
    int point = 3000;
    int addPoint = 100;
    User user = new User(userId, point);
    User updatedUser = new User(userId, addPoint+point);
    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    User result = service.chargePoint(userId, addPoint);

    assertNotNull(result);
    assertEquals(point + addPoint, result.getPoint());
    verify(userRepository).findByIdWithPessimisticLock(userId);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("유저 포인트 충전 - 음수 충전 시도")
  void testChargePoint_NegativeAmount() {
    long userId = 1L;
    int initialPoint = 3000;
    int negativeAddPoint = -100;
    User user = new User(userId, initialPoint);

    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));

    assertThrows(CustomException.class, () -> service.chargePoint(userId, negativeAddPoint));

    verify(userRepository).findByIdWithPessimisticLock(userId);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("유저 포인트 충전 - 오버플로우 처리")
  void testChargePoint_Overflow() {
    // Arrange
    long userId = 1L;
    int initialPoint = Integer.MAX_VALUE - 50;
    User user = new User(userId, initialPoint);

    when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User result1 = service.chargePoint(userId, 50);
    assertEquals(Integer.MAX_VALUE, result1.getPoint());

    assertThrows(CustomException.class, () -> service.chargePoint(userId, 1));

    assertEquals(Integer.MAX_VALUE, user.getPoint());

    verify(userRepository, times(2)).findByIdWithPessimisticLock(userId);
    verify(userRepository, times(1)).save(any(User.class));
  }
}