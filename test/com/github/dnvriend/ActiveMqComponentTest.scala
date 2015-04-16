package com.github.dnvriend

import akka.actor.Props
import akka.camel.CamelMessage
import akka.testkit.TestProbe
import camel.{ActiveMqComponent, CamelHelper}

class ActiveMqComponentTest extends ServiceTest {

  "ActiveMqComponent" should "send a message using request/response" in {
    val producerRef = system.actorOf(Props(new JmsProducerRequestResponse), "jmsProducer")
    val consumerRef = system.actorOf(Props(new JmsConsumer), "jmsConsumer")

    // note, the sender (a testprobe) has been used to send a message to the producer
    // producer, that sends/forwards the message to the queue. The consumer receives the message
    // transforms it and returns a response, the producer then forwards the message to the
    // sender (the testprobe), that expects one message to be received and asserts it to be 'Hello Dennis'
    val sender = TestProbe()
    sender.send(producerRef, "Dennis")
    sender.receiveN(1).head match {
      case msg @ CamelMessage(body, headers) if body == "Hello Dennis" =>
        log.info("Received: {}", msg)
      case u => fail("Unexpected: " + u)
    }
    cleanupActors(producerRef, consumerRef)
    queueStats().head.size shouldBe 0
  }

  it should "send a message using OneWay only, which does not expect a response" in {
    val producerRef = system.actorOf(Props(new JmsProducerOneWay), "jmsProducer")
    val consumerRef = system.actorOf(Props(new JmsConsumer), "jmsConsumer")

    // here we send a message to a Oneway actor, the consumer responds, but the message will not be read
    // from the 'temporary queue', thus the test expects no message to be received
    val sender = TestProbe()
    sender.send(producerRef, "Dennis")
    sender.expectNoMsg()
    cleanupActors(producerRef, consumerRef)
    queueStats().head.size shouldBe 0
  }

  override protected def beforeAll(): Unit = {
    // add the configured CamelComponent with the name 'activemq' to the CamelContext
    // it is an ActiveMQ component that has a pool name, named 'broker1' that uses the TypeSafe
    // config to lookup the configuration; it connect to an ActiveMQ broker
    //
    CamelHelper.addComponent(new ActiveMqComponent("broker1"))
  }
}
