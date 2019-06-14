package org.acreo.auth.twofactor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "PairDevice")
public class PairDevice {
	@Id
	@Column(name = "deviceId")
	@NotEmpty
	private long deviceId;
	private long uid;
	private String dpc;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH:mm:ss", timezone = "GMT")
	private Date devicePairDateTime;
	private long expiryPeriod;
	private int deviceNo;
	private long seqNo;

	public PairDevice(){
		
	}
	public PairDevice(long deviceId, long uid, String dpc, Date devicePairDateTime, long expiryPeriod, int deviceNo,
			long seqNo) {

		this.deviceId = deviceId;
		this.uid = uid;
		this.dpc = dpc;
		this.devicePairDateTime = devicePairDateTime;
		this.expiryPeriod = expiryPeriod;
		this.deviceNo = deviceNo;
		this.seqNo = seqNo;
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

	public String getDpc() {
		return dpc;
	}

	public void setDpc(String dpc) {
		this.dpc = dpc;
	}

	public Date getDevicePairDateTime() {
		return devicePairDateTime;
	}

	public void setDevicePairDateTime(Date devicePairDateTime) {
		this.devicePairDateTime = devicePairDateTime;
	}

	public long getExpiryPeriod() {
		return expiryPeriod;
	}

	public void setExpiryPeriod(long expiryPeriod) {
		this.expiryPeriod = expiryPeriod;
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
}
