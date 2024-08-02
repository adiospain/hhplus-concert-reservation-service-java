package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Nested
@SpringBootTest
@Transactional
class GetConcertDetailUseCaseIntegrationTest {

  @Autowired
  private GetConcertDetailUseCase useCase;

  @Autowired
  private ConcertRepository concertRepository;

  private Long concertId;

  @BeforeEach
  void setUp() {
    concertId = 4L;
    //data.sql & schema.sql로 더미 데이터 생성
  }

  @Test
  @DisplayName("콘서트 상세 조회 성공")
  void getConcerts_Success() {
    // Given
    GetConcertDetailCommand command = GetConcertDetailCommand.builder()
        .concertId(concertId)
        .build();

    // When
    ConcertDomain result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(concertId);
    assertThat(result.getName()).isEqualTo(
        "아일릿 유유유유유유유 매그내릭");
    assertThat(result.getSchedules()).hasSize(1);
    assertThat(result.getSchedules().get(0).getStartAt()).isEqualTo(LocalDateTime.of(2025, 11, 11, 19, 0, 0));
  }

  public Duration calculateAverageTime(List<Double> durations) {
    if (durations.isEmpty()) {
      return Duration.ZERO;
    }
    double totalNanos = durations.stream()
        .mapToLong(millis -> (long) (millis * 1_000_000))
        .sum();
    double averageNanos = totalNanos / durations.size();
    return Duration.ofNanos((long) averageNanos);
  }
  @Test
  @DisplayName("콘서트 상세 여러 번 조회 성공")
  void getConcerts_Detail_MultipleExecutions() {
    // Given
    int numberOfExecutions = 3000;
    List<Double> executionTimes = new ArrayList<>();

    // When & Then
    for (int i = 0; i < numberOfExecutions; i++) {
      Instant start = Instant.now();

      try {
        GetConcertDetailCommand command = GetConcertDetailCommand.builder()
            .concertId(concertId)
            .build();

        ConcertDomain result = useCase.execute(command);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        double durationInMillis = duration.toNanos() / 1_000_000.0;
        executionTimes.add(durationInMillis);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(concertId);
        assertThat(result.getName()).isEqualTo("아일릿 유유유유유유유 매그내릭");
        assertThat(result.getSchedules()).hasSize(1);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // 실행 시간 분석
    double firstExecutionTime = executionTimes.get(0);
    Duration averageSubsequentTime = calculateAverageTime(executionTimes.subList(1, executionTimes.size()));

    System.out.println("캐싱 없는 첫 실행시간: " + firstExecutionTime + " ms");
    System.out.printf("캐싱 있는 실행시간의 평균값: %.6f ms%n", averageSubsequentTime.toNanos() / 1_000_000.0);

    // 캐싱을 사용한 실행이 첫 실행보다 빠른가?
    assertThat(averageSubsequentTime.toNanos() / 1_000_000.0).isLessThan(firstExecutionTime);
  }

  @Test
  @DisplayName("존재하지 않는 콘서트 ID로 조회 시 예외 발생")
  void execute_WithNonExistentConcertId_ThrowsException() {
    // Given
    GetConcertDetailCommand command = GetConcertDetailCommand.builder()
        .concertId(999L)
        .build();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);
  }

  @Test
  @DisplayName("ConcertService에서 null 반환 시 예외 발생")
  void execute_WithNullFromService_ThrowsException() {
    // Given
    GetConcertDetailCommand command = GetConcertDetailCommand.builder()
        .concertId(999L)
        .build();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);
  }
}