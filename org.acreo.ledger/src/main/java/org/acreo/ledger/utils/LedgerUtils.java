package org.acreo.ledger.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockHeader;
import org.acreo.common.entities.LedgerVeidblockCO;
import org.acreo.common.entities.VeidblockHeaderCO;
import org.acreo.common.entities.VeidblockHeaderListCO;
import org.acreo.common.entities.VeidblockIdentityCO;
import org.acreo.common.entities.VeidblockIdentityList;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.acreo.ledger.database.entities.VeidblockLedgerDB;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.Hashing;

public class LedgerUtils {

	public LedgerUtils() {
		// log.debug("=================== =====================
		// =====================");
	}

	public VeidblockHeaderDB toVeIdBlockHeaderDB(VeidblockHeader blockHeader) {
		VeidblockHeaderDB veIdBlockHeaderDB = new VeidblockHeaderDB();
		veIdBlockHeaderDB.setVersion(toBase64(blockHeader.getVersion()));
		veIdBlockHeaderDB.setHashPrevBlock(toBase64(blockHeader.getHashPrevBlock()));
		veIdBlockHeaderDB.setHashMerkleRoot(toBase64(blockHeader.getHashMerkleRoot()));
		veIdBlockHeaderDB.setCreationTime(toBase64(blockHeader.getTime()));
		veIdBlockHeaderDB.setExtbits(toBase64(blockHeader.getBits()));
		veIdBlockHeaderDB.setNonce(toBase64(blockHeader.getNonce()));
		return veIdBlockHeaderDB;
	}

	public VeidblockHeader toVeIdBlockHeader(VeidblockHeaderDB veIdBlockHeaderDB) {
		VeidblockHeader blockHeader = new VeidblockHeader();
		LedgerUtils ledgerUtils = new LedgerUtils();
		blockHeader.setVersion(ledgerUtils.fromBase64(veIdBlockHeaderDB.getVersion()));
		blockHeader.setHashPrevBlock(ledgerUtils.fromBase64(veIdBlockHeaderDB.getHashPrevBlock()));
		blockHeader.setHashMerkleRoot(ledgerUtils.fromBase64(veIdBlockHeaderDB.getHashMerkleRoot()));
		blockHeader.setTime(ledgerUtils.fromBase64(veIdBlockHeaderDB.getCreationTime()));
		blockHeader.setBits(ledgerUtils.fromBase64(veIdBlockHeaderDB.getExtbits()));
		blockHeader.setNonce(ledgerUtils.fromBase64(veIdBlockHeaderDB.getNonce()));
		return blockHeader;
	}

	public VeidblockHeader toVeIdBlockHeader(VeidblockHeaderCO veIdBlockHeaderCo) {
		VeidblockHeader blockHeader = new VeidblockHeader();
		LedgerUtils ledgerUtils = new LedgerUtils();
		blockHeader.setVersion(veIdBlockHeaderCo.getVersion());
		blockHeader.setHashPrevBlock(veIdBlockHeaderCo.getHashPrevBlock());
		blockHeader.setHashMerkleRoot(veIdBlockHeaderCo.getHashMerkleRoot());
		blockHeader.setTime(veIdBlockHeaderCo.getTime());
		blockHeader.setBits(veIdBlockHeaderCo.getBits());
		blockHeader.setNonce(veIdBlockHeaderCo.getNonce());
		return blockHeader;
	}

	public VeidblockHeaderCO toVeIdBlockHeaderCO(VeidblockHeaderDB veIdBlockHeaderDB) {
		VeidblockHeaderCO blockHeader = new VeidblockHeaderCO();
		LedgerUtils ledgerUtils = new LedgerUtils();
		blockHeader.setVersion(ledgerUtils.fromBase64(veIdBlockHeaderDB.getVersion()));
		blockHeader.setHashPrevBlock(ledgerUtils.fromBase64(veIdBlockHeaderDB.getHashPrevBlock()));
		blockHeader.setHashMerkleRoot(ledgerUtils.fromBase64(veIdBlockHeaderDB.getHashMerkleRoot()));
		blockHeader.setTime(ledgerUtils.fromBase64(veIdBlockHeaderDB.getCreationTime()));
		blockHeader.setBits(ledgerUtils.fromBase64(veIdBlockHeaderDB.getExtbits()));
		blockHeader.setNonce(ledgerUtils.fromBase64(veIdBlockHeaderDB.getNonce()));
		return blockHeader;
	}

