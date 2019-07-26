package org.acreo.proposal.launch.districtadminA;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.Ledger;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.Representation;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;
import org.acreo.proposal.launch.citizen.ManageUsers;
import org.acreo.proposal.launch.commons.ConsensusResponse;
import org.acreo.proposal.launch.commons.ConsensusResponse.CONSENSUD_RESPONSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CitizenDistrictAMain {

	final static Logger logger = LoggerFactory.getLogger(CitizenDistrictAMain.class);

	public String proposalName = "propose-park-area";

	public static void main(String[] args) throws VeidblockException, InterruptedException {
		 
	}
	
	public void usecaseProposalResponse(int noc, String fileName) throws VeidblockException, IOException{
		
		downloadPasswordFile(fileName);
		// Check Proposal already created  
		ResourceCO admin = loadPasswordFile("distrcit_a_admin", fileName);
		// Log in as Admin and check proposal exist
		init(admin);
		Ledger ledger = Ledger.builder().resource(admin).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if(Objects.isNull(transactionHeaderCO)){
			System.err.println("Please create proposal first and then try to submit response !");
			return; 
		}
		
		System.out.println("Creating "+noc+" citizens !");
		createCitizens("distrcit_a_admin", noc, fileName);
	}
	
	private void createCitizens(String username, int totalResidents, String fileName)
			throws VeidblockException, IOException {
		
		ResourceCO admin = loadPasswordFile(username, fileName);
		downloadPasswordFile(fileName);
		System.out.println("Authenticating as Admin user to create District Administrator . . .");
		ClientAuthenticator authenticator = ClientAuthenticator.builder().uniqueIdentifier("" + admin.getResourceId())
				.password(admin.getPassword()).application(admin.getResourceId() + "")
				.verifier(new Configuration().getAuthServerUrl()).build();

		if (authenticator.autheticate()) {
			System.out.println("Admin user successfully autheticated !");
			System.out.println("Access Token Path: \n\t" + authenticator.getTokenPath());
		}
		System.out.println("District Administrator " + username + " is registering citizens . . .");
		for (int citizenNo = 0; citizenNo < totalResidents; citizenNo++) {
			createCitizens(citizenNo, authenticator);
		}

		System.out.println("Done.");

	}
	private void createCitizens(int citizenNo, ClientAuthenticator authenticator) throws VeidblockException {
		String password = "pass" + citizenNo;
		ResourceCO resourceCOAdmin = new ResourceCO();
		resourceCOAdmin.setName("citizen_" + citizenNo);
		resourceCOAdmin.setEmail("citizen_" + citizenNo + "@gmail.com");
		resourceCOAdmin.setUsername("citizen" + citizenNo);
		resourceCOAdmin.setPassword(password);
		resourceCOAdmin.setOrganizationName("RISE Acreo, AB");
		resourceCOAdmin.setOrganizationUnit("NetLab");
		resourceCOAdmin.setStreet("Isafjordsgatan 22");
		resourceCOAdmin.setCity("16440, Kista");
		resourceCOAdmin.setState("NA");
		resourceCOAdmin.setCountry("SE");
		resourceCOAdmin.setMobile("01052255" + citizenNo);
		resourceCOAdmin.setPhone("01052255" + citizenNo);
		resourceCOAdmin.setBackupEmail("citizen_back" + citizenNo+"@gmail.com");
		resourceCOAdmin.setUrl(new Configuration().getIPVServerUrl() + "/user/resname/ipv");
		Identity identity = Identity.builder().resource(resourceCOAdmin).build();
		ResourceCO resourceCO2 = identity.registerResourceByAdmin(authenticator);
		identity = Identity.builder().resource(resourceCO2).build();
		identity.assignRole("User", authenticator);
	}
	
	
	ClientAuthenticator authenticator = null;
	
	public void publisCitizenCertificates(int noc, String fileName) throws VeidblockException{
		for (int citizenNo = 0; citizenNo < noc; citizenNo++) {
			System.out.println("Citizen No "+(citizenNo +1)+" is submitting response ...");
			ResourceCO resourceCO = loadPasswordFile("citizen"+citizenNo, fileName);	
			init(resourceCO);
		}
	}
	
	
	
	public void sendComments(int noc, String fileName) throws VeidblockException{
		fillOptions();
		for (int citizenNo = 0; citizenNo < noc; citizenNo++) {
			System.out.println("Citizen No "+(citizenNo +1)+" is submitting response ...");
			ResourceCO resourceCO = loadPasswordFile("citizen"+citizenNo, fileName);	
			init(resourceCO);
			OptionsCitizen  optionsCitizen = getOptions(resourceCO.getResourceId());
			submitResponse(resourceCO, optionsCitizen.getResponse() , optionsCitizen.getComments());
			System.out.println("Citizen No "+(citizenNo +1)+" successfully submitted response !");
			
		}
	}
		
	private Hashtable<Integer, OptionsCitizen> options = new Hashtable<>();
	
	private OptionsCitizen getOptions(long resourceId){
		int op = (int)resourceId%11;
		OptionsCitizen optionsCitizen = options.get(op);
		
		return  optionsCitizen ;
	}
	
	private void fillOptions(){
		
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
	
	private class OptionsCitizen{
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
	
	
	

	private ResourceCO loadPasswordFile(String username, String fileName) throws VeidblockException {
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

	private void loadPropertyFile(String config) {

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
	public void downloadPasswordFile(String args){
		try {
			RestClient restClient = RestClient.builder().baseUrl(new Configuration().getIPVServerUrl()).build();
			Representation representation = restClient.get("/resource/file/dd", null);
			System.out.println(representation.getBody().toString());
			
			if(new File(args).exists()){
				new File(args).delete();
				new File(args).createNewFile();
			}
			
			Path path = Paths.get(args);
			Files.write(path, representation.getBody().toString().getBytes());
			

		} catch (Exception e) {
			return ;
		}
	}
}