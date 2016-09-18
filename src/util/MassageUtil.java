package util;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class MassageUtil {
	boolean sendMessage(String broker,String topic,String resultTxt){
		
		/*Properties props = new Properties();
		//props.put("metadata.broker.list", "kafka-test-001.epicdevs.com:9092,kafka-test-002.epicdevs.com:9092,kafka-test-003.epicdevs.com:9092");
		props.put("metadata.broker.list", broker);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig producerConfig = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(producerConfig);
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, resultTxt);
		producer.send(message);
		producer.close();*/

		Properties props = new Properties();
		props.put("metadata.broker.list", broker);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks","1");
		props.put("producer.type", "sync");
		ProducerConfig producerConfig = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(producerConfig);
		//KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, resultTxt);
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic, resultTxt);
		producer.send(message);
		producer.close();
		return true;
	}
}
