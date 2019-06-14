package org.acreo.cleint.resources;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.AuthorizedDataCO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorizeCommand {
	
	public static boolean authorizeResource(RestClient restClient, String sender, String recipient, String verifier,ClientAuthenticator authenticator){

		AuthorizedDataCO authorizedDataCO = new AuthorizedDataCO();
		authorizedDataCO.setScp("auth-key-pkcs7");
		
		authorizedDataCO.setSecureSharedData("drowssap");
		//authz -from 761204906 -to 761204906 -d -f /home/aghafoor/auth.json
		Representation response = null;
		try {
			System.out.println(verifier+"/verify/authorize/"+sender+"/"+recipient+"");
			response = restClient.put("/verify/authorize/"+sender+"/"+recipient+"",  authorizedDataCO, AuthenticationHeader.authHeader(verifier,authenticator));
			System.out.println(response.getBody().toString());
			response =  new ObjectMapper().readValue(response.getBody().toString(), Representation.class);
			System.out.println("Authorized my Identity : "+response.getBody());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}
}
