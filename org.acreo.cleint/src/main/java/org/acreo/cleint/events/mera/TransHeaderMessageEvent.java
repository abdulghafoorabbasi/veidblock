package org.acreo.cleint.events.mera;

import java.util.EventObject;

import org.acreo.common.entities.lc.TransactionHeaderCO;

public class TransHeaderMessageEvent extends EventObject {
	private TransactionHeaderCO transactionHeaderCO;
	public TransHeaderMessageEvent(Object source) {
		    super(source);
		  }
	public TransactionHeaderCO getTransactionHeaderCO() {
		return transactionHeaderCO;
	}
	public void setTransactionHeaderCO(TransactionHeaderCO transactionHeaderCO) {
		this.transactionHeaderCO = transactionHeaderCO;
	}
}
