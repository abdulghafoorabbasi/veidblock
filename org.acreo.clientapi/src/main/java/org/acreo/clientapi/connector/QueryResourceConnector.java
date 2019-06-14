package org.acreo.clientapi.connector;

import org.acreo.clientapi.utils.AuthenticationHeader;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryResourceConnector {

	private RestClient restClient = null;
	ClientAuthenticator authenticator = null;
	public QueryResourceConnector(RestClient restClient,ClientAuthenticator authenticator) {
		this.restClient = restClient;
		this.authenticator=authenticator;
	}

	public ResourceCO getUser(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {

			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.get("/resource/user/resname/" + resourceCO.getUsername(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.get("/resource/user/id/" + resourceCO.getResourceId(),
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

	public ResourceCO getResource(String verifier, ResourceCO resourceCO) throws VeidblockException{
		Representation<?> response = null;
		try {

			if (resourceCO.getResourceId() == 0) {

				if(resourceCO.getUsername() == null){
					throw new VeidblockException("Please specify either usename or resource id !");
				}
				response = restClient.get("/resource/server/resname/" + resourceCO.getUsername(),
						AuthenticationHeader.authHeader(verifier, authenticator));
			} else {
				response = restClient.get("/resource/server/id/" + resourceCO.getResourceId(),
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
	
	public ResourceCOList listAllResources(String verifier) throws VeidblockException{
		Representation<?> response = null;
		try {
			response = restClient.get("/resource", AuthenticationHeader.authHeader(verifier, authenticator));
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			ResourceCOList resourceCOList = new ObjectMapper().readValue(response.getBody().toString(),
					ResourceCOList.class);
			return resourceCOList;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
		
	}
	public ResourceCOList listUsers(String verifier) throws VeidblockException{
		Representation<?> response = null;
		try {
			response = restClient.get("/resource/user", AuthenticationHeader.authHeader(verifier, authenticator));
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			ResourceCOList resourceCOList = new ObjectMapper().readValue(response.getBody().toString(),
					ResourceCOList.class);
			return resourceCOList;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
		
	}

	public ResourceCOList listServers(String verifier) throws VeidblockException{
		Representation<?> response = null;
		try {
			response = restClient.get("/resource/server", AuthenticationHeader.authHeader(verifier, authenticator));
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			ResourceCOList resourceCOList = new ObjectMapper().readValue(response.getBody().toString(),
					ResourceCOList.class);
			return resourceCOList;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}
}
