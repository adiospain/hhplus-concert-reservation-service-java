package io.hhplus.concert_reservation_service_java.domain.concert;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "concert")
public class Concert {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  @OneToMany(mappedBy = "concert")
  private List<ConcertSchedule> schedules;

  public Concert (long id, String name, List<ConcertSchedule> schedules){
    this.id = id;
    this.name = name;
    this.schedules = schedules;
  }

  public Concert() {

  }
}
