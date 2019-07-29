package org.acreo.messaging.event;

import java.util.EventObject;

import org.acreo.common.entities.lc.TransactionCO;

public class TransMessageEvent extends EventObject {
	public TransMessageEvent(Object source) {
		super(source);
	}
}
