package com.github.dnvriend

import akka.actor.ActorLogging
import akka.camel.{Oneway, Producer}

/**
 * This actor forwards a received message to the queue using
 * Oneway only. It does not expect a response. Just send a message
 * to this actor and it handles everything
 */
class JmsProducerOneWay extends Producer with Oneway with ActorLogging {
  override def endpointUri: String = QueueNames.testQueue
}
