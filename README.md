# LivySparkExample
A simple program that calculate Pi and interact with Apache Spark trought an API REST provided by Livy.

## How to use it?

First you need a Livy Server running. You can check the [docs](https://github.com/cloudera/livy#prerequisites) for this.

Then just need to run:

```shell
$> sbt assembly
```

After this a *.jar* will be located on *target/scala-2.11/ProofOfConcept-assembly-1.0.jar*. To run it just need to execute:

```shell
$> scala target/scala-2.11/ProofOfConcept-assembly-1.0.jar

Successfully uploaded ProofOfConcept-assembly-1.0.jar
Running PiJob with 20 samples...
Pi is roughly: 3.1418295709147857
```