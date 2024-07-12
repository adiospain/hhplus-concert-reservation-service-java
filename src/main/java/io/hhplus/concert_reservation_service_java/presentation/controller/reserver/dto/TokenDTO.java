  package io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto;

  import java.time.LocalDateTime;
  import lombok.AllArgsConstructor;
  import lombok.Builder;
  import lombok.Getter;
  import lombok.NoArgsConstructor;

  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Getter
  public class TokenDTO {
    private long id;
    private LocalDateTime expiredAt;
    private long order;
  }
