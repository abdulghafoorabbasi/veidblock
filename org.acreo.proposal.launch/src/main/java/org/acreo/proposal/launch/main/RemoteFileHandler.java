package org.acreo.proposal.launch.main;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.Representation;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.proposal.launch.cityadmin.ResultsSubmission;
import org.acreo.proposal.launch.commons.CitizenCommentsList;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.CryptoStructure.ENCODING_DECODING_SCHEME;
import org.acreo.security.crypto.Encryption;
import org.acreo.security.crypto.Hashing;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RemoteFileHandler {

	/*public static void main(String[] args) {

		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		CitizenCommentsList citizenCommentsList = new CitizenCommentsList();
		CitizenComments e = new CitizenComments();
		e.setComments("These are comments");
		e.setDateTime("2008-12-01 13:24:12");
		e.setResourceId("135467874");

		citizenCommentsList.add(e);
		String temp;
		

	}
*/
	public byte[] createRandomKey() throws VeidblockException {
		String password;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec((System.currentTimeMillis() + "").toCharArray(),
					new CryptoPolicy().getSalt().getBytes(), 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), new CryptoPolicy().getEncAlgorithm());
			return secret.getEncoded();
		} catch (Exception exp) {
			throw new VeidblockException("Status.ERROR, ExceptionType.PROTECTION_KEY_GEN_EXP");
		}

	}

	public CitizenCommentsList downloadComments(String url,String encodedKey){
		try {
			Encryption encryption = new Encryption(new CryptoPolicy());
			
			String downloadedFile = new RemoteFileHandler().download(url);
			System.out.println(new String(downloadedFile));
			byte [] downBytes = encryption.decrypt(Base64.getDecoder().decode(encodedKey.getBytes()), downloadedFile.getBytes(), ENCODING_DECODING_SCHEME.BASE64);
			System.out.println(new String(downBytes));
			CitizenCommentsList citizenCommentsListdwn =  new ObjectMapper().readValue(downBytes, CitizenCommentsList.class);
			System.out.println(citizenCommentsListdwn.size());
			return citizenCommentsListdwn;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}
	
	public ResultsSubmission upploadComments(CitizenCommentsList citizenCommentsList, ResultsSubmission resultsSubmission) throws Exception{
		
		String temp = new ObjectMapper().writeValueAsString(citizenCommentsList);
		Encryption encryption = new Encryption(new CryptoPolicy());
		byte[] key = new RemoteFileHandler().createRandomKey();
		byte[] value = encryption.encrypt(key, temp.getBytes(), ENCODING_DECODING_SCHEME.BASE64);
		String url = new RemoteFileHandler().upload(new String(value));
		resultsSubmission.setSecretKey(new String(Base64.getEncoder().encode(key)));
		resultsSubmission.setDocumentURL(url);
		Hashing hashing =new Hashing(new CryptoPolicy());
		byte [] hash= hashing.generateHash(temp.getBytes());
		resultsSubmission.setHash(new String(Base64.getEncoder().encode(hash)));
		return resultsSubmission;
	}
	
	
	public String upload(String temp) {
		try {
			RestClient restClient = RestClient.builder().baseUrl(new Configuration().getAuthServerUrl()).build();

			Representation representation = restClient.post("/verify/file/", temp, null);

			return representation.getBody().toString();

		} catch (Exception e) {
			return null;
		}

	}

	public String download(String temp) {
		try {
			RestClient restClient = RestClient.builder().baseUrl(temp).build();
			Representation representation = restClient.get("", null);
			return representation.getBody().toString();

		} catch (Exception e) {
			return null;
		}

	}

}
