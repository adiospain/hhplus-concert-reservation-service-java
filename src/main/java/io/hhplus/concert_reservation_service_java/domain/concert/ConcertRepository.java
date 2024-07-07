package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;


public interface ConcertRepository {

  List<Concert > findAll();

  Optional<Concert> findById(long concertId);
}
