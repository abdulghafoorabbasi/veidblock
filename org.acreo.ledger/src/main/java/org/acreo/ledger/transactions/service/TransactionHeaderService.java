package org.acreo.ledger.transactions.service;

import java.util.List;

import org.acreo.ledger.transactions.database.TransactionHeaderDAO;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class TransactionHeaderService {
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";

	@CreateSqlObject
	abstract TransactionHeaderDAO transactionHeaderDAO();

	public int createTransactionHeader(@BindBean final TransactionHeader transactionHeader) {
		return transactionHeaderDAO().createTransactionHeader(transactionHeader);
	}
	
	public int updateHashMarkleRoot(@Bind("ref") final String ref, @Bind("merkle") final String merkle) {
		return transactionHeaderDAO().updateHashMarkleRoot(ref, merkle);
	}
	
	

	public List<TransactionHeader> getTransactionHeaders() {
		return transactionHeaderDAO().getTransactionHeaders();
	}

	public TransactionHeader getTransactionHeaderByRef(@Bind("ref") final String ref) {
		return transactionHeaderDAO().getTransactionHeaderByRef(ref);
	}

	public List<TransactionHeader> getTransactionHeaderByCreator(@Bind("creator") final String creator) {
		return transactionHeaderDAO().getTransactionHeaderByCreator(creator);
	}

	public TransactionHeader getTransactionHeaderByName(@Bind("name") final String name) {
		return transactionHeaderDAO().getTransactionHeaderByName(name);
	}

	public int getTotalHeaders() {
		return transactionHeaderDAO().getTotalHeaders();
	}
	
	public TransactionHeader getLastTransactionHeader(){
		return transactionHeaderDAO().getLastTransactionHeader();
	}

	public String performHealthCheck() {
		try {
			transactionHeaderDAO().getTransactionHeaders();
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