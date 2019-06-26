from kafka.admin import KafkaAdminClient, NewTopic

adminClient = KafkaAdminClient(
    bootstrap_servers="kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217",
    security_protocol="SSL",
    ssl_cafile="cert/ca.pem",
    ssl_certfile="cert/service.cert",
    ssl_keyfile="cert/service.key"
)

def createTopic():
    try:
        topic = str(input("Please enter a topic name:")).strip()
        topic_partitions = int(input("Please enter a number of partition:"))
        if str(input("Confirm the creation of topic '{}' with {} partitions? [Y/N]".format(topic,topic_partitions))) == 'Y':
            print("Creating topic {}...".format(topic))
            newTopic_list = [NewTopic(name=topic,num_partitions=topic_partitions, replication_factor=1)]
            adminClient.create_topics(newTopic_list)
        else:
            pass
    except Exception as e:
        print(e)


def deleteTopic():
    try:
        string_input = str(input("Please enter a topic list comma separated:"))
        topic_list = list(map(lambda s : s.strip(), str.split(string_input, ',')))
        if str(input("Confirm the deletion of{}? [Y/N]".format(topic_list))) == 'Y':
            print("Deleting {}".format(topic_list))
            adminClient.delete_topics(topic_list)
        else:
            pass
    except Exception as e:
        print(e)

def describeConsumerGroup():
    try:
        string_input = str(input("Please enter a list of group_id comma separated:"))
        group_id_list = list(map(lambda s : s.strip(), str.split(string_input, ',')))
        print(adminClient.describe_consumer_groups(group_id_list))
    except Exception as e:
        print(e)

def exit_gracefully():
    adminClient.close()
    exit(0)

menu = [{'title':"0 : exit", 'function':exit_gracefully},
        {'title':"1 : create a topic", 'function':createTopic},
        {'title':"2 : delete some topics", 'function':deleteTopic},
        {'title':"3 : describe consumer groups", 'function':describeConsumerGroup},
        ]


while True:
    print('\n'.join(map(lambda x : x['title'], menu)))
    try:
        option = int(input("Please enter an option:"))
        menu[option]['function']()
    except Exception as e:
        print(e)
        exit(-1)

