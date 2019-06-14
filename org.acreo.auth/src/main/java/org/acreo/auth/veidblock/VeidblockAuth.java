package org.acreo.auth.veidblock;

import java.util.Optional;

import org.acreo.auth.context.AuthentictionManager;
import org.acreo.common.Representation;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.hibernate.UnitOfWork;

public class VeidblockAuth implements Authenticator<VeidblockCredentials, VeidblockUser> {

	private VerificationService verificationService;
	private JWSTokenService jwsTokenService;
	private LocalCertificateManager localCertificateManager;

	public VeidblockAuth(LocalCertificateManager localCertificateManager, VerificationService verificationService, JWSTokenService jwsTokenService) {
		this.verificationService = verificationService;
		this.jwsTokenService = jwsTokenService;		
		this.localCertificateManager = localCertificateManager;
	}

	@UnitOfWork
	public java.util.Optional<VeidblockUser> authenticate(VeidblockCredentials credentials) throws AuthenticationException {
		Optional<VeidblockUser> user = null;
		VeidblockUser authenticatedUser = null;
		AuthentictionManager authentictionManager = new AuthentictionManager(verificationService, this.jwsTokenService, localCertificateManager);
		Representation representation;
		try {
			representation = authentictionManager.verifyCredentials(credentials, null, null);
			if (representation.getCode() == 0) {
				ObjectMapper objectMapper = new ObjectMapper();
				VeidblockCredentials result = objectMapper.readValue(representation.getBody().toString(),
						VeidblockCredentials.class);
				authenticatedUser = new VeidblockUser(result.getUserId(), result.getAuthenticationType());
				authenticatedUser.setToken(result.getToken());
			} else{
				throw new AuthenticationException("Invalid credentials !");
			}
		} catch (Exception e) {
			throw new AuthenticationException(e);
		}
		return Optional.ofNullable(authenticatedUser);
	}

	
}