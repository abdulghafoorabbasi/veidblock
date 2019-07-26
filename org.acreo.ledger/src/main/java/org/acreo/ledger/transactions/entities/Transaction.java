package org.acreo.ledger.transactions.entities;

import javax.persistence.Id;
import javax.persistence.IdClass;

@IdClass(TransactionKey.class)
public class Transaction {

	@Id
	private String ref;
	@Id
	private long depth;

	private String hashPrevBlock;
	private String creationTime;
	private String scope; // It should be the same as smart contract
	private String sender;
	private String receiver;

	private String payload;
	private String payloadType; // Should be one of the defined in the smart
								// contract
	private String cryptoOperationsOnPayload;

	private String creatorSignature;
	private String creatorURL;

	private String signedBy;
	private String signedDate;
	private String signerUrl;
	private String signature;

	public Transaction() {

	}

	public Transaction(String ref, long depth, String hashPrevBlock, String creationTime, String scope, String sender,
			String receiver, String payload, String payloadType, String cryptoOperationsOnPayload,
			String creatorSignature, String creatorURL, String signedBy, String signedDate, String signerUrl,
			String signature) {
		this.ref = ref;
		this.depth = depth;
		this.hashPrevBlock = hashPrevBlock;
		this.creationTime = creationTime;
		this.scope = scope;
		this.sender = sender;
		this.receiver = receiver;
		this.payload = payload;
		this.payloadType = payloadType;
		this.cryptoOperationsOnPayload = cryptoOperationsOnPayload;

		this.creatorSignature = creatorSignature;
		this.creatorURL = creatorURL;

		this.signedBy = signedBy;
		this.signedDate = signedDate;
		this.signerUrl = signerUrl;
		this.signature = signature;

	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public long getDepth() {
		return depth;
	}

	public void setDepth(long depth) {
		this.depth = depth;
	}

	public String getHashPrevBlock() {
		return hashPrevBlock;
	}

	public void setHashPrevBlock(String hashPrevBlock) {
		this.hashPrevBlock = hashPrevBlock;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(String payloadType) {
		this.payloadType = payloadType;
	}

	public String getCryptoOperationsOnPayload() {
		return cryptoOperationsOnPayload;
	}

	public void setCryptoOperationsOnPayload(String cryptoOperationsOnPayload) {
		this.cryptoOperationsOnPayload = cryptoOperationsOnPayload;
	}

	public String getSignedBy() {
		return signedBy;
	}

	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
	}

	public String getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(String signedDate) {
		this.signedDate = signedDate;
	}

	public String getSignerUrl() {
		return signerUrl;
	}

	public void setSignerUrl(String signerUrl) {
		this.signerUrl = signerUrl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getCreatorSignature() {
		return creatorSignature;
	}

	public void setCreatorSignature(String creatorSignature) {
		this.creatorSignature = creatorSignature;
	}

	public String getCreatorURL() {
		return creatorURL;
	}

	public void setCreatorURL(String creatorURL) {
		this.creatorURL = creatorURL;
	}
}