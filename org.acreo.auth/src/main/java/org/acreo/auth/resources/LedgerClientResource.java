/*package org.acreo.auth.resources;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import org.acreo.auth.util.GenericVeidblockOperations;
import org.acreo.auth.util.VeidblockLedgerConnector;
import org.acreo.common.Representation;
import org.acreo.common.entities.AuthorizedDataCO;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ip.service.VerificationService;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;

@Api
@Path("/lc")
@Produces(MediaType.APPLICATION_JSON)
public class LedgerClientResource extends Authentication {

	// private AuthorizationManager authorizeManager;
	// private VerifyToken4ThirdParty verifyToken4ThirdParty;
	// private Enrollment enrollment;
	private String vblaURL = "";

	@Context
	protected SecurityContext securityContext;
	@Context
	private UriInfo uriInfo;
	private VerificationService verificationService;

	public LedgerClientResource(String vblaURL, VerificationService verificationService) {
		super();
		this.vblaURL = vblaURL;
		this.verificationService = verificationService;

	}

	@GET
	@Timed
	@RolesAllowed({ "" })
	@Path("/authorized/{owner}")
	public Representation<String> authorizedIdentityList(@PathParam("owner") final long owner) {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		try {
			VeidblockLedgerConnector veidblockLedgerConnector = new VeidblockLedgerConnector();
			//String endPoint = this.vblaURL + "/vbla/authorized/" + owner;
			return veidblockLedgerConnector.fetchAuthorizedVeidblock(this.vblaURL , owner);

		} catch (Exception e) {
			return new Representation(502, "Internal error :" + e.getMessage());
		}
	}

	@PUT
	@Timed
	@RolesAllowed({ "" })
	@Path("/extract/authorized/{owner}")
	public Representation<String> extractBlock(@PathParam("owner") final String owner,
			GenericVeidblock genericVeidblock) {
		String resourceId = headers.getHeaderString("RID");
		if (resourceId == null) {
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
		}
		GenericVeidblockOperations genericVeidblockOperations = new GenericVeidblockOperations(verificationService);
		
		try {
			AuthorizedDataCO authorizedDataCO = genericVeidblockOperations.extractBlock(genericVeidblock, "" + owner);
			ObjectMapper objectMapper = new ObjectMapper();
			
			String temp;
			try {
				temp = objectMapper.writeValueAsString(authorizedDataCO);
			} catch (JsonProcessingException e) {
				return new Representation(502, "Internal error :"+e.getMessage());
			}
			return new Representation<String>(Status.OK.getStatusCode(), temp);
		} catch (VeidblockException e) {
			
			e.printStackTrace();
			return new Representation(502, "Internal error :"+e.getMessage());
		}		
	}

	@Context
	protected HttpServletResponse response;
	@Context
	protected HttpHeaders headers;
}
*/