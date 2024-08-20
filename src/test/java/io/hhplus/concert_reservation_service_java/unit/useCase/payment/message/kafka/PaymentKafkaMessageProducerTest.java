package io.hhplus.concert_reservation_service_java.unit.useCase.payment.message.kafka;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessageProducer;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessageProducerImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

public class PaymentKafkaMessageProducerTest {

  private KafkaTemplate<String, String> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
  private final PaymentKafkaMessageProducer paymentKafkaMessageProducer = new PaymentKafkaMessageProducerImpl(
      kafkaTemplate
  );
  private PaymentEvent paymentEvent;
  private PaymentOutbox paymentOutbox;
  private String message;

  @BeforeEach
  void setUp() {
    String accesskey = UUID.randomUUID().toString();

    paymentEvent = new PaymentEvent(1L, 10, 1L, accesskey);
    paymentEvent.createKafkaMessage();
    paymentOutbox = PaymentOutbox.builder()
        .message(PaymentOutbox.getUUID(paymentEvent.getMessage()))
        .build();
    message = paymentOutbox.getMessage();
  }

  @Test
  @DisplayName("카프카 메시지 전송 성공")
  void produce_Success() throws Exception {
    paymentKafkaMessageProducer.send(message);
    verify(kafkaTemplate, times(1)).send(null,message);
  }

  @Test
  @DisplayName("카프카 메시지 전송 실패")
  void send_Failure() {
    // Arrange
    String message = "Test message";
    doThrow(new RuntimeException("Failed to send message"))
        .when(kafkaTemplate).send(null, message);

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageProducer.send(message))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to send message");

    // Verify that the send method was indeed called
    verify(kafkaTemplate).send(null, message);
  }
}
