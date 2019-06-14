package org.acreo.auth.twofactor;

import java.util.Date;

public class EnrolledDevice {
	public String deviceId;
	public String uid;
	public String DPC;
	public Date devicePairDate;
	public String signature;
	public String publicKey;
}
