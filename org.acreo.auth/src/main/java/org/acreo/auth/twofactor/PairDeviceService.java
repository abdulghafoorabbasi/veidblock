package org.acreo.auth.twofactor;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.acreo.security.utils.SGen;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class PairDeviceService {
	private static final String PAIR_DEVICE_NOT_FOUND = "Device %s not found.";
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to execute request. Details: %s";
	private static final String ERROR = "%s";
	private static final String SUCCESS = "Success...";
	private static final String UNEXPECTED_ERROR = "An unexpected error occurred while deleting JWSToken.";

	@CreateSqlObject
	abstract PairDeviceDao pairDeviceDao();

	public List<PairDevice> getPaiedDevices() {
		return pairDeviceDao().getPairedDevices();
	}

	public List<PairDevice> getPaiedDevices(String uid) {
		return pairDeviceDao().getPairedDevices(uid);
	}

	public PairDevice getPairedDevice(String uid, String dpc) {
		PairDevice pairDevice = pairDeviceDao().getPairedDevice(uid, dpc);
		if (Objects.isNull(pairDevice)) {
			throw new WebApplicationException(String.format(PAIR_DEVICE_NOT_FOUND, uid + " , " + dpc),
					Status.NOT_FOUND);
		}
		return pairDevice;
	}

	public PairDevice getPairedDevice(long deviceId) {
		return pairDeviceDao().getPairedDevice(deviceId);
	}

	public PairDevice addPairDevice(long uid) {

		PairDevice pairDevice = new PairDevice ();
		long deviceId = new SGen().generateId();
		pairDevice.setDeviceId(deviceId);
		int deviceNo = pairDeviceDao().getMaxPairedDevice(uid);
		if(deviceNo >= 1 ){
			throw new WebApplicationException(
					String.format(ERROR , "Device already exists for pairing. Please wait to clean created devices and try later !"),
					Status.FORBIDDEN);
		}
		pairDevice.setUid(uid);
		pairDevice.setDeviceNo(deviceNo + 1);
		pairDevice.setDevicePairDateTime(new Date());
		pairDevice.setDpc(new SGen().pdc(6));
		pairDevice.setExpiryPeriod(60);
		pairDevice.setSeqNo(Long.parseLong(new SGen().pdc(4)));
		int status = pairDeviceDao().addPairDevice(pairDevice);
		if (status == 1) {
			return pairDevice;
		}
		throw new WebApplicationException(
				String.format(DATABASE_UNEXPECTED_ERROR, "problems when pairing device. please try later !"),
				Status.INTERNAL_SERVER_ERROR);

	}

	public int deleteExpiredEntries(int min) {
		return pairDeviceDao().deleteExpiredEntries(min);
	}

	public String performHealthCheck() {
		try {
			pairDeviceDao().getPairedDevices();
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
