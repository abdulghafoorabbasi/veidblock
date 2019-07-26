package org.acreo.ledger.utils;

import java.security.cert.X509Certificate;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.pkcs.PKCS7Manager;

public class Endorser {
	
	public String endorse(String authorizedVeidblockSer, CryptoPolicy cryptoPolicy, CertificateSuite endorserCertificateSuite) throws VeidblockException{
		PKCS7Manager manager = new PKCS7Manager(cryptoPolicy, endorserCertificateSuite);
		X509Certificate x509Certificate [] = manager.getx509CertPKCS7SignedData(authorizedVeidblockSer);
		for(X509Certificate cert : x509Certificate){
			System.out.println(cert);
		}
		return authorizedVeidblockSer;
	}

}
