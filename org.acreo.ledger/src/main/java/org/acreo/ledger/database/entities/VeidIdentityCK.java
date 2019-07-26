package org.acreo.ledger.database.entities;

import java.io.Serializable;

public class VeidIdentityCK implements Serializable {
	protected long veid;
	protected long counter;

	public VeidIdentityCK() {
	}

	public VeidIdentityCK(long veid, long counter) {
		this.veid = veid;
		this.counter = counter;
	}
}
