package org.acreo.veidblock;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.utils.PEMStream;
import org.acreo.veidblock.token.JWToken;
import org.acreo.veidblock.token.SignatureJWT;

public class JWTokenOperations {

	public JWToken signJWToken(JWToken jwToken, PrivateKey privateKey, X509Certificate x509certificate,
			CryptoPolicy encryptionSuite) throws VeidblockException {
		try {
			ComplexCryptoFunctions complexCrypto = new ComplexCryptoFunctions(encryptionSuite);
			byte[] status = complexCrypto.generateSignature(privateKey, jwToken.toEncoded4Signature().getBytes(),
					ENCODING_DECODING_SCHEME.NONE);
			if (status != null) {
				PEMStream pemStream = new PEMStream();
				SignatureJWT signatureJWT = SignatureJWT.builder().signature(status)
						.publickey(x509certificate.getPublicKey().getEncoded()).build();
				jwToken.setSignatureJWT(signatureJWT);
				return jwToken;
			}
			throw new VeidblockException("Problems when creating JWS Token !");
		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
	}

	public boolean verifyJWToken(JWToken jwToken, X509Certificate x509certificate) throws VeidblockException {
		
		CryptoPolicy encryptionSuite = new CryptoPolicy();
		try {
			ComplexCryptoFunctions complexCrypto = new ComplexCryptoFunctions(encryptionSuite);
			boolean status = complexCrypto.verifySignature(x509certificate.getPublicKey(),
					jwToken.toEncoded4Signature().getBytes(), jwToken.signatureJWT.getSignature(),
					ENCODING_DECODING_SCHEME.NONE);
			return status;
		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
	}

	public boolean verifyJWToken(JWToken jwToken) throws VeidblockException {

		checkExpiryDate(jwToken.getPayload().getExp());
		CryptoPolicy encryptionSuite = new CryptoPolicy();

		try {
			ComplexCryptoFunctions complexCrypto = new ComplexCryptoFunctions(encryptionSuite);
			byte[] pubKey = jwToken.getSignatureJWT().getPublickey();
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKey));

			boolean status = complexCrypto.verifySignature(publicKey, jwToken.toEncoded4Signature().getBytes(),
					jwToken.signatureJWT.getSignature(), ENCODING_DECODING_SCHEME.NONE);
			return status;
		} catch (Exception exp) {
			throw new VeidblockException(exp);
		}
	}

	private boolean checkExpiryDate(String endDateStr) throws VeidblockException {
		
		Date theExpiryDate  = null;
		SimpleDateFormat sdf = null;
		
		try {
			sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			theExpiryDate = sdf.parse(endDateStr);
		} catch (Exception exp) {
			throw new VeidblockException("JWS Toekn expired !");
		}
		
		Date currentDateTime = new Date();
		
		boolean before = currentDateTime.before(theExpiryDate);
		
		if (before) {
			return true;			
		} else {
			throw new VeidblockException("Error validating access token: Session has expired on " + endDateStr
					+ ". The current time is " + sdf.format(currentDateTime + " !"));
		}		
	}
}
