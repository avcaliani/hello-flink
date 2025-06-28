#!/bin/bash

JAR_FILE="hello-flink-1.0.0-uber.jar"

# Jar file will be at "build/libs/hello-flink-*-uber.jar"
function build() {
  ./gradlew uberJar
  printf "\n🐿️Flink Dashboard ➜ http://localhost:8081\n\n"
}

case "$1" in
  --dummy)
    build
    docker compose exec flink-dev /opt/flink/bin/flink run "$JAR_FILE" \
      --pipeline "dummy" \
      --bucket "/data"
    ;;
  --invalid-transactions)
    build
    docker compose exec flink-dev /opt/flink/bin/flink run "$JAR_FILE" \
      --pipeline "invalid-transactions" \
      --bucket "/data" \
      --kafka-brokers "kafka-dev:29092"
    ;;
  --read-topic)
    topic="$2"
    docker compose exec kafka-dev kafka-console-consumer.sh \
        --bootstrap-server "localhost:9092" \
        --topic "$topic" \
        --from-beginning
    ;;
  *)
    echo "Invalid option: $1 ❌"
    exit 1
    ;;
esac
