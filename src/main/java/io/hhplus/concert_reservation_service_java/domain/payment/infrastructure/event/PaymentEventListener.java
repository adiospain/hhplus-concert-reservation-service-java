package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event;


import io.hhplus.concert_reservation_service_java.domain.common.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public interface PaymentEventListener extends EventListener {

  void createOutbox(PaymentEvent event);
  void sendMessage(PaymentEvent event);


  void expireToken(PaymentEvent event);
}
