package io.hhplus.concert_reservation_service_java.application.payment.port.in.useCase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.application.useCase.CreatePaymentUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.model.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreatePaymentUseCaseTest {


  private final ReserverRepository reserverRepository = Mockito.mock(ReserverRepository.class);

  private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);

  private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
  private final PaymentMapper paymentMapper = Mockito.mock(PaymentMapper.class);

  private final CreatePaymentUseCase createPaymentUseCase = new CreatePaymentUseCaseImpl(reserverRepository, paymentRepository, reservationRepository , paymentMapper);


  @Test
  void execute_SuccessfulPayment() {
    // Arrange
    long reserverId = 1L;
    long reservationId = 1L;
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(reserverId)
        .reservationId(reservationId)
        .build();

    Reserver reserver = mock(Reserver.class);
    Reservation reservation = mock(Reservation.class);
    Payment payment = mock(Payment.class);
    Payment savedPayment = mock(Payment.class);
    PaymentDomain paymentDomain = mock(PaymentDomain.class);

    when(reserverRepository.findByIdWithPessimisticLock(reserverId)).thenReturn(Optional.of(reserver));
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    when(reserver.createPayment(reservation)).thenReturn(payment);
    when(paymentRepository.save(payment)).thenReturn(savedPayment);
    when(paymentMapper.of(savedPayment, reservation)).thenReturn(paymentDomain);

    PaymentDomain result = createPaymentUseCase.execute(command);

    assertNotNull(result);
    assertEquals(paymentDomain, result);

    verify(reserverRepository).findByIdWithPessimisticLock(reserverId);
    verify(reservationRepository).findById(reservationId);
    verify(reserver).createPayment(reservation);
    verify(reservationRepository).save(reservation);
    verify(reserverRepository).save(reserver);
    verify(paymentRepository).save(payment);
    verify(paymentMapper).of(savedPayment, reservation);
  }

  @Test
  void execute_ReserverNotFound() {
    // Arrange
    long reserverId = 1L;
    long reservationId = 1L;
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(reserverId)
        .reservationId(reservationId)
            .build();

    when(reserverRepository.findByIdWithPessimisticLock(reserverId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(CustomException.class, () -> createPaymentUseCase.execute(command));
    verify(reserverRepository).findByIdWithPessimisticLock(reserverId);
    verifyNoInteractions(reservationRepository, paymentRepository, paymentMapper);
  }

  @Test
  void execute_ReservationNotFound() {
    // Arrange
    long reserverId = 1L;
    long reservationId = 1L;
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(reserverId)
        .reservationId(reservationId)
        .build();

    Reserver reserver = mock(Reserver.class);

    when(reserverRepository.findByIdWithPessimisticLock(reserverId)).thenReturn(Optional.of(reserver));
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(CustomException.class, () -> createPaymentUseCase.execute(command));
    verify(reserverRepository).findByIdWithPessimisticLock(reserverId);
    verify(reservationRepository).findById(reservationId);
    verifyNoInteractions(paymentRepository, paymentMapper);
  }

  @Test
  void execute_PaymentCreationFails() {
    // Arrange
    long reserverId = 1L;
    long reservationId = 1L;
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(reserverId)
        .reservationId(reservationId)
        .build();

    Reserver reserver = mock(Reserver.class);
    Reservation reservation = mock(Reservation.class);

    when(reserverRepository.findByIdWithPessimisticLock(reserverId)).thenReturn(Optional.of(reserver));
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    when(reserver.createPayment(reservation)).thenThrow(new CustomException(ErrorCode.NOT_ENOUGH_POINT));

    // Act & Assert
    assertThrows(CustomException.class, () -> createPaymentUseCase.execute(command));
    verify(reserverRepository).findByIdWithPessimisticLock(reserverId);
    verify(reservationRepository).findById(reservationId);
    verify(reserver).createPayment(reservation);
    verifyNoInteractions(paymentRepository, paymentMapper);
  }
}