import {concatMap, delay, flatMap, map, reduce, take} from "rxjs/operators";
import {KafkaProducer} from "./kafka-producer";
import {KafkaAdmin} from "./kafka-admin";
import {KafkaConsumer} from "./kafka-consumer";
import {combineLatest, from, of} from "rxjs";
import {readFileSync} from "fs";

const brokers = 'kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217';
const topic = 'testcorp.sophiaconf-2019';

// Produce function
const produce = (producer: KafkaProducer) => {
    return from(
        ['The first message',
            'Another message',
            'Again a message !',
            'This is the last one',]
    )
        .pipe(
            concatMap(x => of(x).pipe(delay(1000))), // delay each message
            map((message) => {
                producer.produce(topic, null, message, (new Date()).toISOString())
            }),
        ).subscribe()
};

// SSL configuration
const ssl = {
    rejectUnauthorized: true,
    ca: [readFileSync('/certs/ca.pem', 'utf-8')],
    cert: readFileSync('/certs/service.cert', 'utf-8'),
    key: readFileSync('/certs/service.key', 'utf-8'),
}

const kafkaAdmin = new KafkaAdmin(brokers, 'KafkaAdminTestRunner', ssl);
kafkaAdmin.connectAdmin()
    .pipe(
        flatMap(() => kafkaAdmin.getTopicList()),
        flatMap(() => kafkaAdmin.createTopic(topic)),
        flatMap(() => {
            // Create new producer
            const producer$ = KafkaProducer.createNewProducer(brokers, 'KafkaProducerTestRunner', ssl);
            // Create new consumer
            const consumer$ = KafkaConsumer.createNewConsumer(brokers, topic, 'KafkaConsumerTestRunner', 'KafkaConsumerTestRunner', ssl);
            // Sync consumer and producer connection
            return combineLatest([producer$, consumer$]);
        }),
        flatMap(([producer, consumer]) => {
            // Produce message with delay
            produce(producer);
            // Consume data
            return consumer.data$
                .pipe(
                    take(4), // Take until 4 messages has been consumed
                    map(data => console.log(`[${(new Date()).toISOString()}] New message consumed : ${JSON.stringify(data)}`)),
                    reduce(() => null, null),
                    flatMap(() => combineLatest([producer.disconnect(), consumer.disconnect()]))
                );
        }),
        flatMap(() => kafkaAdmin.deleteTopic(topic)),
        flatMap(() => kafkaAdmin.disconnectAdmin()),
    )
    .subscribe();