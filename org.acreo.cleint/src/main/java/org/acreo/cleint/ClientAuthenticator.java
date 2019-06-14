/*package org.acreo.cleint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.acreo.common.Representation;
import org.apache.http.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientAuthenticator {

	public enum AuthType {
		BASIC("basic"), DIGEST("digest"), ABASIC("abasic"), ADIGEST("adigest"), TOKEN("bearer"), CLIENT_CERT(
				"client_cert"), OTP("otp"), OTHER("other");

		String type;

		AuthType(String type) {
			this.type = type;
		}

		public String value() {
			return type;
		}
	};

	private String application = null;
	private String uniqueIdentifier = null;
	private String password = null;
	private AuthType authType = null;
	private String token = null;
	private String verfier = null;

	private ClientAuthenticator() {

	}

	private ClientAuthenticator(String uniqueIdentifier, String password, String token, String verfier,
			String application) {
		this.uniqueIdentifier = uniqueIdentifier;
		this.password = password;
		this.token = token;
		this.verfier = verfier;
		this.application = application;
	}

	public static ClientAuthenticator.Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String uniqueIdentifier;
		private String password;
		private AuthType authType;
		private String token;
		private String verifier;
		private String application;

		protected Builder() {
			// Hide default constructor
		}

		public ClientAuthenticator.Builder uniqueIdentifier(String uniqueIdentifier) {
			if (null == uniqueIdentifier) {
				return this;
			}
			this.uniqueIdentifier = uniqueIdentifier;
			return this;
		}

		public ClientAuthenticator.Builder password(String password) {
			if (null == password) {
				return this;
			}
			this.password = password;
			return this;
		}

		public ClientAuthenticator.Builder token(String token) {
			if (null == token) {
				return this;
			}
			this.token = token;
			return this;
		}

		public ClientAuthenticator.Builder verifier(String verifier) {
			if (null == verifier) {
				return this;
			}
			this.verifier = verifier;
			return this;
		}

		public ClientAuthenticator.Builder application(String application) {
			if (null == application) {
				return this;
			}
			this.application = application;
			return this;
		}

		public ClientAuthenticator build() throws IOException {
			return new ClientAuthenticator(uniqueIdentifier, password, token, verifier, application);
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public boolean autheticate() {
		if (application == null) {
			throw new NullPointerException("Name of applcaition should not be null !");
		}
		try {
			loginClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Representation<String> loginClient() throws Exception {
		verfier=verfier+"/verify";
		System.out.println("Endpoint: [Method: " + "POST" + "] " + verfier );
		ObjectMapper objectMapper = new ObjectMapper();
		MultivaluedMap<String, Object> headers;
		String storedToken = retrieveToken();
		this.token = storedToken;
		Response response = null;
		if (null == verfier) {
			throw new NullPointerException("Base URL of Endpoint is null !");
		}
		try {
			Client client = ClientBuilder.newClient();
			// headers.get()
			WebTarget target = client.target(verfier );
			if (storedToken != null) {
				System.out.println(storedToken);
				authType = AuthType.TOKEN;
				response = target.request(MediaType.APPLICATION_JSON_TYPE)
						.header(HttpHeaders.AUTHORIZATION, storedToken).post(Entity.entity("dummy", MediaType.APPLICATION_JSON_TYPE));
				
				if (response.getStatus() == 401) {
					String body = response.readEntity(String.class);
					File f = new File(getTokenPath());
					f.delete();
					throw new AuthenticationException(body);				
				} 
				Representation<String> rep = new Representation(200, storedToken);
				return rep;
				
			} else {
				
				if(uniqueIdentifier == null || password == null){
					Scanner reader = new Scanner(System.in);  // Reading from System.in
					System.out.print("Username/Unique Id : ");
					uniqueIdentifier = reader.nextLine();
					System.out.print("Password: ");
					password = reader.nextLine(); 
					//once finished
					reader.close(); 
				}
				
				String authCredentials = performAuthentication(AuthType.BASIC.value());
				if (authCredentials == null) {
					throw new AuthenticationException("HTTP/1.1 401 Unauthorized");
				}
				client = ClientBuilder.newClient();
				target = client.target(verfier);
				response = target.request(MediaType.APPLICATION_JSON_TYPE)
						.header(HttpHeaders.AUTHORIZATION, authCredentials).post(Entity.entity("dummy", MediaType.APPLICATION_JSON_TYPE));
			}
			headers = response.getHeaders();
			
			List<Object> authHeader = headers.get(HttpHeaders.AUTHORIZATION);
			if (null != authHeader && authHeader.size() > 0) {
				String token = authHeader.get(0).toString();
				String tokenHeader = token.substring(0, 6);
				if (tokenHeader.equalsIgnoreCase(AuthType.TOKEN.value())) {
					this.token = token;
					authType = AuthType.TOKEN;
					saveToken(token);
					Representation<String> rep = new Representation(200, token);
					return rep;
				}

			} else {
				authHeader = headers.get(HttpHeaders.WWW_AUTHENTICATE);
				if (null != authHeader && authHeader.size() > 0) {
					String type = authHeader.get(0).toString();
					String authCredentials = performAuthentication(type);
					if (authCredentials == null) {
						throw new AuthenticationException("HTTP/1.1 401 Unauthorized");
					}
					client = ClientBuilder.newClient();
					target = client.target(verfier);
					response = target.request(MediaType.APPLICATION_JSON_TYPE)
							.header(HttpHeaders.AUTHORIZATION, authCredentials).post(Entity.entity("dummy", MediaType.APPLICATION_JSON_TYPE));
				} else {
					throw new AuthenticationException("HTTP/1.1 401 Unauthorized");
				}
				headers = response.getHeaders();
				authHeader = headers.get(HttpHeaders.AUTHORIZATION);
				if (null != authHeader && authHeader.size() > 0) {
					String token = authHeader.get(0).toString();
					String tokenHeader = token.substring(0, 6);
					if (tokenHeader.equalsIgnoreCase(AuthType.TOKEN.value())) {
						this.token = token;
						authType = AuthType.TOKEN;
						saveToken(token);
						Representation<String> rep = new Representation(200, token);
						return rep;
					}
				}
			}
			String body = response.readEntity(String.class);
			throw new AuthenticationException(body);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new AuthenticationException("HTTP/1.1 401 Unauthorized\n" + e1.getMessage());
		}
	}

	
	 * public MultivaluedMap<String, Object> getHeasers() { return headers; }
	 

	private String performAuthentication(String headerValOrg) {
		String headerVal = "";
		if (headerValOrg.contains(" ")) {
			String tokens[] = headerValOrg.split(" ");
			headerVal = tokens[0];
		} else {
			headerVal = headerValOrg;
		}

		if (headerVal.toLowerCase().startsWith(AuthType.BASIC.value())) {
			authType = AuthType.BASIC;
			String credentials = headerVal + " "
					+ Base64.getMimeEncoder().encodeToString((uniqueIdentifier + ":" + password).getBytes());
			return credentials;
		}
		if (headerVal.toLowerCase().startsWith(AuthType.TOKEN.value())) {
			authType = AuthType.TOKEN;
			return AuthType.TOKEN.value() + " " + token;
		}
		return null;
	}

	private boolean saveToken(String token) throws Exception {
		File tokenFile = new File(application + "_token.json");
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(tokenFile);
			bw = new BufferedWriter(fw);
			bw.write(token);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				throw ex;
			}
		}
		return true;
	}

	public String retrieveToken() {
		File tokenFile = new File(application + "_token.json");
		try {
			if (!tokenFile.exists()) {
				throw new Exception("Token File does not exists !");
			}
			FileInputStream fis = new FileInputStream(tokenFile);
			byte[] data = new byte[(int) tokenFile.length()];
			fis.read(data);
			fis.close();
			return new String(data, "UTF-8");
		} catch (Exception exp) {
			return null;
		}
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public String getPassword() {
		return password;
	}

	public AuthType getAuthType() {
		return authType;
	}

	public String getToken() {
		return token;
	}

	public String getVerfierURL() {
		return verfier;
	}
	public String getTokenPath(){
		File tokenFile = new File(application + "_token.json");
		if(tokenFile.exists()){
			return tokenFile.getAbsolutePath();
		}
		System.err.println("Access token does not exisits at ["+tokenFile.getAbsolutePath() +"]");
		return null;
	}	
}
*/