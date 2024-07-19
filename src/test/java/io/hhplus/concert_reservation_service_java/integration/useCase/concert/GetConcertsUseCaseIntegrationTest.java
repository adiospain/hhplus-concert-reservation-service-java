package io.hhplus.concert_reservation_service_java.integration.useCase.concert;

import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.repository.ConcertRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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