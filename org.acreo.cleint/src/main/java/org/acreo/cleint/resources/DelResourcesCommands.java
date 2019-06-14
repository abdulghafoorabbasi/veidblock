package org.acreo.cleint.resources;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DelResourcesCommands {

	public static ResourceCO deleteUserByUsername(RestClient restClient, String verifier, String username, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setUsername(username);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.deleteUser(verifier,authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResourceCO deleteUserById(RestClient restClient, String verifier, long userid, ClientAuthenticator authenticator){

		
		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(userid);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.deleteUser(verifier, authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResourceCO deleteResourceByName(RestClient restClient, String verifier, String username, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setUsername(username);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.deleteServer(verifier, authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResourceCO deleteResourceById(RestClient restClient, String verifier, long serverId, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setResourceId(serverId);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.deleteServer(verifier, authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	/*private static String getInput(String message){
		Scanner reader = new Scanner(System.in);
		System.out.print(message);
		String temp = reader.nextLine(); 
		return temp.length()== 0 ? null : temp;
	}	*/
	
}
