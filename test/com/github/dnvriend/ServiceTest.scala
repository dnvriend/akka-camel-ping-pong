package com.github.dnvriend

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.event.Logging
import akka.testkit.TestProbe
import akka.util.Timeout
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait ServiceTest extends FlatSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll with ScalaFutures with OptionValues {
  implicit val p = PatienceConfig(timeout = 50.seconds)

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 60.seconds) = Await.result(future, duration)
  }
  implicit val system = ActorSystem("TestSystem")
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(5.seconds)
  implicit val waitDuration = 5.seconds
  implicit val log = Logging(system, this.getClass)

  def cleanupActors(actors: Seq[ActorRef], probe: TestProbe): Unit =
    actors.foreach { ref =>
      ref ! PoisonPill
      probe.watch(ref)
      probe.expectTerminated(ref)
    }

  override def afterAll() = {
    system.shutdown()
    system.awaitTermination()
  }
}
