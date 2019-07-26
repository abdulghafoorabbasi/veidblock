package org.acreo.ledger.transactions.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionHeader")
public class TransactionHeader {

	@Id
	@Column(name = "ref")
	private String ref;
	private String version;
	private String hashPrevBlock;
	private String creationTime;
	private String extbits;
	private String nonce;
	private long height;
	private String hashMerkleRoot;
	// provided by the user
	private String creator;
	private String chainName;
	private String smartcontract; // 

	private String creatorSignature;
	private String creatorURL;
	
	private String signedBy;
	private String signerUrl;
	private String signature;

	public TransactionHeader() {

	}

	public TransactionHeader(String ref, String version, String hashPrevBlock, String creationTime, 
			String extbits, String nonce, long height, String hashMerkleRoot, String creator, String chainName, String smartcontract, String creatorSignature,
			String creatorURL,	String signedBy, String signerUrl,	String signature) {

		this.ref = ref;
		this.version = version;
		this.hashPrevBlock = hashPrevBlock;
		this.creationTime = creationTime;
		this.hashMerkleRoot = hashMerkleRoot;
		this.extbits = extbits;
		this.nonce = nonce;
		this.height = height;
		this.creator = creator;
		this.chainName = chainName;
		this.smartcontract = smartcontract;
		
		this.creatorSignature = creatorSignature;
		this.creatorURL = creatorURL;
		
		this.signedBy = signedBy;
		this.signerUrl = signerUrl;
		this.signature = signature;		
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getHashMerkleRoot() {
		return hashMerkleRoot;
	}

	public void setHashMerkleRoot(String hashMerkleRoot) {
		this.hashMerkleRoot = hashMerkleRoot;
	}

	public String getExtbits() {
		return extbits;
	}

	public void setExtbits(String extbits) {
		this.extbits = extbits;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}	
	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public String getSmartcontract() {
		return smartcontract;
	}

	public void setSmartcontract(String smartcontract) {
		this.smartcontract = smartcontract;
	}

	public String getSignedBy() {
		return signedBy;
	}

	public void setSignedBy(String signedBy) {
		this.signedBy = signedBy;
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