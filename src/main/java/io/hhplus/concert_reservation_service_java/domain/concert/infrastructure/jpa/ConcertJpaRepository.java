package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