	public VeidblockIdentityCO toVeidblockIdentityCO(VeidblockIdentityDB veidblockIdentityDB) {
		VeidblockIdentityCO veidblockIdentityCO = new VeidblockIdentityCO();
		veidblockIdentityCO.setCounter(veidblockIdentityDB.getCounter());
		veidblockIdentityCO.setCreationTime(veidblockIdentityDB.getCreationTime());
		veidblockIdentityCO.setPreviousHash(veidblockIdentityDB.getPreviousHash());
		veidblockIdentityCO.setPayload(veidblockIdentityDB.getPayload());
		veidblockIdentityCO.setVeid(veidblockIdentityDB.getVeid());
		return veidblockIdentityCO;
	}

	public VeidblockIdentityDB toVeidblockIdentity(VeidblockIdentityCO veidblockIdentityCO) {
		VeidblockIdentityDB veidblockIdentityDB = new VeidblockIdentityDB();
		veidblockIdentityDB.setCounter(veidblockIdentityCO.getCounter());
		veidblockIdentityDB.setCreationTime(veidblockIdentityCO.getCreationTime());
		veidblockIdentityDB.setPreviousHash(veidblockIdentityCO.getPreviousHash());
		veidblockIdentityDB.setPayload(veidblockIdentityCO.getPayload());
		veidblockIdentityDB.setVeid(veidblockIdentityCO.getVeid());
		return veidblockIdentityDB;
	}

	public VeidblockIdentityList toVeidblockIdentityDB(VeidblockIdentityList blockIdentityList) {

		List<VeidblockIdentityDB> veidblockIdentities = new ArrayList<VeidblockIdentityDB>();
		VeidblockIdentityList veidblockIdentityList = new VeidblockIdentityList();

		for (VeidblockIdentityCO veidblockIdentityCO : blockIdentityList) {
			veidblockIdentities.add(toVeidblockIdentity(veidblockIdentityCO));
		}
		return veidblockIdentityList;
	}

	public LedgerVeidblock toLedgerVeidblock(LedgerVeidblockCO ledgerVeidblockCO) {

		VeidblockHeaderCO veidblockHeaderCO = ledgerVeidblockCO.getVeidblockHeader();
		VeidblockIdentityList veidblockIdentityList = ledgerVeidblockCO.getVeidblockIdentityList();

		VeidblockHeader veidblockHeader = toVeIdBlockHeader(veidblockHeaderCO);
		List<VeidblockIdentityDB> veidblockIdentities = null;

		LedgerVeidblock ledgerVeidblock = new LedgerVeidblock(veidblockHeader, veidblockIdentities);
		return ledgerVeidblock;
	}

	public VeidblockIdentityList toVeidblockIdentityList(List<VeidblockIdentityDB> blockIdentityList) {
		VeidblockIdentityList veidblockIdentityList = new VeidblockIdentityList();
		for (VeidblockIdentityDB veidblockIdentityDB : blockIdentityList) {
			veidblockIdentityList.add(toVeidblockIdentityCO(veidblockIdentityDB));
		}
		return veidblockIdentityList;
	}

	public LedgerVeidblockCO toLedgerVeidblockCO(VeidblockLedgerDB veidblockLedgerDB) {
		LedgerVeidblockCO ledgerVeidblockCO = new LedgerVeidblockCO();
		VeidblockHeaderCO veidblockHeaderCO = toVeIdBlockHeaderCO(veidblockLedgerDB.getVeidblockHeaderDb());
		ledgerVeidblockCO.setVeidblockHeader(veidblockHeaderCO);
		ledgerVeidblockCO.setVeidblockIdentityList(toVeidblockIdentityList(veidblockLedgerDB.getBlockIdentityList()));
		return ledgerVeidblockCO;
	}

