package org.acreo.auth.veidblock;

import java.security.Principal;

import org.acreo.common.entities.AuthenticationType;

public class VeidblockUser implements Principal {
	private String name;
	private String token;
	private AuthenticationType authenticationHeader;

	public VeidblockUser(String name, AuthenticationType authenticationHeader) {
		this.name = name;
		this.authenticationHeader= authenticationHeader;
		
	}
	public String getName() {
		return name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public AuthenticationType getAuthenticationHeader() {
		return authenticationHeader;
	}
}