from kafka import KafkaConsumer

consumer = KafkaConsumer(
    "sophia-conf-2019.python.tmp",
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key"    
)

# Call poll twice. First call will just assign partitions for our
# consumer without actually returning anything

#for _ in range(2):
#    raw_msgs = consumer.poll(timeout_ms=1000)
#    for tp, msgs in raw_msgs.items():
#        for msg in msgs:
#            print("Received: {}".format(msg.value))

for msg in consumer:
    print(msg)
