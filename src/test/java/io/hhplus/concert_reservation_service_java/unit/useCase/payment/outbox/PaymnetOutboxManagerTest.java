package io.hhplus.concert_reservation_service_java.unit.useCase.payment.outbox;

import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManagerImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.*;

public class PaymnetOutboxManagerTest {

  private final PaymentOutboxRepository paymentOutboxRepository = Mockito.mock(PaymentOutboxRepository.class);

  private final PaymentOutboxManager paymentOutboxManager = new PaymentOutboxManagerImpl(paymentOutboxRepository);

  private PaymentEvent paymentEvent;
  private PaymentOutbox paymentOutbox;

  @BeforeEach
  void setUp() {
    String accesskey = UUID.randomUUID().toString();

    paymentEvent = new PaymentEvent(1L, 10, 1L, accesskey);
    paymentEvent.createKafkaMessage();
    paymentOutbox = PaymentOutbox.builder()
        .message(PaymentOutbox.getUUID(paymentEvent.getMessage()))
        .build();
  }

  @Test
  @DisplayName("아웃박스 생성 성공")
  void create_Success() {

    when(paymentOutboxRepository.save(any(PaymentOutbox.class))).thenReturn(paymentOutbox);

    PaymentOutbox result = paymentOutboxManager.create(paymentEvent);

    verify(paymentOutboxRepository).save(paymentOutbox);
    assertEquals(paymentOutbox, result);
  }

  @Test
  @DisplayName("아웃박스 생성 실패")
  void create_Failure() {
    doThrow(new RuntimeException("Failed to save outbox"))
        .when(paymentOutboxRepository).save(any(PaymentOutbox.class));

    assertThatThrownBy(() -> paymentOutboxManager.create(paymentEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to save outbox");

    verify(paymentOutboxRepository).save(paymentOutbox);
  }


  @Test
  @DisplayName("아웃박스 완료 마크 성공")
  void markComplete_Success() {
    String outboxId = "UUID";
    paymentOutboxManager.markComplete(outboxId);
    verify(paymentOutboxRepository).markComplete(outboxId);
  }

  @Test
  @DisplayName("아웃박스 완료 마크 실패")
  void markComplete_Failure() {

    String outboxId = "UUID";
    doThrow(new RuntimeException("아웃박스 완료 마크 실패"))
        .when(paymentOutboxRepository).markComplete(outboxId);


    assertThatThrownBy(() -> paymentOutboxManager.markComplete(outboxId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("아웃박스 완료 마크 실패");
    verify(paymentOutboxRepository).markComplete(outboxId);
  }
}
