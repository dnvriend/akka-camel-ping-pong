#!/bin/bash
echo "Removing old containers"
docker rm -f $(docker ps -aq)
echo "Building ping-pong.tgz"
sbt clean universal:packageZipTarball
echo "Creating Test Environment"
docker-compose build && docker-compose up -d --allow-insecure-ssl && docker-compose logs
