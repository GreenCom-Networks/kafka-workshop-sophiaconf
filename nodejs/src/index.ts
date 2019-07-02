import {concatMap, delay, flatMap, map, reduce, take} from "rxjs/operators";
import {KafkaProducer} from "./kafka-producer";
import {KafkaAdmin} from "./kafka-admin";
import {KafkaConsumer} from "./kafka-consumer";
import {combineLatest, from, of} from "rxjs";

const brokers = 'kafka-sophiaconf-2019-ubinode-7aab.aivencloud.com:21217';
const topic = 'testcorp.sophiaconf-2019';
const kafkaAdmin = new KafkaAdmin(brokers, 'KafkaAdminTestRunner');

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

kafkaAdmin.connectAdmin()
    .pipe(
        flatMap(() => kafkaAdmin.getTopicList()),
        flatMap(() => kafkaAdmin.disconnectAdmin()),
        flatMap(() => {
            // Create new producer
            const producer$ = KafkaProducer.createNewProducer(brokers, 'KafkaProducerTestRunner');
            // Create new consumer
            const consumer$ = KafkaConsumer.createNewConsumer(brokers, topic, 'KafkaConsumerTestRunner', 'KafkaConsumerTestRunner');
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
    )
    .subscribe();