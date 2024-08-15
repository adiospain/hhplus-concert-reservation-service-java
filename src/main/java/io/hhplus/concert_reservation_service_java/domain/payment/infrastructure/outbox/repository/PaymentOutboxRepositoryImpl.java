package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository{

  private final PaymentOutboxJpaRepository paymentOutboxRepository;

  @Override
  public void save(PaymentOutbox paymentOutbox) {
    paymentOutboxRepository.save(paymentOutbox);
  }

  @Override
  @Transactional
  public void markComplete(long outboxId) {
    paymentOutboxRepository.markComplete(outboxId);
  }
}
