package io.hhplus.concert_reservation_service_java.presentation.consumer;

import org.springframework.kafka.annotation.KafkaListener;

public interface PaymentKafkaMessageConsumer {

  @KafkaListener(topics = "${spring.kafka.topic.payment.name}", groupId = "PaymentOutbox")
  void paidToMarkOutBox(String message);

  @KafkaListener(topics = "${spring.kafka.topic.payment.name}", groupId = "CreatePayment")
  void paidToCreatePayment(String message);
}
