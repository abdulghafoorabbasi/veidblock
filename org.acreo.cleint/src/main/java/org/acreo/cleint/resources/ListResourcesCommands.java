package org.acreo.cleint.resources;

import org.acreo.cleint.RestClient;
import org.acreo.clientapi.Identity;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.ResourceCOList;

public class ListResourcesCommands {

	public static ResourceCOList listResources(RestClient restClient, String verifier, ClientAuthenticator authenticator) {

		ResourceCO resourceCO = new ResourceCO();
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getAllResources(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static ResourceCOList listUserResources(RestClient restClient, String verifier, ClientAuthenticator authenticator) {
		ResourceCO resourceCO = new ResourceCO();
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getAllUsers(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static ResourceCOList listServerResources(RestClient restClient, String verifier, ClientAuthenticator authenticator) {
		ResourceCO resourceCO = new ResourceCO();
		try {
			Identity registration = Identity.builder().resource(resourceCO).build();
			return registration.getAllServers(authenticator);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
}
