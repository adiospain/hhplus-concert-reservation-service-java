package io.hhplus.concert_reservation_service_java.domain.payment.event;


import io.hhplus.concert_reservation_service_java.domain.common.event.EventListener;

public interface PaymentEventListener extends EventListener {

  void createOutbox(PaymentEvent event);
  void sendMessage(PaymentEvent event);


  void expireToken(PaymentEvent event);
}
