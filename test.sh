#!/bin/bash
# Number of times to run the test
RUN_COUNT=10
INTERVAL=60
GRADLE_COMMAND="./gradlew clean test --rerun-tasks --info --tests 'io.hhplus.concert_reservation_service_java.integration.useCase.user.PointUseCaseConcurrencyTest.concurrentChargeUseAndGetPoint'"

for ((i=1; i<=RUN_COUNT; i++))
do
    echo "$i번째 반복 실행..."
    eval $GRADLE_COMMAND

    if [ $i -lt $RUN_COUNT ]; then
        echo "다음 실행을 위해 $INTERVAL초를 기다립니다.."
        sleep $INTERVAL
    fi
done

echo "모든 실행 완료"