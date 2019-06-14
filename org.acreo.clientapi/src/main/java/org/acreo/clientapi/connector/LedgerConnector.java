package org.acreo.clientapi.connector;

import org.acreo.clientapi.utils.AuthenticationHeader;
import org.acreo.clientapi.utils.ClientAuthenticator;
import org.acreo.common.Representation;
import org.acreo.common.entities.lc.BlockHeaderCO;
import org.acreo.common.entities.lc.Chain;
import org.acreo.common.entities.lc.TransactionBlockCO;
import org.acreo.common.entities.lc.TransactionCO;
import org.acreo.common.entities.lc.TransactionHeaderCO;
import org.acreo.common.entities.lc.TransactionHeaders;
import org.acreo.common.entities.lc.Transactions;
import org.acreo.common.exceptions.VeidblockException;
import org.acreo.common.utils.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LedgerConnector {
	private RestClient restClient = null;

	private ClientAuthenticator authenticator = null;
	public LedgerConnector(RestClient restClient,ClientAuthenticator authenticator) {
		this.restClient = restClient;
		this.authenticator = authenticator;
	}

	public TransactionHeaderCO addTransationHeader(BlockHeaderCO chainCode, String verifier) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.post("/vc/chain", chainCode, AuthenticationHeader.authHeader(verifier, authenticator));
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionHeaderCO transactionHeaderCO = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaderCO.class);
			return transactionHeaderCO;

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public TransactionHeaders getTransactionHeaders() throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/chain", null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionHeaders transactionHeaders = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaders.class);
			return transactionHeaders;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}

	}

	public TransactionHeaderCO getTransactionHeaderByRef(final String ref) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/chain/ref/" + ref, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionHeaderCO transactionHeaderCO = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaderCO.class);
			return transactionHeaderCO;

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public TransactionHeaders getTransactionHeaderByCreator(final String creator) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/chain/owner/" + creator, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionHeaders transactionHeaders = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaders.class);
			return transactionHeaders;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public TransactionHeaderCO getTransactionHeaderByName(final String chainName) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/chain/chainName/" + chainName, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionHeaderCO transactionHeaderCO = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionHeaderCO.class);
			return transactionHeaderCO;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public TransactionCO addTransaction(final String ref, TransactionBlockCO transactionBlockCO, String verifier)
			throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.post("/vc/trans/ref/" + ref, transactionBlockCO,
					AuthenticationHeader.authHeader(verifier, authenticator));
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			TransactionCO transactionCO_ = new ObjectMapper().readValue(response.getBody().toString(),
					TransactionCO.class);
			return transactionCO_;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public String verify(TransactionCO transactionCO) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.put("/vc/trans", transactionCO, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			String str = new ObjectMapper().readValue(response.getBody().toString(), String.class);
			return str;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Transactions getTransactionByRef(final String ref) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/trans/ref/" + ref, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			return transactions;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Transactions getTransactionBySender(final String sender) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/trans/sender/" + sender, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			return transactions;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Transactions getTransactionByReceiver(final String receiver) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/trans/receiver/" + receiver, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			return transactions;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Transactions getTransactionBySenderInChain(final String ref, final String sender) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/trans/ref/" + ref + "/sender/" + sender, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			return transactions;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Transactions getTransactionByRef(final String ref, final String receiver) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/trans/ref/" + ref + "/receiver/" + receiver, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Transactions transactions = new ObjectMapper().readValue(response.getBody().toString(), Transactions.class);
			return transactions;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}

	public Chain getCompleteChain(final String ref) throws VeidblockException {
		Representation<?> response = null;
		try {
			response = restClient.get("/vc/" + ref, null);
			if(response.getCode() != 200 ){
				throw new VeidblockException("Error Code: "+response.getCode()+", Message : "+response.getBody().toString());
			}
			Chain chain = new ObjectMapper().readValue(response.getBody().toString(), Chain.class);
			return chain;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			throw new VeidblockException(e1);
		}
	}
}
