/*******************************************************************************
 * Copyright (c) 2021 Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ian Craggs - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.host;

/*
 * This is the primary host Sparkplug send command test.  
 * 
 * There will be a prompt to the person executing the test to send a command to 
 * a device and edge node we will connect.
 * 
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.nio.ByteBuffer;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class SendCommandTest extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
    	"",
    };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    private String host_application_id = null;
	private PublishService publishService = Services.publishService();
    
    public SendCommandTest(TCK aTCK, String[] parms) {
        logger.info("Primary host send command test");
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        host_application_id = parms[0];
        logger.info("Host application id is "+host_application_id);
        
        // First we have to connect an edge node and device      
        state = "ConnectingDevice";
        String payload = "NEW DEVICE";
		Publish message = Builders.publish().topic("SPARKPLUG_TCK/DEVICE_CONTROL").qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes()))
				.build();
		publishService.publish(message);

    }
    
    public void endTest() {
    	state = null;
    	myClientId = null;
    	reportResults(testResults);
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
    }
    
    public String getName() {
    	return "SendCommandTest";
    }
    
    public String[] getTestIds() {
    	return testIds;
    }
    
    public HashMap<String, String> getResults() {
    	return testResults;
    }

	@Override
	public void connect(String clientId, ConnectPacket packet) {
	
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
	
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
	
		if (packet.getTopic().equals("SPARKPLUG_TCK/DEVICE_CONTROL")) {
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			
			if (payload.equals("DEVICE CONNECTED")) {
				
			}
		}
		
	}

}