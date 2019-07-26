package org.acreo.proposal.launch.cityadmin;

import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.SmartContract;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;

public class Request4PlanningProposalSmartContract extends SmartContract{

	
	public Request4PlanningProposalSmartContract() {
		
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
