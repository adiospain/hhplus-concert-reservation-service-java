package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository{

  private final PaymentOutboxJpaRepository paymentOutboxRepository;

  @Override
  public void save(Outbox outbox) {
    paymentOutboxRepository.save((PaymentOutbox) outbox);
  }

  @Override
  public void markComplete(Outbox outbox) {
    //paymentOutboxRepository.complete(outbox);
    return;
  }
}
