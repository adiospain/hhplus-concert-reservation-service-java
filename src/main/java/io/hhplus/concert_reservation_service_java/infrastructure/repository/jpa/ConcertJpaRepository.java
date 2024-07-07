package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
