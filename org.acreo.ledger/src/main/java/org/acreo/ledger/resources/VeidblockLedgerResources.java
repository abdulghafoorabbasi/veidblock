package org.acreo.ledger.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acreo.common.entities.LedgerVeidblockCO;
import org.acreo.common.entities.VeidblockHeaderListCO;
import org.acreo.common.entities.VeidblockIdentityCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.database.entities.VeidblockHeaderDB;
import org.acreo.ledger.database.entities.VeidblockIdentityDB;
import org.acreo.ledger.database.entities.VeidblockLedgerDB;
import org.acreo.ledger.service.VeidblockLedgerService;
import org.acreo.ledger.utils.LedgerUtils;
import org.acreo.ledger.utils.LedgerVeidblock;
import org.acreo.ledger.utils.VeidblockManager;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/vbl")
@Produces(MediaType.APPLICATION_JSON)
public class VeidblockLedgerResources {

	private VeidblockManager veidblockManager;
	private LedgerUtils ledgerUtils = new LedgerUtils();

	public VeidblockLedgerResources(VeidblockLedgerService veidblockLedgerService,
			LocalCertificateManager localCertificateManager) {
		veidblockManager = new VeidblockManager(veidblockLedgerService, localCertificateManager);

	}

	@POST
	@Timed
	@UnitOfWork
	public LedgerVeidblockCO add(String jwsTokenEncoded) throws Exception {
		VeidblockLedgerDB veidblockLedgerDB = veidblockManager.add(jwsTokenEncoded);
		LedgerUtils ledgerUtils = new LedgerUtils();
		return ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("/{resourceId}")
	public LedgerVeidblockCO add(String payload, @PathParam("resourceId") final long resourceId) throws Exception {
		VeidblockLedgerDB veidblockLedgerDB = veidblockManager.add(payload, ""+resourceId);
		LedgerUtils ledgerUtils = new LedgerUtils();
		return ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	
	
	@GET
	@Timed
	@UnitOfWork
	public VeidblockHeaderListCO getAllHeaders() {
		LedgerUtils ledgerUtils = new LedgerUtils();
		List<VeidblockHeaderDB> veidblockHeaderList = veidblockManager.getVeidblockHeaders();
		VeidblockHeaderListCO veidblockHeaderListCO = ledgerUtils.toVeidblockHeaderList(veidblockHeaderList);
		return veidblockHeaderListCO;
	}

	@GET
	@Timed
	@UnitOfWork
	@Path("/{resourceId}")
	public LedgerVeidblockCO fetchVeidblockLedger(@PathParam("resourceId") final long resourceId) {
		LedgerUtils ledgerUtils = new LedgerUtils();
		return ledgerUtils.toLedgerVeidblockCO(veidblockManager.fetchVeidblockLedger(resourceId));
	}

	@GET
	@Timed
	@UnitOfWork
	@Path("/last/{resourceId}")
	public VeidblockIdentityCO getVeidblockIdentity(long resourceId) throws VeidblockException {
		LedgerUtils ledgerUtils = new LedgerUtils();
		return ledgerUtils.toVeidblockIdentityCO(veidblockManager.getVeidblockIdentity(resourceId));
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("/verify/ledger")
	public boolean verify(LedgerVeidblockCO ledgerVeidblockCO) {
		LedgerVeidblock ledgerVeidblock = ledgerUtils.toLedgerVeidblock(ledgerVeidblockCO);
		return veidblockManager.verify(ledgerVeidblock);
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("/verify/identity")
	public boolean verify(VeidblockIdentityCO veidblockIdentityCO) {
		VeidblockIdentityDB veidBlockIdentity = ledgerUtils.toVeidblockIdentity(veidblockIdentityCO);
		return veidblockManager.verify(veidBlockIdentity);
	}
	//-----------------------------------------------------
	
	
}