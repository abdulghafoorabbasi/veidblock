package org.acreo.ledger.transactions.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.ledger.transactions.entities.Transaction;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TransactionMapper implements ResultSetMapper<Transaction> {

	private static final String ref = "ref";
	private static final String depth = "depth";
	private static final String hashPrevBlock = "hashPrevBlock";
	private static final String creationTime = "creationTime";
	private static final String scope = "scope";
	private static final String sender = "sender";
	private static final String receiver = "receiver";
	private static final String payload = "payload";
	private static final String payloadType = "payloadType";
	private static final String cryptoOperationsOnPayload = "cryptoOperationsOnPayload";

	private static final String creatorSignature = "creatorSignature";
	private static final String creatorURL = "creatorURL";
	
	private static final String signedBy = "signedBy";
	private static final String signedDate = "signedDate";
	private static final String signerUrl = "signerUrl";
	private static final String signature = "signature";

	public Transaction map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new Transaction(resultSet.getString(ref), resultSet.getLong(depth), resultSet.getString(hashPrevBlock),
				resultSet.getString(creationTime), resultSet.getString(scope), resultSet.getString(sender),
				resultSet.getString(receiver), resultSet.getString(payload), resultSet.getString(payloadType),
				resultSet.getString(cryptoOperationsOnPayload), resultSet.getString(creatorSignature),
				resultSet.getString(creatorURL), resultSet.getString(signedBy),
				resultSet.getString(signedDate), resultSet.getString(signerUrl), resultSet.getString(signature));
	}
}
