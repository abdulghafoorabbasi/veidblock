package org.acreo.ledger.utils;

public class VeidblockHeader {
	
	private byte version[] = null; // Block version number You upgrade the software and it
					// specifies a new version 4
	private byte hashPrevBlock[] = null; // 256-bit hash of the previous block header A new
							// block comes in 32
	private byte hashMerkleRoot[] = null;// 256-bit hash based on all of the transactions in
							// the block A transaction is accepted 32
	private byte time[] = null;// Current timestamp as seconds since 1970-01-01T00:00 UTC Every
				// few seconds 4
	private byte bits[] = null;// Current target in compact format The difficulty is adjusted 4
	private byte nonce[] = null;// 32-bit number (starts at 0) A hash is tried (increments) 4

	public byte[] getVersion() {
		return version;
	}

	public void setVersion(byte[] version) {
		this.version = version;
	}

	public byte[] getHashPrevBlock() {
		return hashPrevBlock;
	}

	public void setHashPrevBlock(byte[] hashPrevBlock) {
		this.hashPrevBlock = hashPrevBlock;
	}

	public byte[] getHashMerkleRoot() {
		return hashMerkleRoot;
	}

	public void setHashMerkleRoot(byte[] hashMerkleRoot) {
		this.hashMerkleRoot = hashMerkleRoot;
	}

	public byte[] getTime() {
		return time;
	}

	public void setTime(byte[] time) {
		this.time = time;
	}

	public byte[] getBits() {
		return bits;
	}

	public void setBits(byte[] bits) {
		this.bits = bits;
	}

	public byte[] getNonce() {
		return nonce;
	}

	public void setNonce(byte[] nonce) {
		this.nonce = nonce;
	}

	public void String() {

	}	
}
