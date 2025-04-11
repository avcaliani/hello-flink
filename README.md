<img src="https://flink.apache.org/img/logo/png/100/flink_squirrel_100_color.png" align="right" height="64px"/>

# Hello Flink 👋

![License](https://img.shields.io/github/license/avcaliani/hello-flink?logo=apache&color=lightseagreen)
![#](https://img.shields.io/badge/java-17-blue.svg)
![#](https://img.shields.io/badge/apache--flink-2.0.x-ff4757.svg)

My repository with [Apache Flink](https://flink.apache.org) learnings.

### Quick Start

Then, **download the data** 👇 

```bash
# Download the data 
mkdir -p data/raw/users
curl -o "data/raw/users/users.csv" \
  "https://raw.githubusercontent.com/avcaliani/kafka-in-docker/refs/heads/main/scripts/users.csv"
```

Then, start **flink server** 👇

```bash
# 💡 To stop just type `docker compose down`
docker compose up -d
```

> You can check Flink UI here 👉 http://localhost:8081

Then, start **kafka producer** ([ref](https://github.com/avcaliani/kafka-in-docker/tree/main/scripts)) 👇

```bash
docker compose exec kafka-dev /opt/scripts/donu-transactions.sh
```

Finally, **build and run** the application 👇

```bash
# Jar file will be at "build/libs/hello-flink-*-uber.jar"
./gradlew uberJar

# Args
# 1. Pipeline Name
# 2. Lake Path
docker compose exec flink-dev \
  /opt/flink/bin/flink run hello-flink-1.0.0-uber.jar --pipeline "dummy" --bucket "/data"
```

> 💡 [Reference](https://nightlies.apache.org/flink/flink-docs-release-2.0/docs/try-flink/local_installation/)
