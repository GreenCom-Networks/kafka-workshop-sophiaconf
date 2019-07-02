import {BehaviorSubject, Observable, of} from 'rxjs';
import {filter, first, map} from 'rxjs/operators';
import {CompressionCodecs, CompressionTypes, Kafka, Producer} from 'kafkajs';
import * as SnappyCodec from 'kafkajs-snappy';
import * as tls from "tls";

CompressionCodecs[CompressionTypes.Snappy] = SnappyCodec;

export class KafkaProducer {

    private kafka: Kafka;

    /**
     * Ready subject
     */
    private readySubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    /**
     * Ready observable
     */
    public ready$: Observable<boolean> = this.readySubject.asObservable();

    /**
     * Return current value for ready
     */
    public get ready() {
        return this.readySubject.value;
    }

    /**
     * The open kafka producer
     */
    private producer: Producer;

    /**
     * Promise to create a new instance of KafkaProducer
     */
    public static createNewProducer(brokerList: string, appName: string = 'kafka', ssl?: tls.ConnectionOptions): Observable<KafkaProducer> {
        const producer = new KafkaProducer(brokerList, appName, ssl);
        if (producer.readySubject.getValue()) {
            return of(producer);
        } else {
            return producer.ready$
                .pipe(
                    filter(rds => !!rds),
                    first(),
                    map(() => producer),
                );
        }
    }

    /**
     * Class constructor
     */
    public constructor(private brokerList: string, private clientId: string, private ssl?: tls.ConnectionOptions) {
        console.log(`Create new producer instance for ${brokerList}`);
        this.kafka = new Kafka({
            clientId,
            brokers: brokerList.split(','),
            ssl
        });
        this.producer = this.createProducer();
    }

    /**
     * Produce in kafka topic
     */
    public produce(topic: string, partition = -1, msg: string, key: string): void {
        if (this.readySubject.getValue()) {
            process.nextTick(() => {
                try {
                    console.log(`[${(new Date()).toISOString()}] New message produced '${msg}'`);
                    this.producer.send({
                        topic,
                        acks: 0,
                        compression: CompressionTypes.Snappy,
                        messages: [
                            {key: Buffer.from(key), value: Buffer.from(msg)},
                        ],
                    });
                } catch (e) {
                    console.error(e);
                }
            });
        } else {
            console.error('Kafka producer not ready, you cannot produce message');
        }
    }

    /**
     * Disconnect producer
     */
    public disconnect(): Observable<true> {
        console.log(`Disconnecting producer (${this.brokerList}) ...`);
        return new Observable<true>((subscriber => {
            this.producer.disconnect()
                .then(() => {
                    console.log(`Producer disconnected.`);
                    subscriber.next(true);
                    subscriber.complete();
                })
                .catch((e) => {
                    console.error(`Error during producer disconnection ${JSON.stringify(e)}`);
                    subscriber.next(e);
                    subscriber.complete();
                });
        }));

    }

    /**
     * Create the new producer
     */
    private createProducer() {
        const producer = this.kafka.producer();
        producer.connect().then(() => this.onProducerReady());
        producer.on('producer.connect', (r) => console.log(`Producer connected.`));
        return producer;
    }

    /**
     * Executed on producer ready
     */
    private onProducerReady() {
        this.readySubject.next(true);
    }
}
