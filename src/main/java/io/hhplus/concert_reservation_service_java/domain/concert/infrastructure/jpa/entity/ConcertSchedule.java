package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert_schedule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ConcertSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "concert_id", nullable = false)
  private Concert concert;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "capacity")
  private Integer capacity;



  public ConcertSchedule(Concert concert, LocalDateTime startAt, int capacity) {
    this.concert = concert;
    this.startAt = startAt;
    this.capacity = capacity;
  }
}
