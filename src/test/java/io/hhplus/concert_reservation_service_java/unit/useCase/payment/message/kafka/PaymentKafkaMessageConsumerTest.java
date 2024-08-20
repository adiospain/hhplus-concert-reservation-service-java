package io.hhplus.concert_reservation_service_java.unit.useCase.payment.message.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.domain.common.message.MessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessage;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.presentation.consumer.PaymentKafkaMessageConsumer;
import io.hhplus.concert_reservation_service_java.presentation.consumer.PaymentKafkaMessageConsumerImpl;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import static org.mockito.Mockito.*;

public class PaymentKafkaMessageConsumerTest {

  private final PaymentOutboxManager paymentOutboxManager = Mockito.mock(PaymentOutboxManager.class);

  private final MessageSender messageSender = Mockito.mock(MessageSender.class);

  private final PaymentService paymentService = Mockito.mock(PaymentService.class);

  private final TokenService tokenService = Mockito.mock(TokenService.class);

  private final PaymentKafkaMessageConsumer paymentKafkaMessageConsumer = new PaymentKafkaMessageConsumerImpl(paymentOutboxManager, messageSender, paymentService, tokenService);

  private KafkaMessageListenerContainer<String, String> container;
  private CountDownLatch latch = new CountDownLatch(1);
  private String receivedMessage;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("paidToMarkOutBox 성공")
  public void testPaidToMarkOutBox() throws Exception {
    // Arrange
    ObjectMapper objectMapper = new ObjectMapper();

    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11}";

    paymentKafkaMessageConsumer.paidToMarkOutBox(message);
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);
    verify(paymentOutboxManager, times(1)).markComplete(PaymentOutbox.getUUID(message));
  }

  @Test
  @DisplayName("paidToMarkOutBox 실패 - JSON 처리 오류")
  public void testPaidToMarkOutBox_Failure_JsonProcessingException() {
    // Arrange
    String malformedMessage = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11"; // Missing closing brace

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageConsumer.paidToMarkOutBox(malformedMessage))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(JsonProcessingException.class);

    // Verify that markComplete was never called due to the exception
    verify(paymentOutboxManager, never()).markComplete(PaymentOutbox.getUUID(malformedMessage));
  }

  @Test
  @DisplayName("paidToMarkOutBox 실패 - markComplete 호출 시 예외 발생")
  public void testPaidToMarkOutBox_Failure_MarkCompleteException() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null}";
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    doThrow(new RuntimeException("Failed to mark complete"))
        .when(paymentOutboxManager).markComplete(PaymentOutbox.getUUID(message));

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageConsumer.paidToMarkOutBox(message))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to mark complete");

    // Verify that markComplete was called once, but it threw an exception
    verify(paymentOutboxManager, times(1)).markComplete(PaymentOutbox.getUUID(message));
  }

  @Test
  @DisplayName("paidToCreatePayment 성공")
  public void testPaidToCreatePayment_Success() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11}";
    ObjectMapper objectMapper = new ObjectMapper();
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    // Act
    paymentKafkaMessageConsumer.paidToCreatePayment(message);

    // Assert
    verify(paymentService, times(1)).createPayment(
        paymentMessage.getUserId(),
        paymentMessage.getReservationId(),
        paymentMessage.getReservedPrice()
    );
  }

  @Test
  @DisplayName("paidToCreatePayment 실패 - JSON 처리 오류")
  public void testPaidToCreatePayment_Failure_JsonProcessingException() {
    // Arrange
    String malformedMessage = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1"; // Missing closing brace

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageConsumer.paidToCreatePayment(malformedMessage))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(JsonProcessingException.class);

    // Verify that createPayment was never called due to the exception
    verify(paymentService, never()).createPayment(anyLong(), anyLong(), anyInt());
  }

  @Test
  @DisplayName("paidToCreatePayment 실패 - createPayment 호출 시 예외 발생")
  public void testPaidToCreatePayment_Failure_CreatePaymentException() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11}";
    ObjectMapper objectMapper = new ObjectMapper();
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    doThrow(new RuntimeException("Failed to create payment"))
        .when(paymentService).createPayment(paymentMessage.getUserId(), paymentMessage.getReservationId(), paymentMessage.getReservedPrice());

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageConsumer.paidToCreatePayment(message))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to create payment");

    // Verify that createPayment was called once, but it threw an exception
    verify(paymentService, times(1)).createPayment(paymentMessage.getUserId(), paymentMessage.getReservationId(), paymentMessage.getReservedPrice());
  }
}
