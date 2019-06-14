package org.acreo.veidblock.token;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class Header {
	private String alg;
	private String type; // content type = null, JWT, JWS or JWE

	protected Header() {

	}

	private Header(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Header header = objectMapper.readValue(json, Header.class);
			copy(header);
		} catch (IOException e) {
		}
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toEncoded() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String str = objectMapper.writeValueAsString(this);			
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static Header.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		Header header = new Header();

		protected Builder() {
			// Hide default constructor
		}

		public Header.Builder alg(String alg) {
			if (null == alg) {
				return this;
			}
			header.setAlg(alg);
			return this;
		}

		public Header.Builder type(String type) {
			if (null == type) {
				return this;
			}
			header.setType(type);
			return this;
		}

		public Header build() {

			return new Header(header.toEncoded());
		}

		public Header build(String json) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				this.header = objectMapper.readValue(json, Header.class);
			} catch (Exception exp) {
				exp.printStackTrace();
			}
			return build();
		}
	}

	public void copy(Header header) {
		this.setAlg(header.getAlg());
		this.setType(header.getType());
	}

	@Override
	public String toString() {
		return this.toEncoded();
	}

	public static void main(String arg[]) throws IOException {

	}

}
