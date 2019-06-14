package org.acreo.cleint.resources;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.GenericVeidblock;
import org.acreo.common.entities.GenericVeidblockList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LCCommands {

	
	public static GenericVeidblockList getAuthorizedBlocks(RestClient restClient, String verifier, String uid,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			
			response = restClient.get("/lc/authorized/"+uid, AuthenticationHeader.authHeader(verifier,authenticator));
			System.out.println(response.getBody().toString());
			response = new ObjectMapper().readValue(response.getBody().toString(),
					Representation.class);
			GenericVeidblockList genericVeidblockList = new ObjectMapper().readValue(response.getBody().toString(),
					GenericVeidblockList.class);
			for(GenericVeidblock genericVeidblock :genericVeidblockList){
				System.out.println("Authorized List : " + genericVeidblock.getSeqNo()+" = " + genericVeidblock.getCreationTime()+" \t"+genericVeidblock.getPayload() );	
			}
			return genericVeidblockList;
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}	
	public static boolean extractAuthorizedBlocks(RestClient restClient, String verifier, String uid,ClientAuthenticator authenticator) {
		Representation<?> response = null;
		try {
			
			GenericVeidblockList genericVeidblockList = getAuthorizedBlocks(restClient, verifier, uid,authenticator);
			response = restClient.put("/lc/extract/authorized/"+uid, genericVeidblockList.get(genericVeidblockList.size()-1), AuthenticationHeader.authHeader(verifier,authenticator));
			System.out.println(response.getBody().toString());
			response = new ObjectMapper().readValue(response.getBody().toString(),
					Representation.class);			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}	
	///
	
	
	
}
