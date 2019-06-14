package org.acreo.cleint.events.mera;

import java.util.EventListener;

public interface TransHeaderMessageListner extends EventListener {
	public void transHeaderReceived(TransHeaderMessageEvent evt);
}