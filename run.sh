#!/bin/bash

JAR_FILE="hello-flink-1.0.0-uber.jar"

function bold() {
  printf "\e[1m$1\e[m"
}

# Jar file will be at "build/libs/hello-flink-*-uber.jar"
function build_jar() {
  ./gradlew uberJar
}

printf "+------------------------------------------+\n"
printf "| 🐿 %s                       |\n" "$(bold "Hello Flink App")"
printf "| Kafka UI        ➜ http://localhost:8080  |\n"
printf "| Flink Dashboard ➜ http://localhost:8081  |\n"
printf "+------------------------------------------+\n\n"

printf "📦 %s\n" "$(bold "Building jar file...")"
build_jar

printf "\n🚀 %s\n" "$(bold "Starting application...")"
docker compose exec flink-dev /opt/flink/bin/flink run "$JAR_FILE" "$@"

exit 0

