package com.github.dnvriend

import java.io.File

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.event.Logging
import akka.testkit.TestProbe
import akka.util.Timeout
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import play.api.libs.ws.{WS, WSAuthScheme}
import play.api.{DefaultApplication, Mode}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.xml.XML

case class Stats(dequeueCount: Long, enqueueCount: Long, consumerCount: Long, size: Long)

trait ServiceTest extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with OptionValues with Eventually {
  implicit val p = PatienceConfig(timeout = 50.seconds)

  def response: Future[String] = WS
    .url("http://boot2docker:8161/admin/xml/queues.jsp")
    .withAuth("admin", "admin", WSAuthScheme.BASIC)
    .get()
    .map(_.body)

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 60.seconds) = Await.result(future, duration)
  }

  implicit val system = ActorSystem("TestSystem")
  implicit val ec = system.dispatcher
  implicit val theTimeOut = Timeout(5.seconds)
  implicit val waitDuration = 5.seconds
  implicit val log = Logging(system, this.getClass)
  implicit val application = new DefaultApplication(new File("."), this.getClass.getClassLoader, None, Mode.Test)

  def queueStats(queueName: String = "testQueue"): Seq[Stats] = {
    val str = response.futureValue
    val elems = XML.loadString(str)
    val res = for { e <- elems \\ "queue"
                    if (e \ "@name").text == queueName
                    stat <- e \ "stats"
             } yield stat

    res.map { stat =>
      Stats((stat \ "@dequeueCount").text.toLong, (stat \ "@enqueueCount").text.toLong, (stat \ "@consumerCount").text.toLong, (stat \ "@size").text.toLong)
    }
  }

  def cleanupActors(actors: ActorRef*): Unit = {
    val probe = TestProbe()
    actors.foreach { ref =>
      ref ! PoisonPill
      probe.watch(ref)
      probe.expectTerminated(ref)
    }
  }

  override def afterAll() = {
    system.shutdown()
    system.awaitTermination()
  }
}
