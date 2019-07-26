package org.acreo.ledger.database;

import java.util.List;

import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(VeidblockIdentityMapper.class)
public interface VeidblockIdentityDao {

	@SqlUpdate("insert into VeidBlockIdentity(veid, counter, payload, creationTime, previousHash, signature) values(:veid, :counter, :payload, :creationTime, :previousHash, :signature)")
	public int createVeidblockIdentity(@BindBean final VeidblockIdentityDB veidBlockIdentity);

	@SqlQuery("select * from VeidBlockIdentity ORDER BY veid")
	public List<VeidblockIdentityDB> getVeidclockIdentities();

	@SqlQuery("select * from VeidBlockIdentity where veid = :veid ORDER BY counter")
	public List<VeidblockIdentityDB> getVeidclockIdentity(@Bind("veid") final long veid);
	
	@SqlQuery("select * from VeidBlockIdentity where veid = :veid AND counter = (select MAX(counter) from VeidBlockIdentity where veid = :veid ) ORDER BY counter")
	public List<VeidblockIdentityDB> getLastVeidclockIdentity(@Bind("veid") final long veid);
	
	
	@SqlQuery("select * from VeidBlockIdentity where veid = :veid AND counter < :counter ORDER BY counter")
	public List<VeidblockIdentityDB> getVeidclockIdentity(@Bind("veid") final long veid, @Bind("counter") final long counter);
	
}