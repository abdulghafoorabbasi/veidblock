package org.acreo.proposal.launch.districtadminB;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.Ledger;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.proposal.launch.commons.ConsensusResponse;
import org.acreo.proposal.launch.commons.ConsensusResponse.CONSENSUD_RESPONSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CitizenDistrictBMain {

	final static Logger logger = LoggerFactory.getLogger(CitizenDistrictBMain.class);

	public String proposalName = "propose-palyground-area";

	public static void main(String[] args) throws VeidblockException, InterruptedException {

		 
	}
	ClientAuthenticator authenticator = null;
	public void sendComments(String fileName) throws VeidblockException{
		
		fillOptions();
		for (int citizenNo = 0; citizenNo < 100; citizenNo++) {
			System.out.println("Citizen No "+(citizenNo +1)+" is submitting response ...");
			ResourceCO resourceCO = loadPasswordFile("citizen"+citizenNo, fileName);	
			init(resourceCO);
			OptionsCitizen  optionsCitizen = getOptions(resourceCO.getResourceId());
			submitResponse(resourceCO, optionsCitizen.getResponse() , optionsCitizen.getComments());
			System.out.println("Citizen No "+(citizenNo +1)+" successfully submitted response !");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private Hashtable<Integer, OptionsCitizen> options = new Hashtable<>();
	
	public OptionsCitizen getOptions(long resourceId){
		return options.get(resourceId%11); 
	}
	
	public void fillOptions(){
		
		options.put(0, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Option is good but we have to thnk about a small palyfround as well in same area "));	
		
		options.put(1, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Good option"));
		  
		options.put(2, new OptionsCitizen(CONSENSUD_RESPONSE.DISAGREE, "Not a Good option"));
		  
		options.put(3, new OptionsCitizen(CONSENSUD_RESPONSE.DISAGREE, "would be good to convert in parking"));
		  
		options.put(4, new OptionsCitizen(CONSENSUD_RESPONSE.DONT_KNOW, "As you wish "));
		  
		options.put(5, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Excelent for kids to play"));
		  
		options.put(6, new OptionsCitizen(CONSENSUD_RESPONSE.DISAGREE, "Convert into shoping mal"));
		  
		options.put(7, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Good option"));
		  
		options.put(8, new OptionsCitizen(CONSENSUD_RESPONSE.DONT_KNOW, "I have not opinion"));
		  
		options.put(9, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Fine for me"));
		  
		options.put(10, new OptionsCitizen(CONSENSUD_RESPONSE.AGREED, "Ok for me"));
	}
	
	public class OptionsCitizen{
		private CONSENSUD_RESPONSE response;
		private String comments;
		
		public OptionsCitizen(CONSENSUD_RESPONSE response, String comments){
			this.response = response;
			this.comments=comments;
		}
		public CONSENSUD_RESPONSE getResponse() {
			return response;
		}
		public void setResponse(CONSENSUD_RESPONSE response) {
			this.response = response;
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
	}
	
	
	

	public ResourceCO loadPasswordFile(String username, String fileName) throws VeidblockException {
		ResourceCO resourceCO = new ResourceCO();
		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			String tmep;
			stream.forEach(temp -> {
				String tokens[] = temp.split("=");
				String userName = tokens[0].trim();
				if (username.equals(userName)) {

					String uid = tokens[1].trim();
					resourceCO.setResourceId(Long.parseLong(uid));
					String password = tokens[2].trim();
					resourceCO.setPassword(password);
					return;
				}
			});
			return resourceCO;

		} catch (IOException e) {
			throw new VeidblockException(e);
		}
	}

	public void loadPropertyFile(String config) {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(config);
			prop.load(input);
			new Configuration(prop.getProperty(Configuration.AUTH_SERVER), prop.getProperty(Configuration.IPV_SERVER),
					prop.getProperty(Configuration.LEDGER_SERVER));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean init(ResourceCO resourceCO) throws VeidblockException {
		//logger.info("--- I --- Initalizing proposal applcaition .... ");
		Configuration configuration = new Configuration();
		
		try {
			authenticator = ClientAuthenticator.builder().password(resourceCO.getPassword()).uniqueIdentifier(""+resourceCO.getResourceId()).application(resourceCO.getResourceId()+"").verifier(configuration.getAuthServerUrl()).build();
		} catch (IOException e) {
			throw new VeidblockException(e);
		}
		
		Identity identity = Identity.builder().resource(resourceCO).build();
		//logger.info("--- I --- Checking citizen already registered !");
		ResourceCO resourceCORet = identity.getUser(authenticator );
		if (Objects.isNull(resourceCORet)) {
			logger.error("--- E --- Citizen's inforamtion deos not exist !");
			throw new VeidblockException("User not registered !");
		}
		identity.generateCredentials();
		
		if (!identity.isCertificateAlreadyPubliched(authenticator)) {
			identity.publishCertificate(configuration.getAuthServerUrl(),authenticator);
		}

		return true;
	}

	public void submitResponse(ResourceCO resourceCO, CONSENSUD_RESPONSE res, String comments)
			throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if (Objects.isNull(transactionHeaderCO)) {
			logger.error("--- E --- Could not find chainblock with name '" + proposalName + "'");
		}
		ConsensusResponse consensusResponse = new ConsensusResponse();
		consensusResponse.setResponse(res);
		consensusResponse.setComments(comments);
		ledger.addTransaction(transactionHeaderCO, null, consensusResponse, new Configuration().getAuthServerUrl());
	}
}