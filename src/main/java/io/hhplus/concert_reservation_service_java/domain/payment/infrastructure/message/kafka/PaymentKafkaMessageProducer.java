package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka;


import io.hhplus.concert_reservation_service_java.domain.common.message.MessageSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Any;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentKafkaMessageProducer implements PaymentMessageSender {

  private final KafkaTemplate<String, String> kafkaTemplate;



  @Override
  public void send(String message) {
    kafkaTemplate.send("PAYMENT_TOPIC", message);
  }
}
