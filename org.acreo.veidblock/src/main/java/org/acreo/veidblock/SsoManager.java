package org.acreo.veidblock;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.veidblock.token.Header;
import org.acreo.veidblock.token.JWToken;
import org.acreo.veidblock.token.Payload;


public class SsoManager {
	
	private Header header = null;
	private Payload payload = null;
	
	public JWToken generateSsoToken(PrivateKey privateKey, X509Certificate certificate, JWToken jwToken) throws VeidblockException{
		JWTokenOperations jwTokenOperations = new JWTokenOperations();
		CryptoPolicy encryptionSuite = new CryptoPolicy();
		return jwTokenOperations.signJWToken(jwToken, privateKey, certificate, encryptionSuite);				
	}
	
	public JWToken generateSsoToken(PrivateKey privateKey, X509Certificate certificate) throws VeidblockException{
		
		JWTokenOperations jwTokenOperations = new JWTokenOperations();
		
		if(header == null || payload == null){
			throw new VeidblockException(new NullPointerException("Token header or payload is null !"));
		}
		
		JWToken jwTokenExt = new JWToken(header, payload);
		CryptoPolicy encryptionSuite = new CryptoPolicy();
		return jwTokenOperations.signJWToken(jwTokenExt, privateKey, certificate, encryptionSuite);
	}
	
	public Header getHeader() {
		return header;
	}
	
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public Payload getPayload() {
		return payload;
	}
	
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
}