package org.acreo.auth.util;

import org.acreo.common.entities.AuthorizedDataCO;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ip.service.VerificationService;

public class GenericVeidblockOperations {

	private VerificationService verificationService;

	public GenericVeidblockOperations(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	public AuthorizedDataCO extractBlock(GenericVeidblock genericVeidblock, String owner) throws VeidblockException {/*
		
		String recipientName = InitSetup.getCredentialsPath() + File.separator + owner;
		
		CertificateSuite reciverCertificateSuite = new CertificateSuite(recipientName, 3);
		UserCertificateManager receiverCertificateManager = new UserCertificateManager(reciverCertificateSuite);

		ResourceCO recResourceCO = new ResourceCO();
		recResourceCO.setResourceId(Long.parseLong(owner));
		Resource resource = null;
		try {

			resource = verificationService.getResourceWithPassword(Long.parseLong(owner));

		} catch (Exception exp) {

			resource = verificationService.getResourceWithPassword(owner);

		}

		if (Objects.isNull(resource)) {
			throw new VeidblockException("Could not find requested resource !");
		}

		DistinguishNameCO dn = receiverCertificateManager.toAnonymousDistinguishNameCO(recResourceCO);
		PKCS7Manager pkcs7Manager = new PKCS7Manager(new CryptoPolicy(), reciverCertificateSuite);
		System.out.println(genericVeidblock.getPayload());
		AuthorizedPayload authorizedPayload = new AuthorizedPayload();
		authorizedPayload.fromJson(genericVeidblock.getPayload());
		
		byte pkcs7[] = pkcs7Manager.openEnvelopedData(
				receiverCertificateManager.fetchCertificate(recResourceCO),
				receiverCertificateManager.getPrivateKey(dn, resource.getPassword()), 
				authorizedPayload.getSecretInfo());
		authorizedPayload.setSecretInfo(new String(pkcs7));
		try {
			JWSToken jwToken = new JWSToken(authorizedPayload.getPayload());
			if(jwToken.verify()){
				AuthorizedDataCO authorizedDataCO = new AuthorizedDataCO();
				authorizedDataCO.setScp(jwToken.getPayload().getScp());
				authorizedDataCO.setSecureSharedData(authorizedPayload.getSecretInfo());
			 return authorizedDataCO;	
			}
			throw new VeidblockException("Problems when verifying access token extracted from authorized block !");
		} catch (Exception e) {
			throw new VeidblockException("Problems when extracting data from authorized block !");
		}
	*/
		return null;
	}

	public String createAuthorizedVeidblock(final long sender, final long recipient, final AuthorizedDataCO authorizedData)
			throws VeidblockException {/*

		ObjectMapper objectMapper = new ObjectMapper();
		AuthorizedVeidblock authorizedVeidblock = new AuthorizedVeidblock();
		authorizedVeidblock.setAuthorizedBy(sender);
		authorizedVeidblock.setAuthorizedTo(recipient);
		LocalCertificateManager localCertificateManager = null;

		Resource resource = null;
		String secret = new SGen().nextString(12);

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

		AuthorizedPayload authorizedPayload = new AuthorizedPayload(jWSTokenHelper.getJWSToken().toEncoded(), secret);
		String jsonPayload = authorizedPayload.toJson();

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

		String pkcs7Enveloped = pkcs7Manager.generateEnvelopedData(recipients, jsonPayload.getBytes());
		authorizedVeidblock.setPayload(pkcs7Enveloped);
		System.out.println(pkcs7Enveloped);
		
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
	*/
		return null;
	}
}
/*public AuthorizedDataCO extractBlock(GenericVeidblock genericVeidblock, String owner) throws VeidblockException {
	
	String recipientName = InitSetup.getCredentialsPath() + File.separator + owner;
	
	CertificateSuite reciverCertificateSuite = new CertificateSuite(recipientName, 3);
	UserCertificateManager receiverCertificateManager = new UserCertificateManager(reciverCertificateSuite);

	ResourceCO recResourceCO = new ResourceCO();
	recResourceCO.setResourceId(Long.parseLong(owner));
	Resource resource = null;
	try {

		resource = verificationService.getResourceWithPassword(Long.parseLong(owner));

	} catch (Exception exp) {

		resource = verificationService.getResourceWithPassword(owner);

	}

	if (Objects.isNull(resource)) {
		throw new VeidblockException("Could not find requested resource !");
	}

	DistinguishNameCO dn = receiverCertificateManager.toAnonymousDistinguishNameCO(recResourceCO);
	PKCS7Manager pkcs7Manager = new PKCS7Manager(new CryptoPolicy(), reciverCertificateSuite);
	System.out.println(genericVeidblock.getPayload());
	AuthorizedPayload authorizedPayload = new AuthorizedPayload();
	authorizedPayload.fromJson(genericVeidblock.getPayload());
	
	byte pkcs7[] = pkcs7Manager.openEnvelopedData(
			receiverCertificateManager.fetchCertificate(recResourceCO),
			receiverCertificateManager.getPrivateKey(dn, resource.getPassword()), 
			authorizedPayload.getSecretInfo());
	authorizedPayload.setSecretInfo(new String(pkcs7));
	try {
		JWSToken jwToken = new JWSToken(authorizedPayload.getPayload());
		if(jwToken.verify()){
			AuthorizedDataCO authorizedDataCO = new AuthorizedDataCO();
			authorizedDataCO.setScp(jwToken.getPayload().getScp());
			authorizedDataCO.setSecureSharedData(authorizedPayload.getSecretInfo());
		 return authorizedDataCO;	
		}
		throw new VeidblockException("Problems when verifying access token extracted from authorized block !");
	} catch (Exception e) {
		throw new VeidblockException("Problems when extracting data from authorized block !");
	}
}

public String createAuthorizedVeidblock(final long sender, final long recipient, final AuthorizedDataCO authorizedData)
		throws VeidblockException {

	ObjectMapper objectMapper = new ObjectMapper();
	AuthorizedVeidblock authorizedVeidblock = new AuthorizedVeidblock();
	authorizedVeidblock.setAuthorizedBy(sender);
	authorizedVeidblock.setAuthorizedTo(recipient);
	LocalCertificateManager localCertificateManager = null;

	Resource resource = null;
	String secret = new SGen().nextString(12);

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

	AuthorizedPayload authorizedPayload = new AuthorizedPayload(jWSTokenHelper.getJWSToken().toEncoded(), secret);
	String jsonPayload = authorizedPayload.toJson();

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

	String pkcs7Enveloped = pkcs7Manager.generateEnvelopedData(recipients, jsonPayload.getBytes());
	authorizedVeidblock.setPayload(pkcs7Enveloped);
	System.out.println(pkcs7Enveloped);
	
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
}*/