package org.acreo.auth.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;

import org.acreo.ip.entities.JwsTokenDb;
import org.acreo.veidblock.token.Header;
import org.acreo.veidblock.token.JWToken;
import org.acreo.veidblock.token.Payload;
import org.acreo.veidblock.token.SignatureJWT;

public class Convertor {

	 public JwsTokenDb jWTokenToDbToekn(JWToken jwToken) {
		JwsTokenDb jwsTokenDb = new JwsTokenDb();
		jwsTokenDb.setAlg(jwToken.getHeader().getAlg());
		jwsTokenDb.setType(jwToken.getHeader().getType());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			jwsTokenDb.setExp(dateFormat.parse(jwToken.getPayload().getExp()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jwsTokenDb.setIss(jwToken.getPayload().getIss());
		jwsTokenDb.setJti(Long.parseLong(jwToken.getPayload().getJti()));
		jwsTokenDb.setRefreshToken(jwToken.getPayload().getRefreshToken());
		jwsTokenDb.setPub(jwToken.getPayload().getPub());
		jwsTokenDb.setScp(jwToken.getPayload().getScp());
		
		jwsTokenDb.setSignature(Base64.getEncoder().encodeToString(jwToken.getSignatureJWT().getSignature()));
		if (jwToken.getSignatureJWT().getPublickey() == null) {
			jwsTokenDb.setSignerData("");
			jwsTokenDb.setSignerDataType(-1);
		} else {
			jwsTokenDb.setSignerData(Base64.getEncoder().encodeToString(jwToken.getSignatureJWT().getPublickey()));
			jwsTokenDb.setSignerDataType(0);
		}
		jwsTokenDb.setVer(jwToken.getPayload().getVer());
		jwsTokenDb.setSub(jwToken.getPayload().getSub());
		return jwsTokenDb;
	}

	public JWToken dbToeknTojWToken(JwsTokenDb jwsTokenDb) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Header header = Header.builder().alg(jwsTokenDb.getAlg()).type(jwsTokenDb.getType()).build();
		Payload payload = Payload.builder().exp(dateFormat.format(jwsTokenDb.getExp())).iss(jwsTokenDb.getIss())
				.jti("" + jwsTokenDb.getJti()).sub(jwsTokenDb.getSub()).ver(jwsTokenDb.getVer())
				.refreshToken(jwsTokenDb.getRefreshToken()).pub(jwsTokenDb.getPub()) .scp(jwsTokenDb.getScp()).build();
		SignatureJWT signatureJWT = SignatureJWT.builder()
				.signature(Base64.getDecoder().decode(jwsTokenDb.getSignature()))
				.publickey(Base64.getDecoder().decode(jwsTokenDb.getSignerData())).build();

		JWToken jWToken = new JWToken(header, payload);
		jWToken.setSignatureJWT(signatureJWT);
		return jWToken;
	}
}
