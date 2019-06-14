package org.acreo.auth.twofactor;

public class SignedPdc {
	String signature;
	String uidAndPdc;
	String clientCertificate;
	String options;
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUidAndPdc() {
		return uidAndPdc;
	}
	public void setUidAndPdc(String uidAndPdc) {
		this.uidAndPdc = uidAndPdc;
	}
	public String getClientCertificate() {
		return clientCertificate;
	}
	public void setClientCertificate(String clientCertificate) {
		this.clientCertificate = clientCertificate;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}	
}
