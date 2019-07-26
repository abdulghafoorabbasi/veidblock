package org.acreo.ledger.service;

import java.util.List;

import org.acreo.ledger.database.VeidblockHeaderDao;
import org.acreo.ledger.database.VeidblockIdentityDao;
import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class VeidblockLedgerService {
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";

	@CreateSqlObject
	abstract VeidblockIdentityDao veidblockLedgerDao();
	
	@CreateSqlObject
	abstract VeidblockHeaderDao veidblockHeaderDao(); 

	public VeidblockHeaderDB getVeIdBlockHeaderDB(long veid) {
		VeidblockHeaderDB veidblockHeaderDB = veidblockHeaderDao().getVeIdBlockHeaderDB(veid);
		return veidblockHeaderDB;
	}

	public VeidblockHeaderDB getVeIdBlockHeaderDB(String subject) {
		VeidblockHeaderDB veidblockHeaderDB = veidblockHeaderDao().getVeIdBlockHeaderDB(subject);
		return veidblockHeaderDB;
	}

	public List<VeidblockIdentityDB> getVeidclockIdentity(final long veid) {
		return veidblockLedgerDao().getVeidclockIdentity(veid);
	}
	
	public List<VeidblockIdentityDB> getLastVeidclockIdentity(final long veid) {
		return veidblockLedgerDao().getLastVeidclockIdentity(veid);
	}
	
	public List<VeidblockIdentityDB> getVeidclockIdentity(final long veid, final long counter) {
		return veidblockLedgerDao().getVeidclockIdentity(veid,counter);
	}
	

	public VeidblockHeaderDB getLastVeidHeader() {
		return veidblockHeaderDao().getLastVeidHeader();
	}

	public VeidblockHeaderDB createVeidblockHeaderDB(VeidblockHeaderDB veidblockHeaderDB) {
		veidblockHeaderDao().createVeidblockHeaderDB(veidblockHeaderDB);
		return veidblockHeaderDao().getLastVeidHeader();		
	}
		
	public VeidblockIdentityDB createVeidBlockIdentity(VeidblockIdentityDB veidblockIdentity) {
		veidblockLedgerDao().createVeidblockIdentity(veidblockIdentity);
		return veidblockIdentity;
	}
	
	public VeidblockHeaderDB updateVeidblockHeaderDB(VeidblockHeaderDB veidblockHeaderDB){
		veidblockHeaderDao().updateVeidblockHeaderDB(veidblockHeaderDB);
		return veidblockHeaderDB;
	}
	
	public long getLastInsertedId(){
		return veidblockHeaderDao().lastInsertId();
	}
	
	public List<VeidblockHeaderDB> getVeidblockHeaders(){
		return veidblockHeaderDao().getVeidblockHeaders();
	}
	
	public String performHealthCheck() {
		try {
			veidblockHeaderDao().getVeidblockHeaders();
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