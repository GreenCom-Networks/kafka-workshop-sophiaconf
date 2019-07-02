# kafka-workshop-sophiaconf
GreenCom Networks repository for Apache Kafka workshop @SophiaConf2019.

## Resources
A Kafka Cluster is available in order to simplify this workshop at the following url:
- [kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217](kafka://kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217)

The associated certificates (for authentication) are available in the `certs` folder.

Franz Manager (see what this is below):  https://icecorp.energy-app.net/sophiaconf-franz-manager/

## Code snippets
Code snippets to start playing around Kafka are available in this repository for various languages:
- [java](java/README.md)
- [python](python/README.md)
- [nodejs](nodejs/README.md)
- [console](console/README.md)

These snippets each share their own `README.md` which contains various informations including help to get started with our beloved `kafka`.
They are meant to be self-explicit, even though they need a bit of knowledge in the programming language itself.

Please note that these pieces of code are not equal either in usage or in implementation.
We just wanted to give you some insights and help to get started with Apache Kafka, it's now up to you to make it great.

## KSQL
On a slightly different topic, the `ksql` folder covers different use cases and snippets for trying out the magic of sql-like life on kafka.

## Franz Manager
GreenCom implemented its own dashboard for Kafka: `Franz Manager`.
You can find one instance on our infrastructure at the following url: https://icecorp.energy-app.net/sophiaconf-franz-manager/ .

It gives you the possibility to track topics, view messages, search through them, track consumer groups... 
Long story short, it's a lot of fun, on a fancy view.