package org.acreo.ledger.database.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.common.entities.GenericVeidblock;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class GenericVeidblockIMapper implements ResultSetMapper<GenericVeidblock> {

	private static final String SENDER_ID = "senderId";
	private static final String SEQ_NO = "seqNo";
	private static final String RECEIVER_ID = "receiverId";
	private static final String PAYLOAD = "payload";
	private static final String CREATION_TIME = "creationTime";
	private static final String PREVIOUS_HASH = "previousHash";
	private static final String URL = "url";
	private static final String VALIDATOR_SIGNATURE = "validatorSignature";
	private static final String VALIDATOR_PUBLICKEY = "validatorPublicKey";

	private static final String STATUS = "status";

	public GenericVeidblock map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new GenericVeidblock(resultSet.getLong(SENDER_ID), resultSet.getLong(SEQ_NO),
				resultSet.getLong(RECEIVER_ID), resultSet.getString(PAYLOAD), resultSet.getString(CREATION_TIME),
				resultSet.getString(PREVIOUS_HASH), resultSet.getString(URL), resultSet.getString(STATUS),
				resultSet.getString(VALIDATOR_SIGNATURE), resultSet.getString(VALIDATOR_PUBLICKEY));
	}
}