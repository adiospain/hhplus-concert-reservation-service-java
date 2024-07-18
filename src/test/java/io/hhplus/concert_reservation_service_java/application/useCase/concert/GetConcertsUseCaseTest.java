package io.hhplus.concert_reservation_service_java.application.useCase.concert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.hhplus.concert_reservation_service_java.domain.concert.ConcertService;
import io.hhplus.concert_reservation_service_java.domain.concert.application.model.ConcertDomain;
import io.hhplus.concert_reservation_service_java.domain.concert.application.port.out.ConcertMapper;
import io.hhplus.concert_reservation_service_java.domain.concert.application.useCase.GetConcertsUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class GetConcertsUseCaseTest {

  private final ConcertService concertService = Mockito.mock(ConcertService.class);
  private final ConcertMapper concertMapper = Mockito.mock(ConcertMapper.class);
  private final GetConcertsUseCase useCase = new GetConcertsUseCaseImpl(concertService, concertMapper);


  @Test
  @DisplayName("모든 콘서트 조회 성공")
  void execute_WithExistingConcerts_ReturnsListOfConcertDTOs() {
    // Given
    List<Concert> concerts = Arrays.asList(
        new Concert(1L, "Concert 1"),
        new Concert(2L, "Concert 2")
    );
    List<ConcertDomain> expectedDomains = Arrays.asList(
        new ConcertDomain(1L, "Concert 1", null),
        new ConcertDomain(2L, "Concert 2", null)
    );

    when(concertService.getAll())
        .thenReturn(concerts);
    when(concertMapper.WithoutConcertScheduleFrom(concerts))
        .thenReturn(expectedDomains);

    // When
    List<ConcertDomain> result = useCase.execute();

    // Then
    assertThat(result).isNotNull().hasSize(2).isEqualTo(expectedDomains);
    verify(concertService).getAll();
    verify(concertMapper).WithoutConcertScheduleFrom(concerts);
  }

  @Test
  @DisplayName("콘서트가 없는 경우 빈 리스트 반환")
  void execute_WithNoConcerts_ReturnsEmptyList() {
    // Given
    when(concertService.getAll())
        .thenReturn(Collections.emptyList());

    // When
    List<ConcertDomain> result = useCase.execute();

    // Then
    assertThat(result).isNotNull().isEmpty();
    verify(concertService).getAll();
    verify(concertMapper).WithoutConcertScheduleFrom(Collections.emptyList());
  }

  @Test
  @DisplayName("ConcertService에서 예외 발생")
  void execute_WhenServiceThrowsException() {
    // Given
    when(concertService.getAll()).thenThrow(new CustomException(ErrorCode.SERVICE));

    // When
    assertThatThrownBy(() -> useCase.execute())
        .isInstanceOf(CustomException.class);

    // Then
    verify(concertService).getAll();
    verify(concertMapper, never()).WithoutConcertScheduleFrom(any(List.class));
  }

  @Test
  @DisplayName("ConcertMapper에서 예외 발생")
  void execute_WhenMapperThrowsException() {
    // Given
    List<Concert> concerts = Arrays.asList(
        new Concert(1L, "Concert 1"),
        new Concert(2L, "Concert 2")
    );
    List<ConcertDomain> expectedDomains = Arrays.asList(
        new ConcertDomain(1L, "Concert 1", null),
        new ConcertDomain(2L, "Concert 2", null)
    );
    when(concertService.getAll()).thenReturn(concerts);
    when(concertMapper.WithoutConcertScheduleFrom(concerts)).thenThrow(new CustomException(ErrorCode.CONCERT_NOT_FOUND));

    // When
    assertThatThrownBy(() -> useCase.execute())
        .isInstanceOf(CustomException.class);

    // Then
    verify(concertService).getAll();
    verify(concertMapper).WithoutConcertScheduleFrom(any(List.class));
  }
}