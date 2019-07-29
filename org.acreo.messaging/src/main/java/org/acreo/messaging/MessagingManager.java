package org.acreo.messaging;

import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessagingManager {
	
	
	//ConsumerProcess consumerProcess = new ConsumerProcess("123456789", "127.0.0.1", 9092);
	private String kafkaIp;
	private String senderId;
	private int kafkaPort;
	public MessagingManager(String senderId, String kafkaIp, int kafkaPort){
		//System.out.println("2223");
		this.kafkaIp = kafkaIp;
		this.kafkaPort = kafkaPort;
		this.senderId = senderId;
	}
	public synchronized void executeMessageReceiver(){
		//System.out.println("31111");
		ConsumerProcess consumerProcess1 = new ConsumerProcess(senderId, kafkaIp, kafkaPort);
		consumerProcess1.start();
	}
	

	public void sendMesasge(TransactionHeaderCO transactionHeaderCO) throws VeidblockException {
		VeidblockRelayMessage veidblockRelayMessage = new VeidblockRelayMessage();
		veidblockRelayMessage.setSenderId(senderId);
		veidblockRelayMessage.setType(0);
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(transactionHeaderCO);
			veidblockRelayMessage.setPayload(json);
			ProducerProcess producerProcess = new ProducerProcess(kafkaIp, kafkaPort);
			producerProcess.send(veidblockRelayMessage);
		} catch (JsonProcessingException e) {
			throw new VeidblockException(e);
		}		
	}
	public void sendMesasge(TransactionCO transactionCO) throws VeidblockException {
		VeidblockRelayMessage veidblockRelayMessage = new VeidblockRelayMessage();
		veidblockRelayMessage.setSenderId(senderId);
		veidblockRelayMessage.setType(1);
		String json;
		try {
			json = new ObjectMapper().writeValueAsString(transactionCO);
			veidblockRelayMessage.setPayload(json);
			ProducerProcess producerProcess = new ProducerProcess(kafkaIp, kafkaPort);
			producerProcess.send(veidblockRelayMessage);
		} catch (JsonProcessingException e) {
			throw new VeidblockException(e);
		}		
	}

}
