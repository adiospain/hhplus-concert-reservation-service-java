package io.hhplus.concert_reservation_service_java.domain.payment.application.useCase;

import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEventPublisher;
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

  private final PaymentEventPublisher eventPublisher;

  private final PaymentMapper paymentMapper;

  @Override
  @Transactional
  public PaymentDomain execute(CreatePaymentCommand command) {
    Reservation reservation = null;
    User user = null;
    Payment payment = null;
    try {
      reservation = reservationService.getReservationToPay(command.getReservationId());
      user = userService.getUserWithLock(command.getUserId());

      payment = paymentService.createPayment(user.getId(), reservation);
      //userService.usePoint(user.getId(), reservation.getReservedPrice());
      //reservationService.saveToPay(reservation);


      //tokenService.expireToken(command.getUserId(), command.getAccessKey());
      eventPublisher.execute(new PaymentEvent(reservation.getId(), reservation.getReservedPrice(), user.getId(), command.getAccessKey()));

      return paymentMapper.of(payment, reservation, user);
    } catch (CustomException e){
      throw new CustomException(e.getErrorCode());
    } catch (Exception e){
      throw new RuntimeException();
    }
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
