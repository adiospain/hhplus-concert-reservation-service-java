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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {
  private final ReservationMapper reservationMapper;
  private final TokenService tokenService;

  private final UserService userService;
  private final ReservationService reservationService;
  private final ConcertService concertService;

  @Override
  @Transactional
  public ReservationDomain execute(CreateReservationCommand command) {

    TokenDomain tokenDomain = tokenService.getToken(command.getUserId(), null);

    if (tokenDomain.getQueuePosition() == 0){
      return processReservation(command, tokenDomain);
    }
    else {
      throw new CustomException(ErrorCode.WAITING_CONTINUE);
    }
  }

  private ReservationDomain processReservation(CreateReservationCommand command, TokenDomain tokenDomain) {
    User user = userService.getUserWithLock(command.getUserId());
    ConcertScheduleSeat concertScheduleSeat = concertService.getConcertScheduleSeat(command.getConcertScheduleId(), command.getSeatId());
    Reservation reservation = createAndSaveReservation(user, concertScheduleSeat);
    return reservationMapper.from(reservation);
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
