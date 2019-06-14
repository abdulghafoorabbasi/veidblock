package org.acreo.cleint.resources;

import java.util.Scanner;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.exceptions.VeidblockException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceRegistration {
	public static ResourceCO registerUser(ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Name] :"));
		resourceCO.setUsername(getInput("[Username] :"));
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
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
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.registerUser(authenticator); 
		} catch (VeidblockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static ResourceCO registerResource(ClientAuthenticator authenticator){

		ResourceCO resourceCO = new ResourceCO();
		resourceCO.setName(getInput("[Resource Name] :"));
		resourceCO.setUsername(getInput("[Resource unique name] :"));
		resourceCO.setPassword(getInput("[Password] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setEmail(getInput("[Valid email] :"));
		resourceCO.setBackupEmail(getInput("[Valid backup Email] :"));
		resourceCO.setPhone(getInput("[Owner's Phone] :"));
		resourceCO.setMobile(getInput("[Owner's Mobile] :"));
		resourceCO.setAddress1(getInput("[Address 1 ] :"));
		resourceCO.setAddress2(getInput("[Address 2 ] :"));
		resourceCO.setPostCode(getInput("[Post Code ] :"));
		resourceCO.setLocation(getInput("[Location (x,y) ] :"));
		
		resourceCO.setCity(getInput("[City] :"));
		resourceCO.setState(getInput("[State] :"));
		resourceCO.setCountry(getInput("[Country] :"));
		resourceCO.setUrl(getInput("[website] :"));
		
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getResource(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	
	private static String getInput(String message){
		System.out.print(message);
		return new Scanner(System.in).nextLine(); 
	}
	
	
	
	/*public String getPassword(){
		Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.exit(0);
        }

        console.printf("Testing password%n");
        char passwordArray[] = console.readPassword("Enter your secret password: ");
        console.printf("Password entered was: %s%n", new String(passwordArray));
        return new String(passwordArray);
        

	}*/

}
