package com.greencomnetworks.trainng.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminClientExamples {
	private static final Logger log = LoggerFactory.getLogger(AdminClientExamples.class);
	
	

	
	private AdminClient adminClient;
	
	public AdminClientExamples(Properties props) {
		adminClient = AdminClient.create(props);
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

		
		AdminClientExamples adminClientExamples = new AdminClientExamples(props);
		
		adminClientExamples.getClusterInfo();
		adminClientExamples.getConsumerGroupInfo();
		adminClientExamples.getConsumerGroupExtendedInfoForName("Introduction");
	}

	public void getClusterInfo() throws InterruptedException, ExecutionException {
		log.info("Let's get some cluster info:");
		DescribeClusterResult describeClusterResult = this.adminClient.describeCluster();
		String clusterId = describeClusterResult.clusterId().get();
		log.info("\t- Cluster ID " + clusterId);
		log.info("\t- Broker List: ");
		Collection<Node> nodes = describeClusterResult.nodes().get();
		for (Node node : nodes) {
			log.info("\t\t* id " + node.id() + ": " + node.host() );
		}
	}

	public void getConsumerGroupInfo() throws InterruptedException, ExecutionException {
		log.info("Listing Consumer Group Info:");
		ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();
		Collection<ConsumerGroupListing> listOfConsumerGroupListing = listConsumerGroupsResult.all().get();
		for (ConsumerGroupListing cgl : listOfConsumerGroupListing) {
			log.info("\t- Dealing with Consumer Group: " + cgl.groupId());
			log.info("\t\t* Assignments:");
			ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(cgl.groupId());
			Map<TopicPartition, OffsetAndMetadata> resultMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
			for(TopicPartition topicPartition : resultMap.keySet()) {	
				log.info("\t\t\t # Topic: " + topicPartition.topic() + " - Partition: " + topicPartition.partition() +  " - Offset: " +resultMap.get(topicPartition).offset() +  " - Metadata: " +resultMap.get(topicPartition).metadata());
			}
			
		}
		
	}
	
	
	public void getConsumerGroupExtendedInfoForName(String consumerGroupName)
			throws InterruptedException, ExecutionException {
		DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(Arrays.asList(consumerGroupName));
		ConsumerGroupDescription consumerGroupDescription = describeConsumerGroupsResult.all().get().get(consumerGroupName);
		log.info("Group ID: " + consumerGroupDescription.groupId());
		log.info("Partition Assignator: " + consumerGroupDescription.partitionAssignor());
		log.info("State: " + consumerGroupDescription.state().toString());
		log.info("Members: ");
		for (MemberDescription memberDescription : consumerGroupDescription.members()) {
			log.info("\tclientID: " + memberDescription.clientId() + " - consumerId: " + memberDescription.consumerId()
					+ " - host: " + memberDescription.host());
			log.info("\t\tAssignements: ");
			for (TopicPartition topicPartition : memberDescription.assignment().topicPartitions()) {
				log.info("\t\t\tTopic : " + topicPartition.topic() + " - partition " + topicPartition.partition());
			}
		}

	}
	
	public void getConsumerGroupExtendedInfo(String consumerGroupName) throws InterruptedException, ExecutionException {
		DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(Arrays.asList(consumerGroupName));
		Map<String , ConsumerGroupDescription> mapOfConsumerGroupDescription = describeConsumerGroupsResult.all().get();
		for(String consumerGroup : mapOfConsumerGroupDescription.keySet()) {
			ConsumerGroupDescription consumerGroupDescription = mapOfConsumerGroupDescription.get(consumerGroup);
			log.info("Group ID: " + consumerGroupDescription.groupId());
			log.info("Partition Assignator: " + consumerGroupDescription.partitionAssignor());
			log.info("State: " + consumerGroupDescription.state().toString());
			log.info("Members: " );
			for(MemberDescription memberDescription : consumerGroupDescription.members()) {
				log.info("\tclientID: " + memberDescription.clientId() 
					+ " - consumerId: " + memberDescription.consumerId() 
					+ " - host: " + memberDescription.host());
				log.info("\t\tAssignements: ");
				for(TopicPartition topicPartition : memberDescription.assignment().topicPartitions()) {
					log.info("\t\t\tTopic : " + topicPartition.topic() + " - partition " + topicPartition.partition());
				}
			}
			
		}
	}
	
}
