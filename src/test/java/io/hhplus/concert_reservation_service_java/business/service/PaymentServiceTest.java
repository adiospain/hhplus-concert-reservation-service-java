package io.hhplus.concert_reservation_service_java.business.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.exception.CustomException;

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
    Payment payment = Payment.createFrom(1L, 10L);
    payment.setIdForTest(2L);

    when(paymentRepository.save(payment)).thenReturn(payment);

    // Act
    Payment savedPayment = service.save(payment);

    // Assert
    assertNotNull(savedPayment);
    assertEquals(2L, savedPayment.getId());
    assertEquals(1L, savedPayment.getUserId());
    assertEquals(10L, savedPayment.getReservationId());

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
    long reserverId = 100L;
    long reservationId = 200L;

    Payment payment = Payment.createFrom(1L, 10L);
    payment.setIdForTest(2L);

    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    // Act
    Payment createdPayment = service.createPayment(reserverId, reservationId);

    // Assert
    assertNotNull(createdPayment);
    assertEquals(reserverId, createdPayment.getUserId());
    assertEquals(reservationId, createdPayment.getReservationId());

    verify(paymentRepository).save(any(Payment.class));
  }
}