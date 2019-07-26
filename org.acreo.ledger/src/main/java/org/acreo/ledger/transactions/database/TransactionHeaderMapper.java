package org.acreo.ledger.transactions.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class TransactionHeaderMapper implements ResultSetMapper<TransactionHeader> {

	private static final String ref = "ref";
	private static final String version = "version";
	private static final String hashPrevBlock = "hashPrevBlock";
	private static final String creationTime = "creationTime";
	private static final String hashMerkleRoot = "hashMerkleRoot";
	private static final String extbits = "extbits";
	private static final String nonce = "nonce";
	private static final String height = "height";
	private static final String creator = "creator";
	private static final String chainName = "chainName";
	private static final String smartcontract = "smartcontract";

	private static final String creatorSignature = "creatorSignature";
	private static final String creatorURL = "creatorURL";
	
	private static final String signedBy = "signedBy";
	private static final String signerUrl = "signerUrl";
	private static final String signature = "signature";

	public TransactionHeader map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new TransactionHeader(resultSet.getString(ref), resultSet.getString(version),
				resultSet.getString(hashPrevBlock), resultSet.getString(creationTime), resultSet.getString(extbits),
				resultSet.getString(nonce), resultSet.getLong(height), resultSet.getString(hashMerkleRoot),
				resultSet.getString(creator), resultSet.getString(chainName), resultSet.getString(smartcontract),
				resultSet.getString(creatorSignature), resultSet.getString(creatorURL),
				resultSet.getString(signedBy), resultSet.getString(signerUrl), resultSet.getString(signature));
	}
}
