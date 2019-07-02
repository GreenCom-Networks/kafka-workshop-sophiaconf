import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {filter, flatMap, map} from 'rxjs/operators';
import {CompressionCodecs, CompressionTypes, Consumer, Kafka, KafkaMessage} from 'kafkajs';
import {first} from 'rxjs/internal/operators/first';
import * as SnappyCodec from 'kafkajs-snappy';

CompressionCodecs[CompressionTypes.Snappy] = SnappyCodec;

export class KafkaConsumer {

    private kafka: Kafka;

    /**
     * Kafka consumer
     */
    private consumer: Consumer;

    /**
     * Subject for ready state of kafka consumer
     */
    protected readySubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    /**
     * Observable for ready state of kafka consumer
     */
    public ready$: Observable<boolean> = this.readySubject.asObservable();

    /**
     * Data subject
     */
    protected newDataSubject: Subject<any> = new Subject();

    /**
     * On new data pushed, trigger a new event
     */
    public data$ = this.newDataSubject.asObservable();


    public static createNewConsumer(brokers: string, topic: string, group: string, clientId?: string) {
        const consumer = new KafkaConsumer(brokers, topic, group, clientId);
        return consumer.connect()
            .pipe(
                flatMap(() => consumer.ready$),
                filter(rds => !!rds),
                first(),
                flatMap(() => consumer.consume()),
                map(() => consumer),
            )
    }

    /**
     * Class constructor
     */
    public constructor(private brokers: string, protected topic: string, private group: string, private clientId: string) {
        console.log(`Create new KafkaConsumer instance {broker: ${brokers}}, topic: ${topic}`);
        this.kafka = new Kafka({
            clientId,
            brokers: brokers.split(','),
        });
    }

    public consume(): Observable<void> {
        return new Observable(subscriber => {
            this.consumer.run({
                eachMessage: async ({topic, partition, message}) => {
                    this.onNewData(topic, partition, message);
                },
            })
                .then(() => {
                    subscriber.next(null);
                    subscriber.complete();
                })
                .catch(e => subscriber.error(e));
        });
    }

    protected onNewData(topic: string, partition: number, message: KafkaMessage) {
        const key = message.key.toString();
        const value = message.value.toString('utf8');
        this.newDataSubject.next({key, value});
    }

    public connect() {
        this.consumer = this.kafka.consumer({groupId: `${this.group}-test`});
        return new Observable((subscriber => {
            this.consumer.connect()
                .then(() => {
                    this.consumer.subscribe({topic: this.topic, fromBeginning: true})
                        .then(() => {
                            this.readySubject.next(true);
                            subscriber.next(true);
                            subscriber.complete();
                        })
                        .catch(error => {
                            subscriber.error(error);
                            subscriber.complete();
                        });
                });
        }));
    }

    public disconnect() {
        console.log(`Disconnecting consumer (${this.brokers}) ...`);
        return new Observable<true>((subscriber => {
            this.consumer.disconnect()
                .then(() => {
                    console.log(`Consumer disconnected.`);
                    subscriber.next(true);
                    subscriber.complete();
                })
                .catch((e) => {
                    console.error(`Error during consumer disconnection ${JSON.stringify(e)}`);
                    subscriber.next(e);
                    subscriber.complete();
                });
        }));
    }
}
