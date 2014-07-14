# akka-camel-ping-pong
Message brokers and Actors are a possibility using [akka-camel](http://doc.akka.io/docs/akka/snapshot/scala/camel.html). 
[Camel](http://camel.apache.org/) has its purpose as an integration framework for you application, and the message broker can isolate tecnology stacks 
from one another, overcoming incompatibilities eg. between older and newer versions of Akka. I wouldn't recommend using
the 'reliable messaging' feature of the broker and advice implementing your own business-ack, creating stateless, idempotent
services, like you always do when using Akka. It is a basic ingredient when creating reactive applications.

This very simple example shows how to setup ActiveMQ, Camel and send a Ping/Pong message from two actors. One actor is
the PingGenerator that sends a case class to the Pong actor. The PingGenerator has a PingProducer that sends a message
to the queue using a Request/Response Message Exchange Pattern. The Pong actor subscribes to the queue and 
processes the message, creates a new case class, the PongMessage and sends the PingGenerator a response, thus completing
the cycle.

# Starting
This project uses the Typesafe Activator Launcher, only a Java 6 or higher must be installed on your computer and 
the activator-laucher will do the rest:

    $ ./activator 'run-main com.example.Main'

Have fun!