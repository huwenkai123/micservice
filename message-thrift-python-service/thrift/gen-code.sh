#!/usr/bin/env bash
thrift --gen py -out ../ thrift

thrift --gen java -out ../../message-thrift-service-api/src/main/java thrift