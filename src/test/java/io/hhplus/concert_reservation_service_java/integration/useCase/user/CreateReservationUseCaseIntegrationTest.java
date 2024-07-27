package io.hhplus.concert_reservation_service_java.integration.useCase.user;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.repository.ReservationRepository;
import io.hhplus.concert_reservation_service_java.domain.user.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CreateReservationUseCaseIntegrationTest {

  @Autowired
  private CreateReservationUseCase createReservationUseCase;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ConcertRepository concertRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  private User user;
  private Concert concert;
  private ConcertSchedule concertSchedule;
  private Seat seat;
  private ConcertScheduleSeat concertScheduleSeat;

  @BeforeEach
  void setUp() {
    concert = Concert.builder()
        .id(1L)
        .name("아이유 콘서트")
        .build();

    concertSchedule = ConcertSchedule.builder()
        .id(2L)
        .concert(concert)
        .build();

    seat = Seat.builder()
        .id(1L)
        .seatNumber(1)
        .build();

    concertScheduleSeat = ConcertScheduleSeat.builder()
        .id(51L)
        .concertSchedule(concertSchedule)
        .seat(seat)
        .price(100000)
        .build();

    user = new User(1L, 34000);
    user = userRepository.save(user);
  }

  @Test
  void 예약_성공() {
    // Given
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(user.getId())
        .concertScheduleId(concertSchedule.getId())
        .seatId(seat.getId())
        .build();

    // When
    ReservationDomain result = createReservationUseCase.execute(command);

    ReservationDomain result1 = createReservationUseCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getCreatedAt()).isNotNull();
    Reservation savedReservation = reservationRepository.findById(result.getId()).orElseThrow();
    assertThat(savedReservation.getUser().getId()).isEqualTo(user.getId());
    assertThat(savedReservation.getConcertScheduleId()).isEqualTo(concertSchedule.getId());
    assertThat(savedReservation.getSeatId()).isEqualTo(seat.getId());
    assertThat(savedReservation.getStatus()).isEqualTo(ReservationStatus.OCCUPIED);
    assertThat(savedReservation.getReservedPrice()).isEqualTo(concertScheduleSeat.getPrice());
  }

  @Test
  void 사용자_없음_예외() {
    // Given
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(999L)
        .concertScheduleId(concertSchedule.getId())
        .seatId(seat.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
  }

  @Test
  void 콘서트_일정_없음_예외() {
    // Given
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(user.getId())
        .concertScheduleId(999L)
        .seatId(seat.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_SCHEDULE_NOT_FOUND);
  }

  @Test
  void 좌석_없음_예외() {
    // Given
    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(user.getId())
        .concertScheduleId(concertSchedule.getId())
        .seatId(999L)
        .build();

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND);
  }

  @Test
  void 이미_예약된_좌석_예외() {
    // Given
    Reservation existingReservation = Reservation.builder()
        .user(user)
        .concertScheduleId(2L)
        .seatId(seat.getId())
        .status(ReservationStatus.OCCUPIED)
        .build();
    reservationRepository.save(existingReservation);

    CreateReservationCommand command = CreateReservationCommand.builder()
        .userId(user.getId())
        .concertScheduleId(concertSchedule.getId())
        .seatId(seat.getId())
        .build();

    // When & Then
    assertThatThrownBy(() -> createReservationUseCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_RESERVED);
  }

  @Test
  void 동시에_여러_사용자가_같은_좌석_예약_요청시_하나만_성공해야함() throws InterruptedException {
    // Given
    int numberOfThreads = 1000;
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    AtomicInteger reservedSuccessCount = new AtomicInteger(0);
    AtomicInteger reservedFailCount = new AtomicInteger(0);


    // When
    for (int i = 0; i < numberOfThreads; i++) {
      CreateReservationCommand command = CreateReservationCommand.builder()
          .userId(user.getId()+ i%6)
          .concertScheduleId(concertSchedule.getId())
          .seatId(seat.getId())
          .build();
      executorService.submit(() -> {
        try {
          createReservationUseCase.execute(command);
          reservedSuccessCount.incrementAndGet();
        } catch (Exception e) {
          if (e instanceof CustomException){
            if (((CustomException) e).getErrorCode() == ErrorCode.ALREADY_RESERVED) {
              reservedFailCount.incrementAndGet();
            }
            if (((CustomException) e).getErrorCode() == ErrorCode.LOCK_ACQUISITION_FAIL) {
              reservedFailCount.incrementAndGet();
            }
          }
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기

    // Then
    assertThat(reservedSuccessCount.get()).isEqualTo(1);
    assertThat(reservedFailCount.get()).isEqualTo(numberOfThreads - 1);

    List<Reservation> reservations = reservationRepository.findAll();
    assertThat(reservations.size()).isEqualTo(1);
  }
}