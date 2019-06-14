package org.acreo.clientapi;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Objects;

import org.acreo.clientapi.connector.CertificateConnector;
import org.acreo.clientapi.connector.DeleteUpdateIdentityConnector;
import org.acreo.clientapi.connector.QueryResourceConnector;
import org.acreo.clientapi.connector.ResourceRegistrationConnector;
import org.acreo.clientapi.security.ClientCertificateHandler;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.entities.RoleCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.utils.PEMStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Identity {

	final static Logger logger = LoggerFactory.getLogger(Identity.class);
	//ClientAuthenticator authenticator = ClientAuthenticator.builder().application("cli").verifier(verifier).build();
	
	public static final String PUB_CERT = "Certificate";
	private ResourceCO resourceCO;

	private Identity() {

	}
	public ResourceCO registerUser(ClientAuthenticator authenticator) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		ResourceRegistrationConnector registrationConnector = new ResourceRegistrationConnector(restClient, authenticator);
		return registrationConnector.registerUser(resourceCO, configuration.getAuthServerUrl());
	}

	public ResourceCO registerResource(ClientAuthenticator authenticator) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		System.out.println(configuration.getIPVServerUrl());
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		ResourceRegistrationConnector registrationConnector = new ResourceRegistrationConnector(restClient, authenticator);
		return registrationConnector.registerUser(resourceCO, configuration.getAuthServerUrl());
	}
	
	public ResourceCO registerResourceByAdmin(ClientAuthenticator authenticator) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		ResourceRegistrationConnector registrationConnector = new ResourceRegistrationConnector(restClient,authenticator);
		return registrationConnector.registerUserByAdmin(resourceCO, configuration.getAuthServerUrl());
	}
	
	public RoleCO assignRole(String role, ClientAuthenticator authenticator) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		ResourceRegistrationConnector registrationConnector = new ResourceRegistrationConnector(restClient, authenticator);
		return registrationConnector.assignRole(resourceCO.getResourceId(),role, configuration.getAuthServerUrl());
	}
	
	
	

	public ResourceCO getUser(ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		QueryResourceConnector queryResourceConnector = new QueryResourceConnector(restClient, authenticator);
		return queryResourceConnector.getUser(configuration.getAuthServerUrl(), resourceCO);

	}

	public ResourceCO getResource(ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		QueryResourceConnector queryResourceConnector = new QueryResourceConnector(restClient, authenticator);
		return queryResourceConnector.getResource(configuration.getAuthServerUrl(), resourceCO);

	}

	public ResourceCOList getAllUsers(ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		QueryResourceConnector queryResourceConnector = new QueryResourceConnector(restClient, authenticator);
		return queryResourceConnector.listUsers(configuration.getAuthServerUrl());
	}

	public ResourceCOList getAllServers(ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		QueryResourceConnector queryResourceConnector = new QueryResourceConnector(restClient, authenticator);
		return queryResourceConnector.listServers(configuration.getAuthServerUrl());
	}

	public ResourceCOList getAllResources(ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		QueryResourceConnector queryResourceConnector = new QueryResourceConnector(restClient, authenticator);
		return queryResourceConnector.listAllResources(configuration.getAuthServerUrl());
	}

	public ResourceCO deleteUser(String verifier, ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		DeleteUpdateIdentityConnector deleteUpdateIdentityConnector = new DeleteUpdateIdentityConnector(restClient, authenticator);
		return deleteUpdateIdentityConnector.deleteUser(configuration.getAuthServerUrl(), resourceCO);
	}

	public ResourceCO deleteServer(String verifier, ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		DeleteUpdateIdentityConnector deleteUpdateIdentityConnector = new DeleteUpdateIdentityConnector(restClient, authenticator);
		return deleteUpdateIdentityConnector.deleteResource(configuration.getAuthServerUrl(), resourceCO);
	}

	public ResourceCO updateUser(String verifier, ClientAuthenticator authenticator) throws VeidblockException {

		RestClient restClient = null;
		Configuration configuration = new Configuration();

		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		DeleteUpdateIdentityConnector deleteUpdateIdentityConnector = new DeleteUpdateIdentityConnector(restClient, authenticator);
		return deleteUpdateIdentityConnector.updateUser(configuration.getAuthServerUrl(), resourceCO);
	}

	public ResourceCO updateServer(String verifier, ClientAuthenticator authenticator) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getIPVServerUrl()).build();
		DeleteUpdateIdentityConnector deleteUpdateIdentityConnector = new DeleteUpdateIdentityConnector(restClient, authenticator);
		return deleteUpdateIdentityConnector.updateResource(configuration.getAuthServerUrl(), resourceCO);
	}

	public boolean generateCredentials() throws VeidblockException {
		Configuration configuration = new Configuration();
		return new CertificateConnector().createCertificate(""+this.resourceCO.getResourceId(), this.resourceCO.getPassword(), configuration.getAuthServerUrl() );
	}

	public boolean publishCertificate(String verifier, ClientAuthenticator authenticator) throws VeidblockException{
		logger.info("--- I --- Publishing certificate on chain block "+Identity.PUB_CERT+" !");
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeader = ledger.getTransactionHeaderByName(Identity.PUB_CERT);
		if(Objects.isNull(transactionHeader)){
			logger.error("Certificate chain does not exisits, please try later !");
			throw new VeidblockException("Certificate chain does not exisits, please try later !");
		}
		logger.info("--- I --- Chain block already exisits with reference No. "+transactionHeader.getRef()+" !");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId()+ "", 3);
		ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);
		X509Certificate certificate = certificateHandler.fetchCertificate(resourceCO);
		PEMStream pemStream = new PEMStream();
		String strCert = pemStream.toPem(certificate );
		TransactionCO transactionCO  = ledger.addTransaction(transactionHeader,	null, strCert ,  verifier);
		if(Objects.isNull(transactionCO )){
			return false;
		}
		return true;
		 
	}
	public boolean isCertificateAlreadyPubliched(ClientAuthenticator authenticator) throws VeidblockException{
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeader = ledger.getTransactionHeaderByName(Identity.PUB_CERT);
		if(Objects.isNull(transactionHeader)){
			logger.error("Certificate chain does not exisits, please try later !");
			throw new VeidblockException("Certificate chain does not exisits, please try later !");
		}
		CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId()+ "", 3);
		ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);
		X509Certificate certificate = certificateHandler.fetchCertificate(resourceCO);
		Transactions transaction = ledger.getTransactionBySenderInChain(transactionHeader.getRef(), ""+resourceCO.getResourceId() );
		if(Objects.isNull(transaction) || transaction.size() ==0){
			logger.error("Could not find certificate in ledger chain !");
			return false;
		}
		logger.info("Certificate already published in ledger chain !");
		return true;
	}
	
	public boolean isCertificateAlreadyPubliched(X509Certificate certificate, ClientAuthenticator authenticator) throws VeidblockException{
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeader = ledger.getTransactionHeaderByName(Identity.PUB_CERT);
		if(Objects.isNull(transactionHeader)){
			logger.error("Certificate chain does not exisits, please try later !");
			throw new VeidblockException("Certificate chain does not exisits, please try later !");
		}
		Transactions transaction = ledger.getTransactionBySenderInChain(transactionHeader.getRef(), resourceCO.getResourceId()+"" );
		if(Objects.isNull(transaction) || transaction.size() ==0){
			logger.error("Could not find certificate in ledger chain !");
			return false;
		}
		logger.info("Certificate already published in ledger chain !");
		return true;
	}
	
	
	
	
	private Identity(ResourceCO resourceCO) {
		this.resourceCO = resourceCO;

	}

	public static Identity.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private ResourceCO resourceCO = null;

		protected Builder() {
		}

		public Identity.Builder resource(ResourceCO resourceCO) {
			if (null == resourceCO) {
				return this;
			}
			this.resourceCO = resourceCO;
			return this;
		}

		public Identity build() throws VeidblockException {
			if (resourceCO != null)
				return new Identity(resourceCO);
			else {
				throw new VeidblockException("Please specify resource data !");
			}
		}

	}
}
