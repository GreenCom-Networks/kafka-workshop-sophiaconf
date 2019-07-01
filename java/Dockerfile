FROM openjdk:8-jdk

WORKDIR /kafka

COPY certs certs
COPY target/kafka-tutorial-1.0.jar workshop.jar

CMD java -jar workshop.jar
