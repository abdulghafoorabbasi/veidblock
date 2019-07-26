package org.acreo.ledger.database;

import java.util.List;

import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(VeidblockHeaderMapper.class)
public interface VeidblockHeaderDao {

	@SqlUpdate("insert into VeIdBlockHeader (veid, version, hashPrevBlock,	hashMerkleRoot,	creationTime, extbits,	nonce) values(:veid, :version, :hashPrevBlock, :hashMerkleRoot, :creationTime, :extbits, :nonce)")
	public int createVeidblockHeaderDB(@BindBean final VeidblockHeaderDB veidblockHeaderDB);

	@SqlUpdate("update VeIdBlockHeader set version = coalesce(:version, version), hashPrevBlock = coalesce(:hashPrevBlock, hashPrevBlock), "
			+ "hashMerkleRoot = coalesce(:hashMerkleRoot, hashMerkleRoot), "
			+ "creationTime = coalesce(:creationTime, creationTime), " + "extbits = coalesce(:extbits, extbits), "
			+ "nonce = coalesce(:nonce, nonce) where veid = :veid")
	public int updateVeidblockHeaderDB(@BindBean final VeidblockHeaderDB veidblockHeaderDB);

	@SqlQuery("select * from VeIdBlockHeader ORDER BY veid")
	public List<VeidblockHeaderDB> getVeidblockHeaders();

	@SqlQuery("select * from VeIdBlockHeader where veid = :veid")
	public VeidblockHeaderDB getVeIdBlockHeaderDB(@Bind("veid") final long veid);

	@SqlQuery("select * from VeIdBlockHeader where nonce = :nonce")
	public VeidblockHeaderDB getVeIdBlockHeaderDB(@Bind("nonce") final String nonce);

	@SqlQuery("Select * from VeIdBlockHeader where veid= (Select MAX(veid) from VeIdBlockHeader)")
	public VeidblockHeaderDB getLastVeidHeader();

	@SqlQuery("Select MAX(veid)+1 from VeIdBlockHeader")
	public long lastInsertId();
}