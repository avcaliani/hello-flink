<img src="https://flink.apache.org/img/logo/png/100/flink_squirrel_100_color.png" align="right" height="64px"/>

# Hello Flink 👋

My repository with [Apache Flink](https://flink.apache.org) learnigns.

## About Flink

After installing Flink, you can try the following commands.

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

## About the Project

```bash
# Build 👇
# Jar file will be at "build/libs/hello-flink-*-uber.jar"
./gradlew uberJar

# Args
# 1. Pipeline Name
# 2. Lake Path
flink run build/libs/hello-flink-1.0.0-uber.jar dummy $(pwd)/data/
```
