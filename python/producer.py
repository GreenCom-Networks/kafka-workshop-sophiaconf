import sys
from kafka import KafkaProducer

usage="""\
USAGE:
python {} TOPIC
TOPIC are mandatory\
""".format(__file__)

if len(sys.argv) <= 1:
    print(usage)
    exit(-1)

topic = sys.argv[1]

print("Starting producer to topic: '{}'".format(topic))

producer = KafkaProducer(
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key",
)

for i in range(10):
    message = "message num {}".format(i)
    print("Sending: {}".format(message))
    producer.send(topic, message.encode("utf-8"))

producer.flush()