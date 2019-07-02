package com.greencomnetworks.training.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerExamples {
	private static final Logger log = LoggerFactory.getLogger(ConsumerExamples.class);
	

	private static final String CONSUMER_GROUP_NAME = ConsumerExamples.class.getCanonicalName();
	
	private Properties properties;
	
	public ConsumerExamples(Properties props) {
		this.properties =  props;
	}
	
	public static void main(String[] args) {
		log.info("Starting");
		
		
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", Configuration.BOOTSTRAP_SERVERS);
		props.setProperty("group.id", CONSUMER_GROUP_NAME);
		props.setProperty("enable.auto.commit", "true");
		props.setProperty("auto.commit.interval.ms", "100");
		props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
		
        props.put("security.protocol", "SSL");
        props.put("ssl.endpoint.identification.algorithm", "");
        props.put("ssl.truststore.location", Configuration.FILEDIR + "/client.truststore.jks");
        props.put("ssl.truststore.password", "secret");
        props.put("ssl.keystore.type", "PKCS12");
        props.put("ssl.keystore.location", Configuration.FILEDIR + "/client.keystore.p12");
        props.put("ssl.keystore.password", "secret");
        props.put("ssl.key.password", "secret");
		
        
        ConsumerExamples consumerExamples = new ConsumerExamples(props);
        
        //consumerExamples.getTopicInfo();
        consumerExamples.consume("Users.location");
        //consumerExamples.consumeFromBegining("example_topic");
        //consumerExamples.consumeFromTime("example_topic" , 1561474526803l);
	}
	
	public void getTopicInfo() {
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(this.properties);
		
		log.info("Getting Topic Info");
		Map<String , List<PartitionInfo>> topicInfo = consumer.listTopics();
		for(String topicName : topicInfo.keySet()) {
			for(PartitionInfo partitionInfo : topicInfo.get(topicName)) {
				log.info("Topic " + partitionInfo.topic() + " leader is " + partitionInfo.leader().host() + " for partition " + partitionInfo.partition() );
			}
		}
		log.info("Getting Topic Info : OK");
		consumer.close();
	}
	
	public void consume(String topicToConsume) {
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(this.properties);
		consumer.subscribe(Arrays.asList(topicToConsume));
		
		
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				log.info("offset = " + record.offset() + ", key = "  + record.key() + ", value =" + record.value() + ", partition =" + record.partition());
				
			}
		}
	}
	
	
	public void consumeFromBegining(String topicToConsume) {
		log.info("Will try to consume from begining" );
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(this.properties);
		consumer.subscribe(Arrays.asList(topicToConsume) , new ConsumerRebalanceListener() {
			
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				consumer.seekToBeginning(partitions);
				
			}
		});
		
				
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				log.info("offset = " + record.offset() + ", key = "  + record.key() + ", value =" + record.value() + ", partition =" + record.partition() + " -- ts " + record.timestamp());
				
			}
		}
	}
	
	
	public void consumeFromTime(String topicToConsume , Long timestamp) {
		log.info("Will try to consume from time : " + new Date(timestamp));
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(this.properties);
		consumer.subscribe(Arrays.asList(topicToConsume) , new ConsumerRebalanceListener() {
			
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				log.info("consumeFromTime got partitions assigned");
				Map<TopicPartition , Long> timestampsToSearch = new HashMap<>();
				for(TopicPartition partition : partitions) {
					timestampsToSearch.put(partition, timestamp);
				}
				Map<TopicPartition,OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestampsToSearch);
				for(TopicPartition topicPartition : offsets.keySet()) {
					OffsetAndTimestamp offsetAndTimestamp = offsets.get(topicPartition);
					if(offsetAndTimestamp != null) {
						log.info("Topic " + topicPartition.topic() + " - partition " + topicPartition.partition() + " : Jumping to offset " 
							+ offsetAndTimestamp.offset() + " ( ts: " + offsetAndTimestamp.timestamp() + ")");
						consumer.seek(topicPartition, offsetAndTimestamp.offset());
					}else {
						consumer.seekToEnd(Arrays.asList(topicPartition));
					}
				}
				
				log.info("Finisehd jumping all over the place. Let's do the job now..." );
			}
		});
		
				
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				log.info("offset = " + record.offset() + ", key = "  + record.key() + ", value =" + record.value() + ", partition =" + record.partition());
				
			}
		}
	}
	
}
