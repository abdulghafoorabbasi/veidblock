package org.acreo.auth.twofactor.paired;

import java.util.Date;

import org.acreo.auth.twofactor.PairDevice;

public class PairedDeviceCO {
	private long deviceId;
	private long uid;
	private Date devicePairDateTime;
	private int deviceNo;
	private long seqNo;
	private String signature;
	private String pubKey;
	public PairedDeviceCO(){
		
	}
	public PairedDeviceCO(PairedDevice PairedDevice){
		this.deviceId = PairedDevice.getDeviceId();
		this.uid = PairedDevice.getUid();
		this.devicePairDateTime = PairedDevice.getDevicePairDateTime();
		this.deviceNo = PairedDevice.getDeviceNo();
		this.seqNo = PairedDevice.getSeqNo();
		this.signature = PairedDevice.getSignature();
		this.pubKey = PairedDevice.getPubKey();		
	}
	
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public Date getDevicePairDateTime() {
		return devicePairDateTime;
	}
	public void setDevicePairDateTime(Date devicePairDateTime) {
		this.devicePairDateTime = devicePairDateTime;
	}
	public int getDeviceNo() {
		return deviceNo;
	}
	public void setDeviceNo(int deviceNo) {
		this.deviceNo = deviceNo;
	}
	public long getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(long seqNo) {
		this.seqNo = seqNo;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getPubKey() {
		return pubKey;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
}