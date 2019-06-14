package org.acreo.auth.context;

import org.acreo.auth.veidblock.VeidblockCredentials;
import org.acreo.common.Representation;
import org.acreo.common.entities.AuthenticationType;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;

public class AuthentictionManager {

	private VerificationService verificationService;
	private JWSTokenService jwsTokenService;
	private LocalCertificateManager localCertificateManager;

	public AuthentictionManager(VerificationService verificationService, JWSTokenService jwsTokenService,
			LocalCertificateManager localCertificateManager) {
		this.verificationService = verificationService;
		this.jwsTokenService = jwsTokenService;
		this.localCertificateManager = localCertificateManager;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Representation verifyCredentials(VeidblockCredentials credentials, String body, Object other)
			throws VeidblockException {

		AuthenticationType authenticationHeader = getAuthType(credentials);

		credentials.setAuthenticationType(authenticationHeader);

		VeidblockCredentials aaSecurityContext = null;

		if (credentials.getAuthenticationType() == AuthenticationType.BASIC) {
			aaSecurityContext = new BasicSecurityContext(credentials, localCertificateManager, this.verificationService,
					this.jwsTokenService);

		} else if (credentials.getAuthenticationType() == AuthenticationType.DIGEST) {
			DigestSecurityContext digestSecurityContext = new DigestSecurityContext(credentials,
					localCertificateManager, this.verificationService, this.jwsTokenService);
			aaSecurityContext = digestSecurityContext.toSupper();

		} else if (credentials.getAuthenticationType() == AuthenticationType.BEARER) {
			aaSecurityContext = new SSOSecurityContext(credentials, localCertificateManager, this.verificationService, this.jwsTokenService);

		}

		if (aaSecurityContext.isAuthenticated())
			return new Representation(0, aaSecurityContext.toJson());
		else
			return new Representation(-1, (new Boolean(false)).toString());
	}

	private AuthenticationType getAuthType(VeidblockCredentials credentials) throws VeidblockException {

		if (credentials.getAuthenticationType() != null) {
			return credentials.getAuthenticationType();
		}
		String securityContextEncoded = credentials.getToken();
		securityContextEncoded = securityContextEncoded.toLowerCase();
		if (securityContextEncoded.startsWith("basic")) {
			return AuthenticationType.BASIC;
		} else if (securityContextEncoded.startsWith("digest")) {
			return AuthenticationType.DIGEST;
		} else if (securityContextEncoded.startsWith("client_cert")) {
			throw new VeidblockException("client_cert not supported !");
			// return AuthenticationType.CLIENT_CERT;

		} else if (securityContextEncoded.startsWith("otp")) {
			return AuthenticationType.OTP;
		} else if (securityContextEncoded.startsWith("bearer") || securityContextEncoded.startsWith("oauth bearer")
				|| securityContextEncoded.startsWith("oauth2 bearer")) {
			return AuthenticationType.BEARER;
		} else {
			throw new VeidblockException("Authentication method not supported !");
			// return AuthenticationType.OTHER;
		}
	}
}