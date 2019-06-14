package org.acreo.auth.twofactor.paired;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(PairedDeviceMapper.class)
public interface PairedDeviceDao {

	@SqlQuery("select * from PairedDevice;")
	public List<PairedDevice> getPairedDevices();

	@SqlQuery("select * from PairedDevice where deviceId = :deviceId")
	public PairedDevice getPairedDevice(@Bind("deviceId") final long deviceId);
	
	@SqlQuery("select COUNT(deviceNo) from PairedDevice where uid = :uid")
	public int getMaxPairedDevice(@Bind("uid") final long uid);
	
	@SqlQuery("select * from PairedDevice where uid = :uid")
	public List<PairedDevice> getPairedDevices(@Bind("uid") final String uid);

	@SqlUpdate("insert into PairedDevice(deviceId, uid, devicePairDateTime, deviceNo, seqNo, signature, pubKey) values(:deviceId, :uid, :devicePairDateTime, :deviceNo, :seqNo, :signature, :pubKey)")
	public int addPairedDevice(@BindBean final PairedDevice pairDevice);
	
	
	@SqlUpdate("DELETE FROM PairedDevice WHERE deviceId = :deviceId")
	public int deletePairedDevice(@Bind("deviceId") final long deviceId);
	
	@SqlQuery("select last_insert_id();")
	public long lastInsertId();
}

