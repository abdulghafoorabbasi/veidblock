package org.acreo.ledger.utils;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acreo.common.Representation;
import org.acreo.common.entities.AuthorizedVeidblock;
import org.acreo.common.entities.CommonEnums.VEIDBLOCK_STATUS;
import org.acreo.common.entities.GenericLedgerVeidblock;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockHeader;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.database.generic.GenericLedgerServiceWrapper;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.crypto.Encryption;
import org.acreo.security.pkcs.PKCS7Manager;
import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericVeidblockManager {

	private CertificateSuite ledgerCertificateSuite;
	private CryptoPolicy cryptoPolicy;
	private GenericLedgerServiceWrapper genericLedgerServiceWrapper;
private LocalCertificateManager localCertificateManager;
	public GenericVeidblockManager(GenericLedgerServiceWrapper genericLedgerServiceWrapper,
			
			CertificateSuite ledgerCertificateSuite, CryptoPolicy cryptoPolicy, LocalCertificateManager localCertificateManager) {
		this.ledgerCertificateSuite = ledgerCertificateSuite;
		this.cryptoPolicy = cryptoPolicy;
		this.genericLedgerServiceWrapper = genericLedgerServiceWrapper;
		this.localCertificateManager= localCertificateManager;
	}

	public Response add(long owner, String authorizedVeidblockSer, String url) throws VeidblockException {
		PKCS7Manager manager = new PKCS7Manager(this.cryptoPolicy, this.ledgerCertificateSuite);
		byte[] authorizedVeidblockEnveloped = manager.openPKCS7SignedData(authorizedVeidblockSer);

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			AuthorizedVeidblock authorizedVeidblock = objectMapper.readValue(authorizedVeidblockEnveloped,
					AuthorizedVeidblock.class);
			if (authorizedVeidblock.getAuthorizedBy() == owner) {
				GenericLedgerVeidblock genericLedgerVeidblock = createVediblock(authorizedVeidblock, url);
				if (genericLedgerVeidblock.getGenericVeidblockList().size() == 0) {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity("Problems when creating GenericVeidblock !").build();
				}

				GenericVeidblock genericVeidblock = genericLedgerVeidblock.getGenericVeidblockList()
						.get(genericLedgerVeidblock.getGenericVeidblockList().size() - 1);
				if (genericVeidblock == null) {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity("Problems when processing GenericVeidblock !").build();
				}
				// Send authorizedVeidblockSer to Endorser/Validator
				return Response.status(Status.OK).entity(objectMapper.writeValueAsString(genericVeidblock)).build();
			} else {
				return Response.status(Status.UNAUTHORIZED).entity("Invalid source !").build();
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new WebApplicationException(exp);
		}
	}

	private GenericLedgerVeidblock createVediblock(AuthorizedVeidblock authorizedVeidblock, String url) throws VeidblockException {

		GenericVeidblock genericVeIdblock = new GenericVeidblock();
		genericVeIdblock.setPayload(authorizedVeidblock.getPayload());
		genericVeIdblock.setSenderId(authorizedVeidblock.getAuthorizedBy());
		genericVeIdblock.setReceiverId(authorizedVeidblock.getAuthorizedTo());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		genericVeIdblock.setCreationTime(simpleDateFormat.format(new Date()));
		LedgerUtils utils = new LedgerUtils();
		GenericLedgerVeidblock veidblockLedgerDB = null;

		try {
			veidblockLedgerDB = genericLedgerServiceWrapper
					.getVedBlockLedgerWithLastEntry(genericVeIdblock.getSenderId());
		} catch (Exception e) {
			if (e.getMessage().equals("-1")) {
				veidblockLedgerDB = null;
			}
		}
		if (Objects.isNull(veidblockLedgerDB)) {
			GenericLedgerVeidblock veIdBlockLedgerDB = new GenericLedgerVeidblock();
			GenericVeidblockHeader veIdBlockHeader = new GenericVeidblockHeader();

			GenericVeidblockHeader prevVeIdBlockHeader = genericLedgerServiceWrapper.getPreviousHeader();
			if (prevVeIdBlockHeader == null) {
				prevVeIdBlockHeader = new GenericVeidblockHeader();
			}
			byte prevHashoOfHeader[] = utils.generateHashOfHeader(prevVeIdBlockHeader);
			if (prevHashoOfHeader == null) {

				System.out.println("This is Genius block in the ledger !");
				byte[] prevHashOfFirstHeader = new byte[32];
				veIdBlockHeader.setHashPrevBlock(Base64.getEncoder().encodeToString(prevHashOfFirstHeader));
				veIdBlockHeader.setVersion(LedgerVeidblock.version);
				long veid = genericLedgerServiceWrapper.getLastInsertedId();
				veid = veid + 1;
				veIdBlockHeader.setVeid(veid);
				byte bits[] = new byte[0];
				veIdBlockHeader.setCreationTime(simpleDateFormat.format(new Date()));

			} else {

				long veid = genericLedgerServiceWrapper.getLastInsertedId();
				veIdBlockHeader.setVeid(veid);
				System.out.println("Blocks already exisits so creating chain !");
				veIdBlockHeader.setHashPrevBlock(Base64.getEncoder().encodeToString(prevHashoOfHeader));
				veIdBlockHeader.setVersion(LedgerVeidblock.version);
				byte bits[] = new byte[0];
				veIdBlockHeader.setCreationTime(simpleDateFormat.format(new Date()));
			}

			// -----Handle identity------------------------
			byte prevHashoOfIdentity[] = new byte[32];
			genericVeIdblock.setPreviousHash(new LedgerUtils().toBase64(prevHashoOfIdentity));
			genericVeIdblock.setCreationTime(simpleDateFormat.format(new Date()));
			genericVeIdblock.setSeqNo(1);
			genericVeIdblock.setUrl(url);
			genericVeIdblock.setStatus(VEIDBLOCK_STATUS.LOCAL_VALIDATION.value());
			String validatorPublicKey = new String(Base64.getEncoder().encode(localCertificateManager.fetchCertificate().getPublicKey().getEncoded()));
			genericVeIdblock.setValidatorPublicKey(validatorPublicKey);
			
			byte[] hashedIdentity = utils.generateHashOfIdentity(genericVeIdblock);
			byte[] serializeIdentity = utils.toBytes(genericVeIdblock);
			ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(cryptoPolicy);
			String encodedValidatorSig = new String(complexCryptoFunctions.generateSignature(localCertificateManager.getAuthServerPrivateKey(), serializeIdentity, ENCODING_DECODING_SCHEME.BASE64));
			genericVeIdblock.setValidatorSignature(encodedValidatorSig);
			
			
			
			
			// Sign it;
			veIdBlockHeader.setSenderId(genericVeIdblock.getSenderId());
			veIdBlockHeader.setHashMerkleRoot(Base64.getEncoder().encodeToString(hashedIdentity));
			veIdBlockLedgerDB.setGenericVeidblockHeader(veIdBlockHeader);
			// Add url of the Domain Ledger
			// Add sign data of the Domain Ledger
			List<GenericVeidblock> identities = new ArrayList<GenericVeidblock>();
			identities.add(genericVeIdblock);
			veIdBlockLedgerDB.setGenericVeidblockList(identities);
			return genericLedgerServiceWrapper.createVeidBlockLedger(veIdBlockLedgerDB);

		} else {

			GenericVeidblock blockIdentityPrev = veidblockLedgerDB.getGenericVeidblockList()
					.get(veidblockLedgerDB.getGenericVeidblockList().size() - 1);
			long counter = blockIdentityPrev.getSeqNo();

			counter = counter + 1;

			genericVeIdblock.setSenderId(genericVeIdblock.getSenderId());
			genericVeIdblock.setSeqNo(counter);
			genericVeIdblock.setCreationTime(simpleDateFormat.format(new Date()));

			byte[] hashedPreviousIdentity = utils.generateHashOfIdentity(blockIdentityPrev);

			genericVeIdblock.setPreviousHash(utils.toBase64(hashedPreviousIdentity));

			byte prevHashoOfIdentity[] = utils.fromBase64(blockIdentityPrev.getPreviousHash());
			genericVeIdblock.setUrl(url);
			genericVeIdblock.setStatus(VEIDBLOCK_STATUS.LOCAL_VALIDATION.value());
			
			String validatorPublicKey = new String(Base64.getEncoder().encode(localCertificateManager.fetchCertificate().getPublicKey().getEncoded()));
			genericVeIdblock.setValidatorPublicKey(validatorPublicKey);
			
			byte[] hashedIdentity = utils.generateHashOfIdentity(genericVeIdblock);
			
			byte[] serializeIdentity = utils.toBytes(genericVeIdblock);
			ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(cryptoPolicy);
			String encodedValidatorSig = new String(complexCryptoFunctions.generateSignature(localCertificateManager.getAuthServerPrivateKey(), serializeIdentity, ENCODING_DECODING_SCHEME.BASE64));
			genericVeIdblock.setValidatorSignature(encodedValidatorSig);
				
			
			byte hashMerkleRoot[] = utils.generateConcatinatedHash(hashedIdentity, prevHashoOfIdentity);
			String encodedHashMerkleRoot = utils.toBase64(hashMerkleRoot);
			
			
			
			return genericLedgerServiceWrapper.addVeidBlockIdentity(genericVeIdblock, encodedHashMerkleRoot);
		}
	}
	public Representation<String> verifyPublicKey(String publicKeyEncoded){
		try {
			PublicKey publicKey = localCertificateManager.fetchCertificate().getPublicKey();
			String localPublicKey  = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			
			ObjectMapper objectMapper = new ObjectMapper();
			String temp = objectMapper.readValue(publicKeyEncoded, String.class);
			System.out.println(localPublicKey);
			System.out.println(temp);
			System.out.println(publicKeyEncoded);
			
			if (!temp.equals(localPublicKey)) {
				return new Representation<String>(HttpStatus.NOT_FOUND_404,"");
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			return new Representation<String>(HttpStatus.INTERNAL_SERVER_ERROR_500, "Internal server error !");
		}
		return new Representation<String>(HttpStatus.OK_200, "Successfully verified Public Key!");
		}
}