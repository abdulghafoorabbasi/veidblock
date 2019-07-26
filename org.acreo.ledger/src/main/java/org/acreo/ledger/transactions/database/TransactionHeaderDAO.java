package org.acreo.ledger.transactions.database;

import java.util.List;

import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(TransactionHeaderMapper.class)
public interface TransactionHeaderDAO {

	
	@SqlUpdate("insert into TransactionHeader ("
			+ "ref, version, hashPrevBlock, creationTime, extbits, nonce, height,hashMerkleRoot,"
			+ "creator, chainName, smartcontract, creatorSignature, creatorURL, signedBy, signerUrl, signature) "
			+ "values(:ref, :version, :hashPrevBlock, :creationTime, :extbits, :nonce, :height, :hashMerkleRoot,"
			+ ":creator, :chainName, :smartcontract, :creatorSignature, :creatorURL, :signedBy, :signerUrl, :signature)")
	public int createTransactionHeader(@BindBean final TransactionHeader transactionHeader);

	@SqlQuery("select * from TransactionHeader ORDER BY height")
	public List<TransactionHeader> getTransactionHeaders();

	@SqlQuery("select * from TransactionHeader where ref = :ref")
	public TransactionHeader getTransactionHeaderByRef(@Bind("ref") final String ref);

	@SqlQuery("select * from TransactionHeader where creator = :creator")
	public List<TransactionHeader> getTransactionHeaderByCreator(@Bind("creator") final String creator);

	@SqlQuery("select * from TransactionHeader where chainName = :chainName")
	public TransactionHeader getTransactionHeaderByName(@Bind("chainName") final String chainName);

	@SqlQuery("SELECT COUNT(ref) FROM TransactionHeader ")
	public int getTotalHeaders();

	@SqlQuery("Select * from TransactionHeader where height = (Select MAX(height) from TransactionHeader)")
	public TransactionHeader getLastTransactionHeader();

	@SqlUpdate("UPDATE TransactionHeader SET " + "hashMerkleRoot = :hashMerkleRoot "
			+ "where hashMerkleRoot = :hashMerkleRoot AND ref = :ref")
	public int updateHashMarkleRoot(@Bind("ref") final String ref,
			@Bind("hashMerkleRoot") final String hashMerkleRoot);

}
