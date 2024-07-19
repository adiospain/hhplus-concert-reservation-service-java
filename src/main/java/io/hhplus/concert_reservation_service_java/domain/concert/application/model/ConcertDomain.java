package io.hhplus.concert_reservation_service_java.domain.concert.application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ConcertDomain {
    private long id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ConcertScheduleDomain> schedules;
}