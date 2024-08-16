package io.hhplus.concert_reservation_service_java.unit.useCase.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEventPublisher;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.event.ReservationEventPublisher;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.application.useCase.CreatePaymentUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;

import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class CreatePaymentUseCaseTest {

  private final UserService userService = Mockito.mock(UserService.class);
  private final ReservationService reservationService = Mockito.mock(ReservationService.class);
  private final PaymentService paymentService = Mockito.mock(PaymentService.class);
  private final TokenService tokenService = Mockito.mock(TokenService.class);

  private final PaymentEventPublisher eventPublisher = Mockito.mock(PaymentEventPublisher.class);

  private final PaymentMapper paymentMapper = Mockito.mock(PaymentMapper.class);



  private final CreatePaymentUseCase createPaymentUseCase = new CreatePaymentUseCaseImpl(
      userService, reservationService, paymentService , tokenService, eventPublisher, paymentMapper);

  private CreatePaymentCommand command;
  private Reservation reservation;
  private User reserver;
  private Payment payment;
  private PaymentDomain paymentDomain;


  @BeforeEach
  void setUp() {
    command = CreatePaymentCommand.builder()
        .userId(1L)
        .reservationId(1L)
        .build();

    reserver = new User(1L, 990);
    reserver = new User(1L, 990);
    reservation = Reservation.builder()
        .id(1L)
        .userId(reserver.getId())
        .concertScheduleId(2L)
        .seatId(3L)
        .reservedPrice(10)
        .build();
    payment = Payment.builder()
        .id(2L)
        .userId(reserver.getId())
        .reservationId(reservation.getId())
        .createdAt(LocalDateTime.now())
        .build();
    paymentDomain = PaymentDomain.builder()
        .reservationId(reservation.getId())
        .price(reservation.getReservedPrice())
        .pointAfter(reserver.getPoint())
        .build();
  }

  @Test
  @DisplayName("결제 성공")
  void execute_SuccessfulPayment() {
    // Given
    when(reservationService.getReservationToPay(1L)).thenReturn(reservation);
    when(userService.usePoint(1L, reservation.getReservedPrice())).thenReturn(reserver);
    when(reservationService.saveToPay(reservation)).thenReturn(reservation);
    when(paymentMapper.of(reservation, reserver)).thenReturn(paymentDomain);

    // When
    PaymentDomain result = createPaymentUseCase.execute(command);

    // Then
    assertEquals(paymentDomain, result);
    verify(reservationService).getReservationToPay(reservation.getId());
    verify(userService).usePoint(1L, 10);
    verify(reservationService).saveToPay(reservation);

    verify(paymentMapper).of(reservation, reserver);

    ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);
    verify(eventPublisher).execute(eventCaptor.capture());
    PaymentEvent event = eventCaptor.getValue();
    assertEquals(reservation.getId(), event.getReservationId());
    assertEquals(reserver.getId(), event.getUserId());
  }

  @Test
  @DisplayName("예약을 찾을 수 없음")
  void execute_ReservationNotFound() {

    when(reservationService.getReservationToPay(1L))
        .thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));


    // Act & Assert
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
        });

    verify(reservationService).getReservationToPay(1L);
    verify(userService, never()).getUserWithLock(reserver.getId());
    verifyNoMoreInteractions(reservationService, userService, eventPublisher);
  }

  @Test
  @DisplayName("유효하지 않은 예약")
  void execute_WhenReservationValidationFails_ShouldThrowException() {
    // Given
    when(reservationService.getReservationToPay(1L)).thenThrow(new CustomException(ErrorCode.INVALID_RESERVATION_STATUS));

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_RESERVATION_STATUS);

    verify(reservationService).getReservationToPay(reservation.getId());
    verifyNoInteractions(userService, paymentService, paymentMapper);
  }

  @Test
  @DisplayName("만료된 예약")
  void execute_WhenReservationExpired_ShouldThrowException() {
    // Given
    when(reservationService.getReservationToPay(1L)).thenThrow(new CustomException(ErrorCode.EXPIRED_RESERVATION));

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_RESERVATION);

    verify(reservationService).getReservationToPay(reservation.getId());
    verifyNoInteractions(userService, paymentService, paymentMapper);
  }

  @Test
  @DisplayName("충분하지 않은 포인트")
  void execute_WhenNotEnoughPoints_ShouldThrowException() {
    // Given
    when(reservationService.getReservationToPay(1L)).thenReturn(reservation);
    when(userService.usePoint(1L, reservation.getReservedPrice())).thenThrow(new CustomException(ErrorCode.NOT_ENOUGH_POINT));
    when(paymentService.createPayment(1L, reservation)).thenReturn(payment);
    when(paymentMapper.of(reservation, reserver)).thenReturn(paymentDomain);;

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ENOUGH_POINT);

    verify(reservationService).getReservationToPay(reservation.getId());
    verify(userService).usePoint(reserver.getId(), reservation.getReservedPrice());
    verifyNoInteractions(paymentService, paymentMapper);
  }
}