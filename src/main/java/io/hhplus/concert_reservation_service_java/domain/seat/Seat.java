package io.hhplus.concert_reservation_service_java.domain.seat;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table (name = "seat")
@SequenceGenerator(name = "seat_seq", sequenceName = "seat_id_seq", allocationSize = 1)
@IdClass(SeatId.class)
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_id_seq")
  @Column(name = "id")
  private Long id;

  @Id
  @Column(name = "concert_schedule_id")
  private Long concertScheduleId;

  @Id
  @Column(name = "concert_id")
  private Long concertId;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "concert_id", referencedColumnName = "concert_id", insertable = false, updatable = false),
      @JoinColumn(name = "concert_schedule_id", referencedColumnName = "id", insertable = false, updatable = false)
  })
  private ConcertSchedule concertSchedule;


//  @PrePersist
//  public void prePersist() {
//    if (this.id.getId() == null) {
//      this.id.setId(generateId());
//    }
//  }
//
//  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_id_seq")
//  private Long generateId() {
//    // 이 메서드는 실제로 호출되지 않지만, JPA가 시퀀스를 사용하도록 지시합니다.
//    return null;
//  }
}
