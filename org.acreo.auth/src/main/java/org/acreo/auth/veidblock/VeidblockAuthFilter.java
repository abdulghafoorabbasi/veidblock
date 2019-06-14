package org.acreo.auth.veidblock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acreo.auth.context.LocalSecurityContext;
import org.acreo.common.entities.AuthenticationType;
import org.acreo.veidblock.JWSToken;
import org.apache.log4j.Logger;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class VeidblockAuthFilter extends AuthFilter {
	
	private VeidblockAuth authenticator;
	final static Logger logger = Logger.getLogger(VeidblockAuthFilter.class);
	
	public VeidblockAuthFilter(VeidblockAuth authenticator) {
		this.authenticator = authenticator;

	}

	private String getRedirectedURL(String query) {
		
		if (query == null) {
			return null;
		}
		
		int i = query.indexOf("=");
		
		if (i == -1) {
			return null;
		}
		String urlEncoded = query.substring(i + 1, query.length());
		String url = new String(Base64.getDecoder().decode(urlEncoded));
		return url;
	}

	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("Request received ! ");
		String query = requestContext.getUriInfo().getRequestUri().getQuery();
		String url = getRedirectedURL(query);
		java.util.Optional<VeidblockUser> authenticatedUser;

		try {

			VeidblockCredentials credentials = extractCredentials(requestContext);
			authenticatedUser = authenticator.authenticate(credentials);

			if (!authenticatedUser.isPresent()) {
				throw new WebApplicationException(
						generateResponse("Problems when processing credentials !", requestContext));
			}

			VeidblockUser veidblockUser = (VeidblockUser) authenticatedUser.get();
			LocalSecurityContext localSecurityContext = new LocalSecurityContext(requestContext.getSecurityContext(),
					veidblockUser.getName(), true, veidblockUser.getAuthenticationHeader().value());

			requestContext.setSecurityContext(localSecurityContext);
			requestContext.getHeaders().remove(HttpHeaders.AUTHORIZATION);
			requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION,
					veidblockUser.getAuthenticationHeader().value() + " " + veidblockUser.getToken());
			JWSToken jwsToken = null;
			try {
				jwsToken = new JWSToken(veidblockUser.getToken());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jwsToken  == null){
				throw new WebApplicationException(generateResponse("Unable to process access token !", requestContext));
			}
			
			requestContext.getHeaders().add("RID", jwsToken.getPayload().getSub());
			
			if(url != null){
				//requestContext.getHeaders().add(HttpHeaders.LOCATION, url);
				throw new WebApplicationException(redirect4Verification(url , requestContext));
			}

		} catch (AuthenticationException e) {
			throw new WebApplicationException(
					generateResponse(e.getMessage(), requestContext));
		}

		if (!authenticatedUser.isPresent()) {
			throw new WebApplicationException(generateResponse("Credentials are not valid", requestContext));
		}
	}

	private VeidblockCredentials extractCredentials(ContainerRequestContext requestContext) throws IOException {
		VeidblockCredentials credentials = new VeidblockCredentials();
		logger.debug("Extractig authentication credentials !");
		try {
			String rawToken = requestContext.getCookies().get("auth_token").getValue();
			String rawUserId = requestContext.getCookies().get("auth_user").getValue();
			credentials.setToken(rawToken);
			credentials.setUserId(rawUserId);
			credentials.setAuthenticationType(AuthenticationType.BEARER);
		} catch (Exception e) {
			String aaSecurityContextEncoded = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			if (aaSecurityContextEncoded == null) {
				aaSecurityContextEncoded = readRequestBody(requestContext);
				if (aaSecurityContextEncoded == null || aaSecurityContextEncoded.equals("")) {
					logger.error("Problems when processing credentials");
					throw new WebApplicationException(							
							generateResponse("Problems when processing credentials", requestContext));
				}
			}
			credentials.setToken(aaSecurityContextEncoded);
			credentials.setAuthenticationType(null);
		}
		credentials.setMethod(requestContext.getMethod());
		credentials.setUrl(new URL(requestContext.getUriInfo().getAbsolutePath() + ""));
		logger.debug("Successfully extracted authentication credentials !");
		logger.debug("Autheitcation Type: "+credentials.getAuthenticationType());
		logger.debug("Credential: "+credentials.getToken());
		return credentials;
	}

	private Response generateResponse(String message, ContainerRequestContext requestContext) {
		WwwAuthentication wwwAuthentication = new WwwAuthentication(AuthenticationType.BASIC);
		return Response.status(Status.UNAUTHORIZED).entity(message)
				.header(HttpHeaders.WWW_AUTHENTICATE, wwwAuthentication.getStdWwwAuthenHdr("localhost:8080")).build();

	}

	private String readRequestBody(ContainerRequestContext request) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getEntityStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] readChars = new char[1024];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(readChars)) > 0) {
					stringBuilder.append(readChars, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		String body = stringBuilder.toString();
		return body;
	}
	
	private Response redirect4Verification(String url, ContainerRequestContext requestContext) {

		try {
			java.net.URI location = new java.net.URI(url);
			Response response = Response.temporaryRedirect(location)
					.header(HttpHeaders.AUTHORIZATION,requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).build();
			return response;

		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new WebApplicationException("Problems when generating response for redirect !");
		}
	}	
}