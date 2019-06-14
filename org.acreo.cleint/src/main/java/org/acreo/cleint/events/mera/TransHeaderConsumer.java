package org.acreo.cleint.events.mera;

public class TransHeaderConsumer {
	public static void main(String[] argv) throws Exception {
		
		
		int i=0;
		while(i <= 2){
			new TransHeaderConsumer().fireEvent();
			i++;
		}
		
	}
	
	public void fireEvent(){
		TransMessageHandler c = new TransMessageHandler();
		TransHeaderMessageEvent ee = new TransHeaderMessageEvent("Hello");
		c.fireMyEvent(ee);
	}
	
}
