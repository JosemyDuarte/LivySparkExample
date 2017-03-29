package spark

import java.io.{File, FileNotFoundException}
import java.net.URI

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.language.postfixOps
import com.cloudera.livy.LivyClientBuilder
import com.cloudera.livy.scalaapi._

import scala.math.random
import scala.util.{Failure, Success}

/**
  *  A WordCount example using Scala-API which reads text from a stream and saves
  *  it as data frames. The word with maximum count is the result.
  */
object LivyMain {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global


  var scalaClient: LivyScalaClient = _

  /**
    *  Initializes the Scala client with the given url.
    *  @param url The Livy server url.
    */
  def init(url: String): Unit = {

    scalaClient = new LivyClientBuilder(false).setURI(new URI(url)).build().asScalaClient
  }

  /**
    *  Uploads the Scala-API Jar and the examples Jar from the target directory.
    *  @throws FileNotFoundException If either of Scala-API Jar or examples Jar is not found.
    */
  @throws(classOf[FileNotFoundException])
  def uploadRelevantJarsForJobExecution(): Unit = {
    val exampleAppJarPath = getSourcePath(this)
    //val scalaApiJarPath = getSourcePath(scalaClient)
    uploadJar(exampleAppJarPath)
    //uploadJar(scalaApiJarPath)
  }

  @throws(classOf[FileNotFoundException])
  private def getSourcePath(obj: Object): String = {
    val source = obj.getClass.getProtectionDomain.getCodeSource
    if (source != null && source.getLocation.getPath != "") {
      source.getLocation.getPath
    } else {
      throw new FileNotFoundException(s"Jar containing ${obj.getClass.getName} not found.")
    }
  }

  private def uploadJar(path: String) = {
    val file = new File(path)
    val uploadJarFuture = scalaClient.uploadJar(file)
    Await.result(uploadJarFuture, 40 second) match {
      case null => println("Successfully uploaded " + file.getName)
    }
  }

  def Pi(slices: Int): Unit = {
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = scalaClient.submit{context =>
      context.sc.parallelize(1 until n, slices).map { _ =>
        val x = random * 2 - 1
        val y = random * 2 - 1
        if (x*x + y*y <= 1) 1 else 0
      }.reduce(_ + _)
    }
    val int = Await.result(count, 40 second)
    count.onComplete{
      case Success(value) => println("Pi is roughly: " + (4.0 * value / (n - 1)))
      case Failure(e) => e.printStackTrace()
    }
  }

  private def stopClient(): Unit = {
    if (scalaClient != null) {
      scalaClient.stop(true)
      scalaClient = null
    }
  }

  def main(args: Array[String]): Unit = {
    val livyUrl: String = "http://josemy-Inspiron-5567:8998"
    val samples: Int = 20

    try {
      init(livyUrl)
      uploadRelevantJarsForJobExecution()

      printf("Running PiJob with %d samples...\n", samples)
      Pi(samples)

    } finally {
      stopClient()
    }
  }

}
