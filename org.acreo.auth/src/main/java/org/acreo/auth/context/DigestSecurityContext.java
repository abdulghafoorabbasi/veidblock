package org.acreo.auth.context;

import java.util.Objects;

import org.acreo.auth.util.Convertor;
import org.acreo.auth.util.JWSTokenHelper;
import org.acreo.auth.veidblock.VeidblockCredentials;
import org.acreo.common.entities.AuthenticationType;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.entities.JwsTokenDb;
import org.acreo.ip.entities.Resource;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;
import org.acreo.veidblock.JWSToken;
import org.acreo.veidblock.token.JWToken;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreType
public class DigestSecurityContext extends VeidblockCredentials {

	
	private static final String USERNAME = "username";
	private static final String REALM = "realm";
	private static final String NONCE = "nonce";
	private static final String URI = "uri";
	private static final String RESPONSE = "response";
	private static final String OPAQUE = "opaque";
	private static final String QOP = "qop";
	private static final String NC = "nc";
	private static final String CNONCE = "cnonce";
	
	
	private String realm;
	private String nonce;
	private String uri;
	private String response;
	private String opaque;
	private String qop;
	private String nc;
	private String cnonce;
	private String httpmethod = "GET";
	private String body;

	private VerificationService verificationService;
	private JWSTokenService jwsTokenService;
private LocalCertificateManager localCertificateManager;
	public DigestSecurityContext(VeidblockCredentials credentials, LocalCertificateManager localCertificateManager, VerificationService verificationService,
			JWSTokenService jwsTokenService) throws VeidblockException {

		this.verificationService = verificationService;
		this.jwsTokenService = jwsTokenService;
		this.localCertificateManager = localCertificateManager;
		this.httpmethod = credentials.getMethod();
		if (!parser(credentials.getToken())) {
			this.setAuthenticated(false);
			return;
		}
		this.setBody(body);
		this.setMethod(credentials.getMethod());
		verifyDigest();
		this.setAuthenticated(true);
	}

	private void verifyDigest() throws VeidblockException {

		String resourceId = this.getUserId();
		Resource resource = null;
		try {
			try {
				resource = verificationService.getResourceWithPassword(Long.parseLong(resourceId));
			} catch (Exception exp) {
				resource = verificationService.getResourceWithPassword(resourceId);
			}
			if (Objects.isNull(resource)) {
				this.setAuthenticated(false);
				throw new VeidblockException("Resource is not registered !");
			}

			if (!resource.isActiveStatus()) {
				this.setAuthenticated(false);
				throw new VeidblockException("Resource is not active, please check your email for further instructions to activate it !");
			}
			this.setToken(resource.getPassword());
			
			DigestVerifier digestVerifier = new DigestVerifier(this);			
			this.setAuthenticated(true);
		} catch (VeidblockException e) {
			this.setAuthenticated(false);
			throw new VeidblockException(e);
		}

		JWToken jwToken = null;//

		JwsTokenDb db = jwsTokenService.isValidTokenAlreadyExisits(resourceId);
		if (db != null) {
			jwToken = new Convertor().dbToeknTojWToken(db);
			setToken(jwToken.toEncoded());
			setAuthenticationType(AuthenticationType.BEARER);
			this.setAuthenticated(true);			
			return;
		}

		JWSTokenHelper jWSTokenHelper = new JWSTokenHelper(resource, localCertificateManager, "");
		setToken(jWSTokenHelper.getJWSToken().toEncoded());
		setAuthenticationType(AuthenticationType.BEARER);
		jwsTokenService.createJwsToken(new Convertor().jWTokenToDbToekn(jWSTokenHelper.getJWSToken()));
		this.setAuthenticated(true);

	}

	private boolean parser(String encodedSecurityContext) {
		
		try {

			String esc = encodedSecurityContext.substring("Digest".length() + 1, encodedSecurityContext.length());

			String tokenz[] = esc.split(", ");
			for (String token : tokenz) {
				String fields[] = token.split("=");
				if (fields[0].equalsIgnoreCase(USERNAME)) {
					String uniqueId = fields[1].substring(1, fields[1].length() - 1);
					super.setUserId(uniqueId);

				} else if (fields[0].equalsIgnoreCase(REALM)) {
					this.setRealm(fields[1].substring(1, fields[1].length() - 1));
				} else if (fields[0].equalsIgnoreCase(NONCE)) {
					this.setNonce(fields[1].substring(1, fields[1].length() - 1));
				} else if (fields[0].equalsIgnoreCase(URI)) {
					this.setUri(fields[1].substring(1, fields[1].length() - 1));
				} else if (fields[0].equalsIgnoreCase(RESPONSE)) {
					this.setResponse(fields[1].substring(1, fields[1].length() - 1));
				} else if (fields[0].equalsIgnoreCase(OPAQUE)) {
					this.setOpaque(fields[1].substring(1, fields[1].length() - 1));
				} else if (fields[0].equalsIgnoreCase(QOP)) {
					this.setQop(fields[1]);
					
				} else if (fields[0].equalsIgnoreCase(NC)) {
					this.setNc(fields[1]);
				} else if (fields[0].equalsIgnoreCase(CNONCE)) {
					this.setCnonce(fields[1].substring(1, fields[1].length() - 1));
				}
			}
			return true;
		} catch (Exception exp) {
			return false;
		}
	}
	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getOpaque() {
		return opaque;
	}

	public void setOpaque(String opaque) {
		this.opaque = opaque;
	}

	public String getQop() {
		return qop;
	}

	public void setQop(String qop) {
		this.qop = qop;
	}

	public String getNc() {
		return nc;
	}

	public void setNc(String nc) {
		this.nc = nc;
	}

	public String getCnonce() {
		return cnonce;
	}

	public void setCnonce(String cnonce) {
		this.cnonce = cnonce;
	}

	public String getHttpmethod() {
		return httpmethod;
	}

	public void setHttpmethod(String httpmethod) {
		this.httpmethod = httpmethod;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	public VeidblockCredentials toSupper(){
		VeidblockCredentials veidblockCredentials = new VeidblockCredentials();
		veidblockCredentials.setAuthenticated(this.isAuthenticated());
		veidblockCredentials.setAuthenticationType(this.getAuthenticationType());
		veidblockCredentials.setMethod(this.getMethod()); 
		veidblockCredentials.setToken(this.getToken());
		veidblockCredentials.setUrl(this.getUrl());
		veidblockCredentials.setUserId(this.getUserId()); 
		return veidblockCredentials;
	}
}
