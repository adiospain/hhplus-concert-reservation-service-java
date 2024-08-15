package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox;

import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutboxJpaRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentOutboxManagerImpl implements PaymentOutboxManager{

  private final PaymentOutboxRepository paymentOutboxRepository;


  @Override
  public void create(PaymentOutbox outbox) {
      paymentOutboxRepository.save(outbox);
  }

  @Override
  public void markComplete(long outboxId) {
    paymentOutboxRepository.markComplete(outboxId);
  }
}
