package io.hhplus.concert_reservation_service_java.application.payment.port.in.useCase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverService;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase.CreatePaymentUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.reserver.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreatePaymentUseCaseTest {


  private final ReserverService reserverService = Mockito.mock(ReserverService.class);

  private final ReservationService reservationService = Mockito.mock(ReservationService.class);

  private final PaymentService paymentService = Mockito.mock(PaymentService.class);
  private final PaymentMapper paymentMapper = Mockito.mock(PaymentMapper.class);

  private final CreatePaymentUseCase createPaymentUseCase = new CreatePaymentUseCaseImpl(reserverService, reservationService, paymentService , paymentMapper);

  private CreatePaymentCommand command;
  private Reservation reservation;
  private Reserver reserver;
  private Payment payment;
  private PaymentDomain paymentDomain;


  @BeforeEach
  void setUp() {
    command = CreatePaymentCommand.builder()
        .reserverId(1L)
        .reservationId(1L)
        .build();
    reservation = mock(Reservation.class);
    reserver = mock(Reserver.class);
    payment = mock(Payment.class);
    paymentDomain = mock(PaymentDomain.class);
  }

  @Test
  @DisplayName("결제 성공")
  void execute_SuccessfulPayment() {
    // Given
    when(reservationService.getById(1L)).thenReturn(reservation);
    doNothing().when(reservation).validateForPayment();
    when(reserverService.getReserverWithLock(1L)).thenReturn(reserver);
    when(reservation.getReservedPrice()).thenReturn(1000);
    when(reservation.createPayment(reserver)).thenReturn(payment);
    when(paymentService.save(payment)).thenReturn(payment);
    when(paymentMapper.of(payment, reservation, reserver)).thenReturn(paymentDomain);

    // When
    PaymentDomain result = createPaymentUseCase.execute(command);

    // Then
    assertThat(result).isEqualTo(paymentDomain);
    verify(reservationService).getById(1L);
    verify(reservation).validateForPayment();
    verify(reserverService).getReserverWithLock(1L);
    verify(reserver).usePoint(1000);
    verify(reservation).createPayment(reserver);
    verify(paymentService).save(payment);
    verify(reservationService).save(reservation);
    verify(reserverService).save(reserver);
    verify(paymentMapper).of(payment, reservation, reserver);
  }

  @Test
  @DisplayName("예약을 찾을 수 없음")
  void execute_ReservationNotFound() {
    // Arrange
    long reserverId = 89L;
    long reservationId = 1L;
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(reserverId)
        .reservationId(reservationId)
        .build();

    Reserver reserver = mock(Reserver.class);

    when(reservationService.getById(reservationId)).thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    when(reserverService.getReserverWithLock(reserverId)).thenReturn(reserver);


    // Act & Assert
    assertThrows(CustomException.class, () -> createPaymentUseCase.execute(command));
    verify(reserverService, never()).getReserverWithLock(any(long.class));
  }

  @Test
  @DisplayName("유효하지 않은 예약")
  void execute_WhenReservationValidationFails_ShouldThrowException() {
    // Given
    when(reservationService.getById(1L)).thenReturn(reservation);
    doThrow(new CustomException(ErrorCode.INVALID_RESERVATION_STATUS))
        .when(reservation).validateForPayment();

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_RESERVATION_STATUS);

    verify(reservationService).getById(1L);
    verify(reservation).validateForPayment();
    verifyNoInteractions(reserverService, paymentService, paymentMapper);
  }

  @Test
  @DisplayName("충분하지 않은 포인트")
  void execute_WhenNotEnoughPoints_ShouldThrowException() {
    // Given
    when(reservationService.getById(1L)).thenReturn(reservation);
    doNothing().when(reservation).validateForPayment();
    when(reserverService.getReserverWithLock(1L)).thenReturn(reserver);
    when(reservation.getReservedPrice()).thenReturn(1000);
    doThrow(new CustomException(ErrorCode.NOT_ENOUGH_POINT))
        .when(reserver).usePoint(1000);

    // When & Then
    assertThatThrownBy(() -> createPaymentUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ENOUGH_POINT);

    verify(reservationService).getById(1L);
    verify(reservation).validateForPayment();
    verify(reserverService).getReserverWithLock(1L);
    verify(reserver).usePoint(1000);
    verifyNoInteractions(paymentService, paymentMapper);
  }
}