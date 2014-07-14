package com.example

import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor._
import akka.camel.{CamelMessage, CamelExtension, Consumer, Producer}
import org.apache.activemq.broker.{TransportConnector, BrokerService}
import org.apache.activemq.camel.component.{ActiveMQConfiguration, ActiveMQComponent}
import org.apache.camel.Component
import org.apache.camel.impl.DefaultCamelContext

trait ActiveMQSettings {
  def uri: String
  def name: String
  def useShutdownHook = true
  def persistent = false
  def useJmx = false
  def dataDirectory: String
}

trait LocalActiveMQSettings extends ActiveMQSettings {
  val uri = "tcp://localhost:61616"
  val name = "local-broker"
  val dataDirectory = "target/activemq-data"
}

trait ActiveMQ extends ActiveMQSettings {
  val broker = new BrokerService
  val connector = new TransportConnector
  connector.setUri(new URI(uri))
  broker.addConnector(connector)
  broker.setBrokerName(name)
  broker.setUseShutdownHook(useShutdownHook)
  broker.setPersistent(persistent)
  broker.setDataDirectory(dataDirectory)
  broker.setUseJmx(useJmx)
  broker.start()
}

object PingGenerator {
  case object SendPing
  case class Ping(timestamp: Long)
  def props = Props(new PingGenerator)
}

class PingGenerator extends Actor with ActorLogging {
  import scala.concurrent.duration._
  implicit val executionContext = context.system.dispatcher
  val pingProducer = context.actorOf(PingProducer.props)

  context.system.scheduler.schedule(0 seconds, 1 seconds, self, PingGenerator.SendPing)

  override def receive: Actor.Receive = {
    case PingGenerator.SendPing => pingProducer ! PingGenerator.Ping(new Date().getTime)
    case CamelMessage(Pong.PongMessage(timestamp), _) =>
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      log.info(s"Received Pong! ${sdf.format(new Date(timestamp))}")
  }
}

object PingProducer {
  case object PingMessage
  def props = Props(new PingProducer)
}

class PingProducer extends Producer with ActorLogging {
  log.info("Creating Ping")
  override def endpointUri: String = "activemq:queue:pingQueue"
}

object Pong {
  case class PongMessage(timestamp: Long)
  def props = Props(new Pong)
}

class Pong extends Consumer with ActorLogging {
  log.info("Creating Pong")
  override def endpointUri: String = "activemq:queue:pingQueue"

  override def receive: Actor.Receive = {
    case CamelMessage(PingGenerator.Ping(timestamp), _) =>
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      log.info(s"Received Ping! ${sdf.format(new Date(timestamp))}")
      sender() ! Pong.PongMessage(new Date().getTime)
  }
}

trait CamelSystem {
  def system: ActorSystem
  val camelExtension = CamelExtension(system)
  implicit val camelContext = camelExtension.context
}

trait ActiveMqComponent {
  def camelContext: DefaultCamelContext
  def createComponent(host: String, port: String): Component = {
    val activeMqConfiguration = new ActiveMQConfiguration
    activeMqConfiguration.setBrokerURL(s"tcp://$host:$port")
    val component = new ActiveMQComponent(activeMqConfiguration)
    component.asInstanceOf[Component]
  }
  camelContext.addComponent("activemq", createComponent("localhost", "61616"))
}

trait PingPong {
  def system: ActorSystem
  val ping = system.actorOf(PingGenerator.props, "ping")
  val pong = system.actorOf(Pong.props, "pong")
}

trait MyActorSystem {
  val system = ActorSystem("my-system")
}

object Main extends App with MyActorSystem with LocalActiveMQSettings with ActiveMQ with CamelSystem with ActiveMqComponent with PingPong