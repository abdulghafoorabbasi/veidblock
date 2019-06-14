package org.acreo.auth.twofactor;

public class PairDeviceCleaner extends Thread {

	private PairDeviceService pairDeviceService;
	private int min;

	public PairDeviceCleaner(PairDeviceService pairDeviceService, int min) {
		this.pairDeviceService = pairDeviceService;
		this.min = min;
	}

	public void run() {
		while (true) {
			try {
				pairDeviceService.deleteExpiredEntries(min);
				Thread.sleep(20000);				
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	}
}
