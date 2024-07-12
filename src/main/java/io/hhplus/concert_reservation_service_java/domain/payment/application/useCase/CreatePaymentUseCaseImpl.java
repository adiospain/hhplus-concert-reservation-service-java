package io.hhplus.concert_reservation_service_java.domain.payment.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

  private final ReserverRepository reserverRepository;
  private final PaymentRepository paymentRepository;
  private final ReservationRepository reservationRepository;
  private final PaymentMapper paymentMapper;



  @Override
  public PaymentDTO execute(CreatePaymentCommand command) {
    Reserver reserver = findReserverWithLock(command.getReserverId());
    Reservation reservation = findReservation(command.getReservationId());

    Payment payment = reserver.createPayment(reservation);

    reservationRepository.save(reservation);
    reserverRepository.save(reserver);
    Payment savedPayment = paymentRepository.save(payment);

    return paymentMapper.of(savedPayment, reservation);
  }
  private Reserver findReserverWithLock(long reserverId) {
    return reserverRepository.findByIdWithPessimisticLock(reserverId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  private Reservation findReservation(long reservationId) {
    return reservationRepository.findById(reservationId)
        .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
  }


}
