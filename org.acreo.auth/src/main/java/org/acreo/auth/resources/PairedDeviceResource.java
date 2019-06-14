package org.acreo.auth.resources;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.acreo.auth.Authentication;
import org.acreo.auth.twofactor.PairDevice;
import org.acreo.auth.twofactor.PairDeviceService;
import org.acreo.auth.twofactor.PdcVerifier;
import org.acreo.auth.twofactor.SignedPdc;
import org.acreo.auth.twofactor.paired.PairedDevice;
import org.acreo.auth.twofactor.paired.PairedDeviceCO;
import org.acreo.auth.twofactor.paired.PairedDeviceList;
import org.acreo.auth.twofactor.paired.PairedDeviceService;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;

@Api
@Path("/paired")
@Produces(MediaType.APPLICATION_JSON)
public class PairedDeviceResource extends Authentication {

	final static Logger logger = LoggerFactory.getLogger(PairedDeviceResource.class);
	private PairedDeviceService pairedDeviceService;
	private PairDeviceService pairDeviceService;
	@Context
	protected SecurityContext securityContext;
	@Context
	private UriInfo uriInfo;

	public PairedDeviceResource(LocalCertificateManager localCertificateManager,PairDeviceService pairDeviceService,
			PairedDeviceService pairedDeviceService) {
		super();
		logger.info("Initiating Verificatin Service !");
		localCertificateManager.getVeidblockConfig().setValidator("tempVerify");// uriInfo.getBaseUri()+"verify");
		localCertificateManager.getVeidblockConfig().setVerifier("tempVerify");// uriInfo.getBaseUri()+"verify");
		this.pairedDeviceService = pairedDeviceService;
		this.pairDeviceService = pairDeviceService;
		logger.info("Successfully tnitiated Verificatin Service !");
	}

	@GET
	@RolesAllowed({ "" })
	public List<PairedDevice> getPaiedDevices() {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		return this.pairedDeviceService.getPaiedDevices();

	}

	@GET
	@Path("/devices")
	@RolesAllowed({ "" })
	public PairedDeviceList getPaiedDevices(String uid) {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		
		List<PairedDevice> pdl = this.pairedDeviceService.getPairedDevice(resourceId);
		
		PairedDeviceList  pairedDeviceList  = new PairedDeviceList ();
		for(PairedDevice pd: pdl){
			pairedDeviceList  .add(pd);
		}
		return pairedDeviceList;	
	}

	@GET
	@Path("/devices/{deviceId}")
	public PairedDeviceCO getPairedDevice(String deviceId) {
		PairedDevice pairedDevice = this.pairedDeviceService.getPairedDevice(new BigInteger(deviceId).longValue());
		return new PairedDeviceCO(pairedDevice);
		
	}

	@Path("/verify")
	@Timed	
	public PairedDeviceCO verifyPairDevice(SignedPdc signedPdc) {
		
		PdcVerifier pdcVerifier = new PdcVerifier(this.pairDeviceService);
		PairDevice pairDevice;
		try {
			pairDevice = pdcVerifier.verify(signedPdc);
		} catch (VeidblockException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		PairedDevice pairedDevice = this.pairedDeviceService.addPairDevice(pairDevice, signedPdc.getSignature(), signedPdc.getClientCertificate());
		PairedDeviceCO pairedDeviceCO = new PairedDeviceCO(pairedDevice);
		return pairedDeviceCO;
	}
	
	@Context
	protected HttpServletResponse response;
	@Context
	protected HttpHeaders headers;	
	
}