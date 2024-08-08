package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

  List<Payment> findByUserId(long userId);
}
