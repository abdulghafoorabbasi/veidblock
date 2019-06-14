package org.acreo.auth;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.acreo.common.Representation;
import org.eclipse.jetty.http.HttpStatus;

public class Authentication {
	@Context
	protected HttpServletResponse response;
	@Context
	protected HttpHeaders headers;

	public Authentication() {

	}

	public Representation<String> createHttpResponse() {
		//System.out.println("==================================");
		if (headers.getHeaderString(HttpHeaders.LOCATION) != null) {
			response.setHeader(HttpHeaders.LOCATION, headers.getHeaderString(HttpHeaders.LOCATION));
			response.setHeader(HttpHeaders.AUTHORIZATION, headers.getHeaderString(HttpHeaders.AUTHORIZATION));
			return new Representation<String>(HttpStatus.OK_200,
					"Successfully Authenticted; Autorization Token and orgional URL are in the HTTP Header !");
		}
		response.setHeader(HttpHeaders.AUTHORIZATION, headers.getHeaderString(HttpHeaders.AUTHORIZATION));
		return new Representation<String>(HttpStatus.OK_200,
				"Successfully Authenticted and Autorization Token is in the HTTP Header !");
	}
}
