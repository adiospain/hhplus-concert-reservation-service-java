package io.hhplus.concert_reservation_service_java.domain.common.outbox;

import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;

public interface OutboxManager {
  void markComplete (String outboxId);
}
