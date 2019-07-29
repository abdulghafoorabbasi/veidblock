package org.acreo.messaging.event;

import java.util.EventListener;

import org.acreo.common.exceptions.VeidblockException;

public interface TransHeaderMessageListner extends EventListener {
	public void transHeaderReceived(TransHeaderMessageEvent evt) throws VeidblockException;
}