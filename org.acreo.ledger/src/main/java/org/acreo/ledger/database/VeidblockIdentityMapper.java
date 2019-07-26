package org.acreo.ledger.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class VeidblockIdentityMapper implements ResultSetMapper<VeidblockIdentityDB> {
	
	private static final String VEID = "veid";
	private static final String COUNTER = "counter";
	private static final String PAYLOAD= "payload";
	private static final String CREATION_TIME = "creationTime";
	private static final String PREVIOUS_HASH = "previousHash";
	private static final String SIGNATURE = "signature";
	

	public VeidblockIdentityDB map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new VeidblockIdentityDB(resultSet.getLong(VEID), resultSet.getInt(COUNTER), resultSet.getString(PAYLOAD),
				resultSet.getString(CREATION_TIME), resultSet.getString(PREVIOUS_HASH), resultSet.getString(SIGNATURE));
	}
}