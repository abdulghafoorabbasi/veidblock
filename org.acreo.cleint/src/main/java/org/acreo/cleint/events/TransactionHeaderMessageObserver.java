package org.acreo.cleint.events;

import java.util.ArrayList;
import java.util.List;

import org.acreo.common.entities.lc.TransactionHeaderCO;

public class TransactionHeaderMessageObserver {

	private List<Observer> observers = new ArrayList<Observer>();
	private TransactionHeaderCO transactionHeaderCO;
	
	public TransactionHeaderCO getTransactionHeaderCO() {
		return this.transactionHeaderCO;
	}

	public void setTransactionHeaderCO(TransactionHeaderCO transactionHeaderCO) {
		this.transactionHeaderCO = transactionHeaderCO;
		notifyAllObservers();
	}
	public void attach(Observer observer) {
		observers.add(observer);
	}

	public void notifyAllObservers() {
		for (Observer observer : observers) {
			observer.update();
		}
	}
}