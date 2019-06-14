package org.acreo.veidblock;

import java.security.cert.X509Certificate;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.veidblock.token.JWToken;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JWSToken extends JWToken {

	public JWSToken(JWToken jWToken) throws Exception {
		super(jWToken.toEncoded());
	}
	public JWSToken(String encodedJWToken) throws Exception {
		super(encodedJWToken);
	}
	
	public boolean verify(X509Certificate certificate) throws VeidblockException {
		JWTokenOperations jwTokenOperations = new JWTokenOperations();
		return jwTokenOperations.verifyJWToken((JWToken)this, certificate);		
	}
	
	public boolean verify() throws VeidblockException {
		JWTokenOperations jwTokenOperations = new JWTokenOperations();
		return jwTokenOperations.verifyJWToken((JWToken)this);		
	}
	
	public String toString(){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return "Problems when creating JSON based string of JWSToken !";
		} 
	}
	
	
	
}