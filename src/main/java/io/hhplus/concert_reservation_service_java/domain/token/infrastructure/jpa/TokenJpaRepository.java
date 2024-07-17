package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {

  @Query("SELECT t FROM Token t WHERE t.accessKey = :accessKey")
  Optional<Token> findByAccessKey(String accessKey);


  @Query("SELECT MIN(t.id) FROM Token t WHERE t.status = 'ACTIVE'")
  Optional<Long> findSmallestActiveTokenId();




  @Modifying
  @Query("UPDATE Token t SET t.status = 'EXPIRED' WHERE t.expireAt < :now")
  int bulkUpdateExpiredTokens(LocalDateTime now);

  @Modifying
  @Query("UPDATE Token t SET t.status = 'DISCONNECTED' WHERE t.status = 'ACTIVE' AND t.updatedAt < :threshold")
  int bulkUpdateDisconnectedToken(LocalDateTime threshold);

  @Query("SELECT t FROM Token t WHERE t.status = 'ACTIVE' AND t.expireAt < :now")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  List<Token> findActiveExpiredTokens(LocalDateTime now);

  @Query("SELECT DISTINCT t FROM Token t WHERE t.status = 'ACTIVE' AND t.expireAt < :now")
  List<Token> findExpiredTokens(LocalDateTime now);
  @Query("SELECT t FROM Token t WHERE t.status = 'DISCONNECTED' ORDER BY t.updatedAt DESC")
  Optional<Token> findMostRecentlyDisconnectedToken();

  @Modifying
  @Query(value = "UPDATE Token t SET t.status = 'ACTIVE', t.expire_at = :expireAt " +
      "WHERE t.id = ("
      + "SELECT id FROM Token " +
        "WHERE id = :tokenId AND status = 'WAIT' " +
        "ORDER BY created_at ASC LIMIT 1)", nativeQuery = true)
  int activateNextToken(Long tokenId, LocalDateTime expireAt);

  @Modifying
  @Query("UPDATE Token t SET t.status = 'DONE' WHERE t.id = :id")
  void setTokenStatusToDone(long id);

  Optional<Token> findByReserverId(long reserverId);



}
