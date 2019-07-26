package org.acreo.ledger.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.sql.DataSource;
import javax.ws.rs.WebApplicationException;

import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.BlockHeaderCO;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.init.InitSetup;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.LocalCredentialManager;
import org.acreo.init.SystemInfo;
import org.acreo.init.VeidblockIO;
import org.acreo.ledger.LedgerConfiguration;
import org.acreo.ledger.database.generic.GenericVedblockService;
import org.acreo.ledger.resources.VeidblockChain;
import org.acreo.ledger.resources.VeidblockLedgerAuthorizeResources;
import org.acreo.ledger.resources.VeidblockLedgerResources;
import org.acreo.ledger.service.VeidblockLedgerService;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;
import org.acreo.ledger.transactions.utils.CertificateChainSmartContract;
import org.acreo.ledger.transactions.utils.ChainCreationManager;
import org.acreo.ledger.transactions.utils.ChainUtils;
import org.acreo.ledger.transactions.utils.ClientCertificateHandler;
import org.acreo.ledger.transactions.utils.PeerMessageHandler;
import org.acreo.ledger.utils.DomainLedgerHealthCheck;
import org.acreo.messaging.MessagingManager;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.utils.PEMStream;
import org.apache.log4j.Logger;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class VeidblockLedgerApplication extends Application<LedgerConfiguration> {
	private static final String SQL = "sql";
	private static final String LEDGER_SERVICE = "Ledger service";
	final static Logger logger = Logger.getLogger(VeidblockLedgerApplication.class);

	public static void main(String[] args) throws Exception {

		new VeidblockLedgerApplication().run(args);
	}

	@Override
	public void run(LedgerConfiguration configuration, Environment environment) {

		BlockHeaderCO blockCO = new BlockHeaderCO();
		blockCO.setChainName("identity");
		blockCO.setCreator("abdul");
		blockCO.setSignature("signature");
		blockCO.setSignedBy("SignedBY");
		blockCO.setSignedDate("SignedDate");
		blockCO.setSignerUrl("signerUrl");

		SmartContract smartContractStruct = new CertificateChainSmartContract();
		blockCO.setSmartcontract(smartContractStruct);
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(blockCO));

			TransactionBlockCO transaction = new TransactionBlockCO();
			transaction.setCreationTime("creationTime");
			transaction.setCryptoOperationsOnPayload("cryptoOperationsOnPayload");
			transaction.setPayload("THe actual data");
			transaction.setPayloadType("String");
			transaction.setReceiver("receiver");
			transaction.setScope("ALL");
			transaction.setSender("sender");
			transaction.setCreationTime("creationTime");
			transaction.setSignature("signature");
			transaction.setSignedBy("SignedBY");
			transaction.setSignedDate("SignedDate");
			transaction.setSignerUrl("signerUrl");
			System.out.println(mapper.writeValueAsString(transaction));

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// InitSetup.getResourcesPath();
		// -------------------------- INIT + Registration

		LocalCredentialManager localCredentialManager = new LocalCredentialManager("localledger");
		final RestClient restIpvClient = configuration.getIpvConnectorConfig().build(environment);
		InitSetup.build(restIpvClient, localCredentialManager);
		VeidblockIO veidblockIO = new VeidblockIO();
		ObjectMapper objectMapper = new ObjectMapper();
		ResourceCO resourceCO;

		try {
			resourceCO = objectMapper.readValue(veidblockIO.readResourceCO(), ResourceCO.class);
			createCertificate("" + resourceCO.getResourceId(), resourceCO.getPassword(), "http://localhost:9000");
		} catch (IOException | VeidblockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		resourceCO.setPassword(localCredentialManager.getPassword());
		CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId() + "", 5);
		LocalCertificateManager localCertificateManager = new LocalCertificateManager(certificateSuite,
				localCredentialManager.getPassword(), null);
		// Public void Change store name
		// -------------------------- END ----------------------------

		// Datasource configuration
		final DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), SQL);
	/*	
		try {
			createDB(configuration.getDataSourceFactory());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}*/
		
		DBI dbi = new DBI(dataSource);

		DomainLedgerHealthCheck domainLedgerHealthCheck = new DomainLedgerHealthCheck(
				dbi.onDemand(VeidblockLedgerService.class));
		environment.healthChecks().register(LEDGER_SERVICE, domainLedgerHealthCheck);

		VeidblockLedgerService veidblockLedgerService = dbi.onDemand(VeidblockLedgerService.class);
		GenericVedblockService genericVedblockService = dbi.onDemand(GenericVedblockService.class);
		TransactionHeaderService transactionHeaderService = dbi.onDemand(TransactionHeaderService.class);
		TransactionService transactionService = dbi.onDemand(TransactionService.class);

		// ----------------- Handle messaging service -----------------
		String kafkaIP = "127.0.0.1"; 
		int kafkaPort = 9092;
		MessagingManager MessagingManager = new MessagingManager("" + resourceCO.getResourceId(), kafkaIP, kafkaPort);
		MessagingManager.executeMessageReceiver();		
		PeerMessageHandler peerMessageHandler = new PeerMessageHandler(transactionHeaderService, transactionService);
		peerMessageHandler.handlePeerTransactionHeader();
		peerMessageHandler.handlePeerTransaction();
		// ----------------- Handle messaging service END -----------------

		ChainUtils chainUtils = new ChainUtils(localCertificateManager);
		ChainCreationManager chainCreationManager = new ChainCreationManager(kafkaIP, kafkaPort,transactionHeaderService,
				transactionService, localCertificateManager);

		createLocalCertChain(chainCreationManager, chainUtils);
		try {
			loadCertificateOnLedger(chainCreationManager, localCertificateManager, resourceCO, transactionHeaderService,
					transactionService);
		} catch (VeidblockException e) {
			e.printStackTrace();
			return;
		}
		// Register resources
		environment.jersey()
				.register(new VeidblockChain(kafkaIP, kafkaPort,transactionHeaderService, transactionService, localCertificateManager));
		environment.jersey().register(new VeidblockLedgerResources(veidblockLedgerService, localCertificateManager));
		environment.jersey().register(new VeidblockLedgerAuthorizeResources(genericVedblockService,
				veidblockLedgerService, localCertificateManager));

		SystemInfo systemInfo = new SystemInfo("Veidblock Ledger", "1.0.0", "RISE Acreo, AB, Kista, Sweden", "",
				new Date());
		InitSetup.displayTag(systemInfo);

	}

	private void enableCORS(Environment environment) {
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	}

	public boolean createCertificate(String uid, String password, String verifyerURL) throws VeidblockException {
		//logger.info("Starting process to issue certificate for ledger !");
		CertificateSuite certificateSuite = new CertificateSuite(uid + "", 3);
		RestClient restClient;
		restClient = RestClient.builder().baseUrl(verifyerURL + "/cert/request").build();
		//logger.info("Local Verification serivce agent created !");
		ClientCertificateHandler clientCertificateHandler = new ClientCertificateHandler(certificateSuite);
		return clientCertificateHandler.issueCertificate(restClient, uid, password);
	}

	public void createLocalCertChain(ChainCreationManager chainCreationManager, ChainUtils chainUtils) {
		try {
			//logger.info("Creating Certificate chain !");
			BlockHeaderCO blockCO = new BlockHeaderCO();
			blockCO.setChainName(VeidblockChain.PUB_CERT);
			VeidblockIO veidblockIO = new VeidblockIO();
			ResourceCO resourceCO = null;
			try {
				resourceCO = new ObjectMapper().readValue(veidblockIO.readResourceCO(), ResourceCO.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//logger.info("Certificate chain published [" + resourceCO.getResourceId() + "]!");
			blockCO.setCreator(resourceCO.getResourceId() + "");
			blockCO.setSignature("");
			blockCO.setSignedBy("");
			blockCO.setSignedDate("");
			blockCO.setSignerUrl("");

			SmartContract smartContractStruct = new CertificateChainSmartContract();
			smartContractStruct.setStart(new Date());
			smartContractStruct.setScope(SCOPE.OPEN);
			smartContractStruct.setEnd(new Date(Long.MAX_VALUE));
			smartContractStruct.setSecurityLevel(SECURITY_LEVEL.NONE);
			smartContractStruct.setPayloadSupportingTypes(new String[] { String.class.getName() });

			blockCO.setSmartcontract(smartContractStruct);

			chainCreationManager.addCertificateChain(blockCO,
					chainUtils.getLocalHostLANAddress().getHostAddress() + ":10000/vc/pubkey");
		} catch (WebApplicationException | VeidblockException | UnknownHostException e) {
			logger.warn("Ignorable : " + e.getMessage());
		}
	}

	private void loadCertificateOnLedger(ChainCreationManager chainCreationManager,
			LocalCertificateManager localCertificateManager, ResourceCO resourceCO,
			TransactionHeaderService transactionHeaderService, TransactionService transactionService)
			throws VeidblockException {

		TransactionHeader transactionHeader = transactionHeaderService
				.getTransactionHeaderByName(VeidblockChain.PUB_CERT);
		if (Objects.isNull(transactionHeader)) {
			logger.error("Certificate chain does not exisits, please try later !");
			throw new VeidblockException("Certificate chain does not exisits, please try later !");
		}
		List<Transaction> transactions = transactionService.getTransactionBySenderInChain(transactionHeader.getRef(),
				resourceCO.getResourceId() + "");

		if (Objects.isNull(transactions) || transactions.size() == 0) {
			logger.warn("Could not find certificate in ledger chain !");
			stackCertificate(chainCreationManager, localCertificateManager, resourceCO, transactionService,
					transactionHeader.getRef(), "");
		} else {
			return;
		}
	}

	private boolean stackCertificate(ChainCreationManager chainCreationManager,
			LocalCertificateManager localCertificateManager, ResourceCO resourceCO,
			TransactionService transactionService, String ref, String verifier) throws VeidblockException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		X509Certificate certificate = localCertificateManager.fetchCertificate();
		PEMStream pemStream = new PEMStream();
		String strCert = pemStream.toPem(certificate);

		Transaction transaction = signTransactionBlockCO(localCertificateManager, resourceCO, ref,
				simpleDateFormat.format(new Date()), null, strCert, "", certificate);
		if (Objects.isNull(transaction)) {
			return false;
		}

		new Thread() {
			public void run() {
				while (true) {
					try {
						chainCreationManager.addTransaction(ref, transaction, "http://localhost:9000");
						break;
					} catch (Exception exp) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
		return true;
	}

	private Transaction signTransactionBlockCO(LocalCertificateManager localCertificateManager, ResourceCO resourceCO,
			String ref, String creationTime, String receivers[], Object payload, String cryptoOperationsOnPayload,
			X509Certificate cert) throws VeidblockException {

		Transaction transactionBlockCO = new Transaction();
		transactionBlockCO.setRef(ref);
		transactionBlockCO.setCreationTime(creationTime);
		transactionBlockCO.setCryptoOperationsOnPayload(cryptoOperationsOnPayload);
		if (Objects.isNull(payload)) {
			throw new VeidblockException("Paylaod is null !");
		}
		try {
			transactionBlockCO.setPayload(new ObjectMapper().writeValueAsString(payload));
		} catch (JsonProcessingException e) {

		}
		transactionBlockCO.setPayloadType(payload.getClass().getName());
		StringBuffer receiverBuff = new StringBuffer();
		if (!Objects.isNull(receivers)) {
			for (String rec : receivers) {
				receiverBuff.append(rec + "|");
			}
		} else {
			receiverBuff.append("");
		}
		transactionBlockCO.setReceiver(receiverBuff.toString());
		transactionBlockCO.setCryptoOperationsOnPayload(SECURITY_LEVEL.NONE.toString());
		transactionBlockCO.setScope(SCOPE.OPEN.toString());
		transactionBlockCO.setSender(resourceCO.getResourceId() + "");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		transactionBlockCO.setSignedDate(simpleDateFormat.format(new Date()));

		String scJSON;

		transactionBlockCO.setSignedBy(resourceCO.getResourceId() + "");

		StringBuffer signBuff = new StringBuffer();
		signBuff.append(ref + "|");
		signBuff.append(transactionBlockCO.getCreationTime() + "|");
		signBuff.append(transactionBlockCO.getCryptoOperationsOnPayload() + "|");
		signBuff.append(transactionBlockCO.getPayload() + "|");
		signBuff.append(transactionBlockCO.getPayloadType() + "|");
		signBuff.append(transactionBlockCO.getReceiver() + "|");
		signBuff.append(transactionBlockCO.getScope() + "|");
		signBuff.append(transactionBlockCO.getSender() + "|");
		signBuff.append(transactionBlockCO.getSignedBy() + "|");
		signBuff.append(transactionBlockCO.getSignedDate());

		String dataTosign = signBuff.toString();

		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());
		byte signedEncodedBytes[] = complexCryptoFunctions.generateSignature(
				localCertificateManager.getAuthServerPrivateKey(), dataTosign.getBytes(),
				ENCODING_DECODING_SCHEME.BASE64);
		String signedData = new String(signedEncodedBytes);
		transactionBlockCO.setSignerUrl(cert.getIssuerDN().toString());
		transactionBlockCO.setSignature(signedData);
		return transactionBlockCO;
	}
/*	private void createDB(DataSourceFactory dataSourceFactory) throws Exception{
		String dbURLFull = dataSourceFactory.getUrl();
		String user= dataSourceFactory.getUser();
		String password = dataSourceFactory.getPassword();
		String databaseName = dbURLFull.substring(dbURLFull.lastIndexOf("/")+1);
		String dbURL = dbURLFull.substring(0,dbURLFull.lastIndexOf("/"));
		new DatabaseCretor().createVeidblockDatabase(dbURL , user, password, databaseName);
	}*/
}