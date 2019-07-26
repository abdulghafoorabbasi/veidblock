package org.acreo.ledger.database.generic;

import java.util.List;

import org.acreo.common.entities.GenericVeidblock;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(GenericVeidblockIMapper.class)
public interface GenericVeidblockDAO {

	@SqlUpdate("insert into GenericVeidblock(senderId, seqNo, receiverId, payload, creationTime, previousHash, url,	validatorSignature,	status, validatorPublicKey) values(:senderId, :seqNo, :receiverId, :payload, :creationTime,	:previousHash, :url, :validatorSignature, :status, :validatorPublicKey)")
	public int createGenericVeidblock(@BindBean final GenericVeidblock genericVeidblock);

	@SqlQuery("select * from GenericVeidblock ORDER BY senderId")
	public List<GenericVeidblock> getGenericVeidclocks();

	@SqlQuery("select * from GenericVeidblock where senderId = :senderId ORDER BY seqNo")
	public List<GenericVeidblock> getGenericVeidclock(@Bind("senderId") final long senderId);
	
	@SqlQuery("select * from GenericVeidblock where senderId = :senderId AND seqNo = (select MAX(seqNo) from GenericVeidblock where senderId = :senderId ) ORDER BY seqNo")
	public List<GenericVeidblock> getLastGenericVeidclock(@Bind("senderId") final long senderId);
		
	@SqlQuery("select * from GenericVeidblock where senderId = :senderId AND seqNo < :seqNo ORDER BY seqNo")
	public List<GenericVeidblock> getGenericVeidclock(@Bind("senderId") final long senderId, @Bind("seqNo") final long seqno);
	
	@SqlQuery("select * from GenericVeidblock where receiverId = :receiverId ORDER BY seqNo")
	public List<GenericVeidblock> getAuhtorizedLedgerEntries(@Bind("receiverId") final long receiverId);
	
}