package com.github.dnvriend

import akka.actor.ActorLogging
import akka.camel.{CamelMessage, Consumer}

/**
 * Generic JmsConsumer that will respond with Hello + body
 */
class JmsConsumer extends Consumer with ActorLogging {
  override def endpointUri: String = QueueNames.testQueue

  override def receive: Receive = {
    case msg @ CamelMessage(body, headers) =>
      log.info("Received: " + body)
      sender() ! "Hello " + body
  }
}
