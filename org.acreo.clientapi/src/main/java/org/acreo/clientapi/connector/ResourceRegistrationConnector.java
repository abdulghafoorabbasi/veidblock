package org.acreo.clientapi.connector;

import java.util.Objects;

import org.acreo.clientapi.utils.AuthenticationHeader;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.RoleCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceRegistrationConnector {

	private RestClient restClient = null;
	// ClientAuthenticator authenticator =
	// ClientAuthenticator.builder().application("cli").verifier(verifier).build();
	private ClientAuthenticator authenticator;

	public ResourceRegistrationConnector(RestClient restClient, ClientAuthenticator authenticator) {
		this.restClient = restClient;
		this.authenticator = authenticator;
	}

	public ResourceCO registerUserByAdmin(ResourceCO resourceCO, String verifier) throws VeidblockException {
		Representation response = null;
		try {
			response = restClient.post("/resource/user/admin", resourceCO,
					AuthenticationHeader.authHeader(verifier, authenticator));
			if (response.getCode() != 200) {
				throw new VeidblockException(
						"Error Code: " + response.getCode() + ", Message : " + response.getBody().toString());
			}
			ResourceCO resourceCO2 = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceCO2;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}

	public ResourceCO registerUser(ResourceCO resourceCO, String verifier) throws VeidblockException {
		Representation response = null;
		try {
			if (Objects.isNull(authenticator))
				response = restClient.post("/resource/user", resourceCO, null);
			else
				response = restClient.post("/resource/user", resourceCO,
						AuthenticationHeader.authHeader(verifier, authenticator));

			if (response.getCode() != 200) {
				throw new VeidblockException(
						"Error Code: " + response.getCode() + ", Message : " + response.getBody().toString());
			}
			ResourceCO resourceCO2 = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceCO2;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}

	public ResourceCO registerResource(ResourceCO resourceCO) throws VeidblockException {

		Representation response = null;
		try {
			response = restClient.post("/resource/server", resourceCO, null);
			if (response.getCode() != 200) {
				throw new VeidblockException(
						"Error Code: " + response.getCode() + ", Message : " + response.getBody().toString());
			}
			ResourceCO resourceCO2 = new ObjectMapper().readValue(response.getBody().toString(), ResourceCO.class);
			return resourceCO2;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}

	public RoleCO assignRole(long resourceId, String role, String verifier) throws VeidblockException {

		Representation response = null;
		try {
			response = restClient.post("/role/resourceid/" + resourceId + "/role/" + role + "", null,
					AuthenticationHeader.authHeader(verifier, authenticator));
			if (response.getCode() != 200) {
				throw new VeidblockException(
						"Error Code: " + response.getCode() + ", Message : " + response.getBody().toString());
			}
			RoleCO roleCO = new ObjectMapper().readValue(response.getBody().toString(), RoleCO.class);
			return roleCO;
		} catch (Exception e1) {
			throw new VeidblockException(e1);
		}
	}
}
