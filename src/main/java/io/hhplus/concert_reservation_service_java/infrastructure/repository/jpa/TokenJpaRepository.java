package io.hhplus.concert_reservation_service_java.infrastructure.repository.jpa;

import io.hhplus.concert_reservation_service_java.domain.token.Token;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByUserId(long userId);

  @Query("SELECT MIN(t.id) FROM Token t WHERE t.status = 'ACTIVE'")
  Optional<Long> findSmallestActiveTokenId();

  @Modifying
  @Query("UPDATE Token t SET t.status = 'EXPIRED' WHERE t.status = 'ACTIVE' AND t.expireAt < :now")
  int bulkUpdateExpiredTokens(LocalDateTime now);

  @Query("SELECT t FROM Token t WHERE t.status = 'ACTIVE' AND t.expireAt < :now")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  List<Token> findActiveExpiredTokens(LocalDateTime now);

  @Query("SELECT DISTINCT t FROM Token t WHERE t.status = 'ACTIVE' AND t.expireAt < :now")
  List<Token> findExpiredTokens(LocalDateTime now);

  @Modifying
  @Query(value = "UPDATE Token t SET t.status = 'ACTIVE', t.expires_at = :expireAt " +
      "WHERE t.id = ("
      + "SELECT id FROM Token " +
        "WHERE id = :tokenId AND status = 'WAIT' " +
        "ORDER BY created_at ASC LIMIT 1)", nativeQuery = true)
  void activateNextToken(Long tokenId, LocalDateTime expireAt);
}
