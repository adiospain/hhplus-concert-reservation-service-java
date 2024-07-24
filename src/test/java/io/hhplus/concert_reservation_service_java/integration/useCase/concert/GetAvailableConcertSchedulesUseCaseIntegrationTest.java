package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertScheduleDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
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
class GetAvailableConcertSchedulesUseCaseIntegrationTest {

  @Autowired
  private GetAvailableConcertSchedulesUseCase useCase;

  @Autowired
  private ConcertRepository concertRepository;

  private Long concertId;

  @BeforeEach
  void setUp() {
    Concert concert = new Concert(11L, "국립국악원 정기공연");
    concert = concertRepository.save(concert);
    concertId = concert.getId();

    LocalDateTime now = LocalDateTime.now();
    for (int i = 1; i <= 5; i++) {
      ConcertSchedule schedule = new ConcertSchedule(concert, now.plusDays(i), 100+i);;
      concertRepository.save(schedule);
    }
  }

  @Test
  @DisplayName("예약 가능한 콘서트 일정 조회 성공")
  void getAvailableSchedules_ReturnsListOfConcertScheduleDTOs() {
    // Given
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId).build();

    // When
    List<ConcertScheduleDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().hasSize(5);
    assertThat(result).allSatisfy(schedule -> {
      assertThat(schedule.getId()).isNotNull();
      assertThat(schedule.getStartAt()).isAfter(LocalDateTime.now());
      assertThat(schedule.getCapacity()).isGreaterThan(100);
    });
  }

  @Test
  @DisplayName("존재하지 않는 콘서트 ID로 조회 시 빈 리스트 반환")
  void getNonExistentConcert_ReturnsEmptyList() {
    // Given
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(999L).build();

    // When
    List<ConcertScheduleDomain> result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("유효하지 않은 콘서트 ID로 조회 시 예외 발생")
  void getInvalidConcertId_ThrowsException() {
    // Given
    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(-1L).build();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_CONCERT);
        });
  }
}