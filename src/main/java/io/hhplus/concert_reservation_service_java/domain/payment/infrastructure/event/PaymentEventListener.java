package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event;

import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.common.event.EventListener;

public interface PaymentEventListener extends EventListener {

  void createOutbox(PaymentEvent event);
  void sendMessage(PaymentEvent event);
}
