package io.hhplus.concert_reservation_service_java.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverRepository;
import io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa.ReserverJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReserverRepositoryImpl implements ReserverRepository {

  private final ReserverJpaRepository reserverRepository;


  @Override
  public Optional<Reserver> findById(long reserverId) {
    return reserverRepository.findById(reserverId);
  }
}
