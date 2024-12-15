<img src="https://flink.apache.org/img/logo/png/100/flink_squirrel_100_color.png" align="right" height="64px"/>

# Hello Flink 👋

![License](https://img.shields.io/github/license/avcaliani/hello-flink?logo=apache&color=lightseagreen)
![#](https://img.shields.io/badge/java-17-blue.svg)
![#](https://img.shields.io/badge/apache--flink-1.20.x-ff4757.svg)

My repository with [Apache Flink](https://flink.apache.org) learnigns.

## About the Project

Here is what you have to do to execute this project.

### Flink on Docker

```bash
# Start Flink Server
# Check 👉 http://localhost:8081 🌎
docker compose up -d

# Stop
docker compose down
```

### Running the Application

```bash
# Build 👇
# Jar file will be at "build/libs/hello-flink-*-uber.jar"
./gradlew uberJar

# Args
# 1. Pipeline Name
# 2. Lake Path
docker compose exec flink \
  /opt/flink/bin/flink run hello-flink-1.0.0-uber.jar "dummy" "/datalake/raw"
```


## Flink Local 

I created a docker compose, but you can also use a local flink installation.  
You can try the following commands.
If they work, you can do the same with this application jar.

```bash
cd $FLINK_HOME/

# Start the Flink Cluster
# Check 👉 http://localhost:8081 🌎
./bin/start-cluster.sh

# Submit an example!
./bin/flink run ./examples/streaming/WordCount.jar
# Check the results
tail ./log/flink-*-taskexecutor-*.out

# Stop the Flink Cluster
./bin/stop-cluster.sh 
```

> 💡 [Reference](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/try-flink/local_installation/)
