package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event;

import io.hhplus.concert_reservation_service_java.domain.common.DataPlatformClient;
import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Getter
@AllArgsConstructor
public class PaymentEvent implements CustomEvent {
  private Reservation reservation;
  private User user;
  private Payment payment;

  private PaymentOutbox paymentOutbox;

  public PaymentEvent(Reservation reservation, User user, Payment payment) {
    this.reservation = reservation;
    this.user = user;
    this.payment = payment;
    this.paymentOutbox = new PaymentOutbox(this.toString(), false);
  }

  @Override
  public Outbox getOutbox() {
    return this.paymentOutbox;
  }
}