package io.hhplus.concert_reservation_service_java.core.common.common.redisson;

import static net.logstash.logback.argument.StructuredArguments.kv;

import io.hhplus.concert_reservation_service_java.core.common.annotation.DistributedLock;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
  @Aspect
  @Component
  @RequiredArgsConstructor
  @Slf4j
  public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(io.hhplus.concert_reservation_service_java.core.common.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

      String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
      RLock rLock = redissonClient.getLock(key);  // (1)락의 이름으로 RLock 인스턴스를 가져온다.

      try {
        boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());  // (2)정의된 waitTime까지 획득을 시도한다, 정의된 leaseTime이 지나면 잠금을 해제한다.
        if (!available) {
          throw new LockNotAvailableException(ErrorCode.LOCK_ACQUISITION_FAIL,"Failed to acquire lock for method: " + method.getName() + " with key: " + key);
        }

        return aopForTransaction.proceed(joinPoint);  // (3)DistributedLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행한다.
      } catch (InterruptedException e) {
        throw new InterruptedException();
      } finally {
        try {
          if (distributedLock.unlockAfter()) {
            rLock.unlock(); // (4)종료 시 무조건 락을 해제한다.
          }
        } catch (IllegalMonitorStateException e) {
          log.info("Redisson Lock Already UnLock {} {}",
              kv("serviceName", method.getName()),
              kv("key", key)
          );
        }
      }
    }
  }