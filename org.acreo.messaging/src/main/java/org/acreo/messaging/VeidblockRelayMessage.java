package org.acreo.messaging;

public class VeidblockRelayMessage {
	private String senderId;
	private String payload;
	private int type; // 0 for header and 1 for transaction 
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
