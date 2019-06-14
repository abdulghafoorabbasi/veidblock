package org.acreo.auth.veidblock;

import org.acreo.common.entities.AuthenticationType;
import org.acreo.security.utils.SGen;

public class WwwAuthentication {

	private AuthenticationType authenticationType;

	public WwwAuthentication(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getStdWwwAuthenHdr(String realm) {
		switch (authenticationType) {
		case BASIC:
			return "Basic realm=\"" + realm + "\"";
		case DIGEST:
			return "Digest " + toDigestString(realm);
		case CLIENT_CERT:
			return "Client_cert realm=\"" + realm + "\"";
		case OTP:
			return "OTP " + toDigestString(realm);
		case BEARER:
			return "Bearer " + toDigestString(realm);
		case OTHER:
			break;
		}
		return null;
	}

	private String toDigestString(String realm) {
		SGen sGen = new SGen();
		String nonce = sGen.nextHexString(20), opaque = sGen.nextHexString(20);
		//auth,auth-int
		String digestStr = "realm=\"" + realm + "\", qop=\"auth,auth-int\", nonce=\"" + nonce + "\", opaque=\"" + opaque
				+ "\"";
		return digestStr;
	}

}
