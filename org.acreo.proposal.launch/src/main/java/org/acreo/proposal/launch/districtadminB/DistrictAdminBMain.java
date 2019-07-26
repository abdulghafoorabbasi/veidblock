package org.acreo.proposal.launch.districtadminB;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.Ledger;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.proposal.launch.cityadmin.CityAdminMain;
import org.acreo.proposal.launch.cityadmin.ResultsSubmission;
import org.acreo.proposal.launch.commons.CitizenComments;
import org.acreo.proposal.launch.commons.CitizenCommentsList;
import org.acreo.proposal.launch.commons.ConsensusResponse;
import org.acreo.proposal.launch.commons.PlanningProposal;
import org.acreo.proposal.launch.commons.ProposalSmartContract;
import org.acreo.proposal.launch.commons.ConsensusResponse.CONSENSUD_RESPONSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DistrictAdminBMain {

	final static Logger logger = LoggerFactory.getLogger(DistrictAdminBMain.class);

	public String proposalName = "propose-park-area";
	ClientAuthenticator authenticator = null;
	
	public void districtAdminB(String[] args) throws VeidblockException, IOException {

		DistrictAdminBMain districtAdminAMain = new DistrictAdminBMain();

		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		districtAdminAMain.options(args[1]);
	}
	
	
	public static void main(String[] args) throws VeidblockException, IOException {

		DistrictAdminBMain districtAdminAMain = new DistrictAdminBMain();

		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		districtAdminAMain.options(args[1]);
	}

	public void options(String fileName) throws IOException, VeidblockException {
		ResourceCO resourceCO = loadPasswordFile("distrcit_b_admin", fileName);

		init(resourceCO);
		while (true)
			try {
				System.out.println("*************************************************");
				System.out.println("*  1. Create Proposal as District Administrator *");
				System.out.println("*  2. List all chain (Headers)                  *");
				System.out.println("*  3. Citizen Script                            *");
				System.out.println("*  4. List all transactions                     *");
				System.out.println("*  5. Compile Proposal Result                   *");
				System.out.println("*  6. Submit Proposal Results                   *");
				System.out.println("*  7. Exit                                      *");
				System.out.println("*************************************************");
				System.out.print("Please enter option : ");
				Scanner reader = new Scanner(System.in);
				String temp = reader.nextLine();
				int i = Integer.parseInt(temp);
				switch (i) {
				case 1:
					createProposalChain(resourceCO);
					publishProposal(resourceCO);
					break;
				case 2:
					listChaincodes(resourceCO);
					break;
				case 3:
					CitizenDistrictBMain planningProposalMain = new CitizenDistrictBMain();
					planningProposalMain.sendComments(fileName);
					break;
				case 4:
					TransactionHeaderCO transactionHeaderCO = listChaincodesResponse(resourceCO);
					if(!Objects.isNull(transactionHeaderCO))
						listResponses(resourceCO, transactionHeaderCO);
					break;
				case 5:
					ResultsSubmission resultsSubmission = compileResponse(resourceCO);
					displayResult(resultsSubmission);
					break;
				case 6:
					resultsSubmission = compileResponse(resourceCO);
					submitResult(resourceCO, resultsSubmission);
					break;
				case 7:
					System.exit(0);
					break;
				default:
					System.out.println("Invalid option !");
				}
				System.out.print("\n\n");

			} catch (NumberFormatException nfe) {
				System.err.println("Invalid Format!");
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

	public void displayResult(ResultsSubmission resultsSubmission) {
		System.out.println("Total Agreed    : " + resultsSubmission.getAgreed());
		System.out.println("Total Disagreed : " + resultsSubmission.getDisagreed());
		System.out.println("Total Don't Know: " + resultsSubmission.getDontknow());
	}

	public boolean init(ResourceCO resourceCO) throws VeidblockException {
		//logger.info("--- I --- Initalizing proposal applcaition .... ");
		Identity identity = Identity.builder().resource(resourceCO).build();
		Configuration configuration = new Configuration();
		try {
			authenticator = ClientAuthenticator.builder().password(resourceCO.getPassword()).uniqueIdentifier(""+resourceCO.getResourceId()).application(resourceCO.getResourceId()+"").verifier(configuration.getAuthServerUrl()).build();
		} catch (IOException e) {
			throw new VeidblockException(e);
		}
		//logger.info("--- I --- Checking citizen already registered !");
		ResourceCO resourceCORet = identity.getUser(authenticator);
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

	public void createProposalChain(ResourceCO resourceCO) throws VeidblockException {

		//logger.info("--- I --- Creating Chainblock  for " + proposalName);
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		SmartContract smartContractStruct = new ProposalSmartContract();
		smartContractStruct.setStart(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); // Now use today date.
		calendar.add(Calendar.DATE, 15); // Adds 15 days
		smartContractStruct.setEnd(calendar.getTime());
		smartContractStruct.setPayloadSupportingTypes(
				new String[] { PlanningProposal.class.getName(), ConsensusResponse.class.getName() });
		smartContractStruct.setScope(SCOPE.LOCAL);
		smartContractStruct.setSecurityLevel(SECURITY_LEVEL.DIGITAL_SIGNATURE);
		try {
			TransactionHeaderCO transactionHeaderCO = ledger.addTransationHeader(proposalName, smartContractStruct,
					new Configuration().getAuthServerUrl());
			//logger.info("--- I --- Chainblock with reference No." + transactionHeaderCO + " successfully created !");
		} catch (VeidblockException vbe) {
			logger.error("--- E --- " + vbe.getMessage());

		}
		//logger.info("--- I --- Chainblock  witn name '" + proposalName + "' successfully created !");
	}

	public void publishProposal(ResourceCO resourceCO) throws VeidblockException {
		//logger.info("--- I --- Publishing proposal with name " + proposalName);
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		PlanningProposal planningProposal = new PlanningProposal();
		planningProposal.setGeoLocation("1234555,2154555");
		planningProposal.setProposalAddress("Nukar par");
		planningProposal.setProposalDescription("proposalDescription");
		planningProposal.setProposalName("Park");
		planningProposal.setProposalType("green-park");
		planningProposal.setProposalURL("http://clickhereformoreinforamtion/");
		ledger.addTransaction(transactionHeaderCO, null, planningProposal, new Configuration().getAuthServerUrl());
		//logger.info("--- I --- Proposal successfully published ! ");

	}

	public void submitResponse(ResourceCO resourceCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if (Objects.isNull(transactionHeaderCO)) {
			logger.error("--- E --- Could not find chainblock with name '" + proposalName + "'");
		}
		ConsensusResponse consensusResponse = new ConsensusResponse();
		consensusResponse.setResponse(CONSENSUD_RESPONSE.AGREED);
		consensusResponse.setComments("This is really a good oppertunity to utilize this space for part ");
		ledger.addTransaction(transactionHeaderCO, null, consensusResponse, new Configuration().getAuthServerUrl());
	}

	public ResultsSubmission compileResponse(ResourceCO resourceCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if (Objects.isNull(transactionHeaderCO)) {
			logger.error("--- E --- Could not find chainblock with name '" + proposalName + "'");
		}

		if (enforceDateTimeRuleOnTransaction(transactionHeaderCO.getSmartcontract())) {
			logger.error("--- W --- Chaincode is still open !");
			return null;
		}
		Chain chain = ledger.getCompleteChain(transactionHeaderCO.getRef());
		//logger.info("--- I --- Total number of citizens participated : " + chain.getTransactions().size());
		ResultsSubmission resultsSubmission = new ResultsSubmission();

		int i = 0;
		CitizenCommentsList citizenCommentsList = new CitizenCommentsList();
		for (TransactionCO t : chain.getTransactions()) {
			String payload = ledger.extractPayload(transactionHeaderCO, t);
			i++;
			try {
				PlanningProposal planningProposal = new ObjectMapper().readValue(payload, PlanningProposal.class);
				resultsSubmission.setPlanningProposal(planningProposal);
				System.out.println(i + "    " + planningProposal.getProposalAddress());
			} catch (Exception e) {
				ConsensusResponse consensusResponse;
				try {
					consensusResponse = new ObjectMapper().readValue(payload, ConsensusResponse.class);
					if (consensusResponse.getResponse() == CONSENSUD_RESPONSE.AGREED) {
						resultsSubmission.setAgreed(resultsSubmission.getAgreed() + 1);
					} else if (consensusResponse.getResponse() == CONSENSUD_RESPONSE.DISAGREE) {
						resultsSubmission.setDisagreed(resultsSubmission.getDisagreed() + 1);
					}
					if (consensusResponse.getResponse() == CONSENSUD_RESPONSE.DONT_KNOW) {
						resultsSubmission.setDontknow(resultsSubmission.getDontknow() + 1);
					}
					CitizenComments citizenComments = new CitizenComments();
					citizenComments.setComments(consensusResponse.getComments());
					citizenComments.setResourceId(t.getSender());
					citizenComments.setDateTime(t.getCreationTime());
					citizenCommentsList.add(citizenComments);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		String localFilepath = createLocalCommentsFile(citizenCommentsList);
		resultsSubmission.setDocumentURL(localFilepath);
		//logger.info("--- I --- Final result !");
		//logger.info("--- I --- Do you want to submit proposal to City Admin for discussion ?");
		return resultsSubmission;
	}

	private String uploadFile(String localCommentsFileURL, String key) {
		return "";
	}

	private String createLocalCommentsFile(CitizenCommentsList citizenCommentsList) {
		return "";
	}

	public boolean submitResult(ResourceCO resourceCO, ResultsSubmission resultsSubmission) throws VeidblockException {

		String key = "";
		String url = uploadFile(resultsSubmission.getDocumentURL(), key);
		String docHashs = "";
		resultsSubmission.setSecretKey(key);
		resultsSubmission.setDocumentURL(url);
		resultsSubmission.setHash(docHashs);

		String resultSubmissionName = "Request4Proposal-2018";
		//logger.info("--- I --- Compiled Resultys successfully submitted !");
		//logger.info("--- I --- Submittign results in chain " + resultSubmissionName);
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(resultSubmissionName);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		ledger.addTransaction(transactionHeaderCO, new String[] { transactionHeaderCO.getCreator() }, resultsSubmission,
				new Configuration().getAuthServerUrl());
		//logger.info("--- I --- Compiled Results successfully submitted !");
		return true;
	}

	public void listChaincodes(ResourceCO resourceCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaders transactionHeaders = ledger.getTransactionHeaders();
		if (Objects.isNull(transactionHeaders) || transactionHeaders.size() == 0) {
			logger.error("--- I --- Block list is empty !");
			return;
		}
		int blockcNo = 1;
		for (TransactionHeaderCO transactionHeader : transactionHeaders) {

			System.out.println("------- Block No --->" + blockcNo);
			blockcNo++;
			System.out.println(" * Reference No. : " + transactionHeader.getRef());
			System.out.println("\tName : " + transactionHeader.getChainName());
			System.out.println("\tSmart Contract : ");
			System.out.println(transactionHeader.getSmartcontract());
			SmartContract smartContractStruct = null;
			try {
				smartContractStruct = new ObjectMapper().readValue(transactionHeader.getSmartcontract(),
						SmartContract.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\t\tStart Date : " + smartContractStruct.getStart());
			System.out.println("\t\tEnd Date : " + smartContractStruct.getEnd());
			System.out.println("\t\tScope : " + smartContractStruct.getScope());
			System.out.print("\t\tSupporting Data Type(s) : ");
			for (String str : smartContractStruct.getPayloadSupportingTypes()) {
				System.out.print(" " + str);
			}
			System.out.println(" ");
			System.out.println("\t\tSecurty Level: " + smartContractStruct.getSecurityLevel());
		}
	}
	public TransactionHeaderCO listChaincodesResponse(ResourceCO resourceCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		TransactionHeaders transactionHeaders = ledger.getTransactionHeaders();
		if (Objects.isNull(transactionHeaders) || transactionHeaders.size() == 0) {
			logger.error("--- I --- Block list is empty !");
			return null;
		}
		int blockcNo = 1;
		for (TransactionHeaderCO transactionHeader : transactionHeaders) {

			System.out.println("------- Chain code No --->" + blockcNo);
			blockcNo++;
			System.out.println(" * Reference No. : " + transactionHeader.getRef());
			System.out.println("\tName : " + transactionHeader.getChainName());
			System.out.println("\tSmart Contract : ");
			System.out.println(transactionHeader.getSmartcontract());
			SmartContract smartContractStruct = null;
			try {
				smartContractStruct = new ObjectMapper().readValue(transactionHeader.getSmartcontract(),
						SmartContract.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\t\tStart Date : " + smartContractStruct.getStart());
			System.out.println("\t\tEnd Date : " + smartContractStruct.getEnd());
			System.out.println("\t\tScope : " + smartContractStruct.getScope());
			System.out.print("\t\tSupporting Data Type(s) : ");
			for (String str : smartContractStruct.getPayloadSupportingTypes()) {
				System.out.print(" " + str);
			}
			System.out.println(" ");
			System.out.println("\t\tSecurty Level: " + smartContractStruct.getSecurityLevel());
		}
		int selected = getInput("Please enster chaincode No to view complete chain !"); 
		return transactionHeaders.get(selected-1);
	}
	
	

	private static int getInput(String message) {
		System.out.print(message);
		return new Scanner(System.in).nextInt();
	}

	public void listResponses(ResourceCO resourceCO, TransactionHeaderCO transactionHeaderCO)
			throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		Transactions transactions = ledger.getTransactionByRef(transactionHeaderCO.getRef());
		if (Objects.isNull(transactions) || transactions.size() == 0) {
			//logger.info("--- I --- Transaction list is empty !");
			return;
		}
		int tNo = 1;
		for (TransactionCO t : transactions) {
			System.out.println(" ------- Transaction No. --->" + tNo);
			tNo++;
			System.out.println(" Reference No. : " + t.getRef());
			System.out.println(" \tTransaction No. : " + t.getDepth());
			System.out.println(" \tCrypto Operations : " + t.getCryptoOperationsOnPayload());
			System.out.println(" \tCreation Date : " + t.getCreationTime());
			System.out.println(" \tPrevious Hash : " + t.getHashPrevBlock());
			System.out.println(" \tPayload (json) : " + t.getPayload());
			System.out.println(" \tPayload Type : " + t.getPayloadType());
			System.out.println(" \tSender : " + t.getSender());
			System.out.println(" \tPublish Date : " + t.getSignedDate());
		}
		//int i = getInput("***Enter Block No : ");
		//return transactions.get(i - 1);
	}

	public void verifyTransaction(ResourceCO resourceCO, TransactionCO transactionCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		if (Boolean.parseBoolean(ledger.verify(transactionCO))) {
			System.out.println("-*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***-");
			System.out.println("Verified transaction with previous transactions from ledger service !");
			System.out.println("-*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***-");
		} else {
			System.out.println("-*** *** *** *** *** *** *** **-");
			System.err.println("transaction verificatin failed !");
			System.out.println("-*** *** *** *** *** *** *** **-");
		}
	}

	public void verifyTransactionLocally(ResourceCO resourceCO, TransactionCO transactionCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		if (ledger.verifyTransactionLocally(transactionCO)) {
			System.out.println("-** *** *** *** *** *** *** **-");
			System.out.println("Verified transaction loacally !");
			System.out.println("-** *** *** *** *** *** *** **-");
		} else {
			System.out.println("-*** *** *** *** *** *** *** *** *** ***-");
			System.err.println("Loacally transaction verificatin failed !");
			System.out.println("-*** *** *** *** *** *** *** *** *** ***-");
		}
	}

	private boolean enforceDateTimeRuleOnTransaction(String smartContractJson) throws VeidblockException {

		SmartContract smartContract = null;
		try {
			smartContract = new ObjectMapper().readValue(smartContractJson, SmartContract.class);
		} catch (IOException e) {
			throw new VeidblockException("Problems when execurity Smart-contract !");
		}
		;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentDateTime = new Date();
		Date startDateStr = smartContract.getStart();
		Date endDateStr = smartContract.getEnd();
		boolean after = currentDateTime.after(startDateStr);
		boolean before = currentDateTime.before(endDateStr);

		if (after & before) {
			return false;
		} else if (after) {
			return true;
		} else if (before) {
			return false;
		}
		throw new VeidblockException("Problems when executing Smartcontract !");
	}
	/***************************
	 * 
	 * 
	 * //planningProposalMain.createProposalChain(resourceCO);
	 * //TransactionHeaderCO transactionHeaderCO =
	 * planningProposalMain.listChaincodes(resourceCO);
	 * //planningProposalMain.publishProposal(resourceCO);
	 * 
	 * //ResultsSubmission resultsSubmission =
	 * districtAdminAMain.compileResponse(resourceCO);
	 * //districtAdminAMain.displayResult(resultsSubmission);
	 * 
	 * //planningProposalMain.submitResult(resourceCO, resultsSubmission);
	 * 
	 */

}


/*package org.acreo.proposal.launch.districtadminB;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

import org.acreo.clientapi.Identity;
import org.acreo.clientapi.Ledger;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.clientapi.utils.Configuration;
import org.acreo.common.entities.ResourceCO;
import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.proposal.launch.cityadmin.CityAdminMain;
import org.acreo.proposal.launch.cityadmin.ResultsSubmission;
import org.acreo.proposal.launch.commons.CitizenComments;
import org.acreo.proposal.launch.commons.CitizenCommentsList;
import org.acreo.proposal.launch.commons.ConsensusResponse;
import org.acreo.proposal.launch.commons.PlanningProposal;
import org.acreo.proposal.launch.commons.ProposalSmartContract;
import org.acreo.proposal.launch.commons.ConsensusResponse.CONSENSUD_RESPONSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DistrictAdminBMain {

	final static Logger logger = LoggerFactory.getLogger(DistrictAdminBMain.class);

	public String proposalName = "propose-park-area";
	public static void main(String[] args) throws VeidblockException {

		DistrictAdminBMain districtAdminBMain = new DistrictAdminBMain();
		
		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		
		ResourceCO resourceCO = districtAdminBMain.loadPasswordFile("distrcit_b_admin", args[1]);
		//long cityAdmin = 798111071;
		//String password= "7hMC3LEtB7f_";
		//resourceCO.setResourceId(cityAdmin );
		//resourceCO.setPassword(password);
		
		districtAdminBMain.init(resourceCO);

		//planningProposalMain.createProposalChain(resourceCO);

		//TransactionHeaderCO transactionHeaderCO = planningProposalMain.listChaincodes(resourceCO);

	//	planningProposalMain.publishProposal(resourceCO);
		
		
		ResultsSubmission resultsSubmission = districtAdminBMain.compileResponse(resourceCO);
		districtAdminBMain.displayResult(resultsSubmission);
		
		//planningProposalMain.submitResult(resourceCO, resultsSubmission);
		 
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
	
	
	public void displayResult(ResultsSubmission resultsSubmission){
		System.out.println("Total Agreed    : "+resultsSubmission.getAgreed());
		System.out.println("Total Disagreed : "+resultsSubmission.getDisagreed());
		System.out.println("Total Don't Know: "+resultsSubmission.getDontknow());
	}
	
	
	public boolean init(ResourceCO resourceCO) throws VeidblockException {
		//logger.info("--- I --- Initalizing proposal applcaition .... ");
		Identity identity = Identity.builder().resource(resourceCO).build();
		Configuration configuration = new Configuration();
		ClientAuthenticator authenticator = null;
		try {
			authenticator = ClientAuthenticator.builder().application(resourceCO.getResourceId()+"").verifier(configuration.getAuthServerUrl()).build();
		} catch (IOException e) {
			throw new VeidblockException(e);
		}
		//logger.info("--- I --- Checking citizen already registered !");
		ResourceCO resourceCORet = identity.getUser(authenticator);
		if (Objects.isNull(resourceCORet)) {
			logger.error("--- E --- Citizen's inforamtion deos not exist !");
			throw new VeidblockException("User not registered !");
		}
		identity.generateCredentials();
		
		if (!identity.isCertificateAlreadyPubliched()) {
			identity.publishCertificate(configuration.getAuthServerUrl());
		}

		return true;
	}

	public void createProposalChain(ResourceCO resourceCO) throws VeidblockException {
		
		//logger.info("--- I --- Creating Chainblock  for " + proposalName);
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		SmartContract smartContractStruct = new ProposalSmartContract();
		smartContractStruct.setStart(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); // Now use today date.
		calendar.add(Calendar.DATE, 15); // Adds 15 days
		smartContractStruct.setEnd(calendar.getTime());
		smartContractStruct.setPayloadSupportingTypes(new String[] { PlanningProposal.class.getName(),ConsensusResponse.class.getName()});
		smartContractStruct.setScope(SCOPE.LOCAL);
		smartContractStruct.setSecurityLevel(SECURITY_LEVEL.DIGITAL_SIGNATURE);
		try {
			TransactionHeaderCO transactionHeaderCO = ledger.addTransationHeader(proposalName,
					smartContractStruct, new Configuration().getAuthServerUrl());
			//logger.info("--- I --- Chainblock with reference No." + transactionHeaderCO + " successfully created !");
		} catch (VeidblockException vbe) {
			logger.error("--- E --- " + vbe.getMessage());

		}
		//logger.info("--- I --- Chainblock  witn name '" + proposalName+"' successfully created !");
	}

	public void publishProposal(ResourceCO resourceCO)
			throws VeidblockException {
		//logger.info("--- I --- Publishing proposal with name " + proposalName);
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		PlanningProposal planningProposal = new PlanningProposal();
		planningProposal.setGeoLocation("1234555,2154555");
		planningProposal.setProposalAddress("Nukar par");
		planningProposal.setProposalDescription("proposalDescription");
		planningProposal.setProposalName("Park");
		planningProposal.setProposalType("green-park");
		planningProposal.setProposalURL("http://clickhereformoreinforamtion/");
		ledger.addTransaction(transactionHeaderCO, null, planningProposal, new Configuration().getAuthServerUrl());
		//logger.info("--- I --- Proposal successfully published ! " );
		
	}
	
	public void submitResponse(ResourceCO resourceCO) throws VeidblockException{
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if(Objects.isNull(transactionHeaderCO)){
			logger.error("--- E --- Could not find chainblock with name '"+proposalName+"'");
		}
		ConsensusResponse consensusResponse = new ConsensusResponse();
		consensusResponse.setResponse(CONSENSUD_RESPONSE.AGREED);
		consensusResponse.setComments("This is really a good oppertunity to utilize this space for part "); 
		ledger.addTransaction(transactionHeaderCO, null, consensusResponse, new Configuration().getAuthServerUrl());		 	
	}
	
	
	public ResultsSubmission compileResponse(ResourceCO resourceCO) throws VeidblockException{
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if(Objects.isNull(transactionHeaderCO)){
			logger.error("--- E --- Could not find chainblock with name '"+proposalName+"'");
		}
		
		
		if(enforceDateTimeRuleOnTransaction(transactionHeaderCO.getSmartcontract())){
			logger.error("--- W --- Chaincode is still open !");
			return null;
		}
		Chain chain = ledger.getCompleteChain(transactionHeaderCO.getRef());
		//logger.info("--- I --- Total number of citizens participated : "+chain .getTransactions().size());
		ResultsSubmission resultsSubmission = new ResultsSubmission();
		
		
		
		int i=0; 
		CitizenCommentsList citizenCommentsList = new CitizenCommentsList();
		for (TransactionCO t : chain.getTransactions()) {
			String payload = ledger.extractPayload(transactionHeaderCO,t);
			i++;
			try {
				PlanningProposal planningProposal = new ObjectMapper().readValue(payload , PlanningProposal.class);
				resultsSubmission.setPlanningProposal(planningProposal);
				System.out.println(i+"    "+planningProposal.getProposalAddress());
			} catch (Exception e) {
				ConsensusResponse consensusResponse;
				try {
					consensusResponse = new ObjectMapper().readValue(payload , ConsensusResponse.class);
					if(consensusResponse.getResponse() == CONSENSUD_RESPONSE.AGREED){
						resultsSubmission.setAgreed(resultsSubmission.getAgreed()+1); 
					} else if(consensusResponse.getResponse() == CONSENSUD_RESPONSE.DISAGREE){
						resultsSubmission.setDisagreed(resultsSubmission.getDisagreed()+1); 
					} if(consensusResponse.getResponse() == CONSENSUD_RESPONSE.DONT_KNOW){
						resultsSubmission.setDontknow(resultsSubmission.getDontknow()+1); 
					}
					CitizenComments citizenComments = new CitizenComments();
					citizenComments.setComments(consensusResponse.getComments());
					citizenComments.setResourceId(t.getSender());
					citizenComments.setDateTime(t.getCreationTime());
					citizenCommentsList.add(citizenComments);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		String localFilepath = createLocalCommentsFile(citizenCommentsList);
		resultsSubmission.setDocumentURL(localFilepath);
		//logger.info("--- I --- Final result !");
		//logger.info("--- I --- Do you want to submit proposal to City Admin for discussion ?");
		return resultsSubmission;
	}
	private String uploadFile(String localCommentsFileURL, String key){
		return "";
	}

	private String createLocalCommentsFile(CitizenCommentsList citizenCommentsList){
		return "";
	}
	public boolean submitResult(ResourceCO resourceCO, ResultsSubmission resultsSubmission) throws VeidblockException{
		
		String key = "";
		String url = uploadFile(resultsSubmission.getDocumentURL(), key);
		String docHashs= "";
		resultsSubmission.setSecretKey(key);
		resultsSubmission.setDocumentURL(url);
		resultsSubmission.setHash(docHashs);
		
		String resultSubmissionName = "Request4Proposal-2018";
		//logger.info("--- I --- Compiled Resultys successfully submitted !" );
		//logger.info("--- I --- Submittign results in chain " + resultSubmissionName );
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(resultSubmissionName);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		ledger.addTransaction(transactionHeaderCO, new String[]{transactionHeaderCO.getCreator()}, resultsSubmission, new Configuration().getAuthServerUrl());
		//logger.info("--- I --- Compiled Results successfully submitted !" );
		return true;
	}
	
	
	
	
	public TransactionHeaderCO listChaincodes(ResourceCO resourceCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		TransactionHeaders transactionHeaders = ledger.getTransactionHeaders();
		if (Objects.isNull(transactionHeaders) || transactionHeaders.size() == 0) {
			logger.error("--- I --- Block list is empty !");
			return null;
		}
		int blockcNo = 1;
		for (TransactionHeaderCO transactionHeader : transactionHeaders) {

			System.out.println("------- Block No --->" + blockcNo);
			blockcNo++;
			System.out.println(" * Reference No. : " + transactionHeader.getRef());
			System.out.println("\tName : " + transactionHeader.getChainName());
			System.out.println("\tSmart Contract : ");
			System.out.println(transactionHeader.getSmartcontract());
			SmartContract smartContractStruct = null;
			try {
				smartContractStruct = new ObjectMapper().readValue(transactionHeader.getSmartcontract(),
						SmartContract.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("\t\tStart Date : " + smartContractStruct.getStart());
			System.out.println("\t\tEnd Date : " + smartContractStruct.getEnd());
			System.out.println("\t\tScope : " + smartContractStruct.getScope());
			System.out.print("\t\tSupporting Data Type(s) : ");
			for (String str : smartContractStruct.getPayloadSupportingTypes()) {
				System.out.print(" " + str);
			}
			System.out.println(" ");
			System.out.println("\t\tSecurty Level: " + smartContractStruct.getSecurityLevel());
		}
		int i = getInput("Enter Block No : ");
		return transactionHeaders.get(i - 1);
	}

	private static int getInput(String message) {
		System.out.print(message);
		return new Scanner(System.in).nextInt();
	}

	public TransactionCO listResponses(ResourceCO resourceCO, TransactionHeaderCO transactionHeaderCO)
			throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		Transactions transactions = ledger.getTransactionByRef(transactionHeaderCO.getRef());
		if (Objects.isNull(transactions) || transactions.size() == 0) {
			//logger.info("--- I --- Transaction list is empty !");
			return null;
		}
		int tNo = 1;
		for (TransactionCO t : transactions) {
			System.out.println(" ------- Transaction No. --->" + tNo);
			tNo++;
			System.out.println(" Reference No. : " + t.getRef());
			System.out.println(" \tTransaction No. : " + t.getDepth());
			System.out.println(" \tCrypto Operations : " + t.getCryptoOperationsOnPayload());
			System.out.println(" \tCreation Date : " + t.getCreationTime());
			System.out.println(" \tPrevious Hash : " + t.getHashPrevBlock());
			System.out.println(" \tPayload (json) : " + t.getPayload());
			System.out.println(" \tPayload Type : " + t.getPayloadType());
			System.out.println(" \tSender : " + t.getSender());
			System.out.println(" \tPublish Date : " + t.getSignedDate());
		}
		int i = getInput("***Enter Block No : ");
		return transactions.get(i - 1);
	}

	public void verifyTransaction(ResourceCO resourceCO, TransactionCO transactionCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		if (Boolean.parseBoolean(ledger.verify(transactionCO))) {
			System.out.println("-*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***-");
			System.out.println("Verified transaction with previous transactions from ledger service !");
			System.out.println("-*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***-");
		}else{
			System.out.println("-*** *** *** *** *** *** *** **-");
			System.err.println("transaction verificatin failed !");
			System.out.println("-*** *** *** *** *** *** *** **-");
		}
	}

	public void verifyTransactionLocally(ResourceCO resourceCO, TransactionCO transactionCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build();
		if (ledger.verifyTransactionLocally(transactionCO)) {
			System.out.println("-** *** *** *** *** *** *** **-");
			System.out.println("Verified transaction loacally !");
			System.out.println("-** *** *** *** *** *** *** **-");
		} else{
			System.out.println("-*** *** *** *** *** *** *** *** *** ***-");
			System.err.println("Loacally transaction verificatin failed !");
			System.out.println("-*** *** *** *** *** *** *** *** *** ***-");
		}		
	}
	private boolean enforceDateTimeRuleOnTransaction(String smartContractJson) throws VeidblockException {

		SmartContract smartContract = null;
		try {
			smartContract = new ObjectMapper().readValue(smartContractJson, SmartContract.class);
		} catch (IOException e) {
			throw new VeidblockException("Problems when execurity Smart-contract !");
		}
		;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentDateTime = new Date();
		Date startDateStr = smartContract.getStart();
		Date endDateStr = smartContract.getEnd();
		boolean after = currentDateTime.after(startDateStr);
		boolean before = currentDateTime.before(endDateStr);

		if (after & before) {
			return false;
		} else if (after) {
			return true;
		} else if (before) {
			return false;
		}
		throw new VeidblockException("Problems when executing Smartcontract !");
	}
}*/