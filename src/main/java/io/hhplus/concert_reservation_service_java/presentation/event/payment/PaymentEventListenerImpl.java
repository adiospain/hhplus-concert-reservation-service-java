package io.hhplus.concert_reservation_service_java.presentation.event.payment;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEventListener;
import io.hhplus.concert_reservation_service_java.domain.payment.message.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentEventListenerImpl implements PaymentEventListener {

  private final PaymentOutboxManager paymentOutboxManager;
  private final PaymentMessageSender paymentMessageSender;

  private final UserService userService;
  private final ReservationService reservationService;
  private final TokenService tokenService;

  @Override
  @TransactionalEventListener(phase = BEFORE_COMMIT)
  public void createOutbox(PaymentEvent event) {
    log.info("createOutbox::");
    paymentOutboxManager.create(event);
  }

  @Override
  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void sendMessage(PaymentEvent event) {
    log.info("sendMessasge::");
    paymentMessageSender.send(event);
  }

  @Override
  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void expireToken(PaymentEvent event) {
    log.info("expireToken::");
    //tokenService.expireToken(event.getUserId(), event.getAccessKey());
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void usePoint(PaymentEvent event){
    log.info("usePoint::");
    userService.usePoint(event.getUserId(), event.getReservedPrice());
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void paidReservation(PaymentEvent event){
    log.info("paidReservation::");
    reservationService.saveToPay(event.getReservationId());
  }
}
