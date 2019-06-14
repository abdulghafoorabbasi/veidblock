package org.acreo.cleint.resources;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.entities.ResourceCO;

public class GetResourcesCommands {

	public static ResourceCO getUser(String uniduenameORid, ClientAuthenticator authenticator) {
		
		ResourceCO resourceCO = new ResourceCO();
		try{
			resourceCO.setResourceId(Long.parseLong(uniduenameORid));
		}catch(Exception exp){
			resourceCO.setUsername(uniduenameORid);
		}
		
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getUser(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
		
		
		/*Representation<?> response = null;
		try {
			response = restClient.get("/resource/user/resname/"+username, AuthenticationHeader.authHeader(verifier));
			ResourceCO resourceCO = new ObjectMapper().readValue(response.getBody().toString(),
					ResourceCO.class);
			System.out.println("Registered Resource : " + resourceCO .getName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;*/
	}
	public static ResourceCO getResource(String resourceNameORid, ClientAuthenticator authenticator) {
		ResourceCO resourceCO = new ResourceCO();
		try{
			resourceCO.setResourceId(Long.parseLong(resourceNameORid));
		}catch(Exception exp){
			resourceCO.setUsername(resourceNameORid);
		}
		
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getResource(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
}
