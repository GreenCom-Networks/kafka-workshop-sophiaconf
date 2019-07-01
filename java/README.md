# Java workshop for Kafka

Exposes basic usage of Kafka in Python.

A dedicated Kafka instance is reachable @ ```kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217```.

## Requirements
The certificates used for the authentication to the clsuter are located in the `certs` folder.
They are part of the docker image at the following path: `/kafka/certs/`.

## Using docker
In order to use `docker`, you first need to run `mvn clean package` and then `docker built -t sophiaconf-python .` which will copy the jar at the desired path.

## Usage