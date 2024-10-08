package io.hhplus.concert_reservation_service_java.unit.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.useCase.CreateReservationUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.ReservationMapper;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.assertj.core.api.AssertionsForClassTypes;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateReservationUseCaseTest {

  private final UserService userService = Mockito.mock(UserService.class);
  private final ReservationService reservationService = Mockito.mock(ReservationService.class);
  private final ConcertService concertService = Mockito.mock(ConcertService.class);
  private final ReservationMapper reservationMapper = Mockito.mock(ReservationMapper.class);
  private final CreateReservationUseCase useCase = new CreateReservationUseCaseImpl(
      userService, reservationService, concertService, reservationMapper);

  @BeforeEach
  void setUp() {

  }

  @Test
  void 예약_성공() {
    long reserverId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(reserverId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId).build();

    User reserver = new User(reserverId, 34000);

    ConcertSchedule concertSchedule = ConcertSchedule.builder()
            .id(concertScheduleId)
                .build();

    Seat seat = Seat.builder()
        .id(seatId)
        .build();


    ConcertScheduleSeat concertScheduleSeat = new ConcertScheduleSeat();
    concertScheduleSeat.setId(1L);
    concertScheduleSeat.setConcertSchedule(concertSchedule);
    concertScheduleSeat.setSeat(seat);
    concertScheduleSeat.setPrice(25000);


    Reservation reservation = Reservation.builder()
        .id(1L)
        .userId(reserver.getId())
        .concertScheduleId(concertScheduleSeat.getConcertSchedule().getId())
        .seatId(concertScheduleSeat.getSeat().getId())
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
    ReservationDomain reservationDomain = new ReservationDomain(reservation.getId(), reservation.getCreatedAt(), reservation.getCreatedAt().plusMinutes(5));

    when(userService.getUserWithLock(command.getUserId())).thenReturn(reserver);
    when(concertService.getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId())).thenReturn(concertScheduleSeat);
    when(reservationService.saveToCreate(any(Reservation.class))).thenReturn(reservation);
    when(reservationMapper.from(reservation)).thenReturn(reservationDomain);

    ReservationDomain result = useCase.execute(command);


    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(reservation.getId());
    assertThat(result.getCreatedAt()).isEqualTo(reservation.getCreatedAt());

    verify(userService).getUserWithLock(command.getUserId());
    verify(concertService).getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId());
    verify(reservationService).saveToCreate(any(Reservation.class));
    verify(reservationMapper).from(reservation);
  }

  @Test
  void 예약_실패_이미_예약된_좌석() {
    long userId = 1L;
    long concertScheduleId = 1L;
    long seatId = 1L;
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(userId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId)
        .build();

    User user = new User(userId, 34000);

    ConcertSchedule concertSchedule = ConcertSchedule.builder()
        .id(concertScheduleId)
        .build();
        ;

    Seat seat = Seat.builder()
        .id(seatId)
        .build();


    ConcertScheduleSeat concertScheduleSeat = new ConcertScheduleSeat();
    concertScheduleSeat.setId(1L);
    concertScheduleSeat.setConcertSchedule(concertSchedule);
    concertScheduleSeat.setSeat(seat);
    concertScheduleSeat.setPrice(25000);


    Reservation reservation = Reservation.builder()
        .id(1L)
        .userId(user.getId())
        .concertScheduleId(concertScheduleSeat.getConcertSchedule().getId())
        .seatId(concertScheduleSeat.getSeat().getId())
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
    ReservationDomain reservationDomain = new ReservationDomain(reservation.getId(), reservation.getCreatedAt(), reservation.getCreatedAt().plusMinutes(5));

    when(userService.getUserWithLock(command.getUserId())).thenReturn(user);
    when(concertService.getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId())).thenReturn(concertScheduleSeat);

    when(reservationService.saveToCreate(any(Reservation.class))).thenThrow(new CustomException(ErrorCode.ALREADY_RESERVED));

    //when(reservationMapper.from(reservation)).thenReturn(reservationDomain);


    AssertionsForClassTypes.assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_RESERVED);

    verify(userService).getUserWithLock(command.getUserId());
    verify(concertService).getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId());
    verify(reservationService).saveToCreate(any(Reservation.class));
    verify(reservationMapper, never()).from(any(Reservation.class));
  }


}