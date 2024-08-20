package io.hhplus.concert_reservation_service_java.domain.payment.message.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentKafkaMessageProducerImpl implements PaymentKafkaMessageProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${spring.kafka.topic.payment.name}")
  private String PAYMENT_TOPIC_NAME;



  @Override
  public void send(String message) {
    log.info("send:: start - message={}",
        message);
    kafkaTemplate.send(PAYMENT_TOPIC_NAME, message);
  }
}
