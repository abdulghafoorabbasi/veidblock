package org.acreo.veidblock.token;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class SignatureJWT {

	private byte signature[] = null;
	private byte[] publickey = null;

	protected SignatureJWT() {

	}

	private SignatureJWT(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			SignatureJWT signatureJWT = objectMapper.readValue(json, SignatureJWT.class);
			copy(signatureJWT);
		} catch (IOException e) {
		}
	}
	
	public byte[] getPublickey() {
		return publickey;
	}

	public void setPublickey(byte[] publickey) {
		this.publickey = publickey;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte signature[]) {
		this.signature = signature;
	}

	public String toEncoded() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static SignatureJWT.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		SignatureJWT signatureJWT = new SignatureJWT();

		protected Builder() {
			// Hide default constructor
		}

		public SignatureJWT.Builder signature(byte[] signature) {
			if (null == signature) {
				return this;
			}
			signatureJWT.setSignature(signature);
			return this;
		}

		public SignatureJWT.Builder publickey(byte []publickey) {
			if (null == publickey) {
				return this;
			}
			signatureJWT.setPublickey(publickey);
			return this;
		}

		public SignatureJWT build() {

			return new SignatureJWT(signatureJWT.toEncoded());
		}

		public SignatureJWT build(String json) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				this.signatureJWT = objectMapper.readValue(json, SignatureJWT.class);
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			return build();
		}
	}

	public void copy(SignatureJWT signatureJWT) {
		this.setSignature(signatureJWT.getSignature());
		this.setPublickey(signatureJWT.getPublickey());
	}

	@Override
	public String toString() {
		return this.toEncoded();
	}

	public static void main(String arg[]) throws IOException {
		System.out.println(SignatureJWT.builder()
				.build(SignatureJWT.builder().signature("1111".getBytes()).build().toString()).toString());
	}
}
