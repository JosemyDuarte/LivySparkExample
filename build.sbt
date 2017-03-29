name := "ProofOfConcept"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Cloudera Repositories" at "https://repository.cloudera.com/artifactory/cloudera-repos"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.1.0" % "provided",
  "org.apache.kudu" %% "kudu-spark2" % "1.3.0",
  "com.cloudera.livy" % "livy-client-http" % "0.3.0",
  "com.cloudera.livy" %  "livy-api" % "0.3.0",
  "com.cloudera.livy" % "livy-client-http" % "0.3.0",
  "com.cloudera.livy" %% "livy-scala-api" % "0.3.0")

mainClass in (Compile, run) := Some("spark.LivyMain")
