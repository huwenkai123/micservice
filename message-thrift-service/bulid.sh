#!/usr/bin/env bash
#!不成功
mvn package
docker build -t message-service:latest .