package com.greencomnetworks.trainng.kafka;

import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencomnetworks.trainng.kafka.LocationActivityProducer.Actions;
import com.greencomnetworks.trainng.kafka.LocationActivityProducer.Users;

public class UserLocationKafkaStream {
	private static final Logger log = LoggerFactory.getLogger(UserLocationKafkaStream.class);

	
	private Properties properties;
	
	public UserLocationKafkaStream(Properties properties) {
		this.properties = properties;
	}
	
	public static void main(String[] args) {
	       Properties props = new Properties();
	        props.put(StreamsConfig.APPLICATION_ID_CONFIG, UserLocationKafkaStream.class.getSimpleName() + "3");
	        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Configuration.BOOTSTRAP_SERVERS);
	        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	        
	        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
	        
	        props.put(StreamsConfig.POLL_MS_CONFIG, 100);
	        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
	        
	        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
	        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	        
	        props.put("security.protocol", "SSL");
			props.put("ssl.endpoint.identification.algorithm", "");
			props.put("ssl.truststore.location", Configuration.FILEDIR + "/client.truststore.jks");
			props.put("ssl.truststore.password", "secret");
			props.put("ssl.keystore.type", "PKCS12");
			props.put("ssl.keystore.location", Configuration.FILEDIR + "/client.keystore.p12");
			props.put("ssl.keystore.password", "secret");
			props.put("ssl.key.password", "secret");
			
			
			UserLocationKafkaStream kafkaStreamExample = new UserLocationKafkaStream(props);
			//kafkaStreamExample.aliceLocationChanges();
			kafkaStreamExample.situationUpdate();
			
	}
	
	public void aliceLocationChanges() {
	
			log.info("Starting Location Stream");
			
			StreamsBuilder builder = new StreamsBuilder();
	        KStream<String, String> stream = builder.stream("Users.location");
	        stream.filter(new Predicate<String, String>() {

				@Override
				public boolean test(String key, String value) {
					return (key.equals(Users.ALICE.toString()));
				}
			}).print( Printed.toSysOut());
	        

	        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
	        kafkaStreams.start();
	}
	
	
	public void countLogin() {
		
		log.info("Starting Login count Stream");
		
		StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream("Users.activity");
        stream.filter(new Predicate<String, String>() {

			@Override
			public boolean test(String key, String value) {
				return (value.equals(Actions.LOGIN.toString()));
			}
		}).groupByKey().count().toStream().print( Printed.toSysOut());
        

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();
}
	
	public void situationUpdate() {
		
		log.info("Starting situationUpdate Stream");
		
		StreamsBuilder builder = new StreamsBuilder();
		
		KTable<String, String> userLocation = builder.table("Users.location");
		
        KStream<String, String> activityStream = builder.stream("Users.activity");
        
        activityStream.leftJoin(userLocation , new ValueJoiner<String, String, String>() {

			@Override
			public String apply(String action, String location) {
				
				return "is doing " + action + " in " + location;
			}
		}).print( Printed.toSysOut());
        

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();
}	
	
}
