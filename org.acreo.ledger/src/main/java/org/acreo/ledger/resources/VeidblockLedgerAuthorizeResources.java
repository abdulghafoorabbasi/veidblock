package org.acreo.ledger.resources;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.acreo.common.Representation;
import org.acreo.common.entities.GenericLedgerVeidblock;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockList;
import org.acreo.common.entities.LedgerVeidblockCO;
import org.acreo.common.entities.TokenCO;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.database.entities.VeidblockLedgerDB;
import org.acreo.ledger.database.generic.GenericLedgerServiceWrapper;
import org.acreo.ledger.database.generic.GenericVedblockService;
import org.acreo.ledger.service.VeidblockLedgerService;
import org.acreo.ledger.utils.GenericVeidblockManager;
import org.acreo.ledger.utils.LedgerUtils;
import org.acreo.ledger.utils.VeidblockManager;
import org.acreo.security.crypto.CryptoPolicy;
import org.eclipse.jetty.http.HttpStatus;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.hibernate.UnitOfWork;

@Path("/vbla")
@Produces(MediaType.APPLICATION_JSON)
public class VeidblockLedgerAuthorizeResources {

	@Context
	private UriInfo uriInfo;
	
	private VeidblockManager veidblockManager;
	private LedgerUtils ledgerUtils = new LedgerUtils();
	private CryptoPolicy cryptoPolicy = new CryptoPolicy();
	private GenericLedgerServiceWrapper genericLedgerServiceWrapper;
	GenericVeidblockManager genericVeidblockManager ;
	public VeidblockLedgerAuthorizeResources (GenericVedblockService genericVedblockService, VeidblockLedgerService veidblockLedgerService,
			LocalCertificateManager localCertificateManager) {
		veidblockManager = new VeidblockManager(veidblockLedgerService, localCertificateManager);
		genericLedgerServiceWrapper = new GenericLedgerServiceWrapper(genericVedblockService); 
		genericVeidblockManager = new GenericVeidblockManager(genericLedgerServiceWrapper,localCertificateManager.getCertificateSuite(),this.cryptoPolicy, localCertificateManager);

	}
	
	@POST
	@Timed
	@UnitOfWork
	@Path("/{owner}")
	public LedgerVeidblockCO add(String payload, @PathParam("owner") final long owner) throws Exception {
		VeidblockLedgerDB veidblockLedgerDB = veidblockManager.add(payload, ""+owner);
		LedgerUtils ledgerUtils = new LedgerUtils();
		return ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	
	@PUT
	@Timed
	@UnitOfWork
	@Path("/verifykey")
	public Representation<String> verifykey(String encodedPublicKey) throws Exception {
		return genericVeidblockManager.verifyPublicKey(encodedPublicKey); 
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Path("/add/{owner}")
	//http://localhost:10000/vbla/add/670467210
	public Response insertIntoLedger(TokenCO payload, @PathParam("owner") final long owner) throws Exception {
		String pubKeyVerifier = uriInfo.getBaseUri()+"vbla/verifykey";
		return genericVeidblockManager.add(owner, payload.getToken(), pubKeyVerifier); 
		//VeidblockLedgerDB veidblockLedgerDB = veidblockManager.add(payload, ""+owner);
		//LedgerUtils ledgerUtils = new LedgerUtils();
		//return payload;//ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	
	@GET
	@Timed
	@UnitOfWork
	@Path("/ledger/{owner}")
	//http://localhost:10000/vbla/payload/670467210
	public String fetchLedger(@PathParam("owner") final long owner) throws Exception {
		
		GenericLedgerVeidblock genericLedgerVeidblock = genericLedgerServiceWrapper.getVedBlockLedger(owner);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(genericLedgerVeidblock); 
		return json ;//ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	
	@GET
	@Timed
	@UnitOfWork
	@Path("/authorized/{owner}")
	//http://localhost:10000/vbla/authorized/670467210
	public String fetchAuthorizedPayload(@PathParam("owner") final long owner) throws Exception {
		
		List<GenericVeidblock> list = genericLedgerServiceWrapper.getAuhtorizedLedgerEntries(owner);
		GenericVeidblockList genericVeidblockList = new GenericVeidblockList();
		for(GenericVeidblock genericVeidblock: list){
			genericVeidblockList.add(genericVeidblock); 
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(genericVeidblockList); 
		return json;//ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	
	@GET
	@Timed
	@UnitOfWork
	@Path("/ledger/{owner}/{counter}")
	public LedgerVeidblockCO fetchLedgerWithCounter(@PathParam("owner") final long veid, @PathParam("counter") final long counter) throws Exception {
		TokenCO tokenCO = new TokenCO();
		// add pay load in token as value 
		//return tokenCO; 
		//genericVeidblockManager.add(owner, payload.getToken()); 
		//VeidblockLedgerDB veidblockLedgerDB = veidblockManager.add(payload, ""+owner);
		//LedgerUtils ledgerUtils = new LedgerUtils();
		return null;//ledgerUtils.toLedgerVeidblockCO(veidblockLedgerDB);
	}
	// Handle chain code here for general perspective 
	
	
	
	
	
	
	
}