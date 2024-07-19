package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class GetAvailableSeatsUseCaseIntegrationTest {

  @Autowired
  private GetAvailableSeatsUseCae useCase;

  @Autowired
  private ConcertRepository concertRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  private Long concertId;
  private Long concertScheduleId;

  @BeforeEach
  void setUp() {
    //data.sql & schema.sql로 더미 데이터 생성
  }

  private void reserveSeat(Seat seat, ConcertSchedule schedule) {
    Reservation reservation = new Reservation();
    reservation.setSeatId(seat.getId());
    reservation.setConcertScheduleId(schedule.getId());
    reservation.setStatus(ReservationStatus.OCCUPIED);
    reservation.setPaidAt(null);
    reservationRepository.save(reservation);
  }

  @Test
  @DisplayName("사용 가능한 좌석 조회 성공")
  void getAvailableSeats_ReturnsListOfConcertScheduleSeats() {
    // Given
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId)
        .build();

    // When
    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(47); // 50 total seats - 3 reserved
    assertThat(result).noneMatch(seat -> seat.getSeatNumber() == 2 || seat.getSeatNumber() == 3 || seat.getSeatNumber() == 45);
  }

  @Test
  @DisplayName("모든 좌석이 예약된 경우 - EmptyList 반환")
  void getNoAvailableSeat_ReturnsEmptyList() {
    // Given
    ConcertSchedule concertSchedule = concertRepository.findConcertScheduleByConcertSceduleId(1L).orElseThrow();
    List<Seat> allSeats = concertRepository.findSeatsByConcertScheduleId(1L);

    // Reserve all seats
    for (Seat seat : allSeats) {
      reserveSeat(seat, concertSchedule);
    }

    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId)
        .build();

    // When
    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 콘서트 스케줄 ID로 조회 시 예외 발생")
  void getNonExistentConcertSchedule_ThrowsException() {
    // Given
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(999L)
        .build();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND);
        });
  }
}