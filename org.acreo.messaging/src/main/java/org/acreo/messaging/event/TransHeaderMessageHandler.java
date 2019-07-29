package org.acreo.messaging.event;

import javax.swing.event.EventListenerList;

import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.exceptions.VeidblockException;

public class TransHeaderMessageHandler {
	protected static EventListenerList listenerList = new EventListenerList();

	public synchronized void addEventListener(TransHeaderMessageListner listener) {
		listenerList.add(TransHeaderMessageListner.class, listener);
	}

	public synchronized void removeEventListener(TransHeaderMessageListner listener) {
		listenerList.remove(TransHeaderMessageListner.class, listener);
	}

	public void fireEvent(TransHeaderMessageEvent evt) throws VeidblockException {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == TransHeaderMessageListner.class) {
				((TransHeaderMessageListner) listeners[i + 1]).transHeaderReceived(evt);
			}
		}
	}
}
