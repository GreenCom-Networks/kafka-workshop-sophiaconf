package com.greencomnetworks.training.kafka;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationActivityProducer {
	private static final Logger log = LoggerFactory.getLogger(LocationActivityProducer.class);
	
	
	private static final String LOCATION_TOPIC = "Users.location";
	private static final String ACTIVITY_TOPIC = "Users.activity";
	
	
	private static final Random random = new Random();

	
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
	
    public enum Cities{
    	MUNICH, 
    	SOPHIA_ANTIPOLIS,
    	BERLIN, 
    	
    }
    
    public enum Actions{
    	LOGIN, 
    	LOGOUT    	
    }
    
    public enum Users{
    	ALICE, 
    	BOB,
    	CAROL,
    	DAVID,
    	EVE
    }
    
	public static void main(String[] args) {
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

		
		
		
		
		Thread locationThread = new Thread(() ->  {
			Producer<String, String> producer = new KafkaProducer<>(props);
			for (int i = 0; i < 200; i++) {
				Users user = randomEnum(Users.class);
				Cities city = randomEnum(Cities.class);
				producer.send(new ProducerRecord<String, String>(LOCATION_TOPIC, user.toString(), city.toString()));
				log.info("User " + user + " is now in " + city);
				try {
					Thread.sleep(new Random().nextInt(10) * 10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			producer.close();
			
		});
		locationThread.start();

		
		Thread activityThread = new Thread(() ->  {
			Producer<String, String> producer = new KafkaProducer<>(props);
			for (int i = 0; i < 200; i++) {
				Users user = randomEnum(Users.class);
				Actions action = randomEnum(Actions.class);
				
				producer.send(new ProducerRecord<String, String>(ACTIVITY_TOPIC, user.toString(), action.toString()));
				log.info("User " + user + " is doing " + action);

				try {
					Thread.sleep(new Random().nextInt(10) * 10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			producer.close();
			
		});
		activityThread.start();
		
		
	}

}
