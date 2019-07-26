package org.acreo.ledger.transactions.utils;

import java.util.Objects;

import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;

public class ChainQueryManager {

	private TransactionHeaderService transactionHeaderService;
	private TransactionService transactionService;
	
	public ChainQueryManager(TransactionHeaderService transactionHeaderService, TransactionService transactionService) {
		this.transactionHeaderService = transactionHeaderService;
		this.transactionService = transactionService;
	}
	
	public TransactionHeaders getTransactionHeaders() throws VeidblockException {
		return new Convertor().toTransactionHeaders(this.transactionHeaderService.getTransactionHeaders());
	}

	public TransactionHeaderCO getTransactionHeaderByRef(final String ref) throws VeidblockException {
		return new Convertor().toTransactionHeaderCO(this.transactionHeaderService.getTransactionHeaderByRef(ref));
	}

	public TransactionHeaders getTransactionHeaderByCreator(final String creator) throws VeidblockException {
		return new Convertor().toTransactionHeaders(this.transactionHeaderService.getTransactionHeaderByCreator(creator));
	}

	public TransactionHeaderCO getTransactionHeaderByName(final String name) throws VeidblockException {
		return new Convertor().toTransactionHeaderCO(this.transactionHeaderService.getTransactionHeaderByName(name));
	}
	// Transaction Area 
	
	public Transactions getTransactions() throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactions());
	}

	public Transactions getTransactionByRef(final String ref) throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactionByRef(ref));
	}

	public Transactions getTransactionBySender(final String sender) throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactionBySender(sender));
	}

	public Transactions getTransactionBySenderInChain(final String ref, final String sender) throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactionBySenderInChain(ref, sender));
	}

	public Transactions getTransactionByReceiver(final String receiver) throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactionByReceiver(receiver));
	}
	
	public Transactions getTransactionByReceiverInChain(final String ref, final String receiver) throws VeidblockException {
		return new Convertor().toTransactions(transactionService.getTransactionByReceiverInChain(ref, receiver));
	}
	
	public Transaction getLastTransaction(String ref) {
		return transactionService.getLastTransaction(ref);
	}
	
	public Chain getCompleteChain(final String ref) throws VeidblockException {
		
		TransactionHeaderCO transactionHeader = getTransactionHeaderByRef(ref);
		
		if(Objects.isNull(transactionHeader)){
			return null;
		}
		
		Chain chain = new Chain();
		chain.setTransactionHeader(transactionHeader);
		chain.setTransactions(getTransactionByRef(ref));
		return chain;
	}	
}