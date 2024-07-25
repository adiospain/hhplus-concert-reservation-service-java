package io.hhplus.concert_reservation_service_java.domain.user.application.useCase;


import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.user.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.out.ReservationMapper;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
@Slf4j
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {

  private final UserService userService;
  private final ReservationService reservationService;
  private final ConcertService concertService;
  private final ReservationMapper reservationMapper;

  @Override
  @Transactional
  public ReservationDomain execute(CreateReservationCommand command) {
    long startTime = System.nanoTime();
    User user = userService.getUserWithLock(command.getUserId());
    ConcertScheduleSeat concertScheduleSeat = concertService.getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId());
    Reservation reservation = createAndSaveReservation(user, concertScheduleSeat); //Included repository.save
    ReservationDomain reservationDomain = reservationMapper.from(reservation);

    long endTime = System.nanoTime();
    long durationNanos = endTime - startTime;
    double durationMillis = durationNanos / 1_000_000.0;

    log.info("execute::userId={}, concertScheduleId={}, seatId={}, Total Duration: {} ms",
        command.getUserId(), command.getConcertScheduleId(), command.getSeatId(), durationMillis);

    return reservationDomain;
  }

  private Reservation createAndSaveReservation(User user, ConcertScheduleSeat concertScheduleSeat) {
    Reservation savedReservation = Reservation.builder()
        .user(user)
        .concertScheduleId(concertScheduleSeat.getConcertSchedule().getId())
        .seatId(concertScheduleSeat.getSeat().getId())
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
    return reservationService.saveToCreate(savedReservation);
  }
}
