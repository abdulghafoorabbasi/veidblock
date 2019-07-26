package org.acreo.ledger.transactions.utils;

import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;
import org.acreo.ledger.transactions.service.TransactionHeaderService;
import org.acreo.ledger.transactions.service.TransactionService;
import org.acreo.messaging.event.TransHeaderMessageEvent;
import org.acreo.messaging.event.TransHeaderMessageHandler;
import org.acreo.messaging.event.TransHeaderMessageListner;
import org.acreo.messaging.event.TransMessageEvent;
import org.acreo.messaging.event.TransMessageHandler;
import org.acreo.messaging.event.TransMessageListner;

public class PeerMessageHandler {

	private TransactionHeaderService transactionHeaderService;
	private TransactionService transactionService;
	private Convertor convertor = new Convertor();
	
	public PeerMessageHandler(TransactionHeaderService transactionHeaderService, TransactionService transactionService){
		this.transactionHeaderService = transactionHeaderService;;
		this.transactionService = transactionService;
	}
	
	
	public void handlePeerTransactionHeader(){
		TransHeaderMessageHandler c = new TransHeaderMessageHandler();
		c.addEventListener(new TransHeaderMessageListner() {
			public void transHeaderReceived(TransHeaderMessageEvent evt) throws VeidblockException{
				TransactionHeader transactionHeader = convertor.toTransactionHeader((TransactionHeaderCO)evt.getSource());
				transactionHeaderService.createTransactionHeader(transactionHeader);							
			}		
		});
	}
	public void handlePeerTransaction() {
		TransMessageHandler c = new TransMessageHandler();
		c.addEventListener(new TransMessageListner() {
			public void transReceived(TransMessageEvent evt) throws VeidblockException  {
				Transaction transaction = convertor.toTransaction((TransactionCO)evt.getSource());
				transactionService.createTransaction(transaction);							
			}		
		});
	}
}
