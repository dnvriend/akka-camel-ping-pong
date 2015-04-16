package com.github.dnvriend

import akka.actor.ActorLogging
import akka.camel.{CamelMessage, Consumer}
import akka.camel.Ack
import akka.actor.Status.Failure

class JmsConsumerWithAck extends Consumer with ActorLogging {
  // auto ack must be set to false, the consumer will send
  // the Ack when everything is fine, else Failure
  override def autoAck: Boolean = false

  override def endpointUri: String = QueueNames.testQueue

  override def receive: Receive = {
    case msg @ CamelMessage("ok", headers) =>
      log.info("Received: {}", msg)
      sender() ! Ack

    case msg @ CamelMessage("nok", headers) =>
      log.info("Received: {}", msg)
      sender() ! Failure(new RuntimeException("nok"))
  }
}
