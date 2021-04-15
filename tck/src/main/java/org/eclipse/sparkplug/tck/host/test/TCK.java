package org.eclipse.sparkplug.tck.host.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sparkplug.tck.host.test.TCKTest;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;

public class TCK {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	
	private TCKTest current = null;
	
	public void newTest(String test) {
		
		logger.info("Test requested "+test);
		
		if (test.equals("SessionEstablishment")) { // TODO: create test instance from string
			current = new SessionEstablishment(this);
		}
	}
	
	public void endTest() {
		
		if (current != null) { 
			logger.info("Test end requested for "+current.getName());
			current.endTest();
			current = null;
		} else {
			logger.info("Test end requested but no test active");
		}
			
	}
	
	public void connect(String clientId, ConnectPacket packet) {
		if (current != null) {
			current.connect(clientId, packet);
		}
	}
	
	public void subscribe(String clientId, SubscribePacket packet) {
		if (current != null) {
			current.subscribe(clientId, packet);
		}
	}
	
	public void publish(String clientId, PublishPacket packet) {
		if (current != null) {
			current.publish(clientId, packet);
		}
	}
	
}