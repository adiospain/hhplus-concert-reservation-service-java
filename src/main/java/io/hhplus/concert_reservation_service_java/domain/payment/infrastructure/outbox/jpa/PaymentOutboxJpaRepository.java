package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {


  @Modifying
  @Query("UPDATE PaymentOutbox p SET p.completed = true WHERE p.id = :id")
  int markComplete(@Param("id") String id);

  @Modifying
  @Query("DELETE FROM PaymentOutbox p WHERE p.completed = true")
  void deleteCompleted();

  List<PaymentOutbox> findByCompleted(boolean completed);
}
