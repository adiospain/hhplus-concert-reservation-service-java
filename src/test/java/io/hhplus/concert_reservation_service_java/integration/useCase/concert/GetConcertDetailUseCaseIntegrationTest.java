package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;

import io.hhplus.concert_reservation_service_java.domain.concert.application.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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