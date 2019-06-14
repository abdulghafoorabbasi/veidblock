package org.acreo.clientapi.connector;

import org.acreo.clientapi.utils.AuthenticationHeader;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DeleteUpdateIdentityConnector {

	private RestClient restClient = null;
	//ClientAuthenticator authenticator = ClientAuthenticator.builder().application("cli").verifier(verifier).build();
	private ClientAuthenticator authenticator = null;
	public DeleteUpdateIdentityConnector(RestClient restClient, ClientAuthenticator authenticator) {
		this.authenticator = authenticator;
		this.restClient = restClient;
	}

	public ResourceCO updateUser(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {
			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.put("/resource/user/resname/"+ resourceCO.getUsername(),resourceCO,
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.put("/resource/user/id/" + resourceCO.getResourceId(),resourceCO,
						AuthenticationHeader.authHeader(verifier, authenticator));
			}

			if(response .getCode() == 200){
			ResourceCO resourceRet = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceRet;
			}else{
				if(response.getCode() == 401){
					throw new VeidblockException("Unauthorized access. "+response.getBody());
				}
				throw new VeidblockException(response.getBody().toString());
			}
			 
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}

	public ResourceCO updateResource(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {

			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.put("/resource/server/resname/" + resourceCO.getUsername(),resourceCO,
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.put("/resource/server/id/" + resourceCO.getResourceId(),resourceCO,
						AuthenticationHeader.authHeader(verifier, authenticator));
			}

			ResourceCO resourceRet = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceRet;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}
	
	public ResourceCO deleteUser(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {

			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.delete("/resource/user/resname/" + resourceCO.getUsername(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.delete("/resource/user/id/" + resourceCO.getResourceId(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			}
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			ResourceCO resourceRet = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceRet;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
		
	}
	public ResourceCO deleteResource(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {

			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.delete("/resource/server/resname/" + resourceCO.getUsername(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.delete("/resource/server/id/" + resourceCO.getResourceId(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			}
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			ResourceCO resourceRet = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceRet;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}		
	}	
}
