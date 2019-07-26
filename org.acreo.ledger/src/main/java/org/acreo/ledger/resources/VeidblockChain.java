package org.acreo.ledger.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.acreo.common.Representation;
import org.acreo.common.entities.lc.BlockHeaderCO;
import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.init.LocalCertificateManager;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;
import org.acreo.ledger.transactions.utils.ChainCreationManager;
import org.acreo.ledger.transactions.utils.ChainQueryManager;
import org.acreo.ledger.transactions.utils.ChainUtils;
import org.acreo.ledger.transactions.utils.ChainVerificationManager;
import org.acreo.ledger.transactions.utils.Convertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/vc")
@Produces(MediaType.APPLICATION_JSON)
public class VeidblockChain {

	final static Logger logger = LoggerFactory.getLogger(ChainCreationManager.class);
	private ChainCreationManager chainCreationManager;
	private ChainUtils chainUtils;
	private ChainQueryManager chainQueryManager;
	private ChainVerificationManager chainVerificationManager;
	public static final String PUB_CERT = "Certificate";
	
	@Context
	private UriInfo uriInfo;

	public VeidblockChain(String kafkaIP, int kafkaPort,TransactionHeaderService transactionHeaderService, TransactionService transactionService,
			LocalCertificateManager localCertificateManager) {
		chainUtils = new ChainUtils(localCertificateManager);
		chainQueryManager = new ChainQueryManager(transactionHeaderService, transactionService);
		chainCreationManager = new ChainCreationManager(kafkaIP, kafkaPort, transactionHeaderService, transactionService,
				localCertificateManager);
		chainVerificationManager = new ChainVerificationManager(transactionHeaderService, transactionService);
	}

	// Header Area
	@POST
	@Path("/chain")
	public TransactionHeaderCO addTransationHeader(BlockHeaderCO chainCode) {
		//logger.info("Received new request to create transaction chain header !");
		String pubKeyVerification = uriInfo.getBaseUri() + "pubkey";
		try {
			return chainCreationManager.add(chainCode, pubKeyVerification);

		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/chain")
	public TransactionHeaders getTransactionHeaders() {
		try {
			return this.chainQueryManager.getTransactionHeaders();
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/chain/ref/{ref}")
	public TransactionHeaderCO getTransactionHeaderByRef(@PathParam("ref") final String ref) {
		try {
			return this.chainQueryManager.getTransactionHeaderByRef(ref);
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/chain/owner/{creator}")
	public TransactionHeaders getTransactionHeaderByCreator(@PathParam("creator") final String creator) {

		try {
			return this.chainQueryManager.getTransactionHeaderByCreator(processEncodedURL(creator));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/chain/chainName/{chainName}")
	public TransactionHeaderCO getTransactionHeaderByName(@PathParam("chainName") final String chainName) {
		try {
			return this.chainQueryManager.getTransactionHeaderByName(chainName);
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	// Transactions Area

	@POST
	@Path("/trans/ref/{ref}")
	public TransactionCO addTransaction(@PathParam("ref") final String ref, TransactionBlockCO transactionCO) {
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(transactionCO).length());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		long startTime= System.currentTimeMillis();
		String pubKeyVerification = uriInfo.getBaseUri() + "pubkey";
		Transaction transaction = null;
		try {
			transaction = new Convertor().toTransaction(transactionCO);

			transaction = this.chainCreationManager.addTransaction(ref, transaction, pubKeyVerification);
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
		if (transaction != null) {
			try {
				long endTime= System.currentTimeMillis();
				System.out.println("Time for Creating transaction : " + (endTime-startTime));
				createFile(transaction.getRef(), transaction.getDepth(), endTime, startTime, (endTime-startTime));
				return new Convertor().toTransactionCO(transaction);
			} catch (VeidblockException e) {
				logger.error(e.getMessage());
				throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
			}
		}
		return new TransactionCO();
	}
	
	private void createFile(String ref, long tNo, long e, long s, long d){
		try
		{
		    String filename= "transactionCreationResult.txt";
		    File f = new File(filename);
		    System.out.println(f.getAbsolutePath());
		    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
		    fw.write(ref+"="+tNo+"="+e+"="+s+"="+d+"\n");//appends the string to the file
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
	@PUT
	@Path("/trans")
	public String verify(TransactionCO transaction) {
		try {
			return this.chainVerificationManager.verify(new Convertor().toTransaction(transaction));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/trans/ref/{ref}")
	public Transactions getTransactionByRef(@PathParam("ref") final String ref) {
		try {
			return this.chainQueryManager.getTransactionByRef(ref);
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/trans/sender/{sender}")
	public Transactions getTransactionBySender(@PathParam("sender") final String sender) {
		try {
			return this.chainQueryManager.getTransactionBySender(processEncodedURL(sender));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/trans/receiver/{receiver}")
	public Transactions getTransactionByReceiver(@PathParam("receiver") final String receiver) {
		try {
			return this.chainQueryManager.getTransactionByReceiver(processEncodedURL(receiver));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/trans/ref/{ref}/sender/{sender}")
	public Transactions getTransactionBySenderInChain(@PathParam("ref") final String ref,
			@PathParam("sender") final String sender) {
		
		try {
			return this.chainQueryManager.getTransactionBySenderInChain(ref, processEncodedURL(sender));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("/trans/ref/{ref}/receiver/{receiver}")
	public Transactions getTransactionByRef(@PathParam("ref") final String ref,
			@PathParam("receiver") final String receiver) {
		try {
			return this.chainQueryManager.getTransactionByReceiverInChain(ref, processEncodedURL(receiver));
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@GET
	@Path("{ref}")
	public Chain getCompleteChain(@PathParam("ref") final String ref) {
		try {
			return this.chainQueryManager.getCompleteChain(ref);
		} catch (VeidblockException e) {
			logger.error(e.getMessage());
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
		}
	}

	@POST
	@Path("/pubkey")
	public Representation<String> verifyPublicKey(String encodedKey) {
		return chainUtils.verify(encodedKey);
	}

	private String processEncodedURL(String value) {
		if (value.startsWith("encoded=")) {
			String temp = value.substring(value.indexOf("=") + 1);
			return new String(Base64.getDecoder().decode(temp.getBytes()));
		} else{
			return value;
		}
	}
}