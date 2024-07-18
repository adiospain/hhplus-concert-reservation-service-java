package io.hhplus.concert_reservation_service_java.application.useCase.concert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleSeatDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableSeatsCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertScheduleSeatMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetAvailableSeatsUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
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


  private final ConcertService concertservice = Mockito.mock(ConcertService.class);
  private final ReservationService reservationService = Mockito.mock(ReservationService.class);
  private final ConcertScheduleSeatMapper concertScheduleSeatMapper = Mockito.mock(ConcertScheduleSeatMapper.class);;
  private final GetAvailableSeatsUseCae useCase = new GetAvailableSeatsUseCaseImpl(concertservice, reservationService, concertScheduleSeatMapper);


  @Test
  @DisplayName("사용 가능한 좌석 조회 성공")
  void getAvailableSeats_ReturnsListOfConcertScheduleSeats() {
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

    when(concertservice.getSeatsByConcertScheduleId(concertScheduleId))
        .thenReturn(allSeats);
    when(reservationService.getSeatsIdByconcertScheduleId(concertScheduleId))
        .thenReturn(new HashSet<>(Arrays.asList(2L, 45L, 3L)));
    when(concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, reservedSeatIds))
        .thenReturn(expectedDomains);

    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    assertThat(result).isNotNull().hasSize(3).isEqualTo(expectedDomains);

    verify(concertservice).getSeatsByConcertScheduleId(concertScheduleId);
    verify(reservationService).getSeatsIdByconcertScheduleId(concertScheduleId);
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

    when(concertservice.getSeatsByConcertScheduleId(concertScheduleId))
        .thenReturn(allSeats);
    when(reservationService.getSeatsIdByconcertScheduleId(concertScheduleId))
        .thenReturn(new HashSet<>(reservedSeatIds));
    when(concertScheduleSeatMapper.AvailableSeatsFrom(allSeats, new HashSet<>(reservedSeatIds)))
        .thenReturn(expectedDomains);

    // When
    List<ConcertScheduleSeatDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().hasSize(0);

    verify(concertservice).getSeatsByConcertScheduleId(concertScheduleId);
    verify(reservationService).getSeatsIdByconcertScheduleId(concertScheduleId);
    verify(concertScheduleSeatMapper).AvailableSeatsFrom(allSeats, reservedSeatIds);
  }

  @Test
  @DisplayName("ConcertService에서 예외 발생")
  void RepositoryThrowsException() {
    // Given
    Long concertId = 3L;
    Long concertScheduleId = 1L;
    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId).build();

    when(concertservice.getSeatsByConcertScheduleId(concertScheduleId))
        .thenThrow(new CustomException(ErrorCode.SERVICE));

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class);

    verify(concertservice).getSeatsByConcertScheduleId(concertScheduleId);
    verify(concertScheduleSeatMapper, never()).AvailableSeatsFrom(any(), any());
  }
}