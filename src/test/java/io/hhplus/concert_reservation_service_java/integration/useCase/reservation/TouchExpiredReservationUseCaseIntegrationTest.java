package io.hhplus.concert_reservation_service_java.integration.useCase.reservation;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.TouchExpiredReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TouchExpiredReservationUseCaseIntegrationTest {

  @Autowired
  private TouchExpiredReservationUseCase touchExpiredReservationUseCase;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private ConcertRepository concertRepository;


  private Concert concert;
  private ConcertSchedule concertSchedule;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("만료된 예약이 있을 경우 - 예약 삭제 진행")
  void execute_WhenExpiredReservationsExist_ShouldDeleteExpiredReservations() {
    // Given
    createReservation(LocalDateTime.now().minusMinutes(16), ReservationStatus.OCCUPIED);
    createReservation(LocalDateTime.now().minusMinutes(14),ReservationStatus.OCCUPIED);
    createReservation(LocalDateTime.now().minusMinutes(5), ReservationStatus.OCCUPIED);

    // When
    touchExpiredReservationUseCase.execute();

    // Then
    List<Reservation> remainingReservations = reservationRepository.findAll();
    assertThat(remainingReservations).hasSize(1);
    assertThat(remainingReservations.get(0).getCreatedAt()).isAfterOrEqualTo(LocalDateTime.now().minusMinutes(15));
  }

  @Test
  @DisplayName("만료된 예약이 없을 경우 - 예약 삭제 진행 안함")
  void execute_WhenNoExpiredReservations_ShouldNotDeleteAnyReservations() {
    // Given
    createReservation(LocalDateTime.now().minusMinutes(14), ReservationStatus.OCCUPIED);
    createReservation(LocalDateTime.now().minusMinutes(5), ReservationStatus.OCCUPIED);

    // When
    touchExpiredReservationUseCase.execute();

    // Then
    List<Reservation> remainingReservations = reservationRepository.findAll();
    assertThat(remainingReservations).hasSize(2);
  }

  @Test
  @DisplayName("예약 상태가 PAID인 경우 - 삭제되지 않음")
  void execute_WhenReservationStatusIsReserved_ShouldNotDeleteReservation() {
    // Given
    createReservation(LocalDateTime.now().minusMinutes(20), ReservationStatus.PAID);
    createReservation(LocalDateTime.now().minusMinutes(16), ReservationStatus.OCCUPIED);

    // When
    touchExpiredReservationUseCase.execute();

    // Then
    List<Reservation> remainingReservations = reservationRepository.findAll();
    assertThat(remainingReservations).hasSize(1);
    assertThat(remainingReservations.get(0).getStatus()).isEqualTo(ReservationStatus.PAID);
  }

  private Reservation createReservation(LocalDateTime createdAt, ReservationStatus status) {
    Reservation reservation = new Reservation();
    reservation.setConcertScheduleId(1L);
    reservation.setStatus(status);
    reservation.setCreatedAt(createdAt);
    return reservationRepository.save(reservation);
  }
}