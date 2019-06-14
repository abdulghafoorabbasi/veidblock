package org.acreo.veidblock.token;

import java.util.Base64;
import java.util.Date;

public class JWToken {

	public Header header;
	public Payload payload;
	public SignatureJWT signatureJWT;

	public JWToken(String encodedJWToken) throws Exception {
		fromEncoded(encodedJWToken);
	}

	public JWToken(Header header, Payload payload) {
		this.header = header;
		this.payload = payload;		
	}

	public Header getHeader() {
		return header;
	}

	public Payload getPayload() {
		return payload;
	}

	public SignatureJWT getSignatureJWT() {
		return signatureJWT;
	}

	public void setSignatureJWT(SignatureJWT signatureJWT) {
		this.signatureJWT = signatureJWT;
	}

	private void fromEncoded(String encodedJWToken) throws Exception {
		if (encodedJWToken == null) {
			throw new NullPointerException("JWToken is null !");
		}
		String tokens[] = encodedJWToken.split("\\.");
		
		if (tokens.length < 3) {
			throw new NullPointerException("Invalid token !");
		}
		try {
			if (tokens[0] != null) {
				String head = tokens[0];
				head = head.replaceAll("\\s+","");
				String json = new String(Base64.getDecoder().decode(head));
				Header header = Header.builder().build(json);
				this.header = header;
			}
			if (tokens[1] != null) {
				String pay = tokens[1];
				pay = pay.replaceAll("\\s+","");
				String json = new String(Base64.getDecoder().decode(pay));
				Payload payload = Payload.builder().build(json);
				this.payload = payload;
			}
			if (tokens[2] != null) {
				String signature = tokens[2];
				signature = signature.replaceAll("\\s+","");
				String json = new String(Base64.getDecoder().decode(signature));
				SignatureJWT signatureJWT = SignatureJWT.builder().build(json);
				this.setSignatureJWT(signatureJWT);
			}
		} catch (Exception exp) {
			throw new Exception("Invalid attributes of token !");
		}
	}

	public String toEncoded4Signature() {
		return Base64.getEncoder().encodeToString(header.toEncoded().getBytes()) + "."
				+ Base64.getEncoder().encodeToString(payload.toEncoded().getBytes());
	}

	public String toEncoded() {
		return Base64.getEncoder().encodeToString(header.toEncoded().getBytes()) + "."
				+ Base64.getEncoder().encodeToString(payload.toEncoded().getBytes()) + "."
				+ Base64.getEncoder().encodeToString(signatureJWT.toEncoded().getBytes());
	}
	public String toString() {
		return "Header: " + header.toEncoded() + "\nPayload: " + payload.toEncoded() + "\nSignature: "
				+ signatureJWT.toEncoded();
	}	
}