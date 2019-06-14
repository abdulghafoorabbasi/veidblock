package org.acreo.clientapi.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpHeaders;

public class AuthenticationHeader {

	public static HashMap<String, String> authHeader(String verifier, ClientAuthenticator authenticator) throws Exception {
		String path = authenticator.getTokenPath();
		if (path != null) {
			File f = new File(path);
			if (f.exists()) {
				String token = authenticator.retrieveToken();
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put(HttpHeaders.AUTHORIZATION, token);
				return headers;
			}else if(authenticator.autheticate()) {
				String token = authenticator.getToken();
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put(HttpHeaders.AUTHORIZATION, token);
				return headers;
			} else {
				throw new Exception("Problems when authenticating user !");
			}
		} else {
			if (authenticator.autheticate()) {
				String token = authenticator.getToken();
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put(HttpHeaders.AUTHORIZATION, token);
				return headers;
			} else {
				throw new Exception("Problems when authenticating user !");
			}
		}
	}
	/*public static void deleteAccesToken(ClientAuthenticator authenticator) throws IOException{
		new File(authenticator.getTokenPath()).delete();

	}*/
}
