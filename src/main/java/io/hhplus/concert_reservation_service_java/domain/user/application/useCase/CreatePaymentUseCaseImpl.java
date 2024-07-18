package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.user.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

  private final UserService userService;
  private final ReservationService reservationService;
  private final PaymentService paymentService;
  private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDomain execute(CreatePaymentCommand command) {
        Reservation reservation = reservationService.getReservationToPay(command.getReservationId());
        User user = userService.usePoint(command.getUserId(), reservation.getReservedPrice());
        Payment payment = paymentService.createPayment(user.getId(), reservation.getId());

        reservationService.saveToPay(reservation);

        return paymentMapper.of(payment, reservation, user);
    }
}
