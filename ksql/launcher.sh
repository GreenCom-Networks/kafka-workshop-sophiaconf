#!/bin/bash

docker network create kafka

docker run -d -p 127.0.0.1:8088:8088 \
--name ksql-server \
--network kafka \
-e KSQL_BOOTSTRAP_SERVERS=kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217 \
-e KSQL_LISTENERS=http://0.0.0.0:8088/ \
-e KSQL_KSQL_SERVICE_ID=default_ \
-e KSQL_KSQL_SINK_REPLICAS=3 \
-e KSQL_KSQL_STREAMS_REPLICATION_FACTOR=3 \
-e KSQL_SECURITY_PROTOCOL=SSL \
-e KSQL_SSL_TRUSTSTORE_LOCATION=/certs/client.truststore.jks \
-e KSQL_SSL_TRUSTSTORE_PASSWORD=secret \
-e KSQL_SSL_KEYSTORE_TYPE=PKCS12 \
-e KSQL_SSL_KEYSTORE_LOCATION=/certs/client.keystore.p12 \
-e KSQL_SSL_KEYSTORE_PASSWORD=secret \
-e KSQL_SSL_KEY_PASSWORD=secret \
-v /your/path/to/kafka-workshop-sophiaconf/certs:/certs \
confluentinc/cp-ksql-server

