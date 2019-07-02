import {Admin, Kafka, PartitionMetadata} from "kafkajs";
import {Observable} from "rxjs";
import * as tls from "tls";

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

    public constructor(private brokers: string, private clientId: string, private ssl?: tls.ConnectionOptions) {
        this.kafka = new Kafka({
            clientId: clientId,
            brokers: brokers.split(','),
            ssl
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

    public createTopic(topicName: string, nbsPartitions: number = 1): Observable<void> {
        console.log(`Create new topic '${topicName}' ...`);
        return new Observable(subscriber => {
            this.kafkaAdmin.createTopics({
                topics: [{
                    topic: topicName,
                    numPartitions: nbsPartitions,
                }],
                validateOnly: false,
                waitForLeaders: true,
            }).then(() => {
                console.log(`Topic '${topicName}' created`);
                subscriber.next();
                subscriber.complete();
            }).catch(error => {
                subscriber.next(error);
                subscriber.complete();
            })
        });
    }

    public deleteTopic(topicName: string) {
        console.log(`Delete topic '${topicName}' ...`);
        return new Observable(subscriber => {
            this.kafkaAdmin.deleteTopics({
                topics: [topicName],
                timeout: 15000
            }).then(() => {
                console.log(`Topic '${topicName}' deleted`);
                subscriber.next();
                subscriber.complete();
            }).catch(error => {
                subscriber.next(error);
                subscriber.complete();
            })
        })
    }
}