package org.acreo.auth.util;

import java.util.Base64;

import org.acreo.common.Representation;
import org.acreo.common.entities.Endorsement;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.init.LocalCertificateManager;
import org.acreo.init.MyIdentity;
import org.acreo.security.crypto.ComplexCryptoFunctions;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Enrollment {

	private LocalCertificateManager localCertificateManager;
	private MyIdentity identity;

	public Enrollment(LocalCertificateManager localCertificateManager, MyIdentity identity) {
		this.localCertificateManager = localCertificateManager;
		this.identity = identity;
	}

	public String enroll(String enrollmentServerURL, String pubVerificationURL) throws VeidblockException {
		String pubKey = Base64.getEncoder()
				.encodeToString(localCertificateManager.fetchCertificate().getPublicKey().getEncoded());
		Endorsement endorsement = fill(this.identity, pubVerificationURL, pubKey);
		endorsement.setSignature("");
		endorsement.setPrefix("0");
		ObjectMapper objectMapper = new ObjectMapper();
		String endorsementSer;
		try {
			endorsementSer = objectMapper.writeValueAsString(endorsement);
		} catch (JsonProcessingException e) {
			throw new VeidblockException(e);
		}
		ComplexCryptoFunctions complexCryptoFunctions = new ComplexCryptoFunctions(new CryptoPolicy());
		byte[] sig = complexCryptoFunctions.generateSignature(localCertificateManager.getAuthServerPrivateKey(),
				endorsementSer.getBytes(), ENCODING_DECODING_SCHEME.BASE64);
		endorsement.setSignature(new String(sig));
		Representation<String> representation = sendEnrollmentReq(enrollmentServerURL, endorsement);
		if (representation.getCode() == 200) {
			try {
				new EnrollmentIO().saveEndorsement(representation.getBody());
				return "Successfully enrolled";
			} catch (Exception exp) {
				return "Problems when enrolling. Internal server error !";
			}
		} else {
			return "Problems when enrolling !";
		}
	}

	private Representation<String> sendEnrollmentReq(String vblURL, Object obj) {

		try {
			RestClient restClient = RestClient.builder().baseUrl(vblURL).build();
			Representation<String> response = restClient.post("", obj, null);
			return response;
		} catch (VeidblockException e) {
			return new Representation<String>(502, "Internal error :" + e.getMessage());
		}
	}

	private Endorsement fill(MyIdentity identity, String pubKeyVeriURL, String encodedPubKey) {

		Endorsement endorsement = new Endorsement();
		endorsement.setName(identity.getName());
		endorsement.setEmail(identity.getEmail());

		endorsement.setAddress1(identity.getAddress1());
		endorsement.setAddress2(identity.getAddress2());
		endorsement.setPostCode(identity.getPostCode());
		endorsement.setLocation(identity.getLocation());
		endorsement.setOrganizationId(identity.getOrganizationId());

		endorsement.setCity(identity.getCity());
		endorsement.setState(identity.getState());
		endorsement.setCountry(identity.getCountry());
		endorsement.setMobile(identity.getMobile());
		endorsement.setPhone(identity.getPhone());
		endorsement.setUrl(identity.getUrl());
		endorsement.setPubKeyVeriURL(pubKeyVeriURL);
		endorsement.setEncodedPubKey(encodedPubKey);
		return endorsement;

	}
}