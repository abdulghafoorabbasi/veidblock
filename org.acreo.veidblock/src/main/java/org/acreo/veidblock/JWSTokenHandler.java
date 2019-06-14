package org.acreo.veidblock;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.security.certificate.CertificateSuite;
import org.acreo.security.utils.DistinguishName;
import org.acreo.security.utils.SGen;
import org.acreo.security.utils.StoreHandling;
import org.acreo.veidblock.token.Header;
import org.acreo.veidblock.token.JWToken;
import org.acreo.veidblock.token.Payload;

public class JWSTokenHandler {

	private JWToken jWToken = null;
	
	private X509Certificate certificate = null;
	private PrivateKey privateKey = null;
	public JWSTokenHandler (X509Certificate certificate, PrivateKey privateKey){
		this.certificate = certificate; 
		this.privateKey = privateKey;
	}
	
	
	public JWToken generateToken(String sub, String iss,
			String ver, String encodedPublicKey, String scp) throws VeidblockException {

		SsoManager ssoManager = new SsoManager();
		SGen sGen = new SGen();
		jWToken = ssoManager.generateSsoToken(privateKey, certificate, createTokenValues(sub, iss, ver, encodedPublicKey, scp, sGen.generateId()+""));

		if (jWToken != null) {
			return jWToken;
		} else {
			throw new VeidblockException("UNAUTHORIZED");
		}
	}
	
	public JWToken generateToken(Header header, Payload payload) throws VeidblockException {
		jWToken = new JWToken(header, payload);
		SsoManager ssoManager = new SsoManager();
		jWToken = ssoManager.generateSsoToken(privateKey, certificate, jWToken);
		if (jWToken != null) {
			return jWToken;
		} else {
			throw new VeidblockException("UNAUTHORIZED");
		}
	}	

	private JWToken createTokenValues(String sub, String iss, String ver, String encodedPublicKey, String scp, String jti) {

		Header header = Header.builder().alg("RSA.SHA-256").type("JWS").build();

		Calendar cal = Calendar.getInstance(); // creates calendar
	    cal.setTime(new Date()); // sets calendar time/date
	    cal.add(Calendar.DAY_OF_WEEK, 1); // adds one day
	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    Payload payload = Payload.builder().iss(iss).sub(sub).ver(ver).exp(dateFormat.format((cal.getTime()))).pub(encodedPublicKey).scp(scp)
				.refreshToken("NOT-USED").jti(jti).build();

		jWToken = new JWToken(header, payload);

		return jWToken;
	}

	public JWToken getJWToken() {
		return jWToken;
	}	
}