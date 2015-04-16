package com.github.dnvriend

import akka.actor.Props
import akka.testkit.TestProbe
import camel.{ActiveMqComponent, CamelHelper}

class TransactionTest extends ServiceTest {

  "Queue messages" should "should be consumed when all is ok" in {
    val producerRef = system.actorOf(Props(new JmsProducerOneWay), "jmsProducerTx")
    val consumerRef = system.actorOf(Props(new JmsConsumerWithAck), "jmsConsumerWithAckTx")
    val sender = TestProbe()
    // store 5 'ok' messages on the queue
    for(i <- 1 to 5) {
      sender.send(producerRef, "ok")
    }
    eventually {
      queueStats().head.size shouldBe 0
    }

    // store 5 'nok' messages, that should stay on the queue
    for(i <- 1 to 5) {
      sender.send(producerRef, "nok")
    }
    eventually {
      queueStats().head.size shouldBe 5
    }
    cleanupActors(producerRef, consumerRef)
  }

  override protected def beforeAll(): Unit = {
    CamelHelper.addComponent(new ActiveMqComponent("broker1"))
  }
}
