name := "hello-flink"
organization := "br.avcaliani"
version := "1.0"

scalaVersion := "2.12.20"

// Dependencies
libraryDependencies ++= Seq(
  "org.apache.flink" %% "flink-scala" % "1.20.0" % "provided",
  "org.apache.flink" %% "flink-streaming-scala" % "1.20.0" % "provided"
)

// über JARs
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}