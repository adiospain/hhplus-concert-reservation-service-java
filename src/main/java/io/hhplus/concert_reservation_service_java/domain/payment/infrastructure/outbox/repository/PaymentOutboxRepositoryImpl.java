package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutboxJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository{

  private final PaymentOutboxJpaRepository paymentOutboxRepository;

  @Override
  public PaymentOutbox save(PaymentOutbox paymentOutbox) {
    return paymentOutboxRepository.save(paymentOutbox);
  }

  @Override
  @Transactional
  public void markComplete(long outboxId) {
    paymentOutboxRepository.markComplete(outboxId);
  }

  @Override
  @Transactional
  public void deleteCompleted() {
    paymentOutboxRepository.deleteCompleted();
  }

  @Override
  @Transactional
  public List<PaymentOutbox> findByCompleted(boolean completed){
    return paymentOutboxRepository.findByCompleted(completed);
  }
}
