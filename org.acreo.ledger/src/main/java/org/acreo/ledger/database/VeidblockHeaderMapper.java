package org.acreo.ledger.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class VeidblockHeaderMapper implements ResultSetMapper<VeidblockHeaderDB> {
	
	private static final String VEID= "veid";
	private static final String VERSION = "version";
	private static final String HSHH_PREV_BLOCK = "hashPrevBlock";
	private static final String HAHS_MERKLE_ROOT = "hashMerkleRoot";
	private static final String CREATION_TIME = "creationTime";
	private static final String EXT_BITS = "extbits";
	private static final String NONCE = "nonce";
	
	public VeidblockHeaderDB map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new VeidblockHeaderDB(resultSet.getLong(VEID), resultSet.getString(VERSION), resultSet.getString(HSHH_PREV_BLOCK),
				resultSet.getString(HAHS_MERKLE_ROOT), resultSet.getString(CREATION_TIME),resultSet.getString(EXT_BITS),resultSet.getString(NONCE));
	}
}