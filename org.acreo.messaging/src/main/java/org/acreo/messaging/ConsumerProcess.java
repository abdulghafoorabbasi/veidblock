package org.acreo.messaging;

import java.util.Arrays;
import java.util.Properties;

import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.messaging.event.TransHeaderMessageEvent;
import org.acreo.messaging.event.TransHeaderMessageHandler;
import org.acreo.messaging.event.TransMessageEvent;
import org.acreo.messaging.event.TransMessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConsumerProcess extends Thread {

	final static Logger logger = LoggerFactory.getLogger(ConsumerProcess.class);
	private String ip;
	private int port;
	private String resourceId = null;

	public ConsumerProcess(String resourceId, String ip, int port) {
		System.out.println("1");
		this.ip = ip;
		this.port = port;
		this.resourceId = resourceId;
	}

	public void run() {
		processIncomingMessage(ip, port);
	}

	public Properties getBrokerProperties(String serverIp, int port) {
		Properties props = new Properties();
		props.put("bootstrap.servers", serverIp + ":" + port);
		props.put("group.id", "group-1");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "earliest");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	public KafkaConsumer<String, String> initBroadcastConsumer(Properties props) {
		KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);
		kafkaConsumer.subscribe(Arrays.asList("veidblock"));
		return kafkaConsumer;
	}

	public boolean processIncomingMessage(String ip, int port) {
		System.out.println("1");
		KafkaConsumer<String, String> kafkaConsumer = null;
		try {
			System.out.println("2");
			Properties props = getBrokerProperties(ip, port);
			kafkaConsumer = initBroadcastConsumer(props);
			System.out.println("3");
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		}
		while (true) {

			ConsumerRecords<String, String> records = null;
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				records = kafkaConsumer.poll(100);
			} catch (Exception ce) {
				logger.error(
						"Problems when connecting with local messaging systems. Please check messaging service is running !");
			}
			for (ConsumerRecord<String, String> record : records) {

				VeidblockRelayMessage veidblockRelayMessage = null;
				try {
					veidblockRelayMessage = objectMapper.readValue(record.value(), VeidblockRelayMessage.class);
				} catch (Exception e) {
					logger.error("1. " + e.getMessage());
				}
				if (!resourceId.equals(veidblockRelayMessage.getSenderId())) {
					try {

						if (veidblockRelayMessage.getType() == 0) {
							logger.debug("Received transaction header message !");
							TransactionHeaderCO transactionHeaderCO = objectMapper
									.readValue(veidblockRelayMessage.getPayload(), TransactionHeaderCO.class);
							TransHeaderMessageHandler c = new TransHeaderMessageHandler();
							TransHeaderMessageEvent ee = new TransHeaderMessageEvent(transactionHeaderCO);
							c.fireEvent(ee);
						} else if (veidblockRelayMessage.getType() == 1) {
							logger.debug("Received transaction message !");
							TransactionCO transactionCO = objectMapper.readValue(veidblockRelayMessage.getPayload(),
									TransactionCO.class);
							TransMessageHandler c = new TransMessageHandler();
							TransMessageEvent ee = new TransMessageEvent(transactionCO);
							c.fireEvent(ee);
						}

					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
			}
		}
	}
}
