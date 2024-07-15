package io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class ReserverRepositoryImpl implements ReserverRepository {

  private final ReserverJpaRepository reserverRepository;


  @Override
  public Optional<Reserver> findById(long reserverId) {
    return reserverRepository.findById(reserverId);
  }

  @Override
  public Reserver save(Reserver reserver) {
    return reserverRepository.save(reserver);
  }

  @Override
  public Optional<Reserver> findByIdWithPessimisticLock(long reserverId) {
    return reserverRepository.findByIdWithPessimisticLock(reserverId);
  }
}
