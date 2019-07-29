package org.acreo.messaging.event;

import javax.swing.event.EventListenerList;

import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;

public class TransMessageHandler {
	protected static EventListenerList listenerList = new EventListenerList();

	public synchronized void addEventListener(TransMessageListner listener) {
		listenerList.add(TransMessageListner.class, listener);
	}

	public synchronized void removeEventListener(TransMessageListner listener) {
		listenerList.remove(TransMessageListner.class, listener);
	}

	public void fireEvent(TransMessageEvent evt) throws VeidblockException {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == TransMessageListner.class) {
				((TransMessageListner) listeners[i + 1]).transReceived(evt);
			}
		}
	}
}
