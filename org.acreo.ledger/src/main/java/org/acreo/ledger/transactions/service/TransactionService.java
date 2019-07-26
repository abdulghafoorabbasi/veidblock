package org.acreo.ledger.transactions.service;

import java.util.List;

import org.acreo.ledger.transactions.database.TransactionDAO;
import org.acreo.ledger.transactions.entities.Transaction;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class TransactionService {
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";

	@CreateSqlObject
	abstract TransactionDAO transactionDAO();

	public int createTransaction(final Transaction transaction) {
		return transactionDAO().createTransaction(transaction);
	}

	public List<Transaction> getTransactions() {
		return transactionDAO().getTransactions();
	}

	public List<Transaction> getTransactionByRef(final String ref) {
		return transactionDAO().getTransactionByRef(ref);
	}

	public List<Transaction> getTransactionBySender(final String sender) {
		return transactionDAO().getTransactionBySender(sender);
	}

	public List<Transaction> getTransactionBySenderInChain(final String ref, final String sender) {
		try{
			List<Transaction> transactions = transactionDAO().getTransactionBySenderInChain(ref, sender);
			return transactions ;
		}catch (Exception exp){
			exp.printStackTrace();
		}
		return null;
	}

	public List<Transaction> getTransactionByReceiverInChain(final String ref, final String receiver) {
		return transactionDAO().getTransactionByReceiverInChain(ref, "%"+receiver+"%");
	}
	
	
	public List<Transaction> getTransactionByReceiver(final String receiver) {
		return transactionDAO().getTransactionByReceiver("%"+receiver+"%");
	}
	
	public Transaction getLastTransaction(String ref) {
		return transactionDAO().getLastTransaction(ref);
	}

	public List<Transaction> getTransactionsByRefAndDepth(String ref, long depth){
		return transactionDAO().getTransactionsByRefAndDepth(ref, depth);
	}
	
	
	
	public String performHealthCheck() {
		try {
			transactionDAO().getTransactions();
		} catch (UnableToObtainConnectionException ex) {
			return checkUnableToObtainConnectionException(ex);
		} catch (UnableToExecuteStatementException ex) {
			return checkUnableToExecuteStatementException(ex);
		} catch (Exception ex) {
			return DATABASE_UNEXPECTED_ERROR + ex.getCause().getLocalizedMessage();
		}
		return null;
	}

	private String checkUnableToObtainConnectionException(UnableToObtainConnectionException ex) {
		if (ex.getCause() instanceof java.sql.SQLNonTransientConnectionException) {
			return DATABASE_REACH_ERROR + ex.getCause().getLocalizedMessage();
		} else if (ex.getCause() instanceof java.sql.SQLException) {
			return DATABASE_CONNECTION_ERROR + ex.getCause().getLocalizedMessage();
		} else {
			return DATABASE_UNEXPECTED_ERROR + ex.getCause().getLocalizedMessage();
		}
	}

	private String checkUnableToExecuteStatementException(UnableToExecuteStatementException ex) {
		if (ex.getCause() instanceof java.sql.SQLSyntaxErrorException) {
			return DATABASE_CONNECTION_ERROR + ex.getCause().getLocalizedMessage();
		} else {
			return DATABASE_UNEXPECTED_ERROR + ex.getCause().getLocalizedMessage();
		}
	}
}