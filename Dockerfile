FROM murad/java8
MAINTAINER Dennis Vriend <dnvriend@gmail.com>

ADD /target/scala-2.11/akka-camel-ping-pong_2.11-0.0.1-one-jar.jar /appl/
CMD java -jar /appl/akka-camel-ping-pong_2.11-0.0.1-one-jar.jar