package org.acreo.clientapi.security;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;

import org.acreo.common.Representation;
import org.acreo.common.entities.DistinguishNameCO;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.security.bc.CertificateHandlingBC;
import org.acreo.security.certificate.CMSDelegator;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.utils.DistinguishName;
import org.acreo.security.utils.PEMStream;
import org.acreo.security.utils.StoreHandling;
import org.apache.http.HttpHeaders;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientCertificateHandler {

	final static Logger logger = LoggerFactory.getLogger(ClientCertificateHandler.class);
	private CertificateSuite certificateSuite;
	public ClientCertificateHandler(CertificateSuite certificateSuite) {
		this.certificateSuite = certificateSuite;
	}

	public boolean issueCertificate(RestClient restClient, String uid, String password) throws VeidblockException {
		logger.error("--- I --- Issuing credentials for reosurce !");
		if (password == null || password.equals("")) {
			logger.error("--- E --- Password not specified !");
			throw new VeidblockException("Password not specified !");
		}
		if (uid == null || uid.equals("")) {
			logger.error("--- E --- Resource id not specified !");
			throw new VeidblockException("Resource id not specified !");
		}
		long resId;
		try {
			resId = Long.parseLong(uid);
		} catch (Exception exp) {
			logger.error("--- E --- Invalid Resource id !");
			throw new VeidblockException("Invalid Resource id !");
		}

		if (isCertificateIssued(uid, password)) {
			return true;
		}
		logger.info("--- I --- Certificate does not exisit !");
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(resId);
		resourceCO.setPassword(password);
		if (isCertificateIssued(resourceCO)) {
			return true;
		}
		
		String encodedCertRq = createCertRequest(uid, password);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.AUTHORIZATION,
				Base64.getMimeEncoder().encodeToString((uid + ":" + password).getBytes()));
		Representation temp = restClient.post("", encodedCertRq, headers);
		if (temp.getCode() != 200) {
			throw new VeidblockException("Error Code: "+temp.getCode()+", Message : "+temp.getBody().toString());
			
		}
		ObjectMapper objectMapper = new ObjectMapper();
		Representation<String> finalPemRep;
		try {
			finalPemRep = objectMapper.readValue(temp.getBody().toString(), Representation.class);
			if (finalPemRep.getCode() != 200) {
				throw new VeidblockException("Error Code: "+temp.getCode()+", Message : "+temp.getBody().toString());
			}
			logger.info("--- I --- Certificate response received !");
			String finalPem = finalPemRep.getBody();
			X509Certificate[] x509Certificate = processCertResponse(resourceCO, finalPem);
			// finalPem			
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
		return true;
	}

	public String createCertRequest(String uid, String password) throws VeidblockException {

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uid));
		resourceCO.setPassword(password);
		logger.info("--- I --- Creating certificate request for resource : "+uid+" !");
		DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		int keyUsage = KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment;

		CertificateHandlingBC certificateHandlingBC = new CertificateHandlingBC(this.certificateSuite,
				resourceCO.getPassword());
		byte[] encidedCSR = certificateHandlingBC.createCSR(dn, keyUsage);

		PEMStream pemStream = new PEMStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (pemStream.csr2pem(encidedCSR, baos)) {
			logger.info("--- I --- Successfully created certificate request for resource : "+uid+" !");
			return new String(baos.toByteArray());
		}
		logger.error("--- E --- Problems when creating Certificate Request (PKCS10) for resource : "+uid+" !");
		throw new VeidblockException("Problems when creating Certificate Request (PKCS10) !");
	}

	public boolean isCertificateIssued(ResourceCO resourceCO) {

		X509Certificate resourceCert;
		try {
			resourceCert = fetchCertificate(resourceCO);
			return true;
		} catch (VeidblockException e) {
			return false;
		}
	}

	public X509Certificate fetchCertificate(ResourceCO resourceCO) throws VeidblockException {
		logger.info("--- I --- Fetching certificate for resource "+resourceCO.getResourceId()+" !");
		StoreHandling storeHandling = new StoreHandling();
		DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		X509Certificate certificate = storeHandling.fetchCertificate(certificateSuite, dn);
		if (null == certificate) {
			logger.warn("--- W --- Certificate not found !");
			throw new VeidblockException("Certificate not found !");
		} else {

			if (certificate.getSubjectDN().toString().equals(certificate.getIssuerDN().toString())) {
				logger.warn("--- W --- Only self signed certificate found !");
				throw new VeidblockException("Only self signed certificate found !");
			}
			logger.info("--- I --- Successfully feteched certificate for resource "+resourceCO.getResourceId()+" !");
			return certificate;
		}
	}

	public String fetchCertificateChain(ResourceCO resourceCO) throws VeidblockException {

		if (resourceCO.getPassword() == null || resourceCO.getPassword().equals("")) {
			throw new VeidblockException("Password not specified !");
		}

		CMSDelegator pkiDelegator = new CMSDelegator(resourceCO.getPassword(), certificateSuite);
		try {
			DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
			return pkiDelegator.fetchPkiPemChain(dn);

		} catch (Exception e) {
			throw new VeidblockException(e);
		}
	}

	public X509Certificate[] fetchX509CertificateChain(ResourceCO resourceCO) throws VeidblockException {

		if (resourceCO.getPassword() == null || resourceCO.getPassword().equals("")) {
			throw new VeidblockException("Password not specified !");
		}

		CMSDelegator pkiDelegator = new CMSDelegator(resourceCO.getPassword(), certificateSuite);
		try {
			DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
			return pkiDelegator.fetchCertChain(dn);

		} catch (Exception e) {
			throw new VeidblockException(e);
		}
	}

	public X509Certificate[] processCertResponse(ResourceCO resourceCO, String certResponse) throws VeidblockException {
		logger.info("--- I --- Processing certificate response !");
		PEMStream pemStream = new PEMStream();

		X509Certificate chain[] = null;
		try {
			chain = pemStream.extractCertChain(certResponse);
		} catch (Exception e) {
			logger.error("--- E --- Problems when parsing certificate response. : " + e.getMessage());
			throw new VeidblockException("Problems when parsing certificate response. : " + e.getMessage());
		}
		if (chain.length == 0) {
			logger.error("--- E --- Certificate response is empty !");
			throw new VeidblockException("Certificate response is empty !");
		}
		StoreHandling storeHandling = new StoreHandling();
		DistinguishName subject = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		PrivateKey privateKey = storeHandling.fetchPrivateKey(certificateSuite, resourceCO.getPassword(), subject);
		storeHandling.deleteCertificateStoredWithPrivateKey(certificateSuite, resourceCO.getPassword(), subject);
		storeHandling.deleteCertificate(certificateSuite, subject);
		if (!storeHandling.stroePrivateKeywithCertificate(certificateSuite, resourceCO.getPassword(), chain,
				privateKey)) {
			logger.error("--- E --- Problems when processing certificate response !");
			throw new VeidblockException("Problems when processing certificate response !");
		}
		logger.error("--- E --- Certificate response successfully processed !");
		return chain;
	}

	public PrivateKey getPrivateKey(DistinguishNameCO distinguishNameCO, String password) throws VeidblockException {

		StoreHandling storeHandling = new StoreHandling();
		PrivateKey privateKey = storeHandling.fetchPrivateKey(certificateSuite, password,
				DistinguishName.builder().build(distinguishNameCO));
		if (privateKey == null) {
			logger.error("--- E --- Could not find Private key of "+distinguishNameCO+" from store ["+certificateSuite.getStorePath()+"]!");
			throw new VeidblockException("Could not find Private key of "+distinguishNameCO+" from store ["+certificateSuite.getStorePath()+"]!");
		}
		return privateKey;
	}

	public DistinguishNameCO toAnonymousDistinguishNameCO(ResourceCO resourceCO) {
		DistinguishNameCO distinguishNameCO = new DistinguishNameCO();
		distinguishNameCO.setName("" + resourceCO.getResourceId());
		return distinguishNameCO;
	}

	public PublicKey getUserPublicKey(ResourceCO resourceCO) throws VeidblockException {
		try {
			X509Certificate resourceCert = fetchCertificate(resourceCO);
			return resourceCert.getPublicKey();
		} catch (VeidblockException veidblockException) {
			throw veidblockException;
		}
	}

	private boolean isCertificateIssued(String uid, String password) throws VeidblockException {
		logger.info("--- I --- Checking certificate already issued !");
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uid));
		resourceCO.setPassword(password);
		try {
			X509Certificate cert = fetchCertificate(resourceCO);
			if (cert == null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception exp) {
			return false;
		}
	}
}