	public VeidblockHeaderListCO toVeidblockHeaderList(List<VeidblockHeaderDB> headers) {
		VeidblockHeaderListCO veidblockHeaderListCO = new VeidblockHeaderListCO();
		for (VeidblockHeaderDB veidblockHeaderDB : headers) {
			veidblockHeaderListCO.add(toVeIdBlockHeaderCO(veidblockHeaderDB));
		}
		return veidblockHeaderListCO;
	}

	public String toBase64(byte[] data) {
		if (data == null) {
			return "-";
		}
		byte[] encodedBytes = Base64.getEncoder().encode(data);
		return new String(encodedBytes);
	}

	public byte[] fromBase64(String data) {
		if (data == null) {
			return null;
		}
		byte[] encodedBytes = Base64.getDecoder().decode(data);
		return encodedBytes;
	}

	// -----------------------------------
	public byte[] generateHashOfHeader(VeidblockHeader veIdBlockHeader) throws VeidblockException {
		if (veIdBlockHeader.getHashPrevBlock() == null) {
			return null;
		}

		Hashing hashing = new Hashing(new CryptoPolicy());
		byte[] forHashs = toBytes(veIdBlockHeader);
		return hashing.generateHash(forHashs);
	}

	// -----------------------------------
	public byte[] generateHashOfHeader(GenericVeidblockHeader genericVeidblockHeader) throws VeidblockException {
		if (genericVeidblockHeader.getHashPrevBlock() == null) {
			return null;
		}

		Hashing hashing = new Hashing(new CryptoPolicy());
		byte[] forHashs = toBytes(genericVeidblockHeader);
		return hashing.generateHash(forHashs);
	}

	// ---------------------------------
	public boolean verifyHashOfHeader(VeidblockHeader veIdBlockHeader, byte hashedOfPrevHeader[])
			throws VeidblockException {
		Hashing hashing = new Hashing(new CryptoPolicy());

		byte[] forHashs = toBytes(veIdBlockHeader);
		return hashing.verifyHash(hashedOfPrevHeader, forHashs);
	}

