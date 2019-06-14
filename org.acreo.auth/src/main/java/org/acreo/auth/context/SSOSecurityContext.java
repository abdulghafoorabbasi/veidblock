package org.acreo.auth.context;

import java.util.Objects;

import org.acreo.auth.util.Convertor;
import org.acreo.auth.util.JWSTokenHelper;
import org.acreo.auth.veidblock.VeidblockCredentials;
import org.acreo.common.entities.AuthenticationType;
import org.acreo.common.exceptions.SecurityContextFormatException;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.entities.JwsTokenDb;
import org.acreo.ip.entities.Resource;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;
import org.acreo.veidblock.JWSToken;
import org.acreo.veidblock.token.JWToken;

public class SSOSecurityContext extends VeidblockCredentials {

	private JWSToken jWSToken;
	// private X509Certificate certificate;
	private JWSTokenService jwsTokenService;
	private VerificationService verificationService;
	private LocalCertificateManager localCertificateManager;

	public SSOSecurityContext(VeidblockCredentials credentials, LocalCertificateManager localCertificateManager,
			VerificationService verificationService, JWSTokenService jwsTokenService) throws VeidblockException {

		this.verificationService = verificationService;
		this.jwsTokenService = jwsTokenService;
		this.localCertificateManager = localCertificateManager;
		setAuthenticationType(AuthenticationType.BEARER);

		if (!parser(credentials.getToken())) {

			SecurityContextFormatException sce = new SecurityContextFormatException(
					"Problems when parsing JWS Token !");
			throw new VeidblockException(sce);
		}
		verifyToken();
	}

	private boolean parser(String encodedSecurityContext) throws VeidblockException {

		String temp = encodedSecurityContext;

		temp = temp.toLowerCase();
		String jwnTokenencoded = null;

		if (temp.startsWith("oauth2 bearer")) {
			jwnTokenencoded = encodedSecurityContext.substring(14, encodedSecurityContext.length());
		} else if (temp.startsWith("oauth bearer")) {
			jwnTokenencoded = encodedSecurityContext.substring(13, encodedSecurityContext.length());
		} else {
			jwnTokenencoded = encodedSecurityContext.substring(encodedSecurityContext.indexOf(" "));
		}

		jwnTokenencoded = jwnTokenencoded.trim();

		try {
			jWSToken = new JWSToken(jwnTokenencoded);
			return true;
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
	}

	private void verifyToken() throws VeidblockException {
		if (!jWSToken.verify()) {
			throw new VeidblockException("Invalid token !");
		}

		JWToken jwToken = null;

		JwsTokenDb db = jwsTokenService.getJwsToken(Long.parseLong(jWSToken.getPayload().getJti()));

		if (db == null) {
			throw new VeidblockException("Invalid token. It was not issued by Trusted Veidblock Service !");
		}
		this.setUserId(db.getSub());
		db = jwsTokenService.isValidTokenAlreadyExisits(jWSToken.getPayload().getSub());

		if (db != null) {
			jwToken = new Convertor().dbToeknTojWToken(db);
			setToken(jwToken.toEncoded());
			setAuthenticationType(AuthenticationType.BEARER);
			this.setAuthenticated(true);
			return;
		}

		String resourceId = this.getUserId();
		Resource resource = null;

		try {
			resource = verificationService.getResourceWithPassword(Long.parseLong(resourceId));
		} catch (Exception exp) {
			resource = verificationService.getResourceWithPassword(resourceId);
		}

		if (Objects.isNull(resource)) {
			this.setAuthenticated(false);
			throw new VeidblockException("Resource is not registered !");
		}

		JWSTokenHelper jWSTokenHelper = new JWSTokenHelper(resource, localCertificateManager, "");
		setToken(jWSTokenHelper.getJWSToken().toEncoded());
		setAuthenticationType(AuthenticationType.BEARER);
		jwsTokenService.createJwsToken(new Convertor().jWTokenToDbToekn(jWSTokenHelper.getJWSToken()));

		/*
		 * try { JWSToken jwsToken = new JWSToken(jWSTokenHelper.getJWSToken());
		 * System.out.println("TEST : "+jwsToken.verify()); } catch (Exception
		 * e) { throw new VeidblockException(e); }
		 */
		this.setAuthenticated(true);
	}	
}
