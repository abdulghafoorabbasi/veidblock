package org.acreo.ledger.transactions.utils;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;
import org.acreo.security.crypto.CryptoPolicy;
import org.acreo.security.crypto.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainVerificationManager {

	final static Logger logger = LoggerFactory.getLogger(ChainCreationManager.class);
	private TransactionHeaderService transactionHeaderService;
	private TransactionService transactionService;

	public ChainVerificationManager(TransactionHeaderService transactionHeaderService,
			TransactionService transactionService) {
		this.transactionHeaderService = transactionHeaderService;
		this.transactionService = transactionService;
	}

	public String verify(Transaction transaction) throws VeidblockException {
		TransactionHeader transactionHeader = transactionHeaderService.getTransactionHeaderByRef(transaction.getRef());
		if (Objects.isNull(transactionHeader)) {
			logger.error("Ref [" + transaction.getRef() + "] not found !");
			throw new WebApplicationException(
					Response.status(Status.NOT_FOUND).entity("Ref [" + transaction.getRef() + "] not found !").build());
		}
		List<Transaction> transactions = transactionService.getTransactionsByRefAndDepth(transaction.getRef(),
				transaction.getDepth());
		System.out.println(transactions.size());
		if (Objects.isNull(transactionHeader)) {
			logger.error("Chain [" + transaction.getRef() + "] does not have any transaction !");
			throw new WebApplicationException(Response.status(Status.NOT_FOUND)
					.entity("Chain [" + transaction.getRef() + "] does not have any transaction !").build());
		}

		if (transactions.size() == 0) {
			logger.error("Chain [" + transaction.getRef() + "] does not have any transaction !");
			throw new WebApplicationException(Response.status(Status.EXPECTATION_FAILED)
					.entity("Chain [" + transaction.getRef() + "] does not have any transaction !").build());
		}
		for (int i = transactions.size(); i > 1; i--) {
			Transaction current = transactions.get(i - 1);
			Transaction previous = transactions.get(i - 2);
			if (!verify(current, previous)) {
				
				throw new WebApplicationException(Response.status(Status.EXPECTATION_FAILED)
						.entity("Problems when verifying transactions with ref number [" + transaction.getRef() + "] !")
						.build());
			}
		}
		
		Transaction firstTransaction = transactions.get(0);
		if (!verify(firstTransaction, transactionHeader)) {
			logger.error("Ref [" + transaction.getRef() + "] not found !");
			throw new WebApplicationException(Response.status(Status.EXPECTATION_FAILED)
					.entity("Problems when verifying transactions with ref number [" + transaction.getRef() + "] !")
					.build());
		}
		//logger.info("Successfully verified transaction chain with ref number [" + transaction.getRef() + "]!");
		return "true";
	}

	public boolean verify(Transaction current, Transaction previous) throws VeidblockException {

		Hashing hashing = new Hashing(new CryptoPolicy());
		Serializer serializer = new Serializer();
		byte previousSerl[] = serializer.toByteSerialize(previous);
		byte[] previousHash = hashing.generateHash(previousSerl);
		String encodedPreviousHash = new String(Base64.getEncoder().encode(previousHash));
		boolean result = current.getHashPrevBlock().equals(encodedPreviousHash);
		return current.getHashPrevBlock().equals(encodedPreviousHash);
	}

	public boolean verify(Transaction firstTransaction, TransactionHeader transactionheader) throws VeidblockException {

		Hashing hashing = new Hashing(new CryptoPolicy());
		Serializer serializer = new Serializer();
		byte previousSerl[] = serializer.toByteSerialize(transactionheader);
		byte[] previousHash = hashing.generateHash(previousSerl);
		String encodedPreviousHash = new String(Base64.getEncoder().encode(previousHash));
		boolean result = firstTransaction.getHashPrevBlock().equals(encodedPreviousHash);
		return firstTransaction.getHashPrevBlock().equals(encodedPreviousHash);
	}

}