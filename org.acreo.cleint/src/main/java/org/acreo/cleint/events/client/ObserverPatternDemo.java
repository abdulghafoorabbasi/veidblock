package org.acreo.cleint.events.client;

import org.acreo.cleint.events.TransactionHeaderMessageObserver;
import org.acreo.common.entities.lc.TransactionHeaderCO;

public class ObserverPatternDemo {
	public static void main(String[] args) {
		
		//TMessageObserver subject1 = new TMessageObserver();
		
		//new TransactionListner(subject1);
		
		TransactionHeaderCO transactionHeaderCO = new TransactionHeaderCO();
		transactionHeaderCO.setChainName("Helo Chain 0");
		

		//TransactionCO transactionCO = new TransactionCO();
		//transactionCO.setRef("Helo Chain 1");
		//subject1.setTransactionCO(transactionCO);
		
		// Specify at message generator side
		
		TransactionHeaderMessageObserver subject = new TransactionHeaderMessageObserver();
		new TransactionHeaderListner(subject);
		TransactionHeaderMessageObserver subject1 = new TransactionHeaderMessageObserver();
		new TransactionHeaderListner(subject1);
		subject.setTransactionHeaderCO(transactionHeaderCO);
		subject1.setTransactionHeaderCO(transactionHeaderCO);
		
		
		
	}
}