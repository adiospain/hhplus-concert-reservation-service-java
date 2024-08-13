package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutboxJpaRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentOutboxManagerImpl implements PaymentOutboxManager{

  private final PaymentOutboxRepository paymentOutboxRepository;



  @Override
  public void create(Outbox outbox) {
    paymentOutboxRepository.save(outbox);
  }

  @Override
  public void markComplete(Outbox outbox) {
    paymentOutboxRepository.markComplete(outbox);
  }
}
