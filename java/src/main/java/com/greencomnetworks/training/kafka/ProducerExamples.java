package com.greencomnetworks.training.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.greencomnetworks.training.kafka.misc.User;

public class ProducerExamples {
	private static final Logger log = LoggerFactory.getLogger(ProducerExamples.class);



	private Properties properties;
	
	public ProducerExamples(Properties properties) {
		this.properties = properties;
	}

	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		log.info("Starting");


		Properties props = new Properties();
		props.setProperty("bootstrap.servers", Configuration.BOOTSTRAP_SERVERS);
		props.put("acks", "all");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		props.put("security.protocol", "SSL");
		props.put("ssl.endpoint.identification.algorithm", "");
		props.put("ssl.truststore.location", Configuration.FILEDIR + "/client.truststore.jks");
		props.put("ssl.truststore.password", "secret");
		props.put("ssl.keystore.type", "PKCS12");
		props.put("ssl.keystore.location", Configuration.FILEDIR + "/client.keystore.p12");
		props.put("ssl.keystore.password", "secret");
		props.put("ssl.key.password", "secret");

		
		ProducerExamples producerExamples = new ProducerExamples(props);
		
		//producerExamples.createTopics(props);
		//producerExamples.createCompatedTopics(props);
		//producerExamples.addPartition(props);
		//producerExamples.produceMessages(props);
		producerExamples.produceJsonMessages(props);
	}

	
	public void produceMessages(Properties props) throws InterruptedException, ExecutionException {

		Producer<String, String> producer = new KafkaProducer<>(props);

		log.info("Will now send few messages in topic example_topic");
		for (int i = 0; i < 20; i++) {
			log.info("" + producer.send(new ProducerRecord<String, String>("sophia-conf-2019.python-2", "alice", Integer.toString(i))).get().partition());
			//producer.send( new ProducerRecord<String, String>("example_topic", "1", Integer.toString(i)));
			//producer.send( new ProducerRecord<String, String>("example_topic", "2", Integer.toString(i)));
			Thread.sleep(500);
		}
		log.info("Messages sent");

		/*log.info("Will now send few messages in topic myFirstTopic-2-partitions");
		for (int i = 0; i < 100; i++) {
			producer.send(new ProducerRecord<String, String>("myFirstTopic-2-partitions", Integer.toString(i),
					Integer.toString(i)));
		}
		log.info("Messages sent");*/

		producer.close();

	}

	
	public void produceJsonMessages(Properties props) throws InterruptedException, ExecutionException {

		Producer<String, String> producer = new KafkaProducer<>(props);
		
		User alice = new User();
		alice.setFirstName("Alice");
		alice.setLastname("Doe");
		alice.setPlaceOfBirth("Marseille");
		String aliceString = new Gson().toJson(alice);
		log.info("Alice : " + aliceString);
		
		
		User bob = new User();;
		bob.setFirstName("Bob");
		bob.setLastname("Sponge");
		bob.setPlaceOfBirth("Nice");	
		String bobString = new Gson().toJson(bob);
		log.info("Bob : " + bobString);

		
		
		producer.send(new ProducerRecord<String, String>("Users.json", alice.getFirstName(), aliceString));
		producer.send(new ProducerRecord<String, String>("Users.json", bob.getFirstName(), bobString));
		

		log.info("Messages sent");

		producer.close();

	}
	

	
	
	public void addPartition(Properties props) {
		AdminClient adminClient = AdminClient.create(props);
		NewPartitions newPartitions = NewPartitions.increaseTo(4);
		Map<String , NewPartitions> newPart = new HashMap<>();
		newPart.put("example_topic", newPartitions);
		adminClient.createPartitions(newPart);
	}
	
	public void createTopics(Properties props) {
		// Let's create some topics
		log.info("Will create some topics");
		AdminClient adminClient = AdminClient.create(props);
		NewTopic newTopic = new NewTopic("mySecondTopic", 1, (short) 1); // new NewTopic(topicName, numPartitions,
																			// replicationFactor)
		List<NewTopic> newTopics = new ArrayList<NewTopic>();
		newTopics.add(newTopic);

		adminClient.createTopics(newTopics);
		log.info("Topic Created (almost ?");
		adminClient.close();
	}

	
	public void createCompatedTopics(Properties props) throws InterruptedException, ExecutionException {
		// Let's create some topics
		log.info("Will create some compated topics");
		AdminClient adminClient = AdminClient.create(props);
		NewTopic newTopic = new NewTopic("myCompactedTopic", 1, (short) 1); // new NewTopic(topicName, numPartitions,
							
		Map<String , String> config = new HashMap<>();
		config.put("cleanup.policy", "compact");
		newTopic.configs(config);
		
		// replicationFactor)
		List<NewTopic> newTopics = new ArrayList<NewTopic>();
		newTopics.add(newTopic);

		adminClient.createTopics(newTopics).all().get();
		log.info("Topic Created");
		adminClient.close();
	}
	


}
