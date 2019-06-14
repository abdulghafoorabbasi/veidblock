package org.acreo.auth.context;

import java.util.Base64;
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
import org.acreo.veidblock.token.JWToken;

public class BasicSecurityContext extends VeidblockCredentials {

	private VerificationService verificationService;
	private JWSTokenService jwsTokenService;
	private LocalCertificateManager localCertificateManager;

	public BasicSecurityContext(VeidblockCredentials credentials, LocalCertificateManager localCertificateManager, VerificationService verificationService,
			JWSTokenService jwsTokenService) throws SecurityContextFormatException, VeidblockException {
		this.setUserId(credentials.getUserId());
		this.setToken(credentials.getToken());
		this.setAuthenticationType(credentials.getAuthenticationType());
		this.setMethod(credentials.getMethod());
		this.setUrl(credentials.getUrl());
		this.verificationService = verificationService;
		this.jwsTokenService = jwsTokenService;
		this.localCertificateManager = localCertificateManager;
		this.parse();
		this.verifyPassword();

	}

	private boolean parse() throws SecurityContextFormatException {

		try {
			String encodedSecurityContext = this.getToken().substring("basic ".length());

			byte token[] = Base64.getDecoder().decode(encodedSecurityContext);
			int index = new String(token).indexOf(":");
			if (index == -1) {
				throw new SecurityContextFormatException("");
			}
			byte[] uniqueId = new byte[index];
			byte[] recSecretToken = new byte[token.length - index - 1];
			System.arraycopy(token, 0, uniqueId, 0, index);
			index = index + 1;
			System.arraycopy(token, index, recSecretToken, 0, token.length - index);
			this.setUserId(new String(uniqueId));
			this.setToken(new String(recSecretToken));
			return true;

		} catch (Exception exp) {
			throw new SecurityContextFormatException(exp.getMessage());
		}
	}

	private void verifyPassword() throws VeidblockException {

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

		if (!resource.isActiveStatus()) {
			this.setAuthenticated(false);
			throw new VeidblockException("Resource is not active, please check your email for further instructions to activate it !");
		}
		if (resource.getPassword().equals(this.getToken())) {

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
			
		} else {
			this.setAuthenticated(false);
		}
	}	
}
