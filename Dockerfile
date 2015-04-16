FROM java:8

ADD target/universal/ping-pong.tgz /
RUN chown 1000:1000 /ping-pong

WORKDIR /ping-pong/bin
CMD [ "./akka-camel-ping-pong" ]