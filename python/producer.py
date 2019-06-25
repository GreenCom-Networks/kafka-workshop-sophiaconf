from kafka import KafkaProducer

producer = KafkaProducer(
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key",
)

topic = "sophia-conf-2019.python.tmp"

for i in range(1,10):
    message = "message num {}".format(i)
    print("Sending: {}".format(message))
    producer.send(topic, message.encode("utf-8"))

producer.flush()