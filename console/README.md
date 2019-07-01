# Console workshop for Kafka

You can also use pure-cli style with `docker` to showcase your Kafka skills.

You will find below some command line examples:

```bash
docker run  -it -v certs:/certs confluentinc/cp-kafka /usr/bin/kafka-console-producer --broker-list kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217 --topic example_topic --producer.config /certs/producer.properties
docker run  -it -v certs:/certs confluentinc/cp-kafka /usr/bin/kafka-console-consumer --bootstrap-server kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217 --topic example_topic --consumer.config /certs/consumer.properties --from-beginning
```
