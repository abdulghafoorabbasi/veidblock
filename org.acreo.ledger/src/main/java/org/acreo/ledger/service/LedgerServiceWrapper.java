package org.acreo.ledger.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.acreo.ledger.database.entities.VeidblockLedgerDB;
import org.acreo.ledger.utils.LedgerUtils;
import org.acreo.ledger.utils.VeidblockHeader;

public class LedgerServiceWrapper {

	private VeidblockLedgerService veidblockLedgerService = null;

	public LedgerServiceWrapper(VeidblockLedgerService veidblockLedgerService) {
		this.veidblockLedgerService = veidblockLedgerService;
	}

	public VeidblockLedgerDB getVedBlockLedger(String encodedId) throws VeidblockException {

		VeidblockHeaderDB blockHeaderDB = veidblockLedgerService.getVeIdBlockHeaderDB(encodedId);
		if (Objects.isNull(blockHeaderDB)) {
			throw new VeidblockException("-1"); // Header not found
		}

		List<VeidblockIdentityDB> blockIdentities = veidblockLedgerService
				.getVeidclockIdentity(blockHeaderDB.getVeid());
		if (blockIdentities == null) {
			throw new VeidblockException("-2"); // Identity not found
		}
		if (blockIdentities.size() == 0) {
			throw new VeidblockException("-2"); // Identity not found
		}
		VeidblockLedgerDB blockLedgerDB = new VeidblockLedgerDB();
		blockLedgerDB.setVeidblockHeaderDb(blockHeaderDB);
		blockLedgerDB.setBlockIdentityList(blockIdentities);
		return blockLedgerDB;
	}

	public VeidblockLedgerDB getVedBlockLedgerWithLastEntry(String encodedId) throws VeidblockException {

		VeidblockHeaderDB blockHeaderDB = veidblockLedgerService.getVeIdBlockHeaderDB(encodedId);
		if (Objects.isNull(blockHeaderDB)) {
			throw new VeidblockException("-1"); // Header not found
		}
		List<VeidblockIdentityDB> blockIdentities = null;
		try {
			blockIdentities = veidblockLedgerService
					.getLastVeidclockIdentity(blockHeaderDB.getVeid());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		if (blockIdentities == null) {
			throw new VeidblockException("-2"); // Identity not found
		}
		if (blockIdentities.size() == 0) {
			throw new VeidblockException("-2"); // Identity not found
		}
		VeidblockLedgerDB blockLedgerDB = new VeidblockLedgerDB();
		blockLedgerDB.setVeidblockHeaderDb(blockHeaderDB);
		blockLedgerDB.setBlockIdentityList(blockIdentities);
		return blockLedgerDB;
	}

	public VeidblockLedgerDB createVeidBlockLedger(VeidblockLedgerDB veidblockLedgerDB) throws VeidblockException {
		VeidblockLedgerDB result = new VeidblockLedgerDB();
		try {
			VeidblockHeaderDB veidblockHeaderDB = veidblockLedgerDB.getVeidblockHeaderDb();
			List<VeidblockIdentityDB> blockidentitiesStored = new ArrayList<VeidblockIdentityDB>();
			VeidblockHeaderDB veidblockHeaderDBStroed = veidblockLedgerService
					.createVeidblockHeaderDB(veidblockHeaderDB);
			result.setVeidblockHeaderDb(veidblockHeaderDBStroed);
			List<VeidblockIdentityDB> blockidentities = veidblockLedgerDB.getBlockIdentityList();
			for (VeidblockIdentityDB veidblockIdentity : blockidentities) {
				veidblockIdentity.setVeid(veidblockHeaderDBStroed.getVeid());
				VeidblockIdentityDB veidblockIdentityStored = veidblockLedgerService
						.createVeidBlockIdentity(veidblockIdentity);
				blockidentitiesStored.add(veidblockIdentityStored);
			}
			result.setBlockIdentityList(blockidentitiesStored);
			return result;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new VeidblockException(exp);
		}
	}

	public VeidblockLedgerDB addVeidBlockIdentity(VeidblockIdentityDB veidblockIdentity, String encodedHashMerkleRoot)
			throws VeidblockException {
		try {
			// Try to update HashMerkleRoot
			VeidblockHeaderDB veIdBlockHeaderDB = veidblockLedgerService
					.getVeIdBlockHeaderDB(veidblockIdentity.getVeid());
			veIdBlockHeaderDB.setHashMerkleRoot(encodedHashMerkleRoot);
			veidblockLedgerService.updateVeidblockHeaderDB(veIdBlockHeaderDB);

			VeidblockLedgerDB veidblockLedgerDB = new VeidblockLedgerDB();
			List<VeidblockIdentityDB> blockidentitiesStored = new ArrayList<VeidblockIdentityDB>();

			veidblockLedgerDB.setVeidblockHeaderDb(veIdBlockHeaderDB);
			blockidentitiesStored.add(veidblockLedgerService.createVeidBlockIdentity(veidblockIdentity));
			veidblockLedgerDB.setBlockIdentityList(blockidentitiesStored);

			return veidblockLedgerDB;

		} catch (Exception exp) {

			exp.printStackTrace();
			throw new VeidblockException(exp);
		}
	}

	public List<VeidblockIdentityDB> getVeidclockIdentity(final long veid, final long counter) {
		return veidblockLedgerService.getVeidclockIdentity(veid, counter);
	}

	public VeidblockHeader getPreviousHeader() {
		LedgerUtils ledgerUtils = new LedgerUtils();
		VeidblockHeaderDB blockHeaderDB = veidblockLedgerService.getLastVeidHeader();
		if (Objects.isNull(blockHeaderDB)) {
			return null;
		}
		return ledgerUtils.toVeIdBlockHeader(blockHeaderDB);
	}

	public long getLastInsertedId() {
		return veidblockLedgerService.getLastInsertedId();
	}

	public List<VeidblockHeaderDB> getVeidblockHeaders() {
		return veidblockLedgerService.getVeidblockHeaders();
	}

}