package io.hhplus.concert_reservation_service_java.unit.useCase.payment.message;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.PaymentMessageSenderImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentKafkaMessageProducer;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManagerImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PaymentMessageSenderTest {
  private final PaymentKafkaMessageProducer paymentKafkaMessageProducer = Mockito.mock(PaymentKafkaMessageProducer.class);
  private final PaymentMessageSender paymentMessageSender = new PaymentMessageSenderImpl(paymentKafkaMessageProducer);

  private PaymentEvent paymentEvent;
  private PaymentOutbox paymentOutbox;

  @BeforeEach
  void setUp() {
    String accesskey = UUID.randomUUID().toString();

    paymentEvent = new PaymentEvent(1L, 10, 1L, accesskey);
    paymentEvent.createOutboxMessage();
    paymentOutbox = PaymentOutbox.builder()
        .message(PaymentOutbox.getUUID(paymentEvent.getMessage()))
        .build();
  }

  @Test
  @DisplayName("메시지 전송 성공")
  void send_Success() {
    paymentEvent.createKafkaMessage();
    paymentMessageSender.send(paymentEvent);
    verify(paymentKafkaMessageProducer, times(1)).send(paymentEvent.getMessage());
  }

  @Test
  @DisplayName("메시지 전송 실패")
  void send_Failure() {
    // Arrange
    paymentEvent.createKafkaMessage(); // Assume this sets the message in the event
    doThrow(new RuntimeException("메시지 전송 실패"))
        .when(paymentKafkaMessageProducer).send(paymentEvent.getMessage());

    // Act & Assert
    assertThatThrownBy(() -> paymentMessageSender.send(paymentEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("메시지 전송 실패");

    // Verify that the send method was indeed called
    verify(paymentKafkaMessageProducer).send(paymentEvent.getMessage());
  }
}
