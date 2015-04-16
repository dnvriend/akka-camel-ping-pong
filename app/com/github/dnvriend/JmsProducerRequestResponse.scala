package com.github.dnvriend

import akka.actor.ActorLogging
import akka.camel.Producer

/**
 * This actor forwards a received message to the queue using
 * Request/Reply. Just send a message to this actor and it
 * handles everything
 */
class JmsProducerRequestResponse extends Producer with ActorLogging {
  override def endpointUri: String = QueueNames.testQueue
}
