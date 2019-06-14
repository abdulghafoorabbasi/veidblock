package org.acreo.auth.resources;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.acreo.auth.Authentication;
import org.acreo.auth.util.Enrollment;
import org.acreo.auth.util.EnrollmentIO;
import org.acreo.auth.util.VerifyToken4ThirdParty;
import org.acreo.common.Representation;
import org.acreo.common.entities.TokenCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.VeidblockIO;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;

@Api
@Path("/verify")
@Produces(MediaType.APPLICATION_JSON)
public class VerificationResource extends Authentication {

	final static Logger logger = LoggerFactory.getLogger(VerificationResource.class);
	//private AuthorizationManager authorizeManager;
	private VerifyToken4ThirdParty verifyToken4ThirdParty;
	private Enrollment enrollment;
		
	@Context
	protected SecurityContext securityContext;
	@Context
	private UriInfo uriInfo;

	public VerificationResource(String vblaURL, LocalCertificateManager localCertificateManager,
			VerificationService verificationService, JWSTokenService jwsTokenService) {
		super();
		logger.info("Initiating Verificatin Service !");
		localCertificateManager.getVeidblockConfig().setValidator("tempVerify");//uriInfo.getBaseUri()+"verify");
		localCertificateManager.getVeidblockConfig().setVerifier("tempVerify");//uriInfo.getBaseUri()+"verify");
		//authorizeManager = new AuthorizationManager(localCertificateManager, verificationService, jwsTokenService);
		verifyToken4ThirdParty = new VerifyToken4ThirdParty(localCertificateManager, jwsTokenService);
		try {
			this.enrollment = new Enrollment(localCertificateManager, new VeidblockIO().getMyIdentity());
		} catch (VeidblockException e) {
			logger.error("Problems when enrolling Verificatin Service !");
		}
		logger.info("Successfully tnitiated Verificatin Service !");
	}

	@POST
	@Timed
	@RolesAllowed({ "MANAGER" })
	public Representation<String> post() {
		return createHttpResponse();
	}

	@POST
	@Path("/token")
	public Representation<String> verifyToken(TokenCO token) {

		return verifyToken4ThirdParty.verify(token);
	}

	@POST
	@Path("/pubkey")
	public Representation<String> verifyPublicKey(String encodedKey) {
		return verifyToken4ThirdParty.verify(encodedKey);
	}

	@GET
	@Path("/enroll")
	public String initEnrollment() {
		boolean b = true;
		if(b){
			return "helo";
		}
		
		try {
			if (new EnrollmentIO().isEndorsementExisits()) {
				return "Already enrolled !";
			}
			// http://localhost:9000/verify/enroll;
			return enrollment.enroll("http://localhost:11000/enroll", uriInfo.getBaseUri()+"verify/pubkey");
		} catch (VeidblockException e) {
			return "Problems when enrolling. Internal server error !";
		}
	}
	
	@Context
	protected HttpServletResponse response;
	@Context
	protected HttpHeaders headers;

}