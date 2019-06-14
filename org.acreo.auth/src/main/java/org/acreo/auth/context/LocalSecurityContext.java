package org.acreo.auth.context;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public class LocalSecurityContext implements SecurityContext {

	private SecurityContext currentSecurityContext;
	private String name;
	private boolean isRole;
	private String scheme = null;
	
	public LocalSecurityContext(){		
		
	}
	
	public LocalSecurityContext(final SecurityContext currentSecurityContext, String name, boolean isRole, String scheme/*, String authHeader*/) {
		this.currentSecurityContext = currentSecurityContext;
		this.scheme = scheme ;
		this.name = name;
		this.isRole= isRole;
		//this.authHeader= authHeader ;
	}

	public Principal getUserPrincipal() {
		return new Principal() {
			public String getName() {
				return name;
			}
		};
	}

	public boolean isUserInRole(String role) {
		return isRole;
	}

	public boolean isSecure() {
		return currentSecurityContext.isSecure();
	}

	public String getAuthenticationScheme() {
		return scheme;
	}
}