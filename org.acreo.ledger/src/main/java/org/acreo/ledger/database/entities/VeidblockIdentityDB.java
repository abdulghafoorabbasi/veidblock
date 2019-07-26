package org.acreo.ledger.database.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "VeidBlockIdentity")
@IdClass(VeidIdentityCK.class)
public class VeidblockIdentityDB {

	@Id
	private long veid;
	@Id
	private long counter;

	private String payload;
	private String creationTime;
	private String previousHash;
	private String signature; // SignatureJWT
	
	@Transient
	private byte[] hashChainBytes;

	public VeidblockIdentityDB() {

	}

	public VeidblockIdentityDB(long veid, long counter, String payload, String creationTime, String previousHash, String signature) {
		this.veid = veid;
		this.counter = counter;
		this.payload = payload;
		this.creationTime = creationTime;
		this.previousHash = previousHash;
		this.signature= signature;
	}

	public VeidblockIdentityDB(long counter, String payload) {
		this.counter = counter;
		this.payload = payload;
	}

	public VeidblockIdentityDB(long counter, String payload, byte[] hashChain) {
		this.counter = counter;
		this.payload = payload;
		this.hashChainBytes = hashChain;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public long getVeid() {
		return veid;
	}

	public void setVeid(long veid) {
		this.veid = veid;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String toJson() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(this);
		return json;
	}

	public String toJsonWithHash() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}
}