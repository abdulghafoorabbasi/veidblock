package org.acreo.ledger.utils;

import java.util.List;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;

public class LedgerVeidblock {

	public static final String version = "1.0.0";
	private VeidblockHeader veidblockHeader = null;
	private List<VeidblockIdentityDB> veidblockIdentities = null;

	public LedgerVeidblock(VeidblockHeader veidblockHeader, List<VeidblockIdentityDB> veidblockIdentities) {
		this.veidblockHeader = veidblockHeader;
		this.veidblockIdentities = veidblockIdentities;
	}

	public VeidblockHeader getVeidblockHeader() {
		return veidblockHeader;
	}

	public void setVeidblockHeader(VeidblockHeader veidblockHeader) {
		this.veidblockHeader = veidblockHeader;
	}

	public List<VeidblockIdentityDB> getVeidblockIdentities() {
		return veidblockIdentities;
	}

	public void setVeidblockIdentities(List<VeidblockIdentityDB> veidblockIdentities) {
		this.veidblockIdentities = veidblockIdentities;
	}

	public boolean verify() throws VeidblockException {

		LedgerUtils ledgerUtils = new LedgerUtils();
		if (!verifyHeader()) {
			throw new VeidblockException("Problems when verifying veidblock header chain for '"
					+ new String(veidblockHeader.getNonce()) + "' identity !");
		}
		for (int i = veidblockIdentities.size() - 1; i >= 1; i--) {
			try {
				VeidblockIdentityDB previousIdentity = veidblockIdentities.get(i - 1);
				byte[] hashedPreviousIdentity = ledgerUtils.generateHashOfIdentity(previousIdentity);
				if (!ledgerUtils.toBase64(hashedPreviousIdentity)
						.equals(veidblockIdentities.get(i).getPreviousHash())) {
					throw new VeidblockException("Problems when verifying veidblock chain for '"
							+ new String(veidblockHeader.getNonce()) + "' identity !");
				}
			} catch (Exception e) {
				throw new VeidblockException("Problems when verifying veidblock chain for '"
						+ new String(veidblockHeader.getNonce()) + "' identity !");
			}
		}

		byte[] prevHashOfFirstHeader = new byte[32];
		if (!ledgerUtils.toBase64(prevHashOfFirstHeader).equals(veidblockIdentities.get(0).getPreviousHash())) {
			throw new VeidblockException("Problems when verifying veidblock chain for '"
					+ new String(veidblockHeader.getNonce()) + "' identity !");
		}
		return true;
	}

	private boolean verifyHeader() throws VeidblockException {
		
		LedgerUtils ledgerUtils = new LedgerUtils();
		String current = null, encodedHashMerkleRoot = null;
		byte[] hashMerkleRoot  = null;
		
		if (veidblockIdentities.size() > 1) {
			
			VeidblockIdentityDB blockIdentityPrev = veidblockIdentities.get(veidblockIdentities.size() - 2);
			VeidblockIdentityDB blockIdentityCurrent = veidblockIdentities.get(veidblockIdentities.size() - 1);

			byte prevHashoOfIdentity[] = ledgerUtils.fromBase64(blockIdentityPrev.getPreviousHash());
			byte[] hashedIdentity = ledgerUtils.generateHashOfIdentity(blockIdentityCurrent);
			hashMerkleRoot = ledgerUtils.generateConcatinatedHash(hashedIdentity, prevHashoOfIdentity);			

		} else {
			
			VeidblockIdentityDB blockIdentityCurrent = veidblockIdentities.get(veidblockIdentities.size() - 1);
		
			byte prevHashoOfIdentity[] = new byte[32];
			hashMerkleRoot = ledgerUtils.generateHashOfIdentity(blockIdentityCurrent);			
		}
		
		encodedHashMerkleRoot = ledgerUtils.toBase64(hashMerkleRoot);
		current = ledgerUtils.toBase64(veidblockHeader.getHashMerkleRoot());
		
		if (current.equals(encodedHashMerkleRoot)) {
			return true;
		} else {
			return false;
		}
	}
}