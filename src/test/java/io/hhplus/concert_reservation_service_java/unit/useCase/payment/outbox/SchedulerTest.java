package io.hhplus.concert_reservation_service_java.unit.useCase.payment.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessage;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessageProducer;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.presentation.scheduler.OutboxScheduler;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class SchedulerTest {

  private final PaymentOutboxRepository paymentOutboxRepository = Mockito.mock(PaymentOutboxRepository.class);
  private final ReservationService reservationService = Mockito.mock(ReservationService.class);
  private final PaymentKafkaMessageProducer paymentKafkaMessageProducer = Mockito.mock(PaymentKafkaMessageProducer.class);
  private final TokenService tokenService = Mockito.mock(TokenService.class);

  private final ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);

  private final OutboxScheduler outboxScheduler = new OutboxScheduler(paymentOutboxRepository, reservationService, paymentKafkaMessageProducer, tokenService);

  private Reservation reservation;
  private User user;
  private Payment payment;
  private PaymentDomain paymentDomain;
  private PaymentEvent paymentEvent;
  @BeforeEach
  void setUp() {
    String accesskey = UUID.randomUUID().toString();
    user = new User(1L, 990);
    reservation = Reservation.builder()
        .id(1L)
        .userId(user.getId())
        .concertScheduleId(2L)
        .seatId(3L)
        .reservedPrice(10)
        .build();
    payment = Payment.builder()
        .id(2L)
        .userId(user.getId())
        .reservationId(reservation.getId())
        .createdAt(LocalDateTime.now())
        .build();
    paymentDomain = PaymentDomain.builder()
        .reservationId(reservation.getId())
        .price(reservation.getReservedPrice())
        .pointAfter(user.getPoint())
        .build();
    paymentEvent = new PaymentEvent(reservation.getId(), reservation.getReservedPrice(),
        user.getId(), accesskey);
  }
  @Test
  @DisplayName("아웃박스 이벤트 발행 재시도 성공")
  void retryPaymentOutboxEvent_Success() throws JsonProcessingException {
    // Arrange
    paymentEvent.createKafkaMessage();
    PaymentOutbox  outbox = PaymentOutbox.builder()
        .message(PaymentOutbox.getUUID(paymentEvent.getMessage()))
        .build();

    List<PaymentOutbox> outboxList = Arrays.asList(outbox);

    when(paymentOutboxRepository.findByCompleted(false)).thenReturn(outboxList);

    when(reservationService.getById(any(Long.class))).thenReturn(reservation);

    outboxScheduler.retryPaymentOutboxEvent();

    verify(tokenService).expireToken(any(Long.class), any(String.class));
  }

  @Test
  @DisplayName("아웃박스 이벤트 발행 재시도 실패 - JSON 처리 오류")
  void retryPaymentOutboxEvent_Failure_JsonProcessingException() throws JsonProcessingException {
    // Arrange
    paymentEvent.createKafkaMessage();
    PaymentOutbox outbox = PaymentOutbox.builder()
        .message(PaymentOutbox.getUUID(paymentEvent.getMessage()))
        .build();

    List<PaymentOutbox> outboxList = Arrays.asList(outbox);

    when(paymentOutboxRepository.findByCompleted(false)).thenReturn(outboxList);
    when(objectMapper.readValue(outbox.getMessage(), PaymentKafkaMessage.class))
        .thenThrow(new JsonProcessingException("JSON processing error") {});

    // Act & Assert
    try {
      outboxScheduler.retryPaymentOutboxEvent();
    } catch (RuntimeException e) {
      verify(paymentKafkaMessageProducer, never()).send(anyString());
      verify(tokenService, never()).expireToken(anyLong(), anyString());
    }
  }
}
