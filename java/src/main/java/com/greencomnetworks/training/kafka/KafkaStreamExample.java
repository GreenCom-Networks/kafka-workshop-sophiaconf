package com.greencomnetworks.training.kafka;

import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaStreamExample {
	private static final Logger log = LoggerFactory.getLogger(KafkaStreamExample.class);


	
	private Properties properties;
	
	public KafkaStreamExample(Properties properties) {
		this.properties = properties;
	}
	
	public static void main(String[] args) {
	       Properties props = new Properties();
	        props.put(StreamsConfig.APPLICATION_ID_CONFIG, KafkaStreamExample.class.getSimpleName() + "3");
	        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Configuration.BOOTSTRAP_SERVERS);
	        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	        
	        props.put(StreamsConfig.POLL_MS_CONFIG, 100);
	        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
	        

	        
	        props.put("security.protocol", "SSL");
			props.put("ssl.endpoint.identification.algorithm", "");
			props.put("ssl.truststore.location", Configuration.FILEDIR + "/client.truststore.jks");
			props.put("ssl.truststore.password", "secret");
			props.put("ssl.keystore.type", "PKCS12");
			props.put("ssl.keystore.location", Configuration.FILEDIR + "/client.keystore.p12");
			props.put("ssl.keystore.password", "secret");
			props.put("ssl.key.password", "secret");
			
			
			KafkaStreamExample kafkaStreamExample = new KafkaStreamExample(props);
			
			
	}
	
	public void simpleStream() {
	
			log.info("Starting Stream");
			
			StreamsBuilder builder = new StreamsBuilder();
	        KStream<String, String> stream = builder.stream("sophia-conf-2019.python-2");
	        stream.filter(new Predicate<String, String>() {

				@Override
				public boolean test(String key, String value) {
					return (key != null);
				}
			}).print( Printed.toSysOut());
	        

	        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
	        kafkaStreams.start();
	}
	
	
	public void simpleCount() {
		
		log.info("Starting Stream");
		
		StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream("sophia-conf-2019.python-2");
        stream.filter(new Predicate<String, String>() {

			@Override
			public boolean test(String key, String value) {
				return (key != null);
			}
		}).groupByKey().count().toStream().print( Printed.toSysOut());
        

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();
}
	
	public void TimeWindowedStream() {
		
		log.info("Starting Stream");
		
		StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream("sophia-conf-2019.python-2");

        //TimeWindowedKStream<String, String> timeWindowedKStream = stream.groupByKey().windowedBy(TimeWindows.of(Duration.ofSeconds(5)).advanceBy(Duration.ofSeconds(1)));
        TimeWindowedKStream<String, String> timeWindowedKStream = stream.groupByKey().windowedBy(TimeWindows.of(Duration.ofSeconds(5)));
        timeWindowedKStream.count().toStream().foreach(new ForeachAction<Windowed<String>, Long>() {

			@Override
			public void apply(Windowed<String> key, Long value) {
				log.info("[" + new Date(key.window().start()) + " - " + new Date(key.window().end()) + "]" + key.key() + " -> value is " + value); 
			}
		});
 
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();
}
	
}
