package io.hhplus.concert_reservation_service_java.presentation.event.payment;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEventListener;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
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

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void expireToken(PaymentEvent event) {
    log.info("expireToken::");
    tokenService.expireToken(event.getUserId(), event.getAccessKey());
  }
}
