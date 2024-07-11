package io.hhplus.concert_reservation_service_java.application.payment.port.in.useCase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.application.payment.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
    PaymentDTO paymentDTO = mock(PaymentDTO.class);

    when(reserverRepository.findByIdWithPessimisticLock(reserverId)).thenReturn(Optional.of(reserver));
    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    when(reserver.createPayment(reservation)).thenReturn(payment);
    when(paymentRepository.save(payment)).thenReturn(savedPayment);
    when(paymentMapper.of(savedPayment, reservation)).thenReturn(paymentDTO);

    PaymentDTO result = createPaymentUseCase.execute(command);

    assertNotNull(result);
    assertEquals(paymentDTO, result);

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