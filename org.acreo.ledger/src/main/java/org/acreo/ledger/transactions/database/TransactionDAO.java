package org.acreo.ledger.transactions.database;

import java.util.List;

import org.acreo.ledger.transactions.entities.Transaction;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(TransactionMapper.class)
public interface TransactionDAO {
	
	@SqlUpdate("insert into Transaction (ref, depth, hashPrevBlock, creationTime, scope, sender, receiver, payload, payloadType, cryptoOperationsOnPayload, creatorSignature, creatorURL, signedBy, signedDate, signerUrl, signature)"
			+ "values(:ref, :depth, :hashPrevBlock, :creationTime, :scope, :sender, :receiver, :payload, :payloadType, :cryptoOperationsOnPayload, :creatorSignature, :creatorURL, :signedBy, :signedDate, :signerUrl, :signature)")
	public int createTransaction(@BindBean final Transaction transaction);

	@SqlQuery("select * from Transaction ORDER BY depth ORDER BY ref, depth")
	public List<Transaction> getTransactions();

	@SqlQuery("select * from Transaction where ref = :ref ORDER BY depth")
	public List<Transaction> getTransactionByRef(@Bind("ref") final String ref);

	@SqlQuery("select * from Transaction where sender = :sender")
	public List<Transaction> getTransactionBySender(@Bind("sender") final String sender);
		
	@SqlQuery("select * from Transaction where ref = :ref AND sender = :sender")
	public List<Transaction> getTransactionBySenderInChain(@Bind("ref") final String ref, @Bind("sender") final String sender);
	
	@SqlQuery("select * from Transaction where receiver LIKE :receiver")
	public List<Transaction> getTransactionByReceiver(@Bind("receiver") final String receiver);
	          
	@SqlQuery("select * from Transaction where receiver LIKE :receiver AND ref = :ref")
	public List<Transaction> getTransactionByReceiverInChain(@Bind("ref") final String ref, @Bind("receiver") final String receiver);
		
	@SqlQuery("Select * from Transaction where ref = :ref AND depth = (Select MAX(depth) from Transaction where ref = :ref )")
	public Transaction getLastTransaction(@Bind("ref") final String ref);
	
	@SqlQuery("select * from Transaction where ref = :ref AND depth <= :depth ORDER BY depth")
	public List<Transaction> getTransactionsByRefAndDepth(@Bind("ref") final String ref, @Bind("depth") final long depth);
}
