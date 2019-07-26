package org.acreo.ledger.database.generic;

import java.util.List;

import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockHeader;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class GenericVedblockService {
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";

	@CreateSqlObject
	abstract GenericVeidblockDAO veidblockLedgerDao();
	
	@CreateSqlObject
	abstract GenericVeidblockHeaderDAO veidblockHeaderDao(); 

	public GenericVeidblockHeader getVeIdBlockHeaderDB(long senderid) {
		GenericVeidblockHeader genericVeidblockHeader = veidblockHeaderDao().getVeidblockHeader(senderid);
		return genericVeidblockHeader;
	}

	/*public VeidblockHeaderDB getVeIdBlockHeaderDB(String subject) {
		VeidblockHeaderDB veidblockHeaderDB = veidblockHeaderDao().getVeIdBlockHeaderDB(subject);
		return veidblockHeaderDB;
	}*/

	
	public List<GenericVeidblock> getAuhtorizedLedgerEntries(final long senderId) {
		return veidblockLedgerDao().getAuhtorizedLedgerEntries(senderId);
	}
	
	public List<GenericVeidblock> getVeidclockIdentity(final long senderId) {
		return veidblockLedgerDao().getGenericVeidclock(senderId);
	}
	
	public List<GenericVeidblock> getLastVeidclockIdentity(final long senderId) {
		return veidblockLedgerDao().getLastGenericVeidclock(senderId);
	}
	
	public List<GenericVeidblock> getVeidclockIdentity(final long senderId, final long seqno) {
		return veidblockLedgerDao().getGenericVeidclock(senderId, seqno);
	}
	

	public GenericVeidblockHeader getLastVeidHeader() {
		return veidblockHeaderDao().getLastVeidHeader();
	}

	public GenericVeidblockHeader createVeidblockHeaderDB(GenericVeidblockHeader genericVeidblockHeader) {
		veidblockHeaderDao().createGenericVeidblockHeader(genericVeidblockHeader);
		return veidblockHeaderDao().getVeidblockHeader(genericVeidblockHeader.getSenderId());		
	}
		
	public GenericVeidblock createVeidBlockIdentity(GenericVeidblock genericVeidblock) {
		veidblockLedgerDao().createGenericVeidblock(genericVeidblock);
		return genericVeidblock;
	}
	
	public GenericVeidblockHeader updateVeidblockHeaderDB(GenericVeidblockHeader veidblockHeaderDB){
		veidblockHeaderDao().updateGenericVeidblockHeader(veidblockHeaderDB);
		return veidblockHeaderDB;
	}
	
	public long getLastInsertedId(){
		return veidblockHeaderDao().lastInsertId();
	}
	
	public List<GenericVeidblockHeader> getVeidblockHeaders(){
		return veidblockHeaderDao().getGenericVeidblockHeaders();
	}
	
	public String performHealthCheck() {
		try {
			veidblockHeaderDao().getGenericVeidblockHeaders();
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