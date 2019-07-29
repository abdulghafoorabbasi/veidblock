package org.acreo.messaging.event;

import java.util.EventObject;

import org.acreo.common.entities.lc.TransactionHeaderCO;

public class TransHeaderMessageEvent extends EventObject {
	public TransHeaderMessageEvent(Object source) {
		super(source);
	}
}
