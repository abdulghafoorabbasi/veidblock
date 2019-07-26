package org.acreo.ledger.database.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.common.entities.GenericVeidblockHeader;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class GenericVeidblockHeaderMapper implements ResultSetMapper<GenericVeidblockHeader> {
	
	private static final String VEID = "veid";
	private static final String SENDER_ID = "senderId";
	private static final String VERSION = "version";
	private static final String HSHH_PREV_BLOCK = "hashPrevBlock";
	private static final String HASH_MERKLE_ROOT = "hashMerkleRoot";
	private static final String CREATION_TIME = "creationTime";
	
	
	public GenericVeidblockHeader map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new GenericVeidblockHeader(resultSet.getLong(VEID), resultSet.getLong(SENDER_ID), resultSet.getString(VERSION), resultSet.getString(HSHH_PREV_BLOCK),
				resultSet.getString(HASH_MERKLE_ROOT), resultSet.getString(CREATION_TIME));
	}
}