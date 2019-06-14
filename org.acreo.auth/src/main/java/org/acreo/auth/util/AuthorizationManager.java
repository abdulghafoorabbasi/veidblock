/*package org.acreo.auth.util;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.acreo.common.entities.AuthorizedDataCO;
import org.acreo.common.entities.AuthorizedPayload;
import org.acreo.common.entities.AuthorizedVeidblock;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.VeidSignerInfo;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.entities.Resource;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.ip.service.VerificationService;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.pkcs.PKCS7Manager;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorizationManager {

	private JWSTokenService jwsTokenService;
	private VerificationService verificationService;
	private LocalCertificateManager localCertificateManager;

	public AuthorizationManager(LocalCertificateManager localCertificateManager, VerificationService verificationService,
			JWSTokenService jwsTokenService) {
		this.jwsTokenService = jwsTokenService;
		this.localCertificateManager = localCertificateManager;
		this.verificationService = verificationService;
	}

	public String createAuthorizedVeidblock(final long sender, final long recipient, final AuthorizedDataCO authorizedData)
			throws VeidblockException {

		ObjectMapper objectMapper = new ObjectMapper();
		AuthorizedDataCO authorizedData;
		try {
			authorizedData = objectMapper.readValue(data, AuthorizedDataCO.class);
		} catch (Exception e) {
			throw new VeidblockException(e);
		}

		AuthorizedVeidblock authorizedVeidblock = new AuthorizedVeidblock();
		authorizedVeidblock.setAuthorizedBy(sender);
		authorizedVeidblock.setAuthorizedTo(recipient);
		

		Resource resource = null;
		try {

			resource = verificationService.getResourceWithPassword(sender);

		} catch (Exception exp) {

			resource = verificationService.getResourceWithPassword("" + sender);

		}

		if (Objects.isNull(resource)) {
			throw new VeidblockException("Could not find requested resource !");
		}

		JWSTokenHelper jWSTokenHelper = new JWSTokenHelper(resource, localCertificateManager, authorizedData.getScp());
		//jwsTokenService.createJwsToken(new Convertor().jWTokenToDbToekn(jWSTokenHelper.getJWSToken()));

		

		String senderName = InitSetup.getCredentialsPath() + File.separator + sender;
		
		String recipientName = InitSetup.getCredentialsPath() + File.separator + recipient;

		CertificateSuite senderCertificateSuite = new CertificateSuite(senderName, 3);

		CertificateSuite receiverCertificateSuite = new CertificateSuite(recipientName, 3);

		PKCS7Manager pkcs7Manager = new PKCS7Manager(new CryptoPolicy(),senderCertificateSuite);
		
		UserCertificateManager receiverCertificateManager = new UserCertificateManager(receiverCertificateSuite);
		ResourceCO recResourceCO = new ResourceCO();
		recResourceCO.setResourceId(recipient);
		X509Certificate aRecipient = null;
		try {
			// If he is a local user, then it will fetch from local certificate database
			aRecipient = receiverCertificateManager.fetchCertificate(recResourceCO);			
		} catch (VeidblockException veidExp) {
			// Otherwise a new protocol is required to fetch from Ledger 
			System.out.println("ToDo: Fetch certificate from internet ");			
		}
		
		UserCertificateManager sernerCertificateManager = new UserCertificateManager(senderCertificateSuite);
		ResourceCO resourceCO = jWSTokenHelper.toResourceDTO(resource);
		X509Certificate signerChain[] = sernerCertificateManager.fetchX509CertificateChain(resourceCO);

		X509Certificate recipients[] = { aRecipient, signerChain[signerChain.length-1] };
		DistinguishNameCO dn = sernerCertificateManager.toAnonymousDistinguishNameCO(resourceCO);
		PrivateKey signerPrivateKey = sernerCertificateManager.getPrivateKey(dn, resource.getPassword());

		String pkcs7Enveloped = pkcs7Manager.generateEnvelopedData(recipients, authorizedData.getSecureSharedData().getBytes());
		
		AuthorizedPayload authorizedPayload = new AuthorizedPayload(jWSTokenHelper.getJWSToken().toEncoded(), pkcs7Enveloped);
		String jsonPayload = authorizedPayload.toJson();
		
		
		authorizedVeidblock.setPayload(jsonPayload);
		System.out.println(jsonPayload);
		
		List<VeidSignerInfo> veidSignerInfoList = new ArrayList<VeidSignerInfo>();
		
		VeidSignerInfo serverSignerInfo = new VeidSignerInfo();
		serverSignerInfo.setChain(localCertificateManager.fetchCertificateChain());
		serverSignerInfo.setPrivateKey(localCertificateManager.getAuthServerPrivateKey());
		veidSignerInfoList.add(serverSignerInfo);
		VeidSignerInfo senderSignerInfo = new VeidSignerInfo();
		senderSignerInfo.setChain(signerChain);
		senderSignerInfo.setPrivateKey(signerPrivateKey);
		veidSignerInfoList.add(senderSignerInfo);
		
		String signedPkcs7 = pkcs7Manager.generatePKCS7Signed(veidSignerInfoList, authorizedVeidblock.toJson().getBytes());  
		return signedPkcs7;
	}
	
	public String createAuthorizedVeidblock(final long sender, final long recipient, final AuthorizedDataCO authorizedData)
			throws VeidblockException {

		ObjectMapper objectMapper = new ObjectMapper();
		AuthorizedDataCO authorizedData;
		try {
			authorizedData = objectMapper.readValue(data, AuthorizedDataCO.class);
		} catch (Exception e) {
			throw new VeidblockException(e);
		}

		AuthorizedVeidblock authorizedVeidblock = new AuthorizedVeidblock();
		authorizedVeidblock.setAuthorizedBy(sender);
		authorizedVeidblock.setAuthorizedTo(recipient);
		

		Resource resource = null;
		try {

			resource = verificationService.getResourceWithPassword(sender);

		} catch (Exception exp) {

			resource = verificationService.getResourceWithPassword("" + sender);

		}

		if (Objects.isNull(resource)) {
			throw new VeidblockException("Could not find requested resource !");
		}

		JWSTokenHelper jWSTokenHelper = new JWSTokenHelper(resource, localCertificateManager, authorizedData.getScp());
		//jwsTokenService.createJwsToken(new Convertor().jWTokenToDbToekn(jWSTokenHelper.getJWSToken()));

		

		//String senderName = InitSetup.getCredentialsPath() + File.separator + sender;
		
		//String recipientName = InitSetup.getCredentialsPath() + File.separator + recipient;

		//CertificateSuite senderCertificateSuite = new CertificateSuite(senderName, 3);

		//CertificateSuite receiverCertificateSuite = new CertificateSuite(recipientName, 3);

		PKCS7Manager pkcs7Manager = new PKCS7Manager(new CryptoPolicy(),localCertificateManager.getCertificateSuite());
		
		//UserCertificateManager receiverCertificateManager = new UserCertificateManager(receiverCertificateSuite);
		//ResourceCO recResourceCO = new ResourceCO();
		//recResourceCO.setResourceId(recipient);
		X509Certificate aRecipient = null;
		try {
			// If he is a local user, then it will fetch from local certificate database
			aRecipient = localCertificateManager.fetchRegisteredResourcesCert(""+recipient); //receiverCertificateManager.fetchCertificate(recResourceCO);			
		} catch (VeidblockException veidExp) {
			// Otherwise a new protocol is required to fetch from Ledger 
			System.out.println("ToDo: Fetch certificate from internet ");			
		}
		
		
		ResourceCO resourceCO = jWSTokenHelper.toResourceDTO(resource);
		//X509Certificate signerChain[] = sernerCertificateManager.fetchX509CertificateChain(resourceCO);

		X509Certificate recipients[] = { aRecipient, localCertificateManager.fetchRegisteredResourcesCert(""+sender) };
		//DistinguishNameCO dn = sernerCertificateManager.toAnonymousDistinguishNameCO(resourceCO);
		//PrivateKey signerPrivateKey = sernerCertificateManager.getPrivateKey(dn, resource.getPassword());

		String pkcs7Enveloped = pkcs7Manager.generateEnvelopedData(recipients, authorizedData.getSecureSharedData().getBytes());
		
		AuthorizedPayload authorizedPayload = new AuthorizedPayload(jWSTokenHelper.getJWSToken().toEncoded(), pkcs7Enveloped);
		String jsonPayload = authorizedPayload.toJson();
		
		
		authorizedVeidblock.setPayload(jsonPayload);
		//System.out.println(jsonPayload);
		
		List<VeidSignerInfo> veidSignerInfoList = new ArrayList<VeidSignerInfo>();
		
		VeidSignerInfo serverSignerInfo = new VeidSignerInfo();
		serverSignerInfo.setChain(localCertificateManager.fetchCertificateChain());
		serverSignerInfo.setPrivateKey(localCertificateManager.getAuthServerPrivateKey());
		veidSignerInfoList.add(serverSignerInfo);
		VeidSignerInfo senderSignerInfo = new VeidSignerInfo();
		senderSignerInfo.setChain(signerChain);
		senderSignerInfo.setPrivateKey(signerPrivateKey);
		veidSignerInfoList.add(senderSignerInfo);
		
		String signedPkcs7 = pkcs7Manager.generatePKCS7Signed(veidSignerInfoList, authorizedVeidblock.toJson().getBytes());  
		return signedPkcs7;
	}
}*/