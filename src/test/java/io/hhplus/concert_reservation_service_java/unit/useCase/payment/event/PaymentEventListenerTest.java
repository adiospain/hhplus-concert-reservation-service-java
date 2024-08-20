package io.hhplus.concert_reservation_service_java.unit.useCase.payment.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.event.PaymentEventListener;
import io.hhplus.concert_reservation_service_java.domain.payment.message.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.presentation.event.payment.PaymentEventListenerImpl;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.*;

@RecordApplicationEvents
public class PaymentEventListenerTest {

  private final PaymentOutboxManager paymentOutboxManager = Mockito.mock(PaymentOutboxManager.class);
  private final PaymentMessageSender paymentMessageSender = Mockito.mock(PaymentMessageSender.class);

  private final TokenService tokenService = Mockito.mock(TokenService.class);

  private final PaymentEventListener paymentEventListener = new PaymentEventListenerImpl(
      paymentOutboxManager, paymentMessageSender, tokenService);

  private CreatePaymentCommand command;
  private Reservation reservation;
  private User user;
  private Payment payment;
  private PaymentDomain paymentDomain;

  private PaymentEvent paymentEvent;


  @BeforeEach
  void setUp() {
    String accesskey = UUID.randomUUID().toString();
    command = CreatePaymentCommand.builder()
        .userId(1L)
        .reservationId(1L)
        .accessKey(accesskey)
        .build();

    
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
    PaymentEvent paymentEvent = new PaymentEvent(reservation.getId(), reservation.getReservedPrice(),
        user.getId(), command.getAccessKey());
  }

  @Test
  @DisplayName("이벤트 리스너 성공 :: 아웃박스 생성")
  void createOutbox_Success() {
    paymentEventListener.createOutbox(paymentEvent);
    verify(paymentOutboxManager).create(paymentEvent);
  }

  @Test
  @DisplayName("이벤트 리스너 실패 :: 아웃박스 생성 실패")
  void createOutbox_Failure() {
    // Arrange: Simulate an exception when create is called
    doThrow(new RuntimeException("Outbox creation failed"))
        .when(paymentOutboxManager).create(paymentEvent);

    // Act & Assert: Verify that the exception is thrown
    assertThatThrownBy(() -> paymentEventListener.createOutbox(paymentEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Outbox creation failed");

    // Optionally verify that create was called once
    verify(paymentOutboxManager).create(paymentEvent);
  }

  @Test
  @DisplayName("이벤트 리스너 성공 :: 메시지 전송")
  void sendMessage_Success() {
    paymentEventListener.sendMessage(paymentEvent);
    verify(paymentMessageSender).send(paymentEvent);
  }

  @Test
  @DisplayName("이벤트 리스너 실패 :: 메시지 전송 실패")
  void sendMessage_Failure() {
    // Arrange: Simulate an exception when send is called
    doThrow(new RuntimeException("Message sending failed"))
        .when(paymentMessageSender).send(paymentEvent);

    // Act & Assert: Verify that the exception is thrown
    assertThatThrownBy(() -> paymentEventListener.sendMessage(paymentEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Message sending failed");

    // Optionally verify that send was called once
    verify(paymentMessageSender).send(paymentEvent);
  }

  @Test
  @DisplayName("이벤트 리스너 성공 :: 토큰 만료")
  void execute_Success_expireToken() throws JsonProcessingException {
    paymentEventListener.expireToken(paymentEvent);
    verify(tokenService).expireToken(user.getId(), command.getAccessKey());
  }

  @Test
  @DisplayName("이벤트 리스너 실패 :: 토큰 만료")
  void execute_Failure_expireToken() throws JsonProcessingException {
    doThrow(new RuntimeException("Token expiration failed"))
        .when(tokenService).expireToken(user.getId(), command.getAccessKey());

    PaymentEvent paymentEvent = new PaymentEvent(reservation.getId(), reservation.getReservedPrice(),
        user.getId(), command.getAccessKey());

    assertThatThrownBy(() -> paymentEventListener.expireToken(paymentEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Token expiration failed");

    // Optionally verify that expireToken was called once
    verify(tokenService, times(1)).expireToken(user.getId(), command.getAccessKey());
  }

}
