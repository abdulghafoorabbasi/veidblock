package org.acreo.cleint.events.mera;

import javax.swing.event.EventListenerList;

import org.acreo.common.entities.lc.TransactionHeaderCO;

public class TransMessageHandler {
	protected static EventListenerList listenerList = new EventListenerList();

	public void addMyEventListener(TransHeaderMessageListner listener) {
		listenerList.add(TransHeaderMessageListner.class, listener);
	}

	public void removeMyEventListener(TransHeaderMessageListner listener) {
		listenerList.remove(TransHeaderMessageListner.class, listener);
	}

	void fireMyEvent(TransHeaderMessageEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i + 2) {
			if (listeners[i] == TransHeaderMessageListner.class) {
				((TransHeaderMessageListner) listeners[i + 1]).transHeaderReceived(evt);
			}
		}
	}
}
