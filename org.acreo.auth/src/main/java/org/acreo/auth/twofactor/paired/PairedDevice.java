package org.acreo.auth.twofactor.paired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "PairedDevice")
public class PairedDevice {
	@Id
	@Column(name = "deviceId")
	@NotEmpty
	private long deviceId;
	private long uid;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH:mm:ss", timezone = "GMT")
	private Date devicePairDateTime;
	private int deviceNo;
	private long seqNo;
	private String signature;
	private String pubKey;
	public PairedDevice(){
		
	}
	public PairedDevice(long deviceId,	long uid, Date devicePairDateTime, int deviceNo, long seqNo, String signature, String pubKey){
		this.deviceId = deviceId;
		this.uid = uid;
		this.devicePairDateTime = devicePairDateTime;
		this.deviceNo = deviceNo;
		this.seqNo = seqNo;
		this.signature = signature;
		this.pubKey = pubKey;		
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
