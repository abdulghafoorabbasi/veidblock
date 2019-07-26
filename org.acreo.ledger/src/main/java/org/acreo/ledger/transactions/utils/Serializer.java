package org.acreo.ledger.transactions.utils;

import java.util.Objects;

import org.acreo.ledger.transactions.entities.Transaction;
import org.acreo.ledger.transactions.entities.TransactionHeader;

public class Serializer {

	public byte[] toByteSerialize(TransactionHeader transactionHeader) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(format(transactionHeader.getRef()));
		stringBuffer.append(format(transactionHeader.getVersion()));
		stringBuffer.append(format(transactionHeader.getCreationTime()));
		stringBuffer.append(format(transactionHeader.getHashPrevBlock()));
		stringBuffer.append(format(transactionHeader.getExtbits()));
		stringBuffer.append(format(transactionHeader.getNonce()));
		stringBuffer.append(format(""+transactionHeader.getHeight()));
		stringBuffer.append(format(transactionHeader.getCreator()));
		stringBuffer.append(format(transactionHeader.getChainName()));
		stringBuffer.append(format(transactionHeader.getSmartcontract()));
		return stringBuffer.toString().getBytes();
		
	}
	
	public byte[] toByteSerialize(Transaction transaction) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(format(transaction.getRef()));
		stringBuffer.append(format(""+transaction.getDepth()));
		stringBuffer.append(format(transaction.getHashPrevBlock()));
		stringBuffer.append(format(transaction.getScope()));
		stringBuffer.append(format(transaction.getSender()));
		stringBuffer.append(format(transaction.getReceiver()));
		stringBuffer.append(format(""+transaction.getPayload()));
		stringBuffer.append(format(transaction.getPayloadType()));
		stringBuffer.append(format(transaction.getCryptoOperationsOnPayload()));
		return stringBuffer.toString().getBytes();
	}
	
	private char delimeter = '|';
	private String format(String value){
		if(Objects.isNull(value)){
			return ""+delimeter;
		}
		return value+=delimeter ;
	}
}
