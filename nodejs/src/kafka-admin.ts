import {Admin, Kafka, PartitionMetadata} from "kafkajs";
import {Observable} from "rxjs";

interface ITopicMetadata {
    topic: string;
    partitions: PartitionMetadata[];
    name?: string;
}

export class KafkaAdmin {

    /**
     * KafkaJS instance
     */
    private kafka: Kafka;

    /**
     * Kafka admin instance
     */
    private kafkaAdmin: Admin;

    public constructor(private brokers: string, private clientId: string) {
        this.kafka = new Kafka({
            clientId: clientId,
            brokers: brokers.split(','),
        });
        this.kafkaAdmin = this.kafka.admin();
    }

    /**
     * Connection to client admin
     */
    public connectAdmin(): Observable<true> {
        console.log(`Connection to client admin (${this.brokers}) ...`);
        return new Observable<true>((subscriber => {
            this.kafkaAdmin.connect()
                .then(() => {
                    console.log(`Connected to admin client (${this.brokers})`);
                    subscriber.next(true);
                    subscriber.complete();
                })
                .catch(error => {
                    subscriber.next(error);
                    subscriber.complete();
                });
        }));
    }

    /**
     * Disconnect from client admin
     */
    public disconnectAdmin(): Observable<true> {
        console.log(`Disconnecting client admin (${this.brokers}) ...`);
        return new Observable<true>((subscriber => {
            this.kafkaAdmin.disconnect()
                .then(() => {
                    console.log(`Client admin disconnected (${this.brokers})`);
                    subscriber.next(true);
                    subscriber.complete();
                })
                .catch(error => {
                    subscriber.next(error);
                    subscriber.complete();
                });
        }));
    }

    /**
     * Return topic list
     */
    public getTopicList(): Observable<{ topics: Array<ITopicMetadata> }> {
        console.log(`Get cluster configuration...`);
        return new Observable(subscriber => {
            this.kafkaAdmin.fetchTopicMetadata({topics: []})
                .then(res => {
                    console.log('--------------------------------------------------------------------------------------------------');
                    console.log('------------------------------------- TOPIC LIST RESULT ------------------------------------------');
                    console.log('--------------------------------------------------------------------------------------------------');
                    for (let topic of res.topics) {
                        // Add ts-ignore because of bad typing from library
                        // @ts-ignore
                        console.log(`- ${topic.name} (${topic.partitions.length} partitions)`);
                    }
                    console.log('--------------------------------------------------------------------------------------------------');
                    console.log('------------------------------------- END OF TOPIC LIST -------------------------------------------');
                    console.log('--------------------------------------------------------------------------------------------------');
                    subscriber.next(res);
                    subscriber.complete();
                })
                .catch(error => {
                    subscriber.next(error);
                    subscriber.complete();
                });
        });
    }
}