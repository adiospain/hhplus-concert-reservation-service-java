package io.hhplus.concert_reservation_service_java.application.concertScheduleSeat.port.in.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleSeatMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetAvailableSeatsUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetAvailableSeatsUseCaseTest {


  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ReservationRepository reservationRepository = Mockito.mock(ReservationRepository.class);
  private final ConcertScheduleSeatMapper concertScheduleSeatMapper = Mockito.mock(ConcertScheduleSeatMapper.class);;
  private final GetAvailableSeatsUseCae useCase = new GetAvailableSeatsUseCaseImpl(concertRepository, reservationRepository, concertScheduleSeatMapper);


  @Test
  @DisplayName("사용 가능한 좌석 조회 성공")
  void getAvailableSeats_ReturnsListOfConcertScheduleSeatDTOs() {
    // Given
    Long concertId = 3L;
    Long concertScheduleId = 1L;
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId).build();


    List<Seat> allSeats = new ArrayList<>();
    for (int i = 1; i <= 50; i++) {
      allSeats.add(new Seat((long)i, i));
    }
    Set<Long> reservedSeatIds = new HashSet<>(Arrays.asList(2L, 45L, 3L));

    List<ConcertScheduleSeatDomain> expectedDomains = Arrays.asList(
        new ConcertScheduleSeatDomain(2L, 2),
        new ConcertScheduleSeatDomain(45L, 45),
        new ConcertScheduleSeatDomain(3L, 3)
    );

    when(concertRepository.findSeatsByConcertScheduleId(concertScheduleId))
        .thenReturn(allSeats);
    when(reservationRepository.findSeatIdByconcertScheduleId(concertScheduleId))
        .thenReturn(Arrays.asList(2L, 45L, 3L));
    when(concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, reservedSeatIds))
        .thenReturn(expectedDomains);

    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    assertThat(result).isNotNull().hasSize(3).isEqualTo(expectedDomains);

    verify(concertRepository).findSeatsByConcertScheduleId(concertScheduleId);
    verify(reservationRepository).findSeatIdByconcertScheduleId(concertScheduleId);
    verify(concertScheduleSeatMapper).AvailableSeatsFrom(allSeats, reservedSeatIds);
  }

  @Test
  @DisplayName("모든 좌석이 예약된 경우 - EmptyList 반환")
  void getNoAvailableSeat_ReturnsEmptyList() {
    // Given
    Long concertId = 3L;
    Long concertScheduleId = 1L;
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId).build();

    List<Seat> allSeats = new ArrayList<>();
    Set<Long> reservedSeatIds = new HashSet<>();
    for (int i = 1; i <= 50; i++) {
      Long seatId = (long) i;
      allSeats.add(new Seat(seatId, i));
      reservedSeatIds.add(seatId);
    }

    List<ConcertScheduleSeatDomain> expectedDomains = Collections.emptyList();

    when(concertRepository.findSeatsByConcertScheduleId(concertScheduleId))
        .thenReturn(allSeats);
    when(reservationRepository.findSeatIdByconcertScheduleId(concertScheduleId))
        .thenReturn(new ArrayList<>(reservedSeatIds));
    when(concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, reservedSeatIds))
        .thenReturn(expectedDomains);

    // When
    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().hasSize(0);

    verify(concertRepository).findSeatsByConcertScheduleId(concertScheduleId);
    verify(reservationRepository).findSeatIdByconcertScheduleId(concertScheduleId);
    verify(concertScheduleSeatMapper).AvailableSeatsFrom(allSeats, reservedSeatIds);
  }

  @Test
  @DisplayName("Repository에서 예외 발생")
  void RepositoryThrowsException() {
    // Given
    Long concertId = 3L;
    Long concertScheduleId = 1L;
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId).build();

    when(concertRepository.findSeatsByConcertScheduleId(concertScheduleId))
        .thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class);

    verify(concertRepository).findSeatsByConcertScheduleId(concertScheduleId);
    verify(concertScheduleSeatMapper, never()).AvailableSeatsFrom(any(), any());
  }
}