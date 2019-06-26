# Python tuto for Kafka

Exposes basic usage of Kafka in Python.

A dedicaced Kafka instance is reachable @ ```kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217```.

## Installation
Needed dependencies:
- The library *kafka-python* [github](https://github.com/dpkp/kafka-python) and its [doc](https://kafka-python.readthedocs.io/en/master/)
Should be installable through:
```console
pip install kafka-python
```

## Running

### Scenario 1: single topic, no partitioned
First of all, we have to create a topic where we'll push data.
Under the ```kafka-workshop-sophiaconf/python/``` directory, launch the Admin client script and follow instructions to create a topic named *sophia-conf-2019.python* whith only 1 partition. 
```console
python adminClient.py
```

Once the topic is created, we can launch two consumers and subscribe it. The _consumer.py_ script takes 3 parameters: *client_name*, *topic* and an optional *group_id*. 
On a first console:
```console
python consumer.py kafka-python-client-1 sophia-conf-2019.python
```
On a second console:
```console
python consumer.py kafka-python-client-2 sophia-conf-2019.python
```
Nothing happen until, we push some data on the topic *sophia-conf-2019.python* :
```console
python producer.py sophia-conf-2019.python
```

All the messages sent should be consumed by the two clients.

### Scenario 2: single topic, 2 partitions
As in the first scenario, create a new topic named *sophia-conf-2019.python-2* with 2 partitions.
```console
python adminClient.py
```
Like in the first scenario, launching two consumers on this topic will follow the same behavior: each consumer retrieves all messages.
Try it yourself.

Now, we are going to instantiate two consumers that share a common *group_id*:

On a first console:
```console
python consumer.py kafka-python-client-1 sophia-conf-2019.python-2 group-A
```
On a second console:
```console
python consumer.py kafka-python-client-2 sophia-conf-2019.python-2 group-A
```

*sophia-conf-2019.python-2* :
```console
python producer.py sophia-conf-2019.python-2
```

As you can see, all the messages are consumed 