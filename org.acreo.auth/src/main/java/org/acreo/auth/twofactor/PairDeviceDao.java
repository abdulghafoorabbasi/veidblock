package org.acreo.auth.twofactor;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(PairDeviceMapper.class)
public interface PairDeviceDao {

	@SqlQuery("select * from PairDevice;")
	public List<PairDevice> getPairedDevices();

	@SqlQuery("select * from PairDevice where deviceId = :deviceId")
	public PairDevice getPairedDevice(@Bind("deviceId") final long deviceId);
	
	@SqlQuery("select COUNT(deviceNo) from PairDevice where uid = :uid")
	public int getMaxPairedDevice(@Bind("uid") final long uid);
	
	
	
	@SqlQuery("select * from PairDevice where uid = :uid")
	public List<PairDevice> getPairedDevices(@Bind("uid") final String uid);

	@SqlQuery("select * from PairDevice where uid = :uid AND dpc = :dpc")
	public PairDevice getPairedDevice(@Bind("uid") final String uid, @Bind("dpc") final  String dpc);

	@SqlUpdate("insert into PairDevice(deviceId, uid, dpc, devicePairDateTime, expiryPeriod, deviceNo, seqNo) values(:deviceId, :uid, :dpc, :devicePairDateTime, :expiryPeriod, :deviceNo, :seqNo)")
	public int addPairDevice(@BindBean final PairDevice pairDevice);
	
	
	@SqlUpdate("DELETE FROM PairDevice WHERE devicePairDateTime  <= now() - interval :min minute")
	public int deleteExpiredEntries(@Bind("min") final int min);
	
	@SqlQuery("select last_insert_id();")
	public long lastInsertId();
}
