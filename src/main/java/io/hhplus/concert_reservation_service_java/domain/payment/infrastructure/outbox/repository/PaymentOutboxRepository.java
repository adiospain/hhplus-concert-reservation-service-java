package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.OutboxRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import java.util.List;

public interface PaymentOutboxRepository extends OutboxRepository {

  PaymentOutbox save(PaymentOutbox paymentOutbox);

  void markComplete(String outboxId);

  void deleteCompleted();

  List<PaymentOutbox> findByCompleted(boolean completed);
}
