package io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa;

import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "reserver")
@AllArgsConstructor
@Getter
@Builder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private long version;
  @Column(name = "point")
  private Integer point;

  public User(long id, int point){
    this.id = id;
    this.point = point;
  }

  public User() {

  }
  public void usePoint(int price) {
    if (price > this.point){
      throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
    }
    this.point -= price;
  }

  public void chargePoint(int amount) {
    try {
      if (amount <= 0) {
        throw new CustomException(ErrorCode.INVALID_AMOUNT);
      }
      int pointAfter = Math.addExact(this.point, amount);
      this.point = pointAfter;
    } catch (ArithmeticException e) {
      throw new CustomException(ErrorCode.INTEGER_OVERFLOW);
    }
  }
}
