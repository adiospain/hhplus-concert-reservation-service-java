package io.hhplus.concert_reservation_service_java.presentation.event.payment;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEventListener;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
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

  @Override
  @TransactionalEventListener(phase = BEFORE_COMMIT)
  public void createOutbox(CustomEvent event) {
    paymentOutboxManager.create(event.getOutbox());
  }

  @Async
  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void sendMessage(CustomEvent event) {
    log.info("sendMessasge::");
    paymentMessageSender.send(event.getOutbox().getMessage());
  }
}
