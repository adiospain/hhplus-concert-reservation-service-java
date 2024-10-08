package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenRepository {

  Optional <Token> findMostRecentlyDisconnectedToken();

  Optional<Token> findByUserIdAndAccessKey(long userId, String accessKey);
  Optional<Token> findByUserId(long userId);

  Token save(Token token);

  void delete(Token token);

  Optional<Long> findSmallestActiveTokenId();

  List<Token> findActiveExpiredTokens(LocalDateTime now);

  Optional<Long> findLastActiveToken();

  int bulkUpdateExpiredTokens(LocalDateTime now);
  int bulkUpdateDisconnectedToken(LocalDateTime now);

  List<Token> findExpiredTokens(LocalDateTime now);

  int activateNextToken(Long tokenId, LocalDateTime expireAt);

  Optional<Token> findByAccessKey(String accessKey);

  void setTokenStatusToDone(long id);


  void deleteAll();

  List<Token> findAll();

  List<Token> findActiveTokens();

  List<Token> findWaitingTokens();

  void touchExpiredTokens();
  void activateTokens();


}
