package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa;

public enum ReservationStatus {
  OCCUPIED,  // 좌석이 임시 배정된 상태
  PAID,      // 결제가 완료된 상태
  EXPIRED,   // 예약이 만료된 상태
  CANCELLED  // 예약이 취소된 상태
}