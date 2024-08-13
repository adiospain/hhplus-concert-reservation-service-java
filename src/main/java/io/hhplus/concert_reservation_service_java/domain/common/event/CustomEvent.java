package io.hhplus.concert_reservation_service_java.domain.common.event;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;

public interface CustomEvent {
  Outbox getOutbox();
}
