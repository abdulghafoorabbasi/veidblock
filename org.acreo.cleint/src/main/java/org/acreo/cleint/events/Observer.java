package org.acreo.cleint.events;

public abstract class Observer {
	protected TransactionHeaderMessageObserver thMessageObserver;
	public abstract void update();
}
