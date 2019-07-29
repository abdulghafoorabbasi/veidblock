package org.acreo.messaging;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProducerProcess {

	final static Logger logger = LoggerFactory.getLogger(ProducerProcess.class);
	private Producer<String, String> producer = null;

	public ProducerProcess(String ip, int port) {
		producer = new KafkaProducer<String, String>(getProperties(ip, port));
	}

	// 127.0.0.1:9092
	private Properties getProperties(String ip, int port) {
		Properties props = new Properties();
		props.put("bootstrap.servers", ip+":"+port);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}

	public void send(VeidblockRelayMessage veidblockRelayMessage) {
		ObjectMapper objectMapper = new ObjectMapper();
		String message = null;
		try {
			message = objectMapper.writeValueAsString(veidblockRelayMessage);
		} catch (JsonProcessingException e) {
		}
		logger.debug("Sending message, Type: ["+veidblockRelayMessage.getType()+"]");
		producer.send(new ProducerRecord<String, String>("veidblock", message));
	}

	public void close() {
		producer.close();
	}
}
