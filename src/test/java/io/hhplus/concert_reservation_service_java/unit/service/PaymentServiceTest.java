package io.hhplus.concert_reservation_service_java.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.exception.CustomException;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.business.service.PaymentServiceImpl;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.PaymentRepository;
import org.mockito.Mockito;

class PaymentServiceTest {
  private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
  private final PaymentService service = new PaymentServiceImpl(paymentRepository);

  @Test
  @DisplayName("결제 완료")
  void testSave_SuccessfulSave() {
    // Arrange
    long id = 5L;
    long userId = 2L;
    long concertScheduleId = 4L;
    long seatId = 5L;
    int price = 300;


    Payment payment = Payment.builder()
        .id(5L)
        .userId(userId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId)
        .reservedPrice(price)
        .createdAt(LocalDateTime.now())
        .build();
    when(paymentRepository.save(payment)).thenReturn(payment);

    // Act
    Payment savedPayment = service.save(payment);

    // Assert
    assertNotNull(savedPayment);
    assertEquals(id, savedPayment.getId());
    assertEquals(userId, savedPayment.getUserId());
    assertEquals(concertScheduleId, savedPayment.getConcertScheduleId());
    assertEquals(seatId, savedPayment.getSeatId());
    assertEquals(price, savedPayment.getReservedPrice());

    verify(paymentRepository).save(payment);
  }

  @Test
  @DisplayName("유효하지 않은 결제 정보")
  void testSave_NullPayment() {
    // Arrange
    Payment nullPayment = null;
    // Act & Assert
    assertThrows(
        CustomException.class, () -> service.save(nullPayment));
    verify(paymentRepository, never()).save(any());
  }

  @Test
  @DisplayName("결제 생성")
  void testCreatePayment_SuccessfulCreation() {
    // Arrange
    long id = 5L;
    long userId = 2L;
    long concertScheduleId = 4L;
    long seatId = 5L;
    int price = 300;


    Payment payment = Payment.builder()
        .id(5L)
        .userId(userId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId)
        .reservedPrice(price)
        .createdAt(LocalDateTime.now())
        .build();

    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    // Act
    Payment createdPayment = service.createPayment(userId, concertScheduleId, seatId, price);

    // Assert
    assertNotNull(createdPayment);
    assertEquals(userId, createdPayment.getUserId());
    assertEquals(concertScheduleId, createdPayment.getConcertScheduleId());
    assertEquals(seatId, createdPayment.getSeatId());
    assertEquals(price, createdPayment.getReservedPrice());
    verify(paymentRepository).save(any(Payment.class));
  }
}