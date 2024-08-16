package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message;


import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentKafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentMessageSenderImpl implements PaymentMessageSender {

    private final PaymentKafkaMessageProducer paymentKafkaMessageProducer;

    @Override
    public void send(PaymentEvent event) {
      event.createKafkaMessage();
      paymentKafkaMessageProducer.send(event.getMessage());
    }
}
