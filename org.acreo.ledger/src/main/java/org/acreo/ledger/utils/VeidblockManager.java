package org.acreo.ledger.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.acreo.ledger.database.entities.VeidblockLedgerDB;
import org.acreo.ledger.service.LedgerServiceWrapper;
import org.acreo.ledger.service.VeidblockLedgerService;
import org.acreo.veidblock.JWSToken;
import org.acreo.veidblock.token.JWToken;

public class VeidblockManager {

	private LedgerServiceWrapper ledgerServiceWrapper = null;
	private LedgerUtils ledgerUtils = new LedgerUtils();
	private LocalCertificateManager localCertificateManager;
	
	public VeidblockManager(VeidblockLedgerService veidblockLedgerService, LocalCertificateManager localCertificateManager) {
		ledgerServiceWrapper = new LedgerServiceWrapper(veidblockLedgerService);
		this.localCertificateManager = localCertificateManager;
	}

	public VeidblockLedgerDB add(String jwsTokenEncoded) throws Exception {

		VeidblockIdentityDB veidBlockIdentity = new VeidblockIdentityDB();
		veidBlockIdentity.setPayload(jwsTokenEncoded);
		
		// Check header of this identity already exists
		JWToken jwToken = null;

		try {
			jwToken = new JWToken(veidBlockIdentity.getPayload());
			JWSToken jwsToken = new JWSToken(jwToken);

		} catch (Exception e) {
			throw new VeidblockException(e);
		}

		String id = jwToken.getPayload().getSub();
		if (id == null) {
			throw new VeidblockException("Problems when extracting identity from token !");
		}
		return createVediblock(jwsTokenEncoded, id); 
		
	}
	
	public VeidblockLedgerDB add(String ownerId, String payload) throws Exception {
		return createVediblock(ownerId, payload);		
	}
	
	private VeidblockLedgerDB createVediblock(String payload, String resourceId) throws Exception {
		
		String id = ""+resourceId;
		
		VeidblockIdentityDB veidBlockIdentity = new VeidblockIdentityDB();
		veidBlockIdentity.setPayload(payload);
		
		String encodedId = ledgerUtils.toBase64(id.getBytes());
		VeidblockLedgerDB veidblockLedgerDB = null;
		try {
			veidblockLedgerDB = ledgerServiceWrapper.getVedBlockLedgerWithLastEntry(encodedId);
		} catch (Exception e) {
			if (e.getMessage().equals("-1")) {
				veidblockLedgerDB = null;
			}
		}
		if (Objects.isNull(veidblockLedgerDB)) {
			byte[] hashOfPrevBlock = new byte[32];
			VeidblockLedgerDB veIdBlockLedgerDB = new VeidblockLedgerDB();
			VeidblockHeader veIdBlockHeader = new VeidblockHeader();
			VeidblockHeaderDB veIdBlockHeaderDb = null;

			LedgerUtils utils = new LedgerUtils();
			VeidblockHeader prevVeIdBlockHeader = ledgerServiceWrapper.getPreviousHeader();
			if (prevVeIdBlockHeader == null) {
				prevVeIdBlockHeader = new VeidblockHeader();
			}
			byte prevHashoOfHeader[] = utils.generateHashOfHeader(prevVeIdBlockHeader);
			if (prevHashoOfHeader == null) {
				System.out.println("This is first block in the ledger !");
				byte[] prevHashOfFirstHeader = new byte[32];
				veIdBlockHeader.setHashPrevBlock(prevHashOfFirstHeader);
				veIdBlockHeader.setVersion(LedgerVeidblock.version.getBytes());
				byte bits[] = new byte[0];
				veIdBlockHeader.setBits(bits);
				veIdBlockHeader.setNonce(id.getBytes());
				veIdBlockHeader.setTime(("" + System.currentTimeMillis()).getBytes());				
			} else {
				System.out.println("Blocks already exisits so this is not the first one!");
				veIdBlockHeader.setHashPrevBlock(prevHashoOfHeader);
				veIdBlockHeader.setVersion(LedgerVeidblock.version.getBytes());
				byte bits[] = new byte[0];
				veIdBlockHeader.setBits(bits);
				veIdBlockHeader.setNonce(id.getBytes());
				veIdBlockHeader.setTime(("" + System.currentTimeMillis()).getBytes());
			}
			// -----Handle identity------------------------

			long veid = ledgerServiceWrapper.getLastInsertedId();
			veid = veid+ 1;
			
			byte prevHashoOfIdentity[] = new byte[32];
			veidBlockIdentity.setPreviousHash(new LedgerUtils().toBase64(prevHashoOfIdentity));
			veidBlockIdentity.setCreationTime("" + System.currentTimeMillis());
			veidBlockIdentity.setCounter(1);
			veidBlockIdentity.setVeid(veid);
			
			byte[] hashedIdentity = utils.generateHashOfIdentity(veidBlockIdentity);
			
			// Sign it;
			
			veIdBlockHeader.setHashMerkleRoot(hashedIdentity);
			veIdBlockHeaderDb = utils.toVeIdBlockHeaderDB(veIdBlockHeader);
			veIdBlockHeaderDb.setVeid(veid);
			veIdBlockLedgerDB.setVeidblockHeaderDb(veIdBlockHeaderDb);

			List<VeidblockIdentityDB> identities = new ArrayList<VeidblockIdentityDB>();
			identities.add(veidBlockIdentity);
			veIdBlockLedgerDB.setBlockIdentityList(identities);
			return ledgerServiceWrapper.createVeidBlockLedger(veIdBlockLedgerDB);

		} else {

			VeidblockIdentityDB blockIdentityPrev = veidblockLedgerDB.getBlockIdentityList()
					.get(veidblockLedgerDB.getBlockIdentityList().size() - 1);
			long counter = blockIdentityPrev.getCounter();
			long veid = veidblockLedgerDB.getVeidblockHeaderDb().getVeid();

			counter = counter + 1;

			veidBlockIdentity.setVeid(veid);
			veidBlockIdentity.setCounter(counter);
			veidBlockIdentity.setCreationTime("" + System.currentTimeMillis());

			byte[] hashedPreviousIdentity = ledgerUtils.generateHashOfIdentity(blockIdentityPrev);

			veidBlockIdentity.setPreviousHash(ledgerUtils.toBase64(hashedPreviousIdentity));

			byte prevHashoOfIdentity[] = ledgerUtils.fromBase64(blockIdentityPrev.getPreviousHash());
			byte[] hashedIdentity = ledgerUtils.generateHashOfIdentity(veidBlockIdentity);
			byte hashMerkleRoot[] = ledgerUtils.generateConcatinatedHash(hashedIdentity, prevHashoOfIdentity);
			String encodedHashMerkleRoot = ledgerUtils.toBase64(hashMerkleRoot);

			return ledgerServiceWrapper.addVeidBlockIdentity(veidBlockIdentity, encodedHashMerkleRoot);
		}
	}

