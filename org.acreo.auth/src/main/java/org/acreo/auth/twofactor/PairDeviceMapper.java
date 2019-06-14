package org.acreo.auth.twofactor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class PairDeviceMapper implements ResultSetMapper<PairDevice> {

	private static final String DEVICE_ID = "deviceId";
	private static final String UID = "uid";
	private static final String DPC = "dpc";
	private static final String DEVICE_PAIR_DATE_TIME = "devicePairDateTime";
	private static final String EXPIRY_PERIOD = "expiryPeriod";
	private static final String DEVICE_NO = "deviceNo";
	private static final String SEQ_NO = "seqNo";

	public PairDevice map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
		return new PairDevice(resultSet.getLong(DEVICE_ID), resultSet.getLong(UID), resultSet.getString(DPC),
				resultSet.getTimestamp(DEVICE_PAIR_DATE_TIME), resultSet.getInt(EXPIRY_PERIOD), resultSet.getInt(DEVICE_NO),
				resultSet.getLong(SEQ_NO));
	}
}
