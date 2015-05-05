package com.github.dnvriend

import akka.actor.ActorLogging
import akka.camel.{CamelMessage, Consumer}
import akka.camel.Ack
import akka.actor.Status.Failure

class JmsConsumerWithAck extends Consumer with ActorLogging {
  // auto ack must be set to false, the consumer will send
  // the Ack when everything is fine, else Failure
  override def autoAck: Boolean = false

  val redeliveryTimes = 5

  override def endpointUri: String = QueueNames.testQueue

  override def receive: Receive = {
    case msg @ CamelMessage("ok", headers) =>
      log.info("Received: {}", msg)
      sender() ! Ack

    case msg @ CamelMessage("nok", headers) =>
      log.info("Received: {}", msg)
      headers.get("JMSXDeliveryCount")
        // get the number of times the message has been delivered,
        // note that the header is not available the first time
        .map(_.asInstanceOf[Int])
        .filter(_ == redeliveryTimes)
        .map { count =>
        log.info("Redelivered {} times, time for an Ack", redeliveryTimes)
        sender() ! akka.camel.Ack
      }.getOrElse {
        sender() ! akka.actor.Status.Failure(new RuntimeException("nok"))
      }
  }
}
