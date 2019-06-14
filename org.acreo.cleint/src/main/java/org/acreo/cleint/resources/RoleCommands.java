package org.acreo.cleint.resources;

import java.util.Scanner;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.RoleCO;
import org.acreo.common.entities.RoleCOList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RoleCommands {

	public static RoleCOList listRoles(RestClient restClient, String verifier,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.get("/role", AuthenticationHeader.authHeader(verifier,authenticator));
			RoleCOList resourceCO = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCOList.class);
			return resourceCO;
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
		}
		return null;
	}
	
	public static RoleCO getRole(RestClient restClient, String verifier, String rid,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.get("/role/"+rid, AuthenticationHeader.authHeader(verifier,authenticator));
			RoleCO role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCO.class);
			return role;
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
		}
		return null;
	}	
	
	public static RoleCO addRole(RestClient restClient, String verifier,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			RoleCO roleCO = new RoleCO();
			roleCO.setRole(getInput("[Role]"));
			roleCO.setDescription(getInput("[Description]"));
			response = restClient.post("/role",roleCO, AuthenticationHeader.authHeader(verifier, authenticator));
			RoleCO role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCO.class);
			return role; 
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
			return null;
		}
		
	}	
	
	public static RoleCO delRole(RestClient restClient, String verifier, String roleId,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.delete("/role/"+roleId, AuthenticationHeader.authHeader(verifier, authenticator));
			RoleCO role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCO.class);
			return role;
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
			return null;
		}
	}	
	
	public static RoleCO assignRole(RestClient restClient, String verifier, String to, String be,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.post("/role/resourceid/"+to+"/role/"+be+"","", AuthenticationHeader.authHeader(verifier, authenticator));
			RoleCO role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCO.class);
			return role; 
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
			return null;
		}
		
	}	
	
	public static RoleCO  deleteRole(RestClient restClient, String verifier, String from, String be,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.delete("/role/resourceid/"+from+"/role/"+be+"",AuthenticationHeader.authHeader(verifier,authenticator));
			RoleCO role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCO.class);
			return role ;
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
		}
		return null;
	}	
	
	public static RoleCOList getRolesAssignedToResource(RestClient restClient, String verifier, String uid,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			response = restClient.get("/role/personid/"+uid, AuthenticationHeader.authHeader(verifier,authenticator));
			RoleCOList role = new ObjectMapper().readValue(response.getBody().toString(),
					RoleCOList.class);
			return role;
		} catch (Exception e1) {
			System.out.println("Error message : ");
			System.out.println(response.getBody().toString());
		}
		return null;
	}
	private static String getInput(String message){
		Scanner reader = new Scanner(System.in);
		System.out.print(message);
		return reader.nextLine(); 
	}
}
