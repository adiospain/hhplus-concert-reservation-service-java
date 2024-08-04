package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository;


import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.repository.redis.TokenRedisRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Repository
public class TokenRepositoryImpl implements TokenRepository {

  private final TokenRedisRepository tokenRepository;




  @Override
  @Transactional(readOnly = true)
  public Optional<Token> findByUserIdAndAccessKey(long userId, String accessKey) {
    return tokenRepository.getToken(userId, accessKey);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Token> findByAccessKey(String accessKey){
    return tokenRepository.getToken(accessKey);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Token> findByUserId(long userId) {
    return null;
  }

  @Override
  public Token save(Token token) {
    return tokenRepository.save(token);
  }

  @Override
  public void delete(Token token){
    tokenRepository.delete(token);
  }

  @Override
  public Optional<Long> findSmallestActiveTokenId() {
    return Optional.empty();
  }

  @Override
  public List<Token> findActiveExpiredTokens(LocalDateTime now) {
    return List.of();
  }

  @Override
  public Optional<Long> findLastActiveToken() {
    return null;
  }

  @Override
  public int bulkUpdateExpiredTokens(LocalDateTime now) {
    return 0;
  }

  @Override
  public int bulkUpdateDisconnectedToken(LocalDateTime now) {
    return 0;
  }

  @Override
  public List<Token> findExpiredTokens(LocalDateTime now) {
    return List.of();
  }


  @Override
  public void setTokenStatusToDone(long id) {
  }

  @Override
  public void deleteAll() {
    tokenRepository.deleteAll();
  }

  @Override
  public List<Token> findAll() {
    return tokenRepository.findAll();
  }

  @Override
  public List<Token> findActiveTokens(){
    return tokenRepository.findActiveTokens();
  }

  @Override
  public List<Token> findWaitingTokens(){
    return tokenRepository.findWaitingTokens();
  }

  @Override
  public void touchExpiredTokens() {
    tokenRepository.touchExpiredTokens();
  }

  @Override
  public void activateTokens() {
    tokenRepository.activateTokens();
  }

  @Override
  public int activateNextToken(Long tokenId, LocalDateTime expireAt) {
    return 0;
  }

  @Override
  public Optional<Token> findMostRecentlyDisconnectedToken() {
    return Optional.empty();
  }
}
