import sys
from kafka import KafkaConsumer

client_id = "kafka-python-{}".format(sys.argv[1] if len(sys.argv) > 1 else "clientA" )

consumer = KafkaConsumer(
    "sophia-conf-2019.python.tmp",
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key",
    client_id=client_id
)

print("starting '{}' client...".format(client_id))


for msg in consumer:
    print(msg)
