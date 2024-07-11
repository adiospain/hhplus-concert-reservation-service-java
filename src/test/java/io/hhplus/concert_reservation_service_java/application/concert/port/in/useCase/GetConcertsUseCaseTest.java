package io.hhplus.concert_reservation_service_java.application.concert.port.in.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.concert.ConcertRepository;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class GetConcertsUseCaseTest {

  private final ConcertRepository concertRepository = Mockito.mock(ConcertRepository.class);
  private final ConcertMapper concertMapper = Mockito.mock(ConcertMapper.class);
  private final GetConcertsUseCase useCase = new GetConcertsUseCaseImpl(concertRepository, concertMapper);


  @Test
  @DisplayName("모든 콘서트 조회 성공")
  void execute_WithExistingConcerts_ReturnsListOfConcertDTOs() {
    // Given
    List<Concert> concerts = Arrays.asList(
        new Concert(1L, "Concert 1"),
        new Concert(2L, "Concert 2")
    );
    List<ConcertDTO> expectedDTOs = Arrays.asList(
        new ConcertDTO(1L, "Concert 1", null),
        new ConcertDTO(2L, "Concert 2", null)
    );

    when(concertRepository.findAll())
        .thenReturn(concerts);
    when(concertMapper.WithoutConcertScheduleFrom(concerts))
        .thenReturn(expectedDTOs);

    // When
    List<ConcertDTO> result = useCase.execute();

    // Then
    assertThat(result).isNotNull().hasSize(2).isEqualTo(expectedDTOs);

    verify(concertRepository).findAll();
    verify(concertMapper).WithoutConcertScheduleFrom(concerts);
  }

  @Test
  @DisplayName("콘서트가 없는 경우 빈 리스트 반환")
  void execute_WithNoConcerts_ReturnsEmptyList() {
    // Given
    when(concertRepository.findAll()).thenReturn(Collections.emptyList());
    when(concertMapper.WithoutConcertScheduleFrom(Collections.emptyList())).thenReturn(Collections.emptyList());

    // When
    List<ConcertDTO> result = useCase.execute();

    // Then
    assertThat(result).isNotNull().isEmpty();

    verify(concertRepository).findAll();
    verify(concertMapper).WithoutConcertScheduleFrom(Collections.emptyList());
  }

  @Test
  @DisplayName("Repository에서 예외 발생")
  void execute_WhenRepositoryThrowsException() {
    // Given
    when(concertRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    // When & Then
    assertThatThrownBy(() -> useCase.execute())
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");

    verify(concertRepository).findAll();
    verify(concertMapper, never()).WithoutConcertScheduleFrom(any(List.class));
  }
}