package org.acreo.proposal.launch.cityadmin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.SmartContract.SCOPE;
import org.acreo.common.entities.lc.SmartContract.SECURITY_LEVEL;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.proposal.launch.commons.CitizenComments;
import org.acreo.proposal.launch.commons.CitizenCommentsList;
import org.acreo.proposal.launch.main.RemoteFileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CityAdminMain {

	final static Logger logger = LoggerFactory.getLogger(CityAdminMain.class);

	public String proposalName = "Request4Proposal-2018";
	ClientAuthenticator authenticator = null;
	//private String fileName = "/home/aghafoor/Desktop/Development/veidblockledger/org.acreo.ipv/users.txt";
	
	public void cityadmin(String[] args) throws VeidblockException, IOException {
		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		CityAdminMain planningProposalMain = new CityAdminMain();
		planningProposalMain.options(args[1]);	
	}
	
	
	
	public static void main(String[] args) throws VeidblockException, IOException {
		if (args != null && args.length >= 1) {
			new CityAdminMain().loadPropertyFile(args[0]);
		}
		CityAdminMain planningProposalMain = new CityAdminMain();
		planningProposalMain.options(args[1]);
		/*ResourceCO resourceCO = planningProposalMain.loadPasswordFile("cityadmin", args[1]);
		planningProposalMain.init(resourceCO);
		*/
		//planningProposalMain.createProposalChain(resourceCO);
		//TransactionHeaderCO transactionHeaderCO = planningProposalMain.listChaincodes(resourceCO);
		
		//planningProposalMain.listResponses(resourceCO, transactionHeaderCO);
		
	}
	
	public void options(String fileName) throws IOException, VeidblockException {
		ResourceCO resourceCO = loadPasswordFile("cityadmin", fileName);

		init(resourceCO);
		while (true)
			try {
				System.out.println("*****************************************************");
				System.out.println("*  1. Create Request4Proposal as City Administrator *");
				System.out.println("*  2. List all chain (Headers)                      *");
				System.out.println("*  3. List all transactions                         *");
				System.out.println("*  4. List Proposal Received                        *");
				System.out.println("*  5. Exit                                          *");
				System.out.println("*****************************************************");
				System.out.print("Please enter option : ");
				Scanner reader = new Scanner(System.in);
				String temp = reader.nextLine();
				int i = Integer.parseInt(temp);
				switch (i) {
				case 1:
					createProposalChain(resourceCO);
					break;
				case 2:
					listChaincodes(resourceCO);
					break;
				case 3:
					TransactionHeaderCO transactionHeaderCO = listChaincodesResponse(resourceCO);
					if(!Objects.isNull(transactionHeaderCO))
						listResponses(resourceCO, transactionHeaderCO);
					break;
				case 4:
					listProposals(resourceCO); 
					break;
				case 5:
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
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		SmartContract smartContractStruct = new Request4PlanningProposalSmartContract();
		smartContractStruct.setStart(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); // Now use today date.
		calendar.add(Calendar.DATE, 365); // Adds 365 days
		smartContractStruct.setEnd(calendar.getTime());
		smartContractStruct.setPayloadSupportingTypes(new String[] { ResultsSubmission.class.getName()});
		smartContractStruct.setScope(SCOPE.OPEN);
		smartContractStruct.setSecurityLevel(SECURITY_LEVEL.DIGITAL_SIGNATURE_ENVELOPED);
		try {
			TransactionHeaderCO transactionHeaderCO = ledger.addTransationHeader(proposalName,
					smartContractStruct, new Configuration().getAuthServerUrl());
			//logger.info("--- I --- Chainblock with reference No." + transactionHeaderCO + " successfully created !");
		} catch (VeidblockException vbe) {
			logger.error("--- E --- " + vbe.getMessage());

		}
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

	public void listProposals(ResourceCO resourceCO)
			throws VeidblockException {
		
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
		
		TransactionHeaderCO transactionHeaderCO = ledger.getTransactionHeaderByName(proposalName);
		if (Objects.isNull(transactionHeaderCO ) ) {
			//logger.info("--- E --- Could not find chaincode with name "+proposalName+" !");
			return; 
		}
		
		Transactions transactions = ledger.getTransactionByRef(transactionHeaderCO.getRef());
		if (Objects.isNull(transactions) || transactions.size() == 0) {
			//logger.info("--- I --- Transaction list is empty !");
			return; 
		}
		int tNo = 1;
		for (TransactionCO t : transactions) {
			String payload = ledger.extractPayload(transactionHeaderCO,t);
			try {
			
				ResultsSubmission resultsSubmission = new ObjectMapper().readValue(payload, ResultsSubmission.class);
				System.out.println("Proposal : " +tNo );
				System.out.println("\tSubmitted by : " + t.getSender());
				System.out.println("\tSubmitted on : " + t.getCreationTime());
				System.out.println("\tTotal Agreed : "+resultsSubmission.getAgreed());
				System.out.println("\tTotal Disagreed : " + resultsSubmission.getDisagreed());
				System.out.println("\tTotal Don't Know : " + resultsSubmission.getDontknow());				
				System.out.println("\tFollowing are the comments: ");
				try {
					CitizenCommentsList citizenCommentsListdwn = new RemoteFileHandler().downloadComments(resultsSubmission.getDocumentURL(), resultsSubmission.getSecretKey());
					for(CitizenComments citizenComments : citizenCommentsListdwn ){
						System.out.println(citizenComments.getComments());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
			
			}
		}
		
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
	}
	
	
	
	public void verifyTransaction(ResourceCO resourceCO, TransactionCO transactionCO) throws VeidblockException {
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
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
		Ledger ledger = Ledger.builder().resource(resourceCO).build(authenticator);
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
}