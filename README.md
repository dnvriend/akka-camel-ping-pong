# akka-camel-ping-pong
Message brokers and Actors are a possibility using [akka-camel](http://doc.akka.io/docs/akka/snapshot/scala/camel.html). 
[Camel](http://camel.apache.org/) has its purpose as an integration framework for you application, and the message broker can isolate tecnology stacks 
from one another, overcoming incompatibilities eg. between older and newer versions of Akka. 

This very simple example shows how to setup ActiveMQ, Camel and send a Ping/Pong message from two actors. One actor is
the PingGenerator that sends a case class to the Pong actor. The PingGenerator has a PingProducer that sends a message
to the queue using a Request/Response Message Exchange Pattern. The Pong actor subscribes to the queue and 
processes the message, creates a new case class, the PongMessage and sends the PingGenerator a response, thus completing
the cycle.

# Starting
The project uses docker and docker-compose to launch it. There are two scripts, the `run.sh` to run the example and
the `test.sh` to run tests. 
