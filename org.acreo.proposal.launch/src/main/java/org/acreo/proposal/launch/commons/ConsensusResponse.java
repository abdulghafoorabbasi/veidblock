package org.acreo.proposal.launch.commons;

public class ConsensusResponse {
	public enum CONSENSUD_RESPONSE  {AGREED,DISAGREE, DONT_KNOW}
	public String comments;
	public CONSENSUD_RESPONSE response;
	
	public CONSENSUD_RESPONSE getResponse() {
		return response;
	}
	public void setResponse(CONSENSUD_RESPONSE response) {
		this.response = response;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
