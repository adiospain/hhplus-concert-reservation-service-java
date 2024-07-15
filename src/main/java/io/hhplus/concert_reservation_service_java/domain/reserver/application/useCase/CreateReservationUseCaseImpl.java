package io.hhplus.concert_reservation_service_java.domain.reserver.application.useCase;


import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.token.application.service.TokenWithPosition;
import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reserver.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.application.port.out.ReservationMapper;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class CreateReservationUseCaseImpl implements CreateReservationUseCase {
  private final ReserverRepository reserverRepository;
  private final ReservationRepository reservationRepository;
  private final ConcertRepository concertRepository;
  private final ReservationMapper reservationMapper;
  private final TokenService tokenService;

  @Override
  @Transactional
  public ReservationDomain execute(CreateReservationCommand command) {
    Reserver reserver = findReserver(command.getReserverId());

    TokenWithPosition tokenWithPosition = tokenService.getToken(command.getReserverId());

    if (tokenWithPosition.getQueuePosition() == 1){
      ConcertScheduleSeat concertScheduleSeat = findConcertScheduleSeat(command);
      Reservation reservation = createReservation(reserver, concertScheduleSeat);
      Reservation savedReservation = saveReservation(reservation, command);
      return reservationMapper.from(savedReservation);
    }
    else {
      //waiting Queue logic
      throw new CustomException(ErrorCode.UNSPECIFIED_FAIL);
    }
  }

  private Reservation createReservation(Reserver reserver, ConcertScheduleSeat concertScheduleSeat) {
    return Reservation.builder()
        .reserver(reserver)
        .concertScheduleId(concertScheduleSeat.getConcertSchedule().getId())
        .seatId(concertScheduleSeat.getSeat().getId())
        .status(ReservationStatus.OCCUPIED)
        .createdAt(LocalDateTime.now())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
  }

  private Reserver findReserver(long reserverId) {
    return reserverRepository.findById(reserverId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  private ConcertScheduleSeat findConcertScheduleSeat(CreateReservationCommand command) {
    return concertRepository.findConcertSceduleSeatByconcertScheduleIdAndseatId(command.getConcertScheduleId(), command.getSeatId())
        .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND));
  }


  //오류 처리 시, reservation의 필드값이 이상하게 출력 되는 상황 발생하여, command의 필드값 적용.
  private Reservation saveReservation(Reservation reservation, CreateReservationCommand command) {
    try {
      return reservationRepository.save(reservation);
    } catch (DataIntegrityViolationException e) {
      // 이미 임시 배정된 좌석 예약 시도 시 발생하는 예외 처리
      if (e.getMessage().contains("unique index") ||
          e.getMessage().contains("unique constraint") ||
          e.getMessage().contains("primary key violation") ||
          e.getMessage().contains("UK_concert_schedule_seat")) {
        throw new CustomException(ErrorCode.ALREADY_RESERVED,
            "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
            "콘서트 날짜 ID: " + command.getConcertScheduleId() +
                ", 좌석 ID: " + command.getSeatId());
      } else {
        throw new CustomException(ErrorCode.RESERVATION_FAILED,
            "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
            "콘서트 날짜 ID: " + command.getConcertScheduleId() +
                ", 좌석 ID: " + command.getSeatId());
      }
    } catch (Exception e) {
      throw new CustomException(ErrorCode.RESERVATION_FAILED,
          "예약 중 예기치 않은 오류가 발생했습니다." +
              "콘서트 날짜 : " + reservation.getConcertScheduleId() + "좌석 : " + reservation.getSeatId() +
              "콘서트 날짜 ID: " + command.getConcertScheduleId() +
              ", 좌석 ID: " + command.getSeatId());
    }
  }
}
