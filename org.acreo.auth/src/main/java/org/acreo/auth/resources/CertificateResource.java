package org.acreo.auth.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acreo.auth.util.CertificateResourceDelegate;
import org.acreo.common.Representation;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.security.utils.PEMStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

@Api
@Path("/cert")
@Produces(MediaType.APPLICATION_JSON)
public class CertificateResource {

	
	private CertificateResourceDelegate certificateResourceDelegate;
	private LocalCertificateManager localCertificateManager;
	public CertificateResource( LocalCertificateManager localCertificateManager){
		certificateResourceDelegate = new CertificateResourceDelegate(localCertificateManager);
		this.localCertificateManager = localCertificateManager;
	}
	
	@POST
	@Path("/request")
	public Representation<String> issueCertificate(String certReq) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String temp = objectMapper.readValue(certReq, String.class);
			try {
				String pem = certificateResourceDelegate.signCert(temp);
				return new Representation<String>(200, pem); 
			} catch (VeidblockException e) {
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
			}
		} catch (Exception e1) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		} 
	}
	@GET
	@Path("/srvcert")
	public Representation<String> fetchCertificate() {
		try {
			String pem = new PEMStream().toPem(localCertificateManager.fetchCertificate());
			return new Representation<String>(200, pem);
			
		} catch (VeidblockException e) {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).build());
		}
	}
}
