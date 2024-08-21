package io.hhplus.concert_reservation_service_java.presentation.consumer;

import org.springframework.kafka.annotation.KafkaListener;

public interface PaymentKafkaMessageConsumer {
  
  void paidToMarkOutBox(String message);
}
