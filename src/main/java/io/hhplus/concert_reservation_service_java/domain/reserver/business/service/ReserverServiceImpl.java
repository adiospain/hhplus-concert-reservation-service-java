package io.hhplus.concert_reservation_service_java.domain.reserver.business.service;

import io.hhplus.concert_reservation_service_java.domain.reserver.ReserverService;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.Reserver;
import io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa.ReserverRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReserverServiceImpl implements ReserverService {
  private final ReserverRepository reserverRepository;

  @Override
  public Reserver getReserver(long reserverId) {
    return reserverRepository.findById(reserverId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  @Override
  @Transactional
  public Reserver getReserverWithLock(long reserverId) {
    return reserverRepository.findByIdWithPessimisticLock(reserverId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESERVER_NOT_FOUND));
  }

  @Override
  @Transactional
  public int getPoint(long reserverId) {
    Reserver reserver = this.getReserverWithLock(reserverId);
    return reserver.getPoint();
  }

  @Override
  public Reserver chargePoint(long reserverId, int amount) {
    Reserver reserver = this.getReserverWithLock(reserverId);
    reserver.chargePoint(amount);
    Reserver savedReserver = reserverRepository.save(reserver);
    return savedReserver;
  }

  @Override
  public Reserver usePoint(long reserverId, Integer price) {
    Reserver reserver = this.getReserverWithLock(reserverId);
    reserver.usePoint(price);
    Reserver savedReserver = reserverRepository.save(reserver);
    return savedReserver;
  }

  @Override
  public Reserver save(Reserver reserver) {
    return reserverRepository.save(reserver);
  }


}
