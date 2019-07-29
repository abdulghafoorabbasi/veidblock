package org.acreo.messaging.event;

import java.util.EventListener;

import org.acreo.common.exceptions.VeidblockException;

public interface TransMessageListner extends EventListener {
	public void transReceived(TransMessageEvent evt) throws VeidblockException;
}