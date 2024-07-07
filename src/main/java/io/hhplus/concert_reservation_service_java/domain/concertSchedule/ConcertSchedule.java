package io.hhplus.concert_reservation_service_java.domain.concertSchedule;

import io.hhplus.concert_reservation_service_java.domain.concert.Concert;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Entity
@Data
@Table (name = "concert_schedule")
@SequenceGenerator(name = "concert_schedule_seq", sequenceName = "concert_schedule_id_seq", allocationSize = 1)
@IdClass(ConcertScheduleId.class)
public class ConcertSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concert_schedule_id_seq")
  @Column (name = "id")
  private Long id;

  @Id
  @Column (name = "concert_id")
  private Long concertId;

  @MapsId("concertId")
  @ManyToOne
  @JoinColumn(name = "concert_id", nullable = false)
  private Concert concert;

  @Column(name = "start_at")
  private LocalDateTime startAt;
  @Column(name = "capacity")
  private int capacity;

  @OneToMany(mappedBy = "concertSchedule")
  private Set<Seat> seats;

  public ConcertSchedule() {

  }

//  @PrePersist
//  public void prePersist() {
//    if (this.id.getId() == null) {
//      this.id.setId(generateId());
//    }
//  }

//  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concert_schedule_seq")
//  private Long generateId() {
//    // 이 메서드는 실제로 호출되지 않지만, JPA가 시퀀스를 사용하도록 지시합니다.
//    return null;
//  }

  public ConcertSchedule (Concert concert, LocalDateTime startAt, int capacity){
    this.concertId = concert.getId();
    this.concert = concert;
    this.startAt = startAt;
    this.capacity = capacity;
  }
}
