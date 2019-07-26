package org.acreo.ledger.transactions.utils;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.BlockHeaderCO;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.VeidblockIO;
import org.acreo.ledger.resources.VeidblockChain;
import org.acreo.ledger.transactions.entities.SmartContractStruct;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;
import org.acreo.messaging.MessagingManager;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.crypto.Hashing;
import org.acreo.security.pkcs.PKCS7Manager;
import org.acreo.security.utils.DistinguishName;
import org.acreo.security.utils.PEMStream;
import org.acreo.security.utils.SGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChainCreationManager {

	final static Logger logger = LoggerFactory.getLogger(ChainCreationManager.class);

	public static final String ledgerVersion = "0.7";
	private TransactionHeaderService transactionHeaderService;
	private LocalCertificateManager localCertificateManager;
	private TransactionService transactionService;
	private String kafkaIP;
	private int kafkaPort;

	public ChainCreationManager(String kafkaIP, int kafkaPort, TransactionHeaderService transactionHeaderService,
			TransactionService transactionService, LocalCertificateManager localCertificateManager) {
		this.transactionHeaderService = transactionHeaderService;
		this.localCertificateManager = localCertificateManager;
		this.transactionService = transactionService;
		this.kafkaIP = kafkaIP;
		this.kafkaPort = kafkaPort;
	}

	public TransactionHeaderCO add(BlockHeaderCO blockCO, String url) throws VeidblockException {
		//logger.info("Adding a new chainblock with name " + blockCO.getChainName());
		TransactionHeader temp = this.transactionHeaderService.getTransactionHeaderByName(blockCO.getChainName());
		if (!Objects.isNull(temp)) {
			logger.error("Chain code with same name [" + blockCO.getChainName() + "] already exisits !");
			throw new WebApplicationException(Response.status(Status.CONFLICT)
					.entity("Chain code with same name [" + blockCO.getChainName() + "] already exisits !").build());
		}

		try {
			if (!verifyBlock(blockCO)) {
				logger.error(
						"Could not verify signature, please check that the Certificate published on ledger and being used at client are the same !");
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
						.entity("Could not verify signature, please check that the Certificate published on ledger and being used at client are the same !")
						.build());
			}
		} catch (Exception e) {
			logger.error("Problems when verifying block !");
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity("Problems when verifying block !").build());
		}
		//logger.info("Request received successfully verified, Chainblock name: " + blockCO.getChainName());
		TransactionHeader transactionHeader = this.transactionHeaderService.getLastTransactionHeader();
		
		TransactionHeaderCO result = new TransactionHeaderCO();
		TransactionHeader headerReturned = null;
		// Check name is already exists then ask for change
		if (Objects.isNull(transactionHeader)) {
			//logger.info("Creating genius block !");
			transactionHeader = preapreNewBlock(transactionHeader, blockCO);
			transactionHeader = transactionHeaderCrypto(transactionHeader, url);
			transactionHeader.setCreatorURL(blockCO.getSignerUrl());
			transactionHeader.setCreatorSignature(blockCO.getSignature());
			transactionHeaderService.createTransactionHeader(transactionHeader);
			//logger.info("Successfully created genius block !");

			headerReturned = transactionHeaderService.getTransactionHeaderByRef(transactionHeader.getRef());

			manageSmartContract(headerReturned);

		} else {
			//logger.info("Creating chained block !");
			TransactionHeader nextTransactionHeader = preapreNextBlock(transactionHeader, blockCO);
			nextTransactionHeader = transactionHeaderCrypto(nextTransactionHeader, url);
			nextTransactionHeader.setCreatorURL(blockCO.getSignerUrl());
			nextTransactionHeader.setCreatorSignature(blockCO.getSignature());
			transactionHeaderService.createTransactionHeader(nextTransactionHeader);
			//logger.info("Successfully created chained block header !");
			//logger.info("Ref :" + nextTransactionHeader.getRef());

			headerReturned = transactionHeaderService.getTransactionHeaderByRef(nextTransactionHeader.getRef());

			manageSmartContract(headerReturned);

		}
		result = new Convertor().toTransactionHeaderCO(headerReturned);
		MessagingManager MessagingManager = new MessagingManager(headerReturned.getSignedBy(), kafkaIP, kafkaPort);
		MessagingManager.sendMesasge(result);
		return result;

	}

	public TransactionHeaderCO addCertificateChain(BlockHeaderCO blockCO, String url) throws VeidblockException {
		//logger.info("Adding certificate chain with chain Name [" + blockCO.getChainName() + "]!");
		if (!blockCO.getChainName().equals(VeidblockChain.PUB_CERT)) {
			logger.error("Only used for creating Certificate chain !");
			throw new WebApplicationException(
					Response.status(Status.UNAUTHORIZED).entity("Only used for creating Certificate chain !").build());
		}

		TransactionHeader temp = this.transactionHeaderService.getTransactionHeaderByName(blockCO.getChainName());
		if (!Objects.isNull(temp)) {
			logger.warn("Certificate chain already exisits. Ref Number is [" + temp.getRef() + "] !");
			throw new WebApplicationException(Response.status(Status.FOUND)
					.entity("Certificate chain already exisits. Ref Number is [" + temp.getRef() + "] !").build());
		}

		try {
			if (!verifyBlockCertificatePolicy(blockCO)) {
				logger.error("Could not verify signature or public key !");
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
						.entity("Could not verify signature or public key !").build());
			}
		} catch (Exception e) {
			logger.error("Problems when verifying block !");
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity("Problems when verifying block !").build());
		}
		//logger.info("Successfully verified certificate chain block with name [" + blockCO.getChainName() + "]!");
		TransactionHeader transactionHeader = this.transactionHeaderService.getLastTransactionHeader();
		TransactionHeaderCO result = new TransactionHeaderCO();
		TransactionHeader headerReturned = null;
		if (Objects.isNull(transactionHeader)) {
			//logger.info("Creating genius block !");
			transactionHeader = preapreNewBlock(transactionHeader, blockCO);
			transactionHeader = transactionHeaderCrypto(transactionHeader, url);
			transactionHeaderService.createTransactionHeader(transactionHeader);
			//logger.info("Successfully created genius block !");
			headerReturned = transactionHeaderService.getTransactionHeaderByRef(transactionHeader.getRef());

			manageSmartContract(headerReturned);

			result = new Convertor().toTransactionHeaderCO(headerReturned);

		} else {
			//logger.info("Creating chained block !");
			TransactionHeader nextTransactionHeader = preapreNextBlock(transactionHeader, blockCO);
			nextTransactionHeader = transactionHeaderCrypto(nextTransactionHeader, url);
			transactionHeaderService.createTransactionHeader(nextTransactionHeader);
			transactionHeaderService.getTransactionHeaderByRef(transactionHeader.getRef());
			//logger.info("Successfully created chained block header !");
			//logger.info("Ref :" + nextTransactionHeader.getRef());
			
			headerReturned = transactionHeaderService.getTransactionHeaderByRef(nextTransactionHeader.getRef());
			manageSmartContract(headerReturned);
			result = new Convertor().toTransactionHeaderCO(headerReturned);
		}
		MessagingManager MessagingManager = new MessagingManager(transactionHeader.getSignedBy(), kafkaIP, kafkaPort);
		MessagingManager.sendMesasge(result);
		return result;
	}

	private TransactionHeader preapreNewBlock(TransactionHeader transactionHeader, BlockHeaderCO blockCO)
			throws VeidblockException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		transactionHeader = new TransactionHeader();
		transactionHeader.setRef(new SGen().nextHexString(32));
		transactionHeader.setVersion(ledgerVersion);
		transactionHeader.setCreationTime(simpleDateFormat.format(new Date()));
		byte[] prevHashOfFirstHeader = new byte[32];
		Arrays.fill(prevHashOfFirstHeader, (byte) 1);
		Hashing hashing = new Hashing(new CryptoPolicy());
		byte prevHashOfFirstHeaderHashed[] = hashing.generateHash(prevHashOfFirstHeader);
		String encodedPrevHashOfFirstHeader = new String(Base64.getEncoder().encode(prevHashOfFirstHeaderHashed));
		logger.debug("Used initial hash of the block !");
		transactionHeader.setHashPrevBlock(encodedPrevHashOfFirstHeader);
		transactionHeader.setExtbits("");
		transactionHeader.setNonce(new SGen().nextHexString(16));
		transactionHeader.setHeight(1);
		transactionHeader.setCreator(blockCO.getCreator());
		transactionHeader.setChainName(blockCO.getChainName());
		ObjectMapper objectMapper = new ObjectMapper();
		String scJSON = "";
		try {
			scJSON = objectMapper.writeValueAsString(blockCO.getSmartcontract());
			transactionHeader.setSmartcontract(scJSON);
		} catch (JsonProcessingException e) {
			transactionHeader.setSmartcontract("NONE");
		}

		return transactionHeader;

	}

	private TransactionHeader preapreNextBlock(TransactionHeader transactionHeaderPrevious, BlockHeaderCO blockCO)
			throws VeidblockException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		TransactionHeader transactionHeader = new TransactionHeader();
		transactionHeader = new TransactionHeader();
		transactionHeader.setRef(new SGen().nextHexString(32));
		transactionHeader.setVersion(ledgerVersion);
		transactionHeader.setCreationTime(simpleDateFormat.format(new Date()));
		// Calculate hash of previous block
		Serializer serializer = new Serializer();
		byte[] serializedTH = serializer.toByteSerialize(transactionHeaderPrevious);
		Hashing hashing = new Hashing(new CryptoPolicy());
		serializedTH = hashing.generateHash(serializedTH);
		String encodedHahaTH = new String(Base64.getEncoder().encode(serializedTH));
		logger.debug("Hash of previoud block successfully created !");
		transactionHeader.setHashPrevBlock(encodedHahaTH);
		transactionHeader.setExtbits("");
		transactionHeader.setNonce(new SGen().nextHexString(16));
		transactionHeader.setHeight(transactionHeaderPrevious.getHeight() + 1);
		transactionHeader.setCreator(blockCO.getCreator());
		transactionHeader.setChainName(blockCO.getChainName());
		ObjectMapper objectMapper = new ObjectMapper();
		String scJSON = "";
		try {
			scJSON = objectMapper.writeValueAsString(blockCO.getSmartcontract());
			transactionHeader.setSmartcontract(scJSON);
		} catch (JsonProcessingException e) {
			transactionHeader.setSmartcontract("NONE");
		}
		return transactionHeader;

	}

	private TransactionHeader transactionHeaderCrypto(TransactionHeader transactionHeader, String url)
			throws VeidblockException {

		ResourceCO resourceCO = null;
		try {
			resourceCO = new ObjectMapper().readValue(new VeidblockIO().readResourceCO(), ResourceCO.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transactionHeader.setSignedBy(resourceCO.getResourceId() + "");
		transactionHeader.setSignerUrl(url);

		//logger.info("Serializing transction header !");
		Serializer serializer = new Serializer();
		byte[] serializedTH = serializer.toByteSerialize(transactionHeader);
		Hashing hashing = new Hashing(new CryptoPolicy());
		serializedTH = hashing.generateHash(serializedTH);
		String encodedHashTH = new String(Base64.getEncoder().encode(serializedTH));
		//logger.info("Hash of current block successfully created !");
		transactionHeader.setHashMerkleRoot(encodedHashTH);
		
		//logger.info("Signing transaction block with peer's credentials !");
		
		//logger.info("Fetching peer's private key !");
		
		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());

		byte[] b64Signature = complexCryptoFunctions.generateSignature(
				localCertificateManager.getAuthServerPrivateKey(), encodedHashTH.getBytes(),
				ENCODING_DECODING_SCHEME.BASE64);
		String signedData = new String(b64Signature);
		transactionHeader.setSignature(signedData);
		//logger.info("Successfully signed transaction block by the peer !");
		return transactionHeader;
	}

	private boolean verifyBlockCertificatePolicy(BlockHeaderCO blockCO) throws Exception {
		return true;
	}

	private boolean verifyBlock(BlockHeaderCO blockCO) throws Exception {
		String signedBy = blockCO.getSignedBy();
		String signature = blockCO.getSignature();
		String sigDate = blockCO.getSignedDate();
		String sigURL = blockCO.getSignerUrl();
		TransactionHeader transactionHeader = transactionHeaderService
				.getTransactionHeaderByName(VeidblockChain.PUB_CERT);
		if (Objects.isNull(transactionHeader)) {
			//logger.info("Could not find certificate service !");
			return false;
		}
		List<Transaction> transaction = transactionService.getTransactionBySender(blockCO.getSignedBy());

		Transaction tranCert = transaction.get(transaction.size() - 1);
		PEMStream pemStream = new PEMStream();
		X509Certificate x509Certificate = pemStream
				.pem2x509Cert(new ObjectMapper().readValue(tranCert.getPayload(), String.class));

		String scJSON;

		scJSON = new ObjectMapper().writeValueAsString(blockCO.getSmartcontract());
		String dataTosign = blockCO.getSignedBy() + "|" + blockCO.getSignedDate() + "|" + blockCO.getChainName() + "|"
				+ scJSON;
		logger.debug("Public Key : " + x509Certificate.getPublicKey().toString());
		logger.debug("Data to sign ");
		logger.debug("\t SignedBy() : " + blockCO.getSignedBy());
		logger.debug("\t SignedDate() : " + blockCO.getSignedDate());
		logger.debug("\t SignedDate() : " + blockCO.getSignedDate());
		logger.debug("\t Name() : " + blockCO.getChainName());
		logger.debug("\t Smart COntract() : " + scJSON);
		logger.debug("\t Data : " + dataTosign);
		logger.debug("Signature : " + signature);

		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());
		return complexCryptoFunctions.verifySignature(x509Certificate.getPublicKey(), dataTosign.getBytes(),
				signature.getBytes(), ENCODING_DECODING_SCHEME.BASE64);
	}

	// -------------------------------------------------------------------
	public Transaction addTransaction(String ref, Transaction transaction, String url) throws VeidblockException {
		//logger.info("Adding transaction in chainblock : " + ref);
		TransactionHeader transactionHeader = this.transactionHeaderService.getTransactionHeaderByRef(ref);
		if (Objects.isNull(transactionHeader)) {
			logger.error("Invalid ref [" + ref + "] !");
			throw new WebApplicationException(
					Response.status(Status.PRECONDITION_FAILED).entity("Invalid ref [" + ref + "] !").build());
		}
		SmartContract smartContract = null;
		try {
			smartContract = new ObjectMapper().readValue(transactionHeader.getSmartcontract(), SmartContract.class);
		} catch (IOException e) {
			throw new VeidblockException("Problems when execurity Smart-contract !");
		}
		enforceDateTimeRuleOnTransaction(smartContract);
		enforceAccessRules(smartContract, transactionHeader);

		if (transactionHeader.getChainName().equals(VeidblockChain.PUB_CERT)) {
			//logger.info("Adding transaction in cetificate chainblock with Reference No. : " + ref);
			String encodedCert = transaction.getPayload();
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				encodedCert = objectMapper.readValue(encodedCert, String.class);
			} catch (IOException e1) {
				logger.error("Invalid PEM formated certificate !");
				throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED)
						.entity("Invalid PEM formated certificate !").build());
			}
			PEMStream pemStream = new PEMStream();
			try {
				X509Certificate x509Certificate = pemStream.pem2x509Cert(encodedCert);
				if (x509Certificate.getSubjectDN().toString().equals(x509Certificate.getIssuerDN().toString())) {
					// Verify policy
					//logger.info("Self signed certificate is requested to stack in chain !");
				} else {
					DistinguishName dn = DistinguishName.builder().build(x509Certificate.getIssuerDN().toString());
					List<Transaction> transactions = transactionService
							.getTransactionBySenderInChain(transactionHeader.getRef(), dn.getId());
					if (Objects.isNull(transactions) || transactions.size() == 0) {
						logger.error("Issuer certifcate does not exist !");
						throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED)
								.entity("Issuer certifcate does not exist !").build());
					}
					Transaction issuerTransaction = transactions.get(transactions.size() - 1);
					String encodedIssuerCert = objectMapper.readValue(issuerTransaction.getPayload(), String.class);
					X509Certificate issuerx509Cert = pemStream.pem2x509Cert(encodedIssuerCert);
					try {
						x509Certificate.verify(issuerx509Cert.getPublicKey());
					} catch (Exception exp) {
						logger.error("Invalid certifcate !");
						throw new WebApplicationException(
								Response.status(Status.PRECONDITION_FAILED).entity("Invalid certifcate !").build());
					}
				}

			} catch (Exception e) {
				logger.error("Invalid certificate. " + e.getMessage());
				throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED)
						.entity("Invalid certificate. " + e.getMessage()).build());
			}
		}

		//logger.info("Chain block exisits with ref: [" + ref + "]");
		Transaction lastTransaction = this.transactionService.getLastTransaction(ref);
		
		Hashing hashing = new Hashing(new CryptoPolicy());
		if (Objects.isNull(lastTransaction)) {
			//logger.info("Creating first transaction in the block !");
			Transaction newTransaction = new Transaction();
			newTransaction.setRef(ref);
			newTransaction.setDepth(1);
			//logger.info("Using hash of chainblock as previous hash !");
			Serializer serializer = new Serializer();

			byte[] serializedTH = serializer.toByteSerialize(transactionHeader);
			System.out.println("HEADER ");
			System.out.println(new String(serializedTH));
			byte[] hashSerializedTH = hashing.generateHash(serializedTH);
			String encodedHashSerializedTH = new String(Base64.getEncoder().encode(hashSerializedTH));
			//logger.info("Previous hash (Header) : " + encodedHashSerializedTH);
			newTransaction.setHashPrevBlock(encodedHashSerializedTH);
			newTransaction.setCreationTime(transaction.getCreationTime());
			newTransaction.setScope(transaction.getScope());
			newTransaction.setSender(transaction.getSender());
			newTransaction.setReceiver(transaction.getReceiver());
			newTransaction.setPayload(transaction.getPayload());
			newTransaction.setPayloadType(transaction.getPayloadType());
			
			newTransaction.setCreatorSignature(transaction.getCreatorSignature());
			newTransaction.setCreatorURL(transaction.getCreatorURL());
						
			newTransaction.setCryptoOperationsOnPayload(transaction.getCryptoOperationsOnPayload());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			VeidblockIO veidblockIO = new VeidblockIO();
			ResourceCO resourceCO = null;
			try {
				resourceCO = new ObjectMapper().readValue(veidblockIO.readResourceCO(), ResourceCO.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newTransaction.setSignedBy(resourceCO.getResourceId() + "");
			newTransaction.setSignerUrl(url);
			newTransaction.setSignedDate(simpleDateFormat.format(new Date()));

			// Extract Merkle hash from header
			//logger.info("Extracting hashMerkleRoot from Transaction header !");
			byte hashMerkleRootPrev[] = Base64.getDecoder().decode(transactionHeader.getHashMerkleRoot());

			//logger.info("Serializing transaction to create hash !");
			byte[] serializedTransaction = serializer.toByteSerialize(newTransaction);

			byte[] transactionHash = hashing.generateHash(serializedTransaction);

			//logger.info("Creating buffer for concatinating Merkle Root hash !");
			byte newhashMerkleRoot[] = new byte[hashMerkleRootPrev.length + serializedTH.length
					+ "|".getBytes().length];
			String delim = "|";
			byte delimByte[] = delim.getBytes();

			byte hashMerkleRootBuffer[] = new byte[hashMerkleRootPrev.length + delimByte.length
					+ transactionHash.length];

			System.arraycopy(hashMerkleRootPrev, 0, hashMerkleRootBuffer, 0, hashMerkleRootPrev.length);
			System.arraycopy(delimByte, 0, hashMerkleRootBuffer, hashMerkleRootPrev.length, delimByte.length);
			System.arraycopy(transactionHash, 0, hashMerkleRootBuffer, hashMerkleRootPrev.length + delimByte.length,
					transactionHash.length);

			byte[] newHashMerkleRoot = hashing.generateHash(hashMerkleRootBuffer);

			String encodedNewHashMerkleRoot = new String(Base64.getEncoder().encode(newHashMerkleRoot));
			transactionHeader.setHashMerkleRoot(encodedNewHashMerkleRoot);

			String signature = newTransactionCrypto(serializedTransaction);
			newTransaction.setSignature(signature);
			transactionHeaderService.updateHashMarkleRoot(transactionHeader.getRef(), encodedNewHashMerkleRoot);
			// Save new transaction
			transactionService.createTransaction(newTransaction);			
			
			manageSmartContract(transactionHeader, newTransaction);
			return newTransaction;

		} else {
			logger.debug("Creating transaction in the block !");
			Transaction newTransaction = new Transaction();
			newTransaction.setRef(ref);
			newTransaction.setDepth(lastTransaction.getDepth() + 1);

			Serializer serializer = new Serializer();
			//logger.info("Since trasaction in chain block already exists so using hash of last reansation : ");

			byte[] serializedT = serializer.toByteSerialize(lastTransaction);
			//logger.info("LAST TRANSACTION ");
			logger.info(new String(serializedT));
			byte[] hashSerializedT = hashing.generateHash(serializedT);

			String encodedHashSerializedT = new String(Base64.getEncoder().encode(hashSerializedT));
			//logger.info("Previous hash : " + encodedHashSerializedT);
			newTransaction.setHashPrevBlock(encodedHashSerializedT);
			newTransaction.setCreationTime(transaction.getCreationTime());
			newTransaction.setScope(transaction.getScope());
			newTransaction.setSender(transaction.getSender());
			newTransaction.setReceiver(transaction.getReceiver());
			newTransaction.setPayload(transaction.getPayload());
			newTransaction.setPayloadType(transaction.getPayloadType());
			
			newTransaction.setCreatorSignature(transaction.getCreatorSignature());
			newTransaction.setCreatorURL(transaction.getCreatorURL());
			
			
			newTransaction.setCryptoOperationsOnPayload(transaction.getCryptoOperationsOnPayload());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			ResourceCO resourceCO = null;
			try {
				resourceCO = new ObjectMapper().readValue(new VeidblockIO().readResourceCO(), ResourceCO.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newTransaction.setSignedBy(resourceCO.getResourceId() + "");
			newTransaction.setSignerUrl(url);
			newTransaction.setSignedDate(simpleDateFormat.format(new Date()));

			// Extract Merkle hash from header
			//logger.info("Extracting hashMerkleRoot from Transaction header !");
			byte hashMerkleRootPrev[] = Base64.getDecoder().decode(transactionHeader.getHashMerkleRoot());

			//logger.info("Serializing transaction to create hash !");
			byte[] serializedTransaction = serializer.toByteSerialize(newTransaction);

			byte[] transactionHash = hashing.generateHash(serializedTransaction);

			//logger.info("Creating buffer for concatinating Merkle Root hash !");
			byte newhashMerkleRoot[] = new byte[hashMerkleRootPrev.length + serializedT.length + "|".getBytes().length];
			String delim = "|";
			byte delimByte[] = delim.getBytes();

			byte hashMerkleRootBuffer[] = new byte[hashMerkleRootPrev.length + delimByte.length
					+ transactionHash.length];

			System.arraycopy(hashMerkleRootPrev, 0, hashMerkleRootBuffer, 0, hashMerkleRootPrev.length);
			System.arraycopy(delimByte, 0, hashMerkleRootBuffer, hashMerkleRootPrev.length, delimByte.length);
			System.arraycopy(transactionHash, 0, hashMerkleRootBuffer, hashMerkleRootPrev.length + delimByte.length,
					transactionHash.length);

			byte[] newHashMerkleRoot = hashing.generateHash(hashMerkleRootBuffer);

			String encodedNewHashMerkleRoot = new String(Base64.getEncoder().encode(newHashMerkleRoot));
			transactionHeader.setHashMerkleRoot(encodedNewHashMerkleRoot);

			String signature = newTransactionCrypto(serializedTransaction);
			newTransaction.setSignature(signature);
			// Save transactionHeader into database because its hashMerkleRoot
			// changed and share with others
			transactionHeaderService.updateHashMarkleRoot(transactionHeader.getRef(), encodedNewHashMerkleRoot);
			// Save new transaction
			transactionService.createTransaction(newTransaction);
			try {
				System.out.println(new ObjectMapper().writeValueAsString(newTransaction));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			manageSmartContract(transactionHeader, newTransaction);
			return newTransaction;

		}
	}

	private String newTransactionCrypto(byte[] serializedTransaction) throws VeidblockException {

		PKCS7Manager pkcs7Manager = new PKCS7Manager(new CryptoPolicy(),
				this.localCertificateManager.getCertificateSuite());

		logger.debug("Signing transaction block with peer's credentials !");
		// List<VeidSignerInfo> veidSignerInfoList = new
		// ArrayList<VeidSignerInfo>();
		// VeidSignerInfo veidSignerInfo = new VeidSignerInfo();
		// logger.debug("Fetching peer's certificate chain !");
		// veidSignerInfo.setChain(localCertificateManager.fetchCertificateChain());
		logger.debug("Fetching peer's private key !");
		// veidSignerInfo.setPrivateKey(localCertificateManager.getAuthServerPrivateKey());
		// veidSignerInfoList.add(veidSignerInfo);

		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());

		// String signedData =
		// pkcs7Manager.generatePKCS7Signed(veidSignerInfoList,
		// serializedTransaction);
		byte[] b64Signature = complexCryptoFunctions.generateSignature(
				localCertificateManager.getAuthServerPrivateKey(), serializedTransaction,
				ENCODING_DECODING_SCHEME.BASE64);
		String signedData = new String(b64Signature);// pkcs7Manager.generatePKCS7Signed(veidSignerInfoList,
														// );

		logger.debug("Successfully signed transaction block by the peer !");
		return signedData;
	}

	public boolean manageSmartContract(TransactionHeader transactionHeader) {
		//logger.info("Action : Relay header on Relay Chain to make header consistent !");
		try {
			SmartContractStruct contractStruct = new ObjectMapper().readValue(transactionHeader.getSmartcontract(),
					SmartContractStruct.class);

		} catch (IOException e2) {
			logger.error("Problems when constructung smart contract of ref = [" + transactionHeader.getRef() + "] !");
			throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED)
					.entity("Problems when constructung smart contract of ref = [" + transactionHeader.getRef() + "] !")
					.build());
		}
		return true;
	}

	public boolean manageSmartContract(TransactionHeader transactionHeader, Transaction newTransaction)
			throws VeidblockException {
		//logger.info("Action : Check this transaction is relayable !");
		try {
			SmartContract contractStruct = new ObjectMapper().readValue(transactionHeader.getSmartcontract(),
					SmartContract.class);
			enforceDateTimeRuleOnChainCode(contractStruct);
			if (contractStruct.getScope() == SmartContract.SCOPE.OPEN) {
				//logger.info("Action : Relay this transaction !");
				TransactionCO result = new Convertor().toTransactionCO(newTransaction);
				MessagingManager MessagingManager = new MessagingManager(transactionHeader.getSignedBy(), kafkaIP,
						kafkaPort);
				MessagingManager.sendMesasge(result);

			} else {
				//logger.info("Action : Local transaction, no need to relay !");
			}

		} catch (IOException e2) {
			logger.error("Problems when constructung smart contract of ref = [" + transactionHeader.getRef() + "] !");
			throw new WebApplicationException(Response.status(Status.PRECONDITION_FAILED)
					.entity("Problems when constructung smart contract of ref = [" + transactionHeader.getRef() + "] !")
					.build());
		}
		return true;
	}

	private boolean enforceDateTimeRuleOnChainCode(SmartContract smartContract) throws VeidblockException {

		Date currentDateTime = new Date();
		Date startDate = smartContract.getStart();
		Date endDateStr = smartContract.getEnd();

		if (!startDate.before(endDateStr)) {
			throw new VeidblockException("Invalid start date of Smart-contract !");
		}

		if (currentDateTime.before(endDateStr)) {
			return true;
		} else {
			throw new VeidblockException("Invalid closing date of Smart-contract !");
		}
	}

	private boolean enforceDateTimeRuleOnTransaction(SmartContract smartContract) throws VeidblockException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentDateTime = new Date();
		Date startDateStr = smartContract.getStart();
		Date endDateStr = smartContract.getEnd();
		boolean after = currentDateTime.after(startDateStr);
		boolean before = currentDateTime.before(endDateStr);

		if (after & before) {
			return true;
		} else if (after) {
			throw new VeidblockException("Smart-contract expired and chain block already closed !");
		} else if (before) {
			throw new VeidblockException(
					"Smart-contract will be activated on " + simpleDateFormat.format(endDateStr) + "!");
		}
		throw new VeidblockException("Problems when executing Smartcontract !");
	}

	public void enforceAccessRules(SmartContract smartContract, TransactionHeader transactionHeader){
		VeidblockIO veidblockIO = new VeidblockIO();
		ObjectMapper objectMapper = new ObjectMapper();
		ResourceCO resourceCO;
		try {
			resourceCO = objectMapper.readValue(veidblockIO.readResourceCO(), ResourceCO.class);
			if(smartContract.getScope().equals(SCOPE.LOCAL) && (!transactionHeader.getSignedBy().equals(""+resourceCO.getResourceId()))){
				throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
						.entity("Chainblock with ref = [" + transactionHeader.getRef() + "] does not belong to your domain !")
						.build());
			}
			
		} catch (IOException | VeidblockException e) {
			throw new WebApplicationException(
					Response.status(Status.INTERNAL_SERVER_ERROR).entity("Invalid Ledger object !").build());
		}
	}
	
	
}