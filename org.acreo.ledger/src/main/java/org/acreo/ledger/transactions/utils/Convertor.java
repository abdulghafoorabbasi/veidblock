package org.acreo.ledger.transactions.utils;

import java.util.List;
import java.util.Objects;

import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;

public class Convertor {
	
	public TransactionHeaderCO toTransactionHeaderCO(final TransactionHeader transactionHeader) throws VeidblockException {
		if(Objects.isNull(transactionHeader)){
			throw new VeidblockException("TransactionHeader not defined !");
		}
		TransactionHeaderCO transactionHeaderCO = new TransactionHeaderCO();
		transactionHeaderCO.setRef(transactionHeader.getRef());
		transactionHeaderCO.setVersion(transactionHeader.getVersion()); 
		transactionHeaderCO.setHashPrevBlock(transactionHeader.getHashPrevBlock()); 
		transactionHeaderCO.setCreationTime(transactionHeader.getCreationTime()); 
		transactionHeaderCO.setHashMerkleRoot(transactionHeader.getHashMerkleRoot());
		transactionHeaderCO.setExtbits(transactionHeader.getExtbits()); 
		transactionHeaderCO.setNonce(transactionHeader.getNonce());
		transactionHeaderCO.setHeight(transactionHeader.getHeight()); 
		transactionHeaderCO.setCreator(transactionHeader.getCreator()); 
		transactionHeaderCO.setChainName(transactionHeader.getChainName());
		transactionHeaderCO.setSmartcontract(transactionHeader.getSmartcontract()); 
		transactionHeaderCO.setSignedBy(transactionHeader.getSignedBy()); 
		transactionHeaderCO.setSignerUrl(transactionHeader.getSignerUrl());
		transactionHeaderCO.setSignature(transactionHeader.getSignature());
		transactionHeaderCO.setCreatorSignature(transactionHeader.getCreatorSignature());
		transactionHeaderCO.setCreatorURL(transactionHeader.getCreatorURL());
		
		return transactionHeaderCO;
	}
	
