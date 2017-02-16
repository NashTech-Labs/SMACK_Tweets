name := "SMACK_Tweets"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
                            "org.apache.spark" %% "spark-streaming" % "2.1.0",
                            "org.twitter4j" % "twitter4j-stream" % "4.0.4",
                            "com.typesafe" % "config" % "1.3.1",
                            "org.apache.spark" % "spark-sql_2.11" % "2.1.0",
                            "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.1.0",
                            "com.typesafe.akka" %% "akka-actor" % "2.4.3",
                            "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M3"
)

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}