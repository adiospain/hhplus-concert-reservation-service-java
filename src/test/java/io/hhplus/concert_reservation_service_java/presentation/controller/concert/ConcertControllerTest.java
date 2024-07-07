package io.hhplus.concert_reservation_service_java.presentation.controller.concert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.hhplus.concert_reservation_service_java.application.concert.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private GetConcertsUseCase getConcertsUseCase;
  @MockBean
  private GetConcertDetailUseCase getConcertDetailUseCase;

  @Test
  @DisplayName("콘서트 목록 조회")
  void getConcert_Success() throws Exception{
    List<ConcertDTO> result = new ArrayList<>();
    LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 1, 10, 0, 0);

    for (int i = 0; i < 3; ++i){
      ConcertScheduleDTO concertSchedule1 = new ConcertScheduleDTO(4L+i, fixedDateTime, 54);
      ConcertScheduleDTO concertSchedule2 = new ConcertScheduleDTO(4L+i+3, fixedDateTime.plusDays(3), 54);
      List<ConcertScheduleDTO> concertScheduleDTOList = new ArrayList<>();
      concertScheduleDTOList.add(concertSchedule1);
      concertScheduleDTOList.add(concertSchedule2);
      ConcertDTO concertDTO = new ConcertDTO(4L+i, "아이유"+i, concertScheduleDTOList);
      result.add(concertDTO);
    }

    when(getConcertsUseCase.execute()).thenReturn(result);

    mockMvc.perform(get("/api/concerts"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.concerts.content").isArray())
        .andExpect(jsonPath("$.concerts.content.length()").value(3))
        .andExpect(jsonPath("$.concerts.content[0].id").value(4))
        .andExpect(jsonPath("$.concerts.content[0].name").value("아이유0"))
        .andExpect(jsonPath("$.concerts.content[0].schedules[0].id").value(4))
        .andExpect(jsonPath("$.concerts.content[0].schedules[0].capacity").value(54))
        .andExpect(jsonPath("$.concerts.content[0].schedules[0].startAt").value(fixedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .andExpect(jsonPath("$.concerts.content[1].id").value(5))
        .andExpect(jsonPath("$.concerts.content[1].name").value("아이유1"))
        .andExpect(jsonPath("$.concerts.content[0].schedules[0].id").value(4))
        .andExpect(jsonPath("$.concerts.content[1].schedules[0].capacity").value(54))
        .andExpect(jsonPath("$.concerts.content[1].schedules[1].startAt").value(fixedDateTime.plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .andExpect(jsonPath("$.concerts.content[2].id").value(6))
        .andExpect(jsonPath("$.concerts.content[2].name").value("아이유2"))
        .andExpect(jsonPath("$.concerts.content[0].schedules[1].id").value(7))
        .andExpect(jsonPath("$.concerts.content[2].schedules[0].capacity").value(54))
        .andExpect(jsonPath("$.concerts.content[2].schedules[0].startAt").value(fixedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
  }

  @Test
  @DisplayName("콘서트 상세 조회")
  void getConcertDetail_success() throws Exception {
    List<ConcertDTO> result = new ArrayList<>();
    LocalDateTime fixedDateTime = LocalDateTime.of(2025, 6, 1, 10, 0, 0);

    for (int i = 0; i < 3; ++i){
      ConcertScheduleDTO concertSchedule1 = new ConcertScheduleDTO(4L+i, fixedDateTime, 54);
      ConcertScheduleDTO concertSchedule2 = new ConcertScheduleDTO(4L+i+3, fixedDateTime.plusDays(3), 54);
      List<ConcertScheduleDTO> concertScheduleDTOList = new ArrayList<>();
      concertScheduleDTOList.add(concertSchedule1);
      concertScheduleDTOList.add(concertSchedule2);
      ConcertDTO concertDTO = new ConcertDTO(4L+i, "아이유"+i, concertScheduleDTOList);
      result.add(concertDTO);
    }

    when(getConcertDetailUseCase.execute(any(GetConcertDetailCommand.class))).thenReturn(result.get(0));

    mockMvc.perform(get("/api/concerts/"+result.get(0).getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.concert.id").value(4))
        .andExpect(jsonPath("$.concert.name").value("아이유0"))
        .andExpect(jsonPath("$.concert.name").value("아이유0"))
        .andExpect(jsonPath("$.concert.schedules[0].id").value(4))
        .andExpect(jsonPath("$.concert.schedules[0].capacity").value(54));
  }
}