	public TransactionHeader toTransactionHeader(final TransactionHeaderCO transactionHeaderCO) throws VeidblockException {
		if(Objects.isNull(transactionHeaderCO)){
			throw new VeidblockException("TransactionHeaderCO not defined !");
		}
		TransactionHeader transactionHeader = new TransactionHeader();
		transactionHeader.setRef(transactionHeaderCO.getRef());
		transactionHeader.setVersion(transactionHeaderCO.getVersion()); 
		transactionHeader.setHashPrevBlock(transactionHeaderCO.getHashPrevBlock()); 
		transactionHeader.setCreationTime(transactionHeaderCO.getCreationTime()); 
		transactionHeader.setHashMerkleRoot(transactionHeaderCO.getHashMerkleRoot());
		transactionHeader.setExtbits(transactionHeaderCO.getExtbits()); 
		transactionHeader.setNonce(transactionHeaderCO.getNonce());
		transactionHeader.setHeight(transactionHeaderCO.getHeight()); 
		transactionHeader.setCreator(transactionHeaderCO.getCreator()); 
		transactionHeader.setChainName(transactionHeaderCO.getChainName());
		transactionHeader.setSmartcontract(transactionHeaderCO.getSmartcontract()); 
		transactionHeader.setSignedBy(transactionHeaderCO.getSignedBy()); 
		transactionHeader.setSignerUrl(transactionHeaderCO.getSignerUrl());
		transactionHeader.setSignature(transactionHeaderCO.getSignature());
		transactionHeader.setCreatorSignature(transactionHeaderCO.getCreatorSignature());
		transactionHeader.setCreatorURL(transactionHeaderCO.getCreatorURL());		
		return transactionHeader;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public TransactionCO toTransactionCO(final Transaction transaction) throws VeidblockException {
		if(Objects.isNull(transaction)){
			throw new VeidblockException("Transaction not defined !");
		}
		TransactionCO transactionCO = new TransactionCO();
		transactionCO.setRef(transaction.getRef());
		transactionCO.setDepth(transaction.getDepth());
		transactionCO.setHashPrevBlock(transaction.getHashPrevBlock());
		transactionCO.setCreationTime(transaction.getCreationTime());
		transactionCO.setScope(transaction.getScope());
		transactionCO.setSender(transaction.getSender());
		transactionCO.setReceiver(transaction.getReceiver());
		transactionCO.setPayload(transaction.getPayload());
		transactionCO.setPayloadType(transaction.getPayloadType());
		transactionCO.setCryptoOperationsOnPayload(transaction.getCryptoOperationsOnPayload());
		transactionCO.setSignedBy(transaction.getSignedBy());
		transactionCO.setSignedDate(transaction.getSignedDate());
		transactionCO.setSignerUrl(transaction.getSignerUrl());		
		transactionCO.setSignature(transaction.getSignature());
		
		transactionCO.setCreatorSignature(transaction.getCreatorSignature());		
		transactionCO.setCreatorURL(transaction.getCreatorURL());
		
		return transactionCO;
	}	
	
	public Transaction toTransaction(final TransactionCO transactionIn) throws VeidblockException {
		if(Objects.isNull(transactionIn)){
			throw new VeidblockException("TransactionCO not defined !");
		}
		Transaction transaction = new Transaction();
		transaction.setRef(transactionIn.getRef());
		transaction.setDepth(transactionIn.getDepth());
		transaction.setHashPrevBlock(transactionIn.getHashPrevBlock());
		transaction.setCreationTime(transactionIn.getCreationTime());
		transaction.setScope(transactionIn.getScope());
		transaction.setSender(transactionIn.getSender());
		transaction.setReceiver(transactionIn.getReceiver());
		transaction.setPayload(transactionIn.getPayload());
		transaction.setPayloadType(transactionIn.getPayloadType());
		transaction.setCryptoOperationsOnPayload(transactionIn.getCryptoOperationsOnPayload());
		transaction.setSignedBy(transactionIn.getSignedBy());
		transaction.setSignedDate(transactionIn.getSignedDate());
		transaction.setSignerUrl(transactionIn.getSignerUrl());		
		transaction.setSignature(transactionIn.getSignature());
		
		transaction.setCreatorSignature(transactionIn.getCreatorSignature());		
		transaction.setCreatorURL(transactionIn.getCreatorURL());
		
		return transaction;
	}	
	
	
	public Transactions toTransactions(List<Transaction> transactionRet) throws VeidblockException{
		if(Objects.isNull(transactionRet)){
			throw new VeidblockException("List<Transaction> not defined !");
		}
		Transactions transactions =new  Transactions();
		for(Transaction transactionTmp: transactionRet){
			transactions.add(toTransactionCO(transactionTmp));
		}
		return transactions;
	}
	
	public TransactionHeaders toTransactionHeaders(List<TransactionHeader> transactionHeaderRet) throws VeidblockException{
		if(Objects.isNull(transactionHeaderRet)){
			throw new VeidblockException("List<TransactionHeader> not defined !");
		}
		TransactionHeaders transactionHeaders =new TransactionHeaders();
		for(TransactionHeader transactionHeaderTmp: transactionHeaderRet){
			transactionHeaders.add(toTransactionHeaderCO(transactionHeaderTmp));
		}
		return transactionHeaders;
	}	
	
	public Transaction toTransaction(TransactionBlockCO transactionCO) throws VeidblockException{
		if(Objects.isNull(transactionCO)){
			throw new VeidblockException("TransactionBlockCO not defined !");
		}
		Transaction transaction = new Transaction ();
		transaction.setCreationTime(transactionCO.getCreationTime());
		transaction.setCryptoOperationsOnPayload(transactionCO.getCryptoOperationsOnPayload());
		transaction.setPayload(transactionCO.getPayload());
		transaction.setPayloadType(transactionCO.getPayloadType());
		transaction.setReceiver(transactionCO.getReceiver());
		transaction.setScope(transactionCO.getScope());
		transaction.setSender(transactionCO.getSender());
		transaction.setSignature(transactionCO.getSignature());
		transaction.setSignedBy(transactionCO.getSignedBy());
		transaction.setSignedDate(transactionCO.getSignedDate());
		transaction.setSignerUrl(transactionCO.getSignerUrl());
		transaction.setCreatorSignature(transactionCO.getSignature());
		transaction.setCreatorURL(transactionCO.getSignerUrl());
		
		return transaction;
	}
	

}
