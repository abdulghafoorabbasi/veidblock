package org.acreo.ledger.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VeIdBlockHeader")
public class VeidblockHeaderDB {

	@Id
	@Column(name = "veid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long veid;
	private String version;
	private String hashPrevBlock;
	private String hashMerkleRoot;

	@Column(name = "creationTime")
	private String creationTime;

	@Column(name = "extbits")
	private String extbits;
	private String nonce; // This contain the subject of the owner

	public VeidblockHeaderDB() {
	}

	public VeidblockHeaderDB(long veid, String version, String hashPrevBlock, String hashMerkleRoot,
			String creationTime, String extbits, String nonce) {
		this.veid = veid;
		this.version = version;
		this.hashPrevBlock = hashPrevBlock;
		this.hashMerkleRoot = hashMerkleRoot;
		this.creationTime = creationTime;
		this.extbits = extbits;
		this.nonce = nonce;
	}

	public long getVeid() {
		return veid;
	}

	public void setVeid(long veid) {
		this.veid = veid;
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

	public String getHashMerkleRoot() {
		return hashMerkleRoot;
	}

	public void setHashMerkleRoot(String hashMerkleRoot) {
		this.hashMerkleRoot = hashMerkleRoot;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
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
}