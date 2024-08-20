package io.hhplus.concert_reservation_service_java.domain.payment.message.kafka;

import io.hhplus.concert_reservation_service_java.domain.common.message.MessageSender;

public interface PaymentKafkaMessageProducer {
  void send(String message);
}
