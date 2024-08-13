package io.hhplus.concert_reservation_service_java.domain.common.outbox;

public interface OutboxRepository {
  void save(Outbox outbox);
  void markComplete(Outbox outbox);
}
