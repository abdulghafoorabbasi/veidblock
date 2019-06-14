package org.acreo.auth.util;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.acreo.common.Representation;
import org.acreo.common.entities.TokenCO;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ip.entities.JwsTokenDb;
import org.acreo.ip.service.JWSTokenService;
import org.acreo.veidblock.JWSToken;
import org.eclipse.jetty.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VerifyToken4ThirdParty {

	private LocalCertificateManager localCertificateManager;
	private JWSTokenService jwsTokenService;

	public VerifyToken4ThirdParty(LocalCertificateManager localCertificateManager, JWSTokenService jwsTokenService) {
		this.localCertificateManager = localCertificateManager;
		this.jwsTokenService = jwsTokenService;
	}

	public Representation<String> verify(TokenCO tokenCO) {
		try {

			JWSToken jwsToken = new JWSToken(tokenCO.getToken());
			// String tt =
			// "eyJhbGciOiJSU0EuU0hBLTI1NiIsInR5cGUiOiJKV1MifQ==.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwMDAiLCJzdWIiOiI2NzA0NjcyMTkiLCJ2ZXIiOiJodHRwOi8vbG9jYWxob3N0OjgwMDAiLCJleHAiOiIyMDE3LTEyLTEzIDAyOjE1OjMyIiwicmVmcmVzaFRva2VuIjoiTk9ULVVTRUQiLCJqdGkiOiIyMjE0NDkxNDYiLCJwdWIiOiJNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQWlYYXVMNVJrcGhLVmI5azU3VzEwS2wzcTkvZ0lTcDJWYnoyUU8yMjdBcHVlb3N4dDEzL3RiZzBROEdGUEh5VU1SR2pWQ05rbStjTUVVSk5MNitSZm0zV3JER0xYMWloWElxNkxlVUJjeU44cjlldGxiRml4RXVSZDVuNU8rWFZRV3VnWkZRRTFYc2J4aXdWdXlZWnNiOFBaYitZVGhveVVtVE1aWU5qaTlOS0drcE9hUU9IeERuWWVSVnVkaE53WkNBcFdEZCtPVTJ2RURvSmtONmtNencxSTk2eUMzeXJVUlEybm1ZVUlMQ25NK3pTaVhmTVh3SWFzSncyK2E0ZGgrblhaQ3NHRUYxcHhEN0lmWThiMTRiNWx4aytJZ25UT3h1ZVZKOEkwaVVGOWlrb1ZtbyttYWZ6Y0tnWUJnWXAyYTVRN0ZIRVpsV3lYVTlLLzJSMktqUUlEQVFBQlxuIn0=.eyJzaWduYXR1cmUiOiJTRy9xR1pWd2o4YWRmNk1JMHNVUlE5cXIrUC9mU3hORjZzRE81Zi8wMkI2U3JQQnVMbElOUTVVYzcrRXBXMUZvQ2VrZDNUL2tFT0NYS2VvZENYZjBmZ3FSVEl5Y1A5NUZnSHptWWxpcSs1eEdEVzZTem1naDlhcHpTaG1hbFR2V2VxRkpqcmRRMzE2ZnpTbmtwb3MxUGdPR1lFS1M1VmMzS1FEYXhIdW9DSG0rSzd6QlVBelBxaFprZGFDMWdXTjNMZE45L21uV2JhSS9mTXdWR2ZjMENiZnZMWU1TTU1wRTRsYzJwbXU4VnIzeWk0TG1QTE1OSUxYK2dqU0FrTm1ESnpxcDc5ZjVnWm5xY2NJUDJ4alZSSWU5TFVYTVQ3RlhSYVYwcStzdm1JNy85ei94NkErcTNRWVRERGJOV0kyb0ZpMEFlZGpjMmVCQ1NFZnBvUjJxR0E9PSIsInB1YmxpY2tleSI6Ik1JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBcXU0K2pUSGFFL0pydTFQYXcyRTRJeXlhbWN2Szk1N0FJS09KU1M0bWRqc3cyWEJtRmVLYzBwZUZXOUZLeXVIMGorVkNaOFdKZTRBOHRtT1ZBVGRzc0NpSHhIQm9vclRjYi90ZExINmszdXU3UEVrS2Y2Wmh4UmUrcFpWZFRlSGt2YjdRM0pnbDEyZUpYL0tWaFJ4bjhzNk5EaWRHZXhwak1vbWcraE02OWxYWDMzbkN6RmxPYnJ1VVA3NXIwR2ViNDd2RzVMaTNGS05YbDVXcXgxamFic1ZCWFY0clNKNUJKb0JrbklSMm0wdHdJN1dOV21sSiswV04xTzdLNEVHRGZsR1U1bW5nbXVLQStkbXhKb0VhblBGd0RjSHhJTjdNaTN6Z1ZqaTd5eTZ5aHo1SUo2NytrZXVHMVJ1UzdDbjBIcmZSenJJZG9pME02SVpiWmVwUS9RSURBUUFCIn0=";
			// System.out.println(jwsToken.toEncoded());
			// JWSToken jwsTT = new JWSToken(tt);
			// System.out.println(jwsTT .getPayload().getJti());
			if (!(jwsToken.getPayload().getScp() == null || jwsToken.getPayload().getScp() == "")) {
				return new Representation<String>(HttpStatus.NON_AUTHORITATIVE_INFORMATION_203,
						"Token payload contains SCP which violates privacy rules. Please resubmit without 'scp' value !");
			}
			//System.out.println(jwsToken.getPayload().getJti());
			JwsTokenDb jwsTokenDb = null;
			try {
				jwsTokenDb = jwsTokenService.getJwsToken(Long.parseLong(jwsToken.getPayload().getJti()));
			} catch (Exception exp) {
				jwsTokenDb = null;
			}
			if (jwsTokenDb == null) {
				return new Representation<String>(HttpStatus.UNAUTHORIZED_401, "Invalid Token : Invalid issuer !");
			}
			jwsToken.getPayload().setScp(jwsTokenDb.getScp());
			X509Certificate x509Certificate = localCertificateManager.fetchCertificate();
			if (!jwsToken.verify(x509Certificate)) {
				return new Representation<String>(HttpStatus.UNAUTHORIZED_401, "Invalid Token : Verification failed !");
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			return new Representation<String>(HttpStatus.INTERNAL_SERVER_ERROR_500, "Internal server error !");
		}
		return new Representation<String>(HttpStatus.OK_200, "Successfully verified token !");
	}
	
	public Representation<String> verify(String publicKeyEncoded) {
		try {
			PublicKey publicKey = localCertificateManager.fetchCertificate().getPublicKey();
			String localPublicKey  = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			
			ObjectMapper objectMapper = new ObjectMapper();
			String temp = objectMapper.readValue(publicKeyEncoded, String.class);
			if (!temp.equals(localPublicKey)) {
				return new Representation<String>(HttpStatus.NOT_FOUND_404,"");
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			return new Representation<String>(HttpStatus.INTERNAL_SERVER_ERROR_500, "Internal server error !");
		}
		return new Representation<String>(HttpStatus.OK_200, "Successfully verified Public Key!");
	}
	

}
