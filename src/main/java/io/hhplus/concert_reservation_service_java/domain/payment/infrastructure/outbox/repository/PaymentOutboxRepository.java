package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.OutboxRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;

public interface PaymentOutboxRepository extends OutboxRepository {

  PaymentOutbox save(PaymentOutbox paymentOutbox);

  void markComplete(long outboxId);

  void deleteCompleted();

}
