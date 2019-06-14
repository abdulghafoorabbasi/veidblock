package org.acreo.auth.twofactor.paired;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.acreo.auth.twofactor.PairDevice;
import org.acreo.security.utils.SGen;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

public abstract class PairedDeviceService {
	private static final String PAIR_DEVICE_NOT_FOUND = "Device %s not found.";
	private static final String DATABASE_REACH_ERROR = "Could not reach the MySQL database. The database may be down or there may be network connectivity issues. Details: ";
	private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
	private static final String DATABASE_UNEXPECTED_ERROR = "Unexpected error occurred while attempting to execute request. Details: %s";
	private static final String ERROR = "%s";
	private static final String SUCCESS = "Success...";
	private static final String UNEXPECTED_ERROR = "An unexpected error occurred while deleting JWSToken.";

	@CreateSqlObject
	abstract PairedDeviceDao pairedDeviceDao();

	public List<PairedDevice> getPaiedDevices() {
		return pairedDeviceDao().getPairedDevices();
	}

	public List<PairedDevice> getPaiedDevices(String uid) {
		return pairedDeviceDao().getPairedDevices(uid);
	}

	public List<PairedDevice> getPairedDevice(String uid) {
		List<PairedDevice> pairDevice = pairedDeviceDao().getPairedDevices(""+uid);
		if (Objects.isNull(pairDevice)) {
			throw new WebApplicationException(String.format(PAIR_DEVICE_NOT_FOUND, uid),
					Status.NOT_FOUND);
		}
		return pairDevice;
	}

	public PairedDevice getPairedDevice(long deviceId) {
		return pairedDeviceDao().getPairedDevice(deviceId);
	}

	public PairedDevice addPairDevice(PairDevice pd, String signature, String pubKey) {

		PairedDevice pairDevice = new PairedDevice ();
		long deviceId = new SGen().generateId();
		pairDevice.setDeviceId(deviceId);
		int deviceNo = pairedDeviceDao().getMaxPairedDevice(pd.getUid());
		if(deviceNo >= 5 ){
			throw new WebApplicationException(
					String.format(ERROR , "Device already exists for pairing. Please wait to clean created devices and try later !"),
					Status.FORBIDDEN);
		}
		pairDevice.setUid(pd.getUid());
		pairDevice.setDeviceNo(deviceNo + 1);
		pairDevice.setDevicePairDateTime(new Date());
		pairDevice.setSeqNo(pd.getSeqNo());
		pairDevice.setSignature(signature);
		pairDevice.setPubKey(pubKey);
		int status = pairedDeviceDao().addPairedDevice(pairDevice);
		if (status == 1) {
			return pairDevice;
		}
		throw new WebApplicationException(
				String.format(DATABASE_UNEXPECTED_ERROR, "Problems when pairing device. please try later !"),
				Status.INTERNAL_SERVER_ERROR);

	}

	public int deletePairedDevice(long deviceId) {
		return pairedDeviceDao().deletePairedDevice(deviceId);
	}

	public String performHealthCheck() {
		try {
			pairedDeviceDao().getPairedDevices();
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
