package org.acreo.auth.resources;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.acreo.auth.twofactor.PairDeviceCO;
import org.acreo.auth.twofactor.PairDeviceService;
import org.acreo.auth.twofactor.PdcVerifier;
import org.acreo.init.LocalCertificateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;

@Api
@Path("/pair")
@Produces(MediaType.APPLICATION_JSON)
public class PairDeviceResource extends Authentication {

	final static Logger logger = LoggerFactory.getLogger(PairDeviceResource.class);
	private PairDeviceService pairDeviceService;

	@Context
	protected SecurityContext securityContext;
	@Context
	private UriInfo uriInfo;

	public PairDeviceResource(LocalCertificateManager localCertificateManager,
			PairDeviceService pairDeviceService) {
		super();
		logger.info("Initiating Verificatin Service !");
		localCertificateManager.getVeidblockConfig().setValidator("tempVerify");// uriInfo.getBaseUri()+"verify");
		localCertificateManager.getVeidblockConfig().setVerifier("tempVerify");// uriInfo.getBaseUri()+"verify");
		this.pairDeviceService = pairDeviceService;
		logger.info("Successfully tnitiated Verificatin Service !");
	}

	@GET
	@RolesAllowed({ "" })
	public List<PairDevice> getPaiedDevices() {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		return this.pairDeviceService.getPaiedDevices();

	}

	@GET
	@Path("/devices")
	/*@RolesAllowed({ "" })*/
	public List<PairDevice> getPaiedDevices(String uid) {
		PairDevice pd = this.pairDeviceService.getPairedDevice(new BigInteger("563707126410").longValue());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(simpleDateFormat.format(pd.getDevicePairDateTime()));
		
		return null;
		
	/*	String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		return this.pairDeviceService.getPaiedDevices(resourceId);*/
	}

	@GET
	@Path("/devices/{deviceId}")
	
	public PairDevice getPairedDevice(String deviceId) {
		
		
		
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		PairDevice pairDevice = this.pairDeviceService.getPairedDevice(new BigInteger(deviceId).longValue());
		if (pairDevice.getUid() == Long.parseLong(resourceId)) {
			return pairDevice;
		}
		throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
	}

	@POST
	@Timed
	@RolesAllowed({ "" })
	public PairDeviceCO addPairDevice() {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		PairDevice pairDeviceRet = this.pairDeviceService.addPairDevice(Long.parseLong(resourceId));
		PairDeviceCO pairDeviceCORest = new PairDeviceCO();
		pairDeviceCORest.setDeviceId(pairDeviceRet.getDeviceId());
		pairDeviceCORest.setDpc(pairDeviceRet.getDpc()+"-"+pairDeviceRet.getSeqNo());
		pairDeviceCORest.setExpiryPeriod(pairDeviceRet.getExpiryPeriod());
		pairDeviceCORest.setUid(pairDeviceRet.getUid());
		return pairDeviceCORest;
	}

	@Context
	protected HttpServletResponse response;
	@Context
	protected HttpHeaders headers;	
	
}