	public VeidblockLedgerDB fetchVeidblockLedger(long resourceId) {
		String encodedId = ledgerUtils.toBase64(("" + resourceId).getBytes());
		VeidblockLedgerDB veidblockLedgerDB = null;
		try {
			veidblockLedgerDB = ledgerServiceWrapper.getVedBlockLedger(encodedId);
		} catch (Exception e) {
			if (e.getMessage().equals("-1")) {
				veidblockLedgerDB = null;
			}
		}
		return veidblockLedgerDB;
	}

	public VeidblockIdentityDB getVeidblockIdentity(long resourceId) throws VeidblockException {
		String encodedId = ledgerUtils.toBase64(("" + resourceId).getBytes());
		VeidblockLedgerDB veidblockLedgerDB = null;
		try {
			veidblockLedgerDB = ledgerServiceWrapper.getVedBlockLedger(encodedId);
		} catch (Exception e) {
			if (e.getMessage().equals("-1")) {
				throw new VeidblockException("Ledger Identity not found !");
			}
		}

		LedgerUtils ledgerUtils = new LedgerUtils();
		VeidblockHeader veidblockHeader = ledgerUtils.toVeIdBlockHeader(veidblockLedgerDB.getVeidblockHeaderDb());

		List<VeidblockIdentityDB> veidblockIdentities = veidblockLedgerDB.getBlockIdentityList();
		if (veidblockIdentities.size() > 0) {
			return veidblockIdentities.get(veidblockIdentities.size() - 1);
		}
		throw new VeidblockException("Ledger Identity not found !");
	}

	public boolean verify(LedgerVeidblock ledgerVeidblock) {
		
		try {

			ledgerVeidblock.verify();
			return true;

		} catch (VeidblockException e) {
		
			e.printStackTrace();
			return false;
		}
	}

	public boolean verify(VeidblockIdentityDB veidBlockIdentity) {

		List<VeidblockIdentityDB> veidblockIdentities = ledgerServiceWrapper
				.getVeidclockIdentity(veidBlockIdentity.getVeid(), veidBlockIdentity.getCounter());

		for (int i = veidblockIdentities.size() - 1; i >= 1; i--) {
			try {
				VeidblockIdentityDB previousIdentity = veidblockIdentities.get(i - 1);
				byte[] hashedPreviousIdentity = ledgerUtils.generateHashOfIdentity(previousIdentity);
				if (!ledgerUtils.toBase64(hashedPreviousIdentity)
						.equals(veidblockIdentities.get(i).getPreviousHash())) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}

		byte[] prevHashOfFirstHeader = new byte[32];
		if (!ledgerUtils.toBase64(prevHashOfFirstHeader).equals(veidblockIdentities.get(0).getPreviousHash())) {
			return false;
		}
		return true;
	}
	
	public List<VeidblockHeaderDB> getVeidblockHeaders(){
		return ledgerServiceWrapper.getVeidblockHeaders();
	}
}