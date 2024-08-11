package io.hhplus.concert_reservation_service_java.domain.payment.application.model.useCase;

import io.hhplus.concert_reservation_service_java.domain.common.DataPlatformClient;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentSuccessEvent;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.PaymentMapper;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

  private final UserService userService;
  private final ReservationService reservationService;
  private final PaymentService paymentService;
  private final TokenService tokenService;

  private final PaymentEvent.Publisher eventPublisher;

  private final PaymentMapper paymentMapper;

  @Override
  @Transactional
  public PaymentDomain execute(CreatePaymentCommand command) {
      Reservation reservation = reservationService.getReservationToPay(command.getReservationId());
      User user = userService.usePoint(command.getUserId(), reservation.getReservedPrice());
      Payment payment = paymentService.createPayment(user.getId(), reservation);

      reservationService.saveToPay(reservation);
      //tokenService.expireToken(command.getUserId(), command.getAccessKey());

      eventPublisher.success(new PaymentSuccessEvent(payment));

      return paymentMapper.of(payment, reservation, user);
  }

  @Override
  public void usePoint(long id, int price){
    try{
      userService.usePoint(id, price);
    }
    catch (PessimisticLockingFailureException e){
      throw new CustomException(ErrorCode.CONCURRENT_LOCK);
    }
  }

}
