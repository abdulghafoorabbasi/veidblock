package org.acreo.clientapi;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.acreo.clientapi.connector.LedgerConnector;
import org.acreo.clientapi.security.ClientCertificateHandler;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.clientapi.utils.TransactionHandler;
import org.acreo.common.entities.DistinguishNameCO;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.BlockHeaderCO;
import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.pkcs.PKCS7Manager;
import org.acreo.security.utils.PEMStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Ledger {

	final static Logger logger = LoggerFactory.getLogger(Ledger.class);
	private ResourceCO resourceCO;

	private ClientAuthenticator authenticator; 
	/*private Ledger() {
	}*/
	private Ledger(ResourceCO resourceCO, ClientAuthenticator authenticator) {
		logger.info("--- I --- Active Resource: " + resourceCO.getResourceId());
		this.resourceCO = resourceCO;
		this.authenticator= authenticator;
		
	}

	public TransactionHeaderCO addTransationHeader(String chainName, SmartContract smartContractStruct, String verifier)
			throws VeidblockException {
		logger.debug("--- I --- Creating a new chainblock !");
		isValidResource();
		BlockHeaderCO chainCode = signBlockHeaderCO(chainName, smartContractStruct);
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		logger.debug("--- I --- Submitted new chainblock !");
		return ledgerConnector.addTransationHeader(chainCode, verifier);
	}

	public TransactionHeaders getTransactionHeaders() throws VeidblockException {
		logger.debug("--- I --- Fetchning all chaincodes !");
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		logger.debug("--- I --- Fetched all available chaincodes !");
		return ledgerConnector.getTransactionHeaders();
	}

	public TransactionHeaderCO getTransactionHeaderByRef(final String ref) throws VeidblockException {
		logger.debug("--- I --- Fetchning chaincode with reference No : " + ref);
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		logger.debug("--- I --- Feteched chaincode with reference No : " + ref);
		return ledgerConnector.getTransactionHeaderByRef(ref);
	}

	public TransactionHeaders getTransactionHeaderByCreator(final String creator) throws VeidblockException {
		logger.debug("--- I --- Fetchning chaincode(s) created by : " + creator);
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		logger.debug("--- I --- Fetched chaincode(s) created by : " + creator);
		return ledgerConnector.getTransactionHeaderByCreator(creator);
	}

	public TransactionHeaderCO getTransactionHeaderByName(final String chainName) throws VeidblockException {
		logger.debug("--- I --- Fetchning chaincode with name : " + chainName);
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		logger.debug("--- I --- Fetched chaincode with name : " + chainName);
		return ledgerConnector.getTransactionHeaderByName(chainName);
	}

	public TransactionCO addTransaction(final TransactionHeaderCO transactionHeaderCO, String receiver[],
			Object payload, String verifier) throws VeidblockException {
		logger.debug(
				"--- I --- Adding trasaction in chaincode, chaincode reference No. : " + transactionHeaderCO.getRef());
		isValidResource();
		logger.debug("--- I --- Performing crypto operations !");
		// Handle here
		TransactionBlockCO transactionBlockCO = signTransactionBlockCO(transactionHeaderCO, receiver, payload);

		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.addTransaction(transactionHeaderCO.getRef(), transactionBlockCO, verifier);
	}

	public String verify(TransactionCO transactionCO) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.verify(transactionCO);
	}

	public Transactions getTransactionByRef(final String ref) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getTransactionByRef(ref);
	}

	public Transactions getTransactionBySender(final String sender) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getTransactionBySender(sender);
	}

	public Transactions getTransactionByReceiver(final String receiver) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getTransactionByReceiver(receiver);
	}

	public Transactions getTransactionBySenderInChain(final String ref, final String sender) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getTransactionBySenderInChain(ref, sender);
	}

	public Transactions getTransactionByRef(final String ref, final String receiver) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getTransactionByRef(ref, receiver);
	}

	public Chain getCompleteChain(final String ref) throws VeidblockException {
		RestClient restClient = null;
		Configuration configuration = new Configuration();
		restClient = RestClient.builder().baseUrl(configuration.getLedgerServer()).build();
		LedgerConnector ledgerConnector = new LedgerConnector(restClient, authenticator);
		return ledgerConnector.getCompleteChain(ref);
	}

	public boolean verifyTransactionLocally(TransactionCO transactionCO) throws VeidblockException {
		
		TransactionHeaderCO transactionHeader = getTransactionHeaderByName(Identity.PUB_CERT);
		if (Objects.isNull(transactionHeader)) {
			logger.error("--- E --- Certificate chain does not exisits, please try later !");
			throw new VeidblockException("Certificate chain does not exisits, please try later !");
		}
		Transactions transactions = getTransactionBySenderInChain(transactionHeader.getRef(),
				transactionCO.getSignedBy());
		if (Objects.isNull(transactions) || transactions.size() == 0) {
			logger.error("--- E --- Coluld not find transaction signer certificate. The transaction is invalid !");
			throw new VeidblockException(
					"Coluld not find transaction signer certificate. The transaction is invalid !");
		}

		TransactionCO certTrans = transactions.get(transactions.size() - 1);
		PEMStream pemStream = new PEMStream();
		X509Certificate x509Cert = null;
		String encodedIssuerCert;
		try {
			encodedIssuerCert = new ObjectMapper().readValue(certTrans.getPayload(), String.class);
			x509Cert = pemStream.pem2x509Cert(encodedIssuerCert);
		} catch (Exception e) {
			throw new VeidblockException(e);
		}
		String data = new String(toByteSerialize(transactionCO));

		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());
		return complexCryptoFunctions.verifySignature(x509Cert.getPublicKey(), data.getBytes(),
				transactionCO.getSignature().getBytes(), ENCODING_DECODING_SCHEME.BASE64);

	}

	public String extractPayload(TransactionHeaderCO transactionHeaderCO, TransactionCO transactionCO)
			throws VeidblockException {
		String strSmartCOntract = transactionHeaderCO.getSmartcontract();
		try {
			SmartContract smartContract = new ObjectMapper().readValue(transactionHeaderCO.getSmartcontract(),
					SmartContract.class);
			if (smartContract.getSecurityLevel() == SECURITY_LEVEL.NONE) {
				return transactionCO.getPayload();
			} else if (smartContract.getSecurityLevel() == SECURITY_LEVEL.DIGITAL_SIGNATURE) {
				String payload = transactionCO.getPayload();
				CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId() + "" + "", 3);
				CryptoPolicy cryptoPolicy = new CryptoPolicy();
				PKCS7Manager pkcs7Manager = new PKCS7Manager(cryptoPolicy, certificateSuite);
				byte[] temp = pkcs7Manager.openPKCS7SignedData(payload);
				return new String(temp);
			} else if (smartContract.getSecurityLevel() == SECURITY_LEVEL.DIGITAL_SIGNATURE_ENVELOPED) {
				String payload = transactionCO.getPayload();
				CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId() + "" + "", 3);
				CryptoPolicy cryptoPolicy = new CryptoPolicy();
				PKCS7Manager pkcs7Manager = new PKCS7Manager(cryptoPolicy, certificateSuite);

				ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);
				X509Certificate cert = certificateHandler.fetchCertificate(resourceCO);
				DistinguishNameCO distinguishNameCO = new DistinguishNameCO();
				distinguishNameCO.setName(resourceCO.getResourceId() + "");

				byte[] temp = pkcs7Manager.openPKCS7SignedAndEnveloped(certificateHandler.fetchCertificate(resourceCO),
						certificateHandler.getPrivateKey(distinguishNameCO, resourceCO.getPassword()), payload);
				return new String(temp);
			} else if (smartContract.getSecurityLevel() == SECURITY_LEVEL.ENVELOPED) {
				String payload = transactionCO.getPayload();
				CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId() + "" + "", 3);
				CryptoPolicy cryptoPolicy = new CryptoPolicy();
				PKCS7Manager pkcs7Manager = new PKCS7Manager(cryptoPolicy, certificateSuite);

				ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);
				X509Certificate cert = certificateHandler.fetchCertificate(resourceCO);
				DistinguishNameCO distinguishNameCO = new DistinguishNameCO();
				distinguishNameCO.setName(resourceCO.getResourceId() + "");
				byte[] temp = pkcs7Manager.openEnvelopedData(certificateHandler.fetchCertificate(resourceCO),
						certificateHandler.getPrivateKey(distinguishNameCO, resourceCO.getPassword()), payload);
				return new String(temp);
			}
			throw new VeidblockException("Problems when extracting payload !");

		} catch (Exception e) {
			throw new VeidblockException(e);
		}

	}

	public byte[] toByteSerialize(TransactionCO transaction) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(format(transaction.getRef()));
		stringBuffer.append(format("" + transaction.getDepth()));
		stringBuffer.append(format(transaction.getHashPrevBlock()));
		stringBuffer.append(format(transaction.getScope()));
		stringBuffer.append(format(transaction.getSender()));
		stringBuffer.append(format(transaction.getReceiver()));
		stringBuffer.append(format("" + transaction.getPayload()));
		stringBuffer.append(format(transaction.getPayloadType()));
		stringBuffer.append(format(transaction.getCryptoOperationsOnPayload()));
		return stringBuffer.toString().getBytes();
	}

	private char delimeter = '|';

	private String format(String value) {
		if (Objects.isNull(value)) {
			return "" + delimeter;
		}
		return value += delimeter;
	}

	public boolean isValidResource() throws VeidblockException {
		if (resourceCO.getResourceId() != 0) {
			if (resourceCO.getPassword() != null) {
				return true;
			} else {
				throw new VeidblockException("Please specify password !");
			}

		} else {
			throw new VeidblockException("Please specify either resource id/uid !");
		}
	}

	public static Ledger.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private ResourceCO resourceCO = null;

		protected Builder() {
		}

		public Ledger.Builder resource(ResourceCO resourceCO) {
			if (null == resourceCO) {
				return this;
			}
			this.resourceCO = resourceCO;
			return this;
		}

		public Ledger build(ClientAuthenticator authenticator_ ) throws VeidblockException {
			if (resourceCO != null)
				return new Ledger(resourceCO, authenticator_);
			else {
				throw new VeidblockException("Please specify resource data !");
			}
		}
	}

	private BlockHeaderCO signBlockHeaderCO(String chainName, SmartContract smartContractStruct)
			throws VeidblockException {
		logger.debug("--- I --- Signing new chainblock !");
		BlockHeaderCO chainCodeTemp = new BlockHeaderCO();
		String uid = "" + resourceCO.getResourceId();
		String password = resourceCO.getPassword();
		CertificateSuite certificateSuite = new CertificateSuite(uid + "", 3);
		ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);

		X509Certificate cert = certificateHandler.fetchCertificate(resourceCO);
		chainCodeTemp.setChainName(chainName);
		chainCodeTemp.setSmartcontract(smartContractStruct);
		chainCodeTemp.setCreator("" + resourceCO.getResourceId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String datTime = simpleDateFormat.format(new Date());
		chainCodeTemp.setSignedDate(datTime);
		logger.debug("--- I --- Creating new chainblock at " + datTime);
		chainCodeTemp.setSignedBy("" + resourceCO.getResourceId());
		chainCodeTemp.setSignerUrl(cert.getIssuerDN().toString());
		String scJSON;
		try {
			logger.debug("--- I --- Serializing chainblock !");
			scJSON = new ObjectMapper().writeValueAsString(chainCodeTemp.getSmartcontract());

			String dataTosign = chainCodeTemp.getSignedBy() + "|" + chainCodeTemp.getSignedDate() + "|"
					+ chainCodeTemp.getChainName() + "|" + scJSON;

			DistinguishNameCO distinguishNameCO = new DistinguishNameCO();
			distinguishNameCO.setName("" + resourceCO.getResourceId());
			ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());

			logger.debug("Public Key : " + cert.getPublicKey().toString());
			logger.debug("Data to sign");
			logger.debug("\t SignedBy() : " + chainCodeTemp.getSignedBy());
			logger.debug("\t SignedDate() : " + chainCodeTemp.getSignedDate());
			logger.debug("\t SignedDate() : " + chainCodeTemp.getSignedDate());
			logger.debug("\t Name() : " + chainCodeTemp.getChainName());
			logger.debug("\t Smart Contract() : " + scJSON);
			logger.debug("\t Data : " + dataTosign);

			byte signedEncodedBytes[] = complexCryptoFunctions.generateSignature(
					certificateHandler.getPrivateKey(distinguishNameCO, resourceCO.getPassword()),
					dataTosign.getBytes(), ENCODING_DECODING_SCHEME.BASE64);

			logger.debug("--- I --- Signature : " + new String(signedEncodedBytes));

			chainCodeTemp.setSignature(new String(signedEncodedBytes));

		} catch (JsonProcessingException e) {
			throw new VeidblockException(e);
		}
		return chainCodeTemp;
	}

	private TransactionBlockCO signTransactionBlockCO(TransactionHeaderCO transactionHeaderCO, String receivers[],
			Object payload) throws VeidblockException {

		TransactionHandler transactionHandler = new TransactionHandler();
		TransactionBlockCO transactionBlockCO = transactionHandler.createTransaction(resourceCO, transactionHeaderCO,
				receivers, payload, authenticator);

		CertificateSuite certificateSuite = new CertificateSuite(resourceCO.getResourceId() + "" + "", 3);
		ClientCertificateHandler certificateHandler = new ClientCertificateHandler(certificateSuite);

		X509Certificate cert = certificateHandler.fetchCertificate(resourceCO);

		logger.debug("--- I --- Signing transaction !");
		StringBuffer signBuff = new StringBuffer();
		signBuff.append(transactionHeaderCO.getRef() + "|");
		signBuff.append(transactionBlockCO.getCreationTime() + "|");
		signBuff.append(transactionBlockCO.getCryptoOperationsOnPayload() + "|");
		signBuff.append(transactionBlockCO.getPayload() + "|");
		signBuff.append(transactionBlockCO.getPayloadType() + "|");
		signBuff.append(transactionBlockCO.getReceiver() + "|");
		signBuff.append(transactionBlockCO.getScope() + "|");
		signBuff.append(transactionBlockCO.getSender() + "|");
		signBuff.append(transactionBlockCO.getSignedBy() + "|");
		signBuff.append(transactionBlockCO.getSignedDate());
		transactionBlockCO.setSignerUrl(cert.getIssuerDN().toString());
		String dataTosign = signBuff.toString();
		DistinguishNameCO distinguishNameCO = new DistinguishNameCO();
		distinguishNameCO.setName("" + resourceCO.getResourceId());

		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());
		byte signedEncodedBytes[] = complexCryptoFunctions.generateSignature(
				certificateHandler.getPrivateKey(distinguishNameCO, resourceCO.getPassword()), dataTosign.getBytes(),
				ENCODING_DECODING_SCHEME.BASE64);
		String signedData = new String(signedEncodedBytes);

		transactionBlockCO.setSignature(signedData);
		logger.debug("--- I --- Successfully singed new chainblock !");
		return transactionBlockCO;
	}
}
