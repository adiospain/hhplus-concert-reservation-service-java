package io.hhplus.concert_reservation_service_java.domain.common.event;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;

public interface EventPublisher {
  void execute(CustomEvent event);
}
