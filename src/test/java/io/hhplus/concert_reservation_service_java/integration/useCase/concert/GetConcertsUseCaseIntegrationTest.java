package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class GetConcertsUseCaseIntegrationTest {

  @Autowired
  private GetConcertsUseCase getConcertsUseCase;

  @Autowired
  private ConcertRepository concertRepository;

  @BeforeEach
  void setUp() {
    //data.sql & schema.sql로 더미 데이터 생성
  }

  @Test
  @DisplayName("모든 콘서트 조회 성공")
  void execute_WithExistingConcerts_ReturnsListOfConcertDTOs() {


    // When
    List<ConcertDomain> result = getConcertsUseCase.execute();

    // Then
    assertThat(result).isNotNull().hasSize(6);
    assertThat(result).extracting("name").containsExactlyInAnyOrder( "아이유 콘서트",
        "뉴진스 하우 스윗",
        "아이브 쇼케이스",
        "아일릿 유유유유유유유 매그내릭",
        "트와이스 원스인어마일",
        "레드벨벳 콘서트");
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
  @DisplayName("모든 콘서트 여러 번 조회 성공")
  void execute_WithExistingConcerts_MultipleExecutions() {
    // Given
    int numberOfExecutions = 5000;
    List<Double> executionTimes = new ArrayList<>();

    // When & Then
    for (int i = 0; i < numberOfExecutions; i++) {
      Instant start = Instant.now();

      try {
        List<ConcertDomain> result = getConcertsUseCase.execute();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        double durationInMillis = duration.toNanos() / 1_000_000.0;
        executionTimes.add(durationInMillis);

        // Then
        assertThat(result).isNotNull().hasSize(6);
        assertThat(result).extracting("name").containsExactlyInAnyOrder(
            "아이유 콘서트",
            "뉴진스 하우 스윗",
            "아이브 쇼케이스",
            "아일릿 유유유유유유유 매그내릭",
            "트와이스 원스인어마일",
            "레드벨벳 콘서트"
        );
      } catch (Exception e) {
        int a = 0;
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
  @DisplayName("콘서트가 없는 경우 빈 리스트 반환")
  void execute_WithNoConcerts_ReturnsEmptyList() {
    // When
    concertRepository.deleteAll();
    List<ConcertDomain> result = getConcertsUseCase.execute();

    // Then
    assertThat(result).isNotNull().isEmpty();
  }
}