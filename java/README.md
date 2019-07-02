# Java workshop for Kafka

Exposes basic usage of Kafka in Java.
Please note that the version used is Java 8 (<= it does matter a lot).

A dedicated Kafka instance is reachable @ ```kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217```.

## Requirements
The certificates used for the authentication to the clsuter are located in the `certs` folder.
They are part of the docker image at the following path: `/kafka/certs/`.

## Using docker
In order to use `docker`, you first need to run `mvn clean package` and then `docker built -t sophiaconf-python .` which will copy the jar at the desired path.

## Usage
If you want to run it locally, without docker, you need to change the `certs` file directory to an absolute path matching with your installation in the following source file: `src/main/java/com/greencomnetworks/training/kafka/Configuration.java`.