import sys
import signal

from kafka import KafkaConsumer
from kafka.coordinator.assignors.roundrobin import RoundRobinPartitionAssignor
from kafka.coordinator.assignors.range import RangePartitionAssignor

from config import bootstrap_servers, ssl_cafile, ssl_certfile, ssl_keyfile

usage="""\
USAGE:
python {} CLIENT_ID TOPIC [GROUP_ID]
CLIENT_ID and TOPIC are mandatory\
""".format(__file__)

if len(sys.argv) <= 2:
    print(usage)
    exit(-1)

# Retrieving client's name, topic and group_id through argv
client_id = sys.argv[1]
topic = sys.argv[2]
group_id = sys.argv[3] if len(sys.argv) > 3 else None

#roundRobinPartitionAssignor = RoundRobinPartitionAssignor()
#rangePartitionAssignor = RangePartitionAssignor()

consumer = KafkaConsumer(
    topic,
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    #partition_assignment_strategy=[rangePartitionAssignor],
    security_protocol="SSL",
    ssl_cafile=ssl_cafile,
    ssl_certfile=ssl_certfile,
    ssl_keyfile=ssl_keyfile,
    client_id=client_id,
    group_id=group_id,
)

print("starting client '{}' on topic '{}' with group_id '{}'.".format(client_id, topic, group_id))

def exit_gracefully(a,b):
    consumer.unsubscribe()
    consumer.close()
    exit(0)

signal.signal(signal.SIGINT, exit_gracefully)
signal.signal(signal.SIGTERM, exit_gracefully)

print("Waiting messages...")
for msg in consumer:
    print(msg)
