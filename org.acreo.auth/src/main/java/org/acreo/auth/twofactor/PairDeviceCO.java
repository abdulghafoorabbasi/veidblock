package org.acreo.auth.twofactor;

public class PairDeviceCO {
	
	private long deviceId;
	private long uid;
	private String dpc;
	private long expiryPeriod;
	
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
	public long getExpiryPeriod() {
		return expiryPeriod;
	}

	public void setExpiryPeriod(long expiryPeriod) {
		this.expiryPeriod = expiryPeriod;
	}	
}
