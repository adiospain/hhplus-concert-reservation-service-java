package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.token.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByUserId(long userId);

  @Query("SELECT MIN(t.id) FROM Token t WHERE t.status = 'ACTIVE'")
  Optional<Long> findSmallestActiveTokenId();
}
