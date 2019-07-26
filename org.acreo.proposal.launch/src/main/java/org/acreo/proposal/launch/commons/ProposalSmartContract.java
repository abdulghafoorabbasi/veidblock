package org.acreo.proposal.launch.commons;

import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;

public class ProposalSmartContract extends SmartContract{

	
	public ProposalSmartContract() {
		
	}
	
	@Override
	public void init(TransactionHeaderCO headerCO) {
		System.out.println("Init");
		
	}

	@Override
	public void start(TransactionCO header) {
		System.out.println("Start ");
	}

	@Override
	public void close(Chain header) {
		System.out.println("Stop ");
	}

}
