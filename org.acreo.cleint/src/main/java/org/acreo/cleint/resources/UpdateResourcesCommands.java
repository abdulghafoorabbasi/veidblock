package org.acreo.cleint.resources;

import java.util.Scanner;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.entities.ResourceCO;

public class UpdateResourcesCommands {

	public static ResourceCO updateUserByUsername(RestClient restClient, String verifier, String username, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Name] :"));		
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setBackupEmail(getInput("[Valid backup Email] :"));
		resourceCO.setPhone(getInput("[Phone] :"));
		resourceCO.setMobile(getInput("[Mobile] :"));
		resourceCO.setAddress1(getInput("[Address 1 ] :"));
		resourceCO.setAddress2(getInput("[Address 2 ] :"));
		resourceCO.setPostCode(getInput("[Post Code ] :"));
		resourceCO.setLocation(getInput("[Location (x,y) ] :"));
		
		resourceCO.setCity(getInput("[City] :"));
		resourceCO.setState(getInput("[State] :"));
		resourceCO.setCountry(getInput("[Country] :"));
		resourceCO.setUrl(getInput("[website] :"));
		resourceCO.setUsername(username);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.updateUser(verifier, authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResourceCO updateUserById(RestClient restClient, String verifier, long uid, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Name] :"));		
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setBackupEmail(getInput("[Valid backup Email] :"));
		resourceCO.setPhone(getInput("[Phone] :"));
		resourceCO.setMobile(getInput("[Mobile] :"));
		resourceCO.setAddress1(getInput("[Address 1 ] :"));
		resourceCO.setAddress2(getInput("[Address 2 ] :"));
		resourceCO.setPostCode(getInput("[Post Code ] :"));
		resourceCO.setLocation(getInput("[Location (x,y) ] :"));
		
		resourceCO.setCity(getInput("[City] :"));
		resourceCO.setState(getInput("[State] :"));
		resourceCO.setCountry(getInput("[Country] :"));
		resourceCO.setUrl(getInput("[website] :"));
		resourceCO.setResourceId(uid);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.updateUser(verifier,authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static ResourceCO updateResourceByName(RestClient restClient, String verifier, String username, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Name] :"));		
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setBackupEmail(getInput("[Valid backup Email] :"));
		resourceCO.setPhone(getInput("[Phone] :"));
		resourceCO.setMobile(getInput("[Mobile] :"));
		resourceCO.setAddress1(getInput("[Address 1 ] :"));
		resourceCO.setAddress2(getInput("[Address 2 ] :"));
		resourceCO.setPostCode(getInput("[Post Code ] :"));
		resourceCO.setLocation(getInput("[Location (x,y) ] :"));
		
		resourceCO.setCity(getInput("[City] :"));
		resourceCO.setState(getInput("[State] :"));
		resourceCO.setCountry(getInput("[Country] :"));
		resourceCO.setUrl(getInput("[website] :"));
		resourceCO.setUrl(getInput("[website] :"));
		resourceCO.setUsername(username);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.updateServer(verifier,authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
		
	}
	
	public static ResourceCO updateResourceById(RestClient restClient, String verifier, long uid, ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Name] :"));		
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setBackupEmail(getInput("[Valid backup Email] :"));
		resourceCO.setPhone(getInput("[Phone] :"));
		resourceCO.setMobile(getInput("[Mobile] :"));
		resourceCO.setAddress1(getInput("[Address 1 ] :"));
		resourceCO.setAddress2(getInput("[Address 2 ] :"));
		resourceCO.setPostCode(getInput("[Post Code ] :"));
		resourceCO.setLocation(getInput("[Location (x,y) ] :"));
		
		resourceCO.setCity(getInput("[City] :"));
		resourceCO.setState(getInput("[State] :"));
		resourceCO.setCountry(getInput("[Country] :"));
		resourceCO.setUrl(getInput("[website] :"));
		resourceCO.setResourceId(uid);
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.updateServer(verifier,authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	private static String getInput(String message){
		Scanner reader = new Scanner(System.in);
		System.out.print(message);
		String temp = reader.nextLine(); 
		return temp.length()== 0 ? null : temp;
	}	
	
}
