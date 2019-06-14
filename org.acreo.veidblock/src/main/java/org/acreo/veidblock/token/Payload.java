package org.acreo.veidblock.token;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
public class Payload {
	private String iss;// URL of our idms
	private String sub; // Subject
	private String ver;// URL of our certificate for verification
	private String exp; // Exp time
	private String scp; // scope separated by comma
	private String refreshToken;
	private String jti; // Unique identifier to identify token
	private String pub; // Unique identifier to identify token

	protected Payload() {

	}

	private Payload(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Payload payload = objectMapper.readValue(json, Payload.class);
			copy(payload);
		} catch (IOException e) {
		}
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getScp() {
		return scp;
	}

	public void setScp(String scp) {
		this.scp = scp;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	public String getPub() {
		return pub;
	}

	public void setPub(String pub) {
		this.pub = pub;
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

	public static Payload.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		Payload payload = new Payload();

		protected Builder() {
			// Hide default constructor
		}

		public Payload.Builder iss(String iss) {
			if (null == iss) {
				return this;
			}
			payload.setIss(iss);
			return this;
		}

		public Payload.Builder sub(String sub) {
			if (null == sub) {
				return this;
			}
			payload.setSub(sub);
			return this;
		}

		public Payload.Builder ver(String ver) {
			if (null == ver) {
				return this;
			}
			payload.setVer(ver);
			return this;
		}

		public Payload.Builder exp(String exp) {
			if (exp == null) {
				return this;
			}

			payload.setExp(exp);
			return this;
		}

		public Payload.Builder scp(String scp) {
			if (null == scp) {
				return this;
			}
			payload.setScp(scp);
			return this;
		}
		public Payload.Builder pub(String pub) {
			if (null == pub) {
				return this;
			}
			payload.setPub(pub);
			return this;
		}

		public Payload.Builder refreshToken(String refreshToken) {
			if (null == refreshToken) {
				return this;
			}
			payload.setRefreshToken(refreshToken);
			return this;
		}

		public Payload.Builder jti(String jti) {
			if (null == jti) {
				return this;
			}
			payload.setJti(jti);
			return this;
		}

		public Payload build() {

			return new Payload(payload.toEncoded());
		}

		public Payload build(String json) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				this.payload = objectMapper.readValue(json, Payload.class);
			} catch (IOException exp) {
				exp.printStackTrace();
			}
			return build();
		}
	}

	public void copy(Payload payload) {
		setExp(payload.getExp());
		setIss(payload.getIss());
		setJti(payload.getJti());
		setRefreshToken(payload.getRefreshToken());
		setScp(payload.getScp());
		setSub(payload.getSub());
		setVer(payload.getVer());
		setPub(payload.getPub());
	}

	@Override
	public String toString() {
		return this.toEncoded();
	}
}
