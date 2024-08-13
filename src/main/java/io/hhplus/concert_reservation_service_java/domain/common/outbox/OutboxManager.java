package io.hhplus.concert_reservation_service_java.domain.common.outbox;

public interface OutboxManager {
  void create (Outbox outbox);
  void markComplete (Outbox outbox);
}
