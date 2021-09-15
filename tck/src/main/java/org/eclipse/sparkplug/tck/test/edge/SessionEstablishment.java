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

package org.eclipse.sparkplug.tck.test.edge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
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
public class SessionEstablishment extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private HashMap<String, String> testResults = new HashMap<String, String>();
    String[] testIds = {
    	"message-flow-edge-node-birth-publish-connect",
    	"message-flow-edge-node-birth-publish-subscribe",

    };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    private String host_application_id = null; // The primary host application id to be used
    private boolean commands_supported = true; // Are commands supported by the edge node?
    
    enum TestType {
      GOOD,
      HOST_OFFLINE
    } 
    
    TestType test_type = TestType.GOOD;

    
    /*
     * The 
     */
    
    public SessionEstablishment(TCK aTCK, String[] parms) {
        logger.info("Edge Node session establishment test");
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        host_application_id = parms[0];
        logger.info("Host application id is "+host_application_id);
        
        if (parms.length > 1 && parms[1].equals("false")) {
        	commands_supported = false;
        }
        	
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
    	return "SessionEstablishment";
    }
    
    public String[] getTestIds() {
    	return testIds;
    }
    
    public HashMap<String, String> getResults() {
    	return testResults;
    }
    

    public Optional<WillPublishPacket> checkWillMessage(ConnectPacket packet) {
    	Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
    	if (willPublishPacketOptional.isPresent()) {
    		WillPublishPacket willPublishPacket = willPublishPacketOptional.get();

    		String result = "FAIL";
    		ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
    		if (payload != null && "OFFLINE".equals(StandardCharsets.UTF_8.decode(payload).toString())) {
    			result = "PASS";
    		} 
    		testResults.put("host-topic-phid-death-payload", result);
    		
    		result = "FAIL";
    		if (willPublishPacket.getQos() == Qos.AT_LEAST_ONCE) {
    			result = "PASS";
    		} 
    		testResults.put("host-topic-phid-death-qos", result);

    		result = "FAIL";
    		if (willPublishPacket.getRetain()) {
    			result = "PASS";
    		}
    		testResults.put("host-topic-phid-death-retain", result);
    	}
    	return willPublishPacketOptional;
    }

    @Test
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
    		id = "message-flow-edge-node-birth-publish-connect")
    public void connect(String clientId, ConnectPacket packet) {
        logger.info("Primary host session establishment test - connect");
        
        String result = "FAIL";
        Optional<WillPublishPacket> willPublishPacketOptional = null;
        try
        {
        	willPublishPacketOptional = checkWillMessage(packet);
        	if (willPublishPacketOptional.isPresent()) {
        		result = "PASS";
        	}
        	//testResults.put("primary-application-death-cert", result);
        } catch (Exception e) {
        	logger.info("Exception", e);
        }

        try {
        	myClientId = clientId;
        	state = "CONNECTED";
        	if (!willPublishPacketOptional.isPresent())
        		throw new Exception("Will message is needed");
        	if (packet.getCleanStart() == false)
        		throw new Exception("Clean start should be true");
        	// TODO: what else do we need to check?
        	result = "PASS";
        } catch (Exception e) {
        	logger.info("Test failed "+e.getMessage());
        	result = "FAIL "+e.getMessage();
        }
        testResults.put("message-flow-edge-node-birth-publish-connect", result);
    }
    
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
		
	}
    
    @Test
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
            id = "message-flow-edge-node-birth-publish-subscribe")
    public void subscribe(String clientId, SubscribePacket packet) {
        logger.info("Edge node session establishment test - subscribe");

        if (myClientId.equals(clientId)) {
        	String result = "FAIL";
        	try {
        		if (!state.equals("CONNECTED"))
        			throw new Exception("State should be connected");
        		if (!packet.getSubscriptions().get(0).getTopicFilter().equals("STATE/"+host_application_id))
        			throw new Exception("Topic string wrong");
        		// TODO: what else do we need to check?
        		result = "PASS";
         		state = "SUBSCRIBED";
        	} catch (Exception e) {
        		result = "FAIL "+e.getMessage();
        	}
        	testResults.put("message-flow-edge-node-birth-publish-subscribe", result);
        	
        
        	// A retained message should have been set on the STATE/host_application_id topic to indicate the 
        	// status of the primary host.  The edge node's behavior will vary depending on the result.
        
        }
    }

    @Test
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
            id = "primary-application-state-publish")
    public void publish(String clientId, PublishPacket packet) {
        logger.info("Primary host session establishment test - publish");
        
        if (myClientId.equals(clientId)) {
        	String result = "FAIL";
        	try {
        		if (!state.equals("SUBSCRIBED"))
        			throw new Exception("State should be subscribed");
        		
    			String payload = null;
    			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
    			if (bpayload != null) {
    				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
    			}	
        		if (!payload.equals("ONLINE"))
        			throw new Exception("Payload should be ONLINE");
        		
        		// TODO: what else do we need to check?
        		result = "PASS";
         		state = "PUBLISHED";
        	} catch (Exception e) {
        		result = "FAIL " + e.getMessage();
        	}
        	testResults.put("primary-application-state-publish", result);
        }
        
        theTCK.endTest();
        
    }

}