	// --------------------------------------
	/*public byte[] toBytes(GenericVeidblock veIdBlockHeader) throws VeidblockException {

		byte hashPrevBlock[] = veIdBlockHeader.getPreviousHash().getBytes();
		byte time[] = veIdBlockHeader.getCreationTime().getBytes();
		byte del[] = "|".getBytes();
		int headerLength = hashPrevBlock.length + time.length + 2;
		byte veidInBytes[] = new byte[headerLength];

		int destCurrentPosition = 0;
		try {
			// Copy version
			
			 * System.arraycopy(version, 0, veidInBytes, destCurrentPosition,
			 * version.length); destCurrentPosition += version.length;
			 * System.arraycopy(del, 0, veidInBytes, destCurrentPosition,
			 * del.length); destCurrentPosition += del.length;
			 
			// Copy hashPrevBlock
			System.arraycopy(hashPrevBlock, 0, veidInBytes, destCurrentPosition, hashPrevBlock.length);
			destCurrentPosition += hashPrevBlock.length;
			
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy time
			System.arraycopy(time, 0, veidInBytes, destCurrentPosition, time.length);
			destCurrentPosition += time.length;
			
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
		return veidInBytes;
	}*/
	public byte[] toBytes(GenericVeidblock veIdBlockHeader) throws VeidblockException {


		byte [] senderId = (""+veIdBlockHeader.getSenderId()).getBytes();
		byte [] seqNo = (""+veIdBlockHeader.getSeqNo()).getBytes();
		byte [] receiverId = (""+veIdBlockHeader.getReceiverId()).getBytes();
		byte [] payload = veIdBlockHeader.getPayload().getBytes();
		byte [] creationTime = veIdBlockHeader.getCreationTime().getBytes();
		byte [] prevHashs = veIdBlockHeader.getPreviousHash().getBytes();
		byte [] url = veIdBlockHeader.getUrl().getBytes();
		
		byte [] status = veIdBlockHeader.getStatus().getBytes();
		byte del[] = "|".getBytes();
		
		int length = senderId.length+seqNo.length+receiverId.length+payload.length+creationTime.length+prevHashs.length+url.length+status.length + 7;
		byte veidInBytes[] = new byte[length];
		int destCurrentPosition = 0;
		try {
			// Copy hashPrevBlock
			
			System.arraycopy(senderId, 0, veidInBytes, destCurrentPosition, senderId.length);
			destCurrentPosition += senderId.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			System.arraycopy(seqNo, 0, veidInBytes, destCurrentPosition, seqNo.length);
			destCurrentPosition += seqNo.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			
			System.arraycopy(receiverId, 0, veidInBytes, destCurrentPosition, receiverId.length);
			destCurrentPosition += receiverId.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			
			System.arraycopy(payload, 0, veidInBytes, destCurrentPosition, payload.length);
			destCurrentPosition += payload.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			// Copy time
			System.arraycopy(creationTime, 0, veidInBytes, destCurrentPosition, creationTime.length);
			destCurrentPosition += creationTime.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			
			System.arraycopy(prevHashs, 0, veidInBytes, destCurrentPosition, prevHashs.length);
			destCurrentPosition += prevHashs.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			
			
			System.arraycopy(url, 0, veidInBytes, destCurrentPosition, url.length);
			destCurrentPosition += url.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;
			
			
			
			System.arraycopy(status, 0, veidInBytes, destCurrentPosition, status.length);
			destCurrentPosition += status.length;
			
		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
		return veidInBytes;
	}
	
	
	
	
	// --------------------------------------
	public byte[] toBytes(GenericVeidblockHeader veIdBlockHeader) throws VeidblockException {

		byte version[] = veIdBlockHeader.getVersion().getBytes();
		byte hashPrevBlock[] = veIdBlockHeader.getHashPrevBlock().getBytes();
		byte time[] = veIdBlockHeader.getCreationTime().getBytes();
		byte del[] = "|".getBytes();
		int headerLength = version.length + hashPrevBlock.length + time.length + 3;
		byte veidInBytes[] = new byte[headerLength];

		int destCurrentPosition = 0;
		try {
			// Copy version
			System.arraycopy(version, 0, veidInBytes, destCurrentPosition, version.length);
			destCurrentPosition += version.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy hashPrevBlock
			System.arraycopy(hashPrevBlock, 0, veidInBytes, destCurrentPosition, hashPrevBlock.length);
			destCurrentPosition += hashPrevBlock.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy time
			System.arraycopy(time, 0, veidInBytes, destCurrentPosition, time.length);
			destCurrentPosition += time.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// destCurrentPosition += time.length;

		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
		return veidInBytes;
	}

	public byte[] toBytes(VeidblockHeader veIdBlockHeader) throws VeidblockException {

		byte version[] = veIdBlockHeader.getVersion();
		byte hashPrevBlock[] = veIdBlockHeader.getHashPrevBlock();
		byte time[] = veIdBlockHeader.getTime();
		byte bits[] = veIdBlockHeader.getBits();
		byte nonce[] = veIdBlockHeader.getNonce();
		byte del[] = "|".getBytes();
		int headerLength = version.length + hashPrevBlock.length + time.length + bits.length + nonce.length + 4;
		byte veidInBytes[] = new byte[headerLength];

		int destCurrentPosition = 0;
		try {
			// Copy version
			System.arraycopy(version, 0, veidInBytes, destCurrentPosition, version.length);
			destCurrentPosition += version.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy hashPrevBlock
			System.arraycopy(hashPrevBlock, 0, veidInBytes, destCurrentPosition, hashPrevBlock.length);
			destCurrentPosition += hashPrevBlock.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy time
			System.arraycopy(time, 0, veidInBytes, destCurrentPosition, time.length);
			destCurrentPosition += time.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy bits

			System.arraycopy(bits, 0, veidInBytes, destCurrentPosition, bits.length);
			destCurrentPosition += bits.length;
			System.arraycopy(del, 0, veidInBytes, destCurrentPosition, del.length);
			destCurrentPosition += del.length;

			// Copy nonce
			System.arraycopy(nonce, 0, veidInBytes, destCurrentPosition, nonce.length);
			destCurrentPosition += time.length;

		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
		return veidInBytes;
	}

	// -----------------------------------
	public byte[] toBytes(VeidblockIdentityDB veidBlockIdentity) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("" + veidBlockIdentity.getVeid());
		stringBuffer.append("|");
		stringBuffer.append("" + veidBlockIdentity.getCounter());
		stringBuffer.append("|");
		stringBuffer.append("" + veidBlockIdentity.getCreationTime());
		stringBuffer.append("|");
		stringBuffer.append("" + veidBlockIdentity.getPayload());
		stringBuffer.append("|");
		stringBuffer.append("" + veidBlockIdentity.getPreviousHash());
		return stringBuffer.toString().getBytes();
	}

	public byte[] generateConcatinatedHash(byte[] currenthash, byte[] previousHash) throws VeidblockException {
		int destCurrentPosition = 0;
		byte del[] = "|".getBytes();
		byte dataForHash[] = new byte[currenthash.length + previousHash.length + del.length];
		// Copy currenthash
		System.arraycopy(currenthash, 0, dataForHash, destCurrentPosition, currenthash.length);
		destCurrentPosition += currenthash.length;
		System.arraycopy(del, 0, dataForHash, destCurrentPosition, del.length);
		destCurrentPosition += del.length;
		System.arraycopy(previousHash, 0, dataForHash, destCurrentPosition, previousHash.length);

		Hashing hashing = new Hashing(new CryptoPolicy());
		return hashing.generateHash(dataForHash);
	}

	// ------------------------------------
	public byte[] generateHashOfIdentity(VeidblockIdentityDB veidBlockIdentity) throws VeidblockException {
		Hashing hashing = new Hashing(new CryptoPolicy());

		byte[] identity = toBytes(veidBlockIdentity);
		return hashing.generateHash(identity);
	}

	// ------------------------------------
	public byte[] generateHashOfIdentity(GenericVeidblock veidBlockIdentity) throws VeidblockException {
		Hashing hashing = new Hashing(new CryptoPolicy());

		byte[] identity = toBytes(veidBlockIdentity);
		return hashing.generateHash(identity);
	}

	// ----------------------------------
	public boolean verifyHashOfIdentity(VeidblockIdentityDB veidBlockIdentity, byte[] hash) throws VeidblockException {
		Hashing hashing = new Hashing(new CryptoPolicy());

		byte[] identity = toBytes(veidBlockIdentity);
		return hashing.verifyHash(hash, identity);
	}

	public static void main(String arg[]) throws VeidblockException {
		VeidblockHeader veIdBlockHeader = new VeidblockHeader();
		byte version[] = { (byte) 0x01, (byte) 0x02 };
		veIdBlockHeader.setVersion(version);
		byte hashPrevBlock[] = { (byte) 0x03, (byte) 0x04 };
		veIdBlockHeader.setHashPrevBlock(hashPrevBlock);
		byte time[] = { (byte) 0x05, (byte) 0x06 };
		veIdBlockHeader.setTime(time);
		byte bits[] = { (byte) 0x07, (byte) 0x08 };
		veIdBlockHeader.setBits(bits);
		byte nonce[] = { (byte) 0x09, (byte) 0xa };
		veIdBlockHeader.setNonce(nonce);
		LedgerUtils ledgerUtils = new LedgerUtils();
		byte[] hash = ledgerUtils.generateHashOfHeader(veIdBlockHeader);		
	}
}