package org.acreo.cleint.events.client;

import org.acreo.cleint.events.Observer;
import org.acreo.cleint.events.TransactionHeaderMessageObserver;

public class TransactionHeaderListner extends Observer {

	public TransactionHeaderListner(TransactionHeaderMessageObserver thMessageObserver) {
		this.thMessageObserver = thMessageObserver;
		this.thMessageObserver.attach(this);
	}

	@Override
	public void update() {
		System.out.println("Hex String: "+thMessageObserver.getTransactionHeaderCO().getChainName());
	}
}