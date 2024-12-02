<img src="https://flink.apache.org/img/logo/png/100/flink_squirrel_100_color.png" align="right" height="64px"/>

# Hello Flink 👋

My repository with [Apache Flink](https://flink.apache.org) learnigns.

## Quick Example

After installing Flink, you can try the following commands.

```bash
cd "$FLINK_HOME"
./$FLINK_HOME/bin/start-cluster.sh
./bin/flink run ./examples/streaming/WordCount.jar
tail ./log/flink-*-taskexecutor-*.out
./bin/stop-cluster.sh 
```

> 💡 [Reference](https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/try-flink/local_installation/)

## About the Project

```bash
sbt assembly # build
```
