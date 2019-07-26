package org.acreo.ledger.database.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.acreo.common.entities.GenericLedgerVeidblock;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockHeader;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.utils.LedgerUtils;

public class GenericLedgerServiceWrapper {

	private GenericVedblockService veidblockLedgerService = null;

	public GenericLedgerServiceWrapper(GenericVedblockService veidblockLedgerService) {
		this.veidblockLedgerService = veidblockLedgerService;
	}

	public GenericLedgerVeidblock getVedBlockLedger(long owner) throws VeidblockException {

		GenericVeidblockHeader genericVeidblockHeader = veidblockLedgerService.getVeIdBlockHeaderDB(owner);
		if (Objects.isNull(genericVeidblockHeader)) {
			throw new VeidblockException("-1"); // Header not found
		}

		List<GenericVeidblock> genericVeidblockList = veidblockLedgerService
				.getVeidclockIdentity(genericVeidblockHeader.getSenderId());
		if (genericVeidblockList == null) {
			throw new VeidblockException("-2"); // Identity not found
		}
		if (genericVeidblockList.size() == 0) {
			throw new VeidblockException("-2"); // Identity not found
		}
		GenericLedgerVeidblock genericLedgerVeidblock = new GenericLedgerVeidblock();
		genericLedgerVeidblock.setGenericVeidblockHeader(genericVeidblockHeader);
		genericLedgerVeidblock.setGenericVeidblockList(genericVeidblockList);
		return genericLedgerVeidblock;
	}
	
	public List<GenericVeidblock> getAuhtorizedLedgerEntries(long owner) throws VeidblockException {

		List<GenericVeidblock>  list = veidblockLedgerService.getAuhtorizedLedgerEntries(owner);
		return list;
	}
	
	
	

	public GenericLedgerVeidblock getVedBlockLedgerWithLastEntry(long encodedId) throws VeidblockException {

		GenericVeidblockHeader genericVeidblockHeader = veidblockLedgerService.getVeIdBlockHeaderDB(encodedId);
		if (Objects.isNull(genericVeidblockHeader)) {
			throw new VeidblockException("-1"); // Header not found
		}
		List<GenericVeidblock> genericVeidblockList = null;
		try {
			genericVeidblockList = veidblockLedgerService
					.getLastVeidclockIdentity(genericVeidblockHeader.getSenderId());
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		if (genericVeidblockList == null || genericVeidblockList.size() == 0) {
			throw new VeidblockException("-2"); // Identity not found
		}
		
		GenericLedgerVeidblock genericLedgerVeidblock = new GenericLedgerVeidblock();
		genericLedgerVeidblock.setGenericVeidblockHeader(genericVeidblockHeader);
		genericLedgerVeidblock.setGenericVeidblockList(genericVeidblockList);
		return genericLedgerVeidblock;
	}

	public GenericLedgerVeidblock createVeidBlockLedger(GenericLedgerVeidblock genericLedgerVeidblock) throws VeidblockException {
		GenericLedgerVeidblock result = new GenericLedgerVeidblock();
		try {
			GenericVeidblockHeader GenericVeidblockHeader = genericLedgerVeidblock.getGenericVeidblockHeader();
			List<GenericVeidblock> blockidentitiesStored = new ArrayList<GenericVeidblock>();
			GenericVeidblockHeader veidblockHeaderDBStroed = veidblockLedgerService
					.createVeidblockHeaderDB(GenericVeidblockHeader);
			result.setGenericVeidblockHeader(veidblockHeaderDBStroed);
			
			
			List<GenericVeidblock> blockidentities = genericLedgerVeidblock.getGenericVeidblockList();
			for (GenericVeidblock veidblockIdentity : blockidentities) {
				veidblockIdentity.setSenderId(veidblockHeaderDBStroed.getSenderId());
				GenericVeidblock veidblockIdentityStored = veidblockLedgerService
						.createVeidBlockIdentity(veidblockIdentity);
				blockidentitiesStored.add(veidblockIdentityStored);
			}
			result.setGenericVeidblockList(blockidentitiesStored);
			return result;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new VeidblockException(exp);
		}
	}

	public GenericLedgerVeidblock addVeidBlockIdentity(GenericVeidblock veidblockIdentity, String encodedHashMerkleRoot)
			throws VeidblockException {
		try {
			// Try to update HashMerkleRoot
			GenericVeidblockHeader veIdBlockHeaderDB = veidblockLedgerService
					.getVeIdBlockHeaderDB(veidblockIdentity.getSenderId());
			veIdBlockHeaderDB.setHashMerkleRoot(encodedHashMerkleRoot);
			veidblockLedgerService.updateVeidblockHeaderDB(veIdBlockHeaderDB);

			GenericLedgerVeidblock veidblockLedgerDB = new GenericLedgerVeidblock();
			List<GenericVeidblock> blockidentitiesStored = new ArrayList<GenericVeidblock>();

			veidblockLedgerDB.setGenericVeidblockHeader(veIdBlockHeaderDB);
			blockidentitiesStored.add(veidblockLedgerService.createVeidBlockIdentity(veidblockIdentity));
			//System.out.println("blockidentitiesStored : "+blockidentitiesStored.size());
			veidblockLedgerDB.setGenericVeidblockList(blockidentitiesStored);

			return veidblockLedgerDB;

		} catch (Exception exp) {

			exp.printStackTrace();
			throw new VeidblockException(exp);
		}
	}

	public List<GenericVeidblock> getVeidclockIdentity(final long senderId, final long seqno) {
		return veidblockLedgerService.getVeidclockIdentity(senderId, seqno);
	}

	public GenericVeidblockHeader getPreviousHeader() {
		LedgerUtils ledgerUtils = new LedgerUtils();
		GenericVeidblockHeader blockHeaderDB = veidblockLedgerService.getLastVeidHeader();
		if (Objects.isNull(blockHeaderDB)) {
			return null;
		}
		return blockHeaderDB;
	}

	public long getLastInsertedId() {
		return veidblockLedgerService.getLastInsertedId();
	}

	public List<GenericVeidblockHeader> getVeidblockHeaders() {
		return veidblockLedgerService.getVeidblockHeaders();
	}
	
	

}