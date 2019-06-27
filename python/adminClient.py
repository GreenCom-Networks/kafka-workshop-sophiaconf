import signal
import pprint
from kafka.admin import KafkaAdminClient, NewTopic
from config import bootstrap_servers, ssl_cafile, ssl_certfile, ssl_keyfile

adminClient = KafkaAdminClient(
    bootstrap_servers=bootstrap_servers,
    security_protocol="SSL",
    ssl_cafile=ssl_cafile,
    ssl_certfile=ssl_certfile,
    ssl_keyfile=ssl_keyfile
)

def createTopic():
    try:
        topic = str(input("Please enter a topic name:")).strip()
        topic_partitions = int(input("Please enter a number of partition [1]:") or 1)
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

def describeConsumerGroups():
    try:
        string_input = str(input("Please enter a list of group_id comma separated:"))
        group_id_list = list(map(lambda s : s.strip(), str.split(string_input, ',')))
        pprint.pprint(adminClient.describe_consumer_groups(group_id_list))
    except Exception as e:
        print(e)

def listConsumerGroupOffsets():
    try:
        string_input = str(input("Please enter a group_id:"))
        group_id = string_input.strip()
        pprint.pprint(adminClient.list_consumer_group_offsets(group_id))
    except Exception as e:
        print(e)
    
def listConsumerGroups():
    try:
        print("Listing consumer groups...")
        pprint.pprint(adminClient.list_consumer_groups())
    except Exception as e:
        print(e)
    
def exit_gracefully():
    adminClient.close()
    exit(0)

menu = [{'title':"0 : exit", 'method':exit_gracefully},
        {'title':"1 : create a topic", 'method':createTopic},
        {'title':"2 : delete some topics", 'method':deleteTopic},
        {'title':"3 : describe consumer groups", 'method':describeConsumerGroups},
        {'title':"4 : list consumer group", 'method':listConsumerGroups},
        {'title':"5 : list consumer group offsets", 'method':listConsumerGroupOffsets},
        ]

signal.signal(signal.SIGINT, exit_gracefully)
signal.signal(signal.SIGTERM, exit_gracefully)


while True:
    print('\n'.join(map(lambda x : x['title'], menu)))
    try:
        option = int(input("Please enter an option:") or 0)
        menu[option if option < len(menu) else 0]['method']()
    except Exception as e:
        print(e)
        exit(-1)

