package org.acreo.ledger.database.generic;

import java.util.List;

import org.acreo.common.entities.GenericVeidblockHeader;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(GenericVeidblockHeaderMapper.class)
public interface GenericVeidblockHeaderDAO {

	@SqlUpdate("insert into GenericVeidblockHeader (veid, senderId,	version,hashPrevBlock,	hashMerkleRoot,	creationTime) values(:veid, :senderId,	:version, :hashPrevBlock, :hashMerkleRoot, :creationTime)")
	public int createGenericVeidblockHeader(@BindBean final GenericVeidblockHeader veidblockHeaderDB);

	@SqlUpdate("update GenericVeidblockHeader set version = coalesce(:version, version), hashPrevBlock = coalesce(:hashPrevBlock, hashPrevBlock), "
			+ "hashMerkleRoot = coalesce(:hashMerkleRoot, hashMerkleRoot), "
			+ "creationTime = coalesce(:creationTime, creationTime) " 
			+ "where senderId = :senderId")
	public int updateGenericVeidblockHeader(@BindBean final GenericVeidblockHeader veidblockHeaderDB);

	@SqlQuery("select * from GenericVeidblockHeader ORDER BY senderId")
	public List<GenericVeidblockHeader> getGenericVeidblockHeaders();

	@SqlQuery("select * from GenericVeidblockHeader where senderId = :senderId")
	public GenericVeidblockHeader getGenericVeidblockHeader(@Bind("senderid") final long veid);

	@SqlQuery("select * from GenericVeidblockHeader where senderId = :senderId")
	public GenericVeidblockHeader getVeidblockHeader(@Bind("senderId") final long senderid);

	@SqlQuery("Select * from GenericVeidblockHeader where senderId= (Select MAX(senderId) from VeIdBlockHeader)")
	public GenericVeidblockHeader getLastVeidHeader();

	@SqlQuery("Select MAX(veid)+1 from GenericVeidblockHeader")
	public long lastInsertId();
}