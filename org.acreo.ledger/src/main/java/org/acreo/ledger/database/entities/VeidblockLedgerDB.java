package org.acreo.ledger.database.entities;

import java.util.List;

public class VeidblockLedgerDB {
	
	private VeidblockHeaderDB veidblockHeaderDb;
	private List<VeidblockIdentityDB> blockIdentityList;
	public VeidblockHeaderDB getVeidblockHeaderDb() {
		return veidblockHeaderDb;
	}
	public void setVeidblockHeaderDb(VeidblockHeaderDB veidblockHeaderDb) {
		this.veidblockHeaderDb = veidblockHeaderDb;
	}
	public List<VeidblockIdentityDB> getBlockIdentityList() {
		return blockIdentityList;
	}
	public void setBlockIdentityList(List<VeidblockIdentityDB> blockIdentityList) {
		this.blockIdentityList = blockIdentityList;
	}
}