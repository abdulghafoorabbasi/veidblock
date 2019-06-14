package org.acreo.auth.util;

import java.security.cert.X509Certificate;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.security.bc.CertificateHandlingBC;
import org.acreo.security.certificate.CMSDelegator;
import org.acreo.security.utils.DistinguishName;
import org.acreo.security.utils.PEMStream;
import org.acreo.security.utils.StoreHandling;

public class CertificateResourceDelegate {
	
	private LocalCertificateManager localCertificateManager;
	
	public CertificateResourceDelegate( LocalCertificateManager localCertificateManager){
		 
		this.localCertificateManager = localCertificateManager;
	}
	
	
	public String signCert(String pkcs10EncodedReq) throws VeidblockException {

		CertificateHandlingBC certificateHandlingBC = new CertificateHandlingBC(this.localCertificateManager.getCertificateSuite(),localCertificateManager.getPassword());
		
		PEMStream pemStream = new PEMStream();
		byte pkcs10 [] = pemStream.pem2csr(pkcs10EncodedReq);
		
		X509Certificate x509Certificate = certificateHandlingBC.signCert(localCertificateManager.getAuthServerPrivateKey(), localCertificateManager.fetchCertificate(), pkcs10 ); 
		try {
			
			
			StoreHandling storeHandling = new StoreHandling();
			if (null != x509Certificate) {
				boolean result = storeHandling.storeCertificate(localCertificateManager.getCertificateSuite(), x509Certificate);
				if (!result) {
					throw new VeidblockException("Problems when storing newly signed certificate !");
				} 
			}
			
			CMSDelegator pkiDelegator = new CMSDelegator(localCertificateManager.getPassword(),
					localCertificateManager.getCertificateSuite());
			DistinguishName subject = DistinguishName.builder().build(x509Certificate.getSubjectDN().toString());
			return pkiDelegator.fetchCApemChain(subject);
		} catch (VeidblockException e) {
			throw new VeidblockException(e);
		}
	}

}
