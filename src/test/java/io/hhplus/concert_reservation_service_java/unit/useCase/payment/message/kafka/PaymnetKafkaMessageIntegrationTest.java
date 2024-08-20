package io.hhplus.concert_reservation_service_java.unit.useCase.payment.message.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "payment_test" })
public class PaymnetKafkaMessageIntegrationTest {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

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
    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), new StringDeserializer());
    ContainerProperties containerProperties = new ContainerProperties("payment_test");
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    container.setupMessageListener((MessageListener<String, String>) record -> {
      receivedMessage = record.value();
      latch.countDown();
    });
    container.start();
  }

  @AfterEach
  void tearDown() {
    container.stop();
  }

  @Test
  @DisplayName("paidToMarkOutBox 성공")
  public void testPaidToMarkOutBox_Success() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null";
    ObjectMapper objectMapper = new ObjectMapper();
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    // Act
    paymentKafkaMessageConsumer.paidToMarkOutBox(message);

    // Assert
    verify(paymentOutboxManager, times(1)).markComplete(PaymentOutbox.getUUID(message));
  }

  @Test
  @DisplayName("paidToMarkOutBox 실패 - JSON 처리 오류")
  public void testPaidToMarkOutBox_Failure_JsonProcessingException() {
    // Arrange
    String malformedMessage = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1"; // Missing closing brace

    // Act & Assert
    assertThatThrownBy(() -> paymentKafkaMessageConsumer.paidToMarkOutBox(malformedMessage))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(JsonProcessingException.class);

    // Verify that markComplete was never called due to the exception
    verify(paymentOutboxManager, never()).markComplete(anyString());
  }



  @Test
  @DisplayName("paidToMarkOutBox 실패 - markComplete 호출 시 예외 발생")
  public void testPaidToMarkOutBox_Failure_MarkCompleteException() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null";
    ObjectMapper objectMapper = new ObjectMapper();
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
  public void createPayment_success_ProduceAndConsume() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11}";
    ObjectMapper objectMapper = new ObjectMapper();
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    // Act: Produce the message
    kafkaTemplate.send("payment_test", message);

    // Assert: Consume the message
    assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
    assertThat(receivedMessage).isEqualTo(message);

    // Verify that the consumer processed the message
    verify(paymentService, times(1)).createPayment(
        paymentMessage.getUserId(),
        paymentMessage.getReservationId(),
        paymentMessage.getReservedPrice());
  }

  @Test
  @DisplayName("paidToCreatePayment 실패 - JSON 처리 오류")
  public void testPaidToCreatePayment_Failure_JsonProcessingException() throws Exception {
    // Arrange
    String malformedMessage = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1"; // Missing closing brace

    // Act: Produce the malformed message
    kafkaTemplate.send("payment_test", malformedMessage);

    // Assert: Wait for the message to be consumed and expect an exception
    assertThatThrownBy(() -> {
      latch.await(10, TimeUnit.SECONDS);
      paymentKafkaMessageConsumer.paidToCreatePayment(malformedMessage);
    }).isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(JsonProcessingException.class);

    // Verify that createPayment was never called due to the exception
    verify(paymentService, never()).createPayment(anyLong(), anyLong(), anyInt());
  }

  @Test
  @DisplayName("createPayment 실패 - createPayment 호출 시 예외 발생")
  public void testPaidToCreatePayment_Failure_CreatePaymentException() throws Exception {
    // Arrange
    String message = "{\"reservationId\":1,\"reservedPrice\":10,\"userId\":1,\"accessKey\":null,\"outboxId\":11}";
    ObjectMapper objectMapper = new ObjectMapper();
    PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);

    doThrow(new RuntimeException("Failed to create payment"))
        .when(paymentService).createPayment(paymentMessage.getUserId(), paymentMessage.getReservationId(), paymentMessage.getReservedPrice());

    // Act: Produce the message
    kafkaTemplate.send("payment_test", message);

    // Assert: Wait for the message to be consumed and expect an exception
    assertThatThrownBy(() -> {
      latch.await(10, TimeUnit.SECONDS);
      paymentKafkaMessageConsumer.paidToCreatePayment(message);
    }).isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to create payment");

    // Verify that createPayment was called once, but it threw an exception
    verify(paymentService, times(1)).createPayment(paymentMessage.getUserId(), paymentMessage.getReservationId(), paymentMessage.getReservedPrice());
  }
}