package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentKafkaMessageProducerImpl implements PaymentKafkaMessageProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.payment.name}")
  private String PAYMENT_TOPIC_NAME;



  @Override
  public void send(String message) {

    kafkaTemplate.send(PAYMENT_TOPIC_NAME, message);
  }
}
