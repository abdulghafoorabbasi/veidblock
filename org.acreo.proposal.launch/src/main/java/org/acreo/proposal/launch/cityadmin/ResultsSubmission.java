package org.acreo.proposal.launch.cityadmin;

import org.acreo.proposal.launch.commons.PlanningProposal;

public class ResultsSubmission {
	
	private long agreed;
	private long disagreed;
	private long dontknow;
	private String hash;
	private String documentURL;
	private String secretKey;
	private PlanningProposal planningProposal;
	
	public long getAgreed() {
		return agreed;
	}
	public void setAgreed(long agreed) {
		this.agreed = agreed;
	}
	public long getDisagreed() {
		return disagreed;
	}
	public void setDisagreed(long disagreed) {
		this.disagreed = disagreed;
	}
	public long getDontknow() {
		return dontknow;
	}
	public void setDontknow(long dontknow) {
		this.dontknow = dontknow;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getDocumentURL() {
		return documentURL;
	}
	public void setDocumentURL(String documentURL) {
		this.documentURL = documentURL;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public PlanningProposal getPlanningProposal() {
		return planningProposal;
	}
	public void setPlanningProposal(PlanningProposal planningProposal) {
		this.planningProposal = planningProposal;
	}
}
