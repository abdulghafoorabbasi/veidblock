package org.acreo.auth.twofactor.paired;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.acreo.auth.twofactor.PairDevice;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PairedDeviceMapper implements ResultSetMapper<PairedDevice> {

	
	private static final String DEVICE_ID = "deviceId";
	private static final String UID = "uid";
	private static final String DEVICE_PAIR_DATE_TIME = "devicePairDateTime";
	private static final String DEVICE_NO = "deviceNo";
	private static final String SEQ_NO = "seqNo";
	private static final String SIGNATURE = "signature";
	private static final String PUB_KEY = "pubKey";

	public PairedDevice map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new PairedDevice(resultSet.getLong(DEVICE_ID), resultSet.getLong(UID),
				resultSet.getTimestamp(DEVICE_PAIR_DATE_TIME), resultSet.getInt(DEVICE_NO),
				resultSet.getLong(SEQ_NO), resultSet.getString(SIGNATURE), resultSet.getString(PUB_KEY));
	}
}
