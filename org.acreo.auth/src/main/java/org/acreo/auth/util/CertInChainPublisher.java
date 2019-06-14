package org.acreo.auth.util;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.acreo.common.Representation;
import org.acreo.common.entities.DistinguishNameCO;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.VeidblockIO;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.utils.PEMStream;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CertInChainPublisher extends Thread {

	public static boolean CERT_PUB_STATUS = false; 
	final static Logger logger = LoggerFactory.getLogger(CertInChainPublisher.class);
	String PUB_CERT = "Certificate";
	private LocalCertificateManager localCertificateManager = null;
	String vc = "http://localhost:10000";
	private ResourceCO resourceCO = null;
	public CertInChainPublisher(LocalCertificateManager localCertificateManager) {
		this.localCertificateManager = localCertificateManager;

		VeidblockIO veidblockIO = new VeidblockIO();
		try {
			resourceCO  = new ObjectMapper().readValue(veidblockIO.readResourceCO(), ResourceCO.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		try {
			publish();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public void publish() throws VeidblockException {

		RestClient restClient = null;

		restClient = RestClient.builder().baseUrl(vc).build();
		restClient.setShowLog(false);
		Representation<?> response = null;
		try {
			boolean b = false;
			while (true) {
				try {
					response = restClient.get("/vc/chain/chainName/" + PUB_CERT, null);
					break;
				} catch (Exception exp) {
					if(!b){
						logger.warn("Ledger is not running, so waiting on that to check certificate is published !");
						b = true;
					}
					Thread.sleep(1000);
				}
			}			
			logger.debug("Successfully created connection with Ledger !");
			TransactionHeaderCO transactionHeaderCO = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaderCO.class);
			if (Objects.isNull(transactionHeaderCO)) {
				logger.error("Certificate chain does not exisits, please try later !");
				throw new VeidblockException("Certificate chain does not exisits, please try later !");
			}
			response = restClient.get("/vc/trans/ref/" + transactionHeaderCO.getRef() + "/sender/"+resourceCO.getResourceId() , null);
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			if (Objects.isNull(transactions) || transactions.size() == 0) {
				logger.warn("Could not find certificate in ledger chain !");
				stackCertificate(restClient, transactionHeaderCO.getRef(), "");
				CERT_PUB_STATUS = false;
			} else {
				return;
			}
		} catch (Exception e1) {

			if (e1 instanceof HttpHostConnectException) {
				logger.warn(e1.getMessage());
			} else {
				logger.debug(e1.getMessage());
				throw new VeidblockException(e1);
			}
		}
	}

	private boolean stackCertificate(RestClient restClient, String ref, String verifier) throws VeidblockException {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		X509Certificate certificate = localCertificateManager.fetchCertificate();
		PEMStream pemStream = new PEMStream();
		String strCert = pemStream.toPem(certificate);
		TransactionCO transactionCO = addTransaction(restClient, ref, simpleDateFormat.format(new Date()), null,
				strCert, "", verifier, certificate);
		if (Objects.isNull(transactionCO)) {
			return false;
		}
		return true;
	}

	private TransactionCO addTransaction(RestClient restClient, final String ref, String creationTime,
			String receivers[], Object payload, String cryptoOperationsOnPayload, String verifier, X509Certificate cert)
			throws VeidblockException {

		TransactionBlockCO transactionBlockCO = signTransactionBlockCO(ref, creationTime, receivers, payload,
				cryptoOperationsOnPayload, cert);

		Representation<?> response = null;
		try {
			response = restClient.post("/vc/trans/ref/" + ref, transactionBlockCO, null);
			TransactionCO transactionCO_ = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionCO.class);
			return transactionCO_;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}

	private TransactionBlockCO signTransactionBlockCO(String ref, String creationTime, String receivers[],
			Object payload, String cryptoOperationsOnPayload, X509Certificate cert) throws VeidblockException {

		TransactionBlockCO transactionBlockCO = new TransactionBlockCO();
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
		transactionBlockCO.setSender(resourceCO.getResourceId()+"");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		transactionBlockCO.setSignedDate(simpleDateFormat.format(new Date()));

		String scJSON;

		transactionBlockCO.setSignedBy(resourceCO.getResourceId()+"");

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
	
	public synchronized boolean isCertificatePublicationStatus(){
		return CERT_PUB_STATUS;
	}

}
