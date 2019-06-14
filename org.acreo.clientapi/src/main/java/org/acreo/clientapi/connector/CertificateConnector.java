package org.acreo.clientapi.connector;

import org.acreo.clientapi.security.ClientCertificateHandler;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.security.certificate.CertificateSuite;

public class CertificateConnector {

	public boolean createCertificate(String uid, String password, String verifyerURL) throws VeidblockException {
		
		CertificateSuite certificateSuite = new CertificateSuite(uid + "", 3);
		RestClient restClient;
		restClient = RestClient.builder().baseUrl(verifyerURL+"/cert/request").build();
		ClientCertificateHandler clientCertificateHandler = new ClientCertificateHandler(certificateSuite);
		return clientCertificateHandler.issueCertificate(restClient, uid, password);
	}
}
