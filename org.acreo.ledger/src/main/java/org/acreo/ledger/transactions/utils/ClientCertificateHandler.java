package org.acreo.ledger.transactions.utils;

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
		//logger.info("Checking local credentials of ledger !");
		if (password == null || password.equals("")) {
			throw new VeidblockException("Password not specified !");
		}
		if (uid == null || uid.equals("")) {
			throw new VeidblockException("Resource id not specified !");
		}
		long resId;
		try {
			resId = Long.parseLong(uid);
		} catch (Exception exp) {
			throw new VeidblockException("Invalid Resource id !");
		}
		//logger.info("Resource id ["+uid+"] of Ledger !");
		if (isCertificateIssued(uid, password)) {
			return true;
		}

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
		//logger.info("Certificate request response receive ["+temp.getCode() +"]");
		if (temp.getCode() != 200) {
			//logger.info("Error when sending request !");
			throw new VeidblockException("Error Code: "+temp.getCode()+", Message : "+temp.getBody().toString());
			
		}
		ObjectMapper objectMapper = new ObjectMapper();
		Representation<String> finalPemRep;
		try {
			finalPemRep = objectMapper.readValue(temp.getBody().toString(), Representation.class);
			if (finalPemRep.getCode() != 200) {
				//logger.info("Error when processing resopnse !");
				//logger.info("\tError Code: "+temp.getCode()+", Message : "+temp.getBody().toString());
				throw new VeidblockException("Error Code: "+temp.getCode()+", Message : "+temp.getBody().toString());
			}
			//logger.info("Processing PEM resopnse !");
			String finalPem = finalPemRep.getBody();
			X509Certificate[] x509Certificate = processCertResponse(resourceCO, finalPem);
			//logger.info("Successfullt processed PEM response !");
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
		return true;
	}

	public String createCertRequest(String uid, String password) throws VeidblockException {

		//logger.info("Creating certificate request for resource id ["+uid+"] !" );
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(Long.parseLong(uid));
		resourceCO.setPassword(password);

		DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		int keyUsage = KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment;
		//logger.info("Distingushed Name: "+dn.toString());
		//logger.info("Key usage : "+keyUsage+"[digitalSignature | keyEncipherment | dataEncipherment]" );
		CertificateHandlingBC certificateHandlingBC = new CertificateHandlingBC(this.certificateSuite,
				resourceCO.getPassword());
		byte[] encidedCSR = certificateHandlingBC.createCSR(dn, keyUsage);

		PEMStream pemStream = new PEMStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (pemStream.csr2pem(encidedCSR, baos)) {
			//logger.info("Certificate request for resource id ["+uid+"] successfully created !" );
			return new String(baos.toByteArray());			
		}
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

		StoreHandling storeHandling = new StoreHandling();
		DistinguishName dn = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		X509Certificate certificate = storeHandling.fetchCertificate(certificateSuite, dn);
		if (null == certificate) {
			throw new VeidblockException("Certificate not found !");
		} else {

			if (certificate.getSubjectDN().toString().equals(certificate.getIssuerDN().toString())) {
				throw new VeidblockException("Only self signed certificate found !");
			}
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
		PEMStream pemStream = new PEMStream();

		X509Certificate chain[] = null;
		try {
			chain = pemStream.extractCertChain(certResponse);
		} catch (Exception e) {
			throw new VeidblockException("Problems when parsing certificate response. : " + e.getMessage());
		}
		if (chain.length == 0) {
			throw new VeidblockException("Certificate respone is empty !");
		}
		StoreHandling storeHandling = new StoreHandling();
		DistinguishName subject = DistinguishName.builder().build(toAnonymousDistinguishNameCO(resourceCO));
		PrivateKey privateKey = storeHandling.fetchPrivateKey(certificateSuite, resourceCO.getPassword(), subject);
		storeHandling.deleteCertificateStoredWithPrivateKey(certificateSuite, resourceCO.getPassword(), subject);
		storeHandling.deleteCertificate(certificateSuite, subject);
		if (!storeHandling.stroePrivateKeywithCertificate(certificateSuite, resourceCO.getPassword(), chain,
				privateKey)) {
			throw new VeidblockException("Problems when processing certificate respone !");
		}
		return chain;
	}

	public PrivateKey getPrivateKey(DistinguishNameCO distinguishNameCO, String password) throws VeidblockException {

		StoreHandling storeHandling = new StoreHandling();
		PrivateKey privateKey = storeHandling.fetchPrivateKey(certificateSuite, password,
				DistinguishName.builder().build(distinguishNameCO));
		if (privateKey == null) {
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
