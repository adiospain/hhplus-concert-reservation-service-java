package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverService;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.reserver.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

  private final ReserverService reserverService;
  private final ReservationService reservationService;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;



  @Override
  @Transactional
  public PaymentDomain execute(CreatePaymentCommand command) {
    Reservation reservation = reservationService.getById(command.getReservationId());
    reservation.validateForPayment();

    Reserver reserver = reserverService.getReserverWithLock(command.getReserverId());
    reserver.usePoint(reservation.getReservedPrice());

    Payment payment = reserver.createPayment(reservation);
    Payment savedPayment = paymentService.save(payment);

    reservationService.save(reservation);
    reserverService.save(reserver);

    return paymentMapper.of(savedPayment, reservation, reserver);
  }
}
