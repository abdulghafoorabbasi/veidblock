package org.acreo.auth.context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.VeidblockUtils;

public class DigestVerifier {

	private boolean status = false;
	private final String AUTH_INIT = "auth-int";
	private final String AUTH = "auth";
	private DigestSecurityContext digestSecurityContext;
	private VeidblockUtils veidblockUtils = new VeidblockUtils();

	public DigestVerifier(DigestSecurityContext digestSecurityContext) throws VeidblockException {

		this.digestSecurityContext = digestSecurityContext;

		verifyDigest(digestSecurityContext, digestSecurityContext.getToken());
		if (!status) {
			throw new VeidblockException("Digest authentication failed !");
		}
	}

	public void verifyDigest(DigestSecurityContext digestSecurityContext, String password) {
		
		byte[] A1 = calculateA1(digestSecurityContext, password);
		String a1 = veidblockUtils.bytesToHex(A1);
		
		byte[] A2 = calculateA2(digestSecurityContext);
		String a2 = veidblockUtils.bytesToHex(A2);
		
		String caculatedResponse = calculateResponse(a1.toLowerCase().getBytes(), digestSecurityContext,
				a2.toLowerCase().getBytes());
		
		if (digestSecurityContext.getResponse().compareTo(caculatedResponse) == 0) {
			status = true;
		} else {
			status = false;
		}
	}

	private byte[] generateHash(byte[] value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hashedBytes = digest.digest(value);
			return hashedBytes;
		} catch (NoSuchAlgorithmException exp) {
			return null;
		}
	}

	private byte[] calculateA1(DigestSecurityContext digestSecurityContext, String password) {
		byte[] A1 = generateHash(
				(digestSecurityContext.getUserId() + ":" + digestSecurityContext.getRealm() + ":" + password)
						.getBytes());
		return A1;
	}
	private String parseQop(String t){
		String tt = t;
		String temp = "";
		if(t.contains(",")){
			String tokens[]=t.split(",");
			temp = tokens[0];
			if(!temp.contains("\"")){
				return t;
			}
			if(temp.startsWith("\"")){
				temp = temp.substring(1);
				return temp; 
			} else{
				return temp;
			}
		}
		
		if(!t.contains("\"")){
			return t;
		} else if(t.startsWith("\"") && t.endsWith("\"")){
			temp = t.substring(1, t.length()-1);			
			return temp;
		}else if(t.startsWith("\"")){
			temp = t.substring(1);
			return temp;
		} else {
			temp = t.substring(0, t.length()-1);
			return temp;
		}
	}
	private byte[] calculateA2(DigestSecurityContext digestSecurityContext) {

		String qop = parseQop(digestSecurityContext.getQop());
		if (qop .equals(AUTH)) {
			return generateHash(
					(digestSecurityContext.getHttpmethod() + ":" + digestSecurityContext.getUri()).getBytes());
		} else if (qop.equals(AUTH_INIT)) {
			String hashedXBody = "";
			if (digestSecurityContext.getBody() != null) {
				hashedXBody = veidblockUtils.bytesToHex(generateHash(digestSecurityContext.getBody().getBytes()));
				return veidblockUtils.bytesToHex(generateHash((digestSecurityContext.getHttpmethod() + ":"
						+ digestSecurityContext.getUri() + ":" + hashedXBody).getBytes())).getBytes();
			}
			return veidblockUtils.bytesToHex(generateHash(
					(digestSecurityContext.getHttpmethod() + ":" + digestSecurityContext.getUri())
							.getBytes()))
					.getBytes();
		} else {
			return generateHash(
					(digestSecurityContext.getHttpmethod() + ":" + digestSecurityContext.getUri()).getBytes());
		}
	}

	private String calculateResponse(byte[] A1, DigestSecurityContext digestSecurityContext, byte[] A2) {

		if (digestSecurityContext.getQop() == null || digestSecurityContext.getQop() == "") {
			if (digestSecurityContext.getNonce() != null) {
				throw new IllegalArgumentException("Invalid Nonce value '" + digestSecurityContext.getNonce() + "' !");
			}
			byte[] temp = veidblockUtils.concat(A1, ":".getBytes());
			temp = veidblockUtils.concat(temp, digestSecurityContext.getNonce().getBytes());
			temp = veidblockUtils.concat(temp, ":".getBytes());
			temp = veidblockUtils.concat(temp, A2);
			byte[] responseUpdated = generateHash(temp);
			return veidblockUtils.bytesToHex(responseUpdated);
		}

		String formReplayProtectionString = ":";
		if (digestSecurityContext.getNonce() != null) {
			formReplayProtectionString = formReplayProtectionString + digestSecurityContext.getNonce() + ":";
		} else {
			throw new IllegalArgumentException("Invalid Nonce value '" + digestSecurityContext.getNonce() + "' !");
		}

		if (digestSecurityContext.getNc() != null) {
			formReplayProtectionString = formReplayProtectionString + digestSecurityContext.getNc() + ":";
		} else {
			throw new IllegalArgumentException("Invalid Nc value '" + digestSecurityContext.getNc() + "' !");
		}

		if (digestSecurityContext.getCnonce() != null) {
			formReplayProtectionString = formReplayProtectionString + digestSecurityContext.getCnonce() + ":";
		} else {
			throw new IllegalArgumentException("Invalid Cnonce value '" + digestSecurityContext.getCnonce() + "' !");
		}

		if (digestSecurityContext.getQop() != null) {
			formReplayProtectionString = formReplayProtectionString + digestSecurityContext.getQop();
		} else {
			throw new IllegalArgumentException("Invalid Qop value '" + digestSecurityContext.getQop() + "' !");
		}
		if (formReplayProtectionString.length() > 0) {
			formReplayProtectionString = formReplayProtectionString + ":";
		} else {
			throw new IllegalArgumentException("Invalid value '" + digestSecurityContext.getCnonce() + "' !");
		}
		byte[] temp = veidblockUtils.concat(A1, (formReplayProtectionString).getBytes());
		byte[] finalForVer = veidblockUtils.concat(temp, A2);
		byte[] responseUpdated = generateHash(finalForVer);
		return veidblockUtils.bytesToHex(responseUpdated);
	}

	private HashMap<String, String> parseHeader(String headerString) {
		// seperte out the part of the string which tells you which Auth scheme
		// is it
		String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();
		HashMap<String, String> values = new HashMap<String, String>();
		String keyValueArray[] = headerStringWithoutScheme.split(",");
		for (String keyval : keyValueArray) {
			if (keyval.contains("=")) {
				String key = keyval.substring(0, keyval.indexOf("="));
				String value = keyval.substring(keyval.indexOf("=") + 1);
				values.put(key.trim(), value.replaceAll("\"", "").trim());
			}
		}
		return values;
	}
}