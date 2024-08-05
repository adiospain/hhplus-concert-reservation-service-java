package io.hhplus.concert_reservation_service_java.integration.useCase.user;


import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CreatePaymentUseCaseIntegrationTest {

  @Autowired
  private CreatePaymentUseCase createPaymentUseCase;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private PaymentRepository paymentRepository;

  private User user;
  private Reservation reservation;

  @BeforeEach
  void setUp() {
    user = new User(1L, 6000);
    user = userRepository.save(user);

    reservation = Reservation.builder()
        .user(user)
        .concertScheduleId(2L)
        .seatId(3L)
        .reservedPrice(5000)
        .status(ReservationStatus.OCCUPIED)
        .build();
    reservation = reservationRepository.save(reservation);
  }

  @Test
  @DisplayName("결제 성공")
  void execute_SuccessfulPayment() {
    // Given
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .userId(user.getId())
        .reservationId(reservation.getId())
        .build();

    // When
    PaymentDomain result = createPaymentUseCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getReservationId()).isEqualTo(reservation.getId());
    assertThat(result.getPrice()).isEqualTo(reservation.getReservedPrice());

    // Verify user point is deducted
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getPoint()).isEqualTo(1000);

    // Verify reservation status is updated
    Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();
    assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.PAID);
  }

  @Test
  @DisplayName("예약을 찾을 수 없음")
  void execute_ReservationNotFound() {
    // Given
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .userId(user.getId())
        .reservationId(999L)
        .build();

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);
  }

  @Test
  @DisplayName("유효하지 않은 예약 상태")
  void execute_InvalidReservationStatus() {
    // Given
    reservation.markAs(ReservationStatus.OCCUPIED);
    reservationRepository.save(reservation);

    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .userId(user.getId())
        .reservationId(reservation.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_RESERVATION_STATUS);
  }

  @Test
  @DisplayName("만료된 예약")
  void execute_ExpiredReservation() {
    // Given
    reservation.markCreatedAt(LocalDateTime.now().minusMinutes(16)); // Assuming 15 minutes is the expiration time
    reservationRepository.save(reservation);

    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .userId(user.getId())
        .reservationId(reservation.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_RESERVATION);
  }

  @Test
  @DisplayName("충분하지 않은 포인트")
  void execute_NotEnoughPoints() {
    // Given
    user = new User(1L, 2);
    user = userRepository.save(user);
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .userId(user.getId())
        .reservationId(reservation.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ENOUGH_POINT);
  }
}