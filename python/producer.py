import sys
from kafka import KafkaProducer

from config import bootstrap_servers, ssl_cafile, ssl_certfile, ssl_keyfile

usage="""\
USAGE:
python {} TOPIC
TOPIC is mandatory\
""".format(__file__)

if len(sys.argv) <= 1:
    print(usage)
    exit(-1)

topic = sys.argv[1]

print("Starting producer on topic: '{}'".format(topic))

producer = KafkaProducer(
    bootstrap_servers=bootstrap_servers,
    security_protocol="SSL",
    ssl_cafile=ssl_cafile,
    ssl_certfile=ssl_certfile,
    ssl_keyfile=ssl_keyfile,
)

for i in range(10):
    message = "message num {}".format(i)
    print("Sending: {}".format(message))
    producer.send(topic, message.encode("utf-8"))

producer.flush()