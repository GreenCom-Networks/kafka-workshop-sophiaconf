from kafka.admin import KafkaAdminClient, NewTopic

adminClient = KafkaAdminClient(
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key"
)

topic_list = []
topic_list.append(NewTopic(name="sophia-conf-2019.python.tmp", num_partitions=1, replication_factor=1))

adminClient.create_topics(topic_list, timeout_ms=2000)

adminClient.close()
