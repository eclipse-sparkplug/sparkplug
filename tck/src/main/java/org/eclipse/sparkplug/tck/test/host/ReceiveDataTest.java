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
 * This is the primary host Sparkplug receive data test.  Data can be received from edge
 * nodes and devices.
 * 
 * We manufacture some data events to be received by the primary host.
 * 
 * To verify that they have been handled correctly, we have to rely on the
 * user running the tests to report the results.
 * 
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.publish.RetainedPublish;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.builder.Builders;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class ReceiveDataTest extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private String edge_metric = "TCK_metric/Int32";
    private String device_metric = "Inputs/0";
        
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
    	"",
    };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    
    private String host_application_id = null;
    private String edge_node_id = null;
    private String device_id = null;
	private PublishService publishService = Services.publishService();
    
    public ReceiveDataTest(TCK aTCK, String[] parms) {
        logger.info("Primary host receive data test");
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        host_application_id = parms[0];
        logger.info("Host application id is "+host_application_id);
        
        if (parms.length < 3) {
        	logger.info("Parameters to receive data test must be: host_application_id edge_node_id device_id");
        	return;
        }
        host_application_id = parms[0];
        logger.info("Host application id is "+host_application_id);
        
        boolean host_online = false;
        String topic = "STATE/"+host_application_id;
        // Check that the host application status is ONLINE, ready for the test
        final CompletableFuture<Optional<RetainedPublish>> getFuture = Services.retainedMessageStore().getRetainedMessage(topic);
        
        try {
        	Optional<RetainedPublish> retainedPublishOptional = getFuture.get();
        	if (retainedPublishOptional.isPresent()) {
        		final RetainedPublish retainedPublish = retainedPublishOptional.get();
    			String payload = null;
        		ByteBuffer bpayload = retainedPublish.getPayload().orElseGet(null);
    			if (bpayload != null) {
    				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
    			}
        		if (!payload.equals("ONLINE")) {
        			logger.info("Host status payload: " + payload);
        		} else {
        			host_online = true;
        		}
        	}
        	else {
        		logger.info("No retained message for topic: " + topic);
        	}
        } catch (InterruptedException | ExecutionException e) {

        }
        
        if (!host_online) { 
        	logger.info("Host application not online - test not started.");
        	return;
        }
        
        edge_node_id = parms[1];
        logger.info("Edge node id is "+edge_node_id);
        
        device_id = parms[2];
        logger.info("Device id is "+device_id);
        
        // First we have to connect an edge node and device.
        // We do this by sending an MQTT control message to the TCK device utility.
        // ONLY DO THIS IF THE EDGE/DEVICE haven't already been created!!
        state = "ConnectingDevice";
        String payload = "NEW DEVICE "+host_application_id+" "+edge_node_id+" "+device_id;
		Publish message = Builders.publish().topic("SPARKPLUG_TCK/DEVICE_CONTROL").qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes()))
				.build();
		logger.info("Requesting new device creation.  Edge node id: "+edge_node_id + " device id: "+device_id);
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
    	return "ReceiveData";
    }
    
    public String[] getTestIds() {
    	return testIds;
    }
    
    public HashMap<String, String> getResults() {
    	return testResults;
    }

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		if (packet.getTopic().equals("SPARKPLUG_TCK/LOG")) {
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			String[] words = payload.split(" ");
			logger.info("Receive data test: "+words[0]+" "+words[1]);
			
			if (state.equals("ConnectingDevice") && payload.equals("Device "+device_id+" successfully created")) {
				logger.info("ReceiveDataTest: Device was created");
							
		        // Now tell the device simulator to send some data from the edge node
		        payload = "SEND_EDGE_DATA "+host_application_id+" "+edge_node_id+" "+edge_metric;
				Publish message = Builders.publish().topic("SPARKPLUG_TCK/DEVICE_CONTROL").qos(Qos.AT_LEAST_ONCE)
						.payload(ByteBuffer.wrap(payload.getBytes()))
						.build();
				logger.info("Requesting data from edge node id: "+edge_node_id + " metric: "+edge_metric);
				publishService.publish(message);
				state = "RequestedNodeData";
				
		        payload = "Data is being sent from edge node "+edge_node_id + " metric "+device_metric+".\n";
		        payload += "Check that the value is updated on the host application";
				message = Builders.publish().topic("SPARKPLUG_TCK/CONSOLE_PROMPT").qos(Qos.AT_LEAST_ONCE)
						.payload(ByteBuffer.wrap(payload.getBytes()))
						.build();
				logger.info("Requesting edge node data check for edge id: "+edge_node_id);
				publishService.publish(message);
			}			
			else if (state.equals("RequestedNodeData") && words[0].equals("CONSOLE_RESPONSE")) {
				if (words[1].equals("OK")) {
					
					// Now tell the device simulator to send some data from the devoce
			        payload = "SEND_DEVICE_DATA "+host_application_id+" "+edge_node_id+" "+device_id+" "+device_metric;
					Publish message = Builders.publish().topic("SPARKPLUG_TCK/DEVICE_CONTROL").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap(payload.getBytes()))
							.build();
					logger.info("Requesting data from device id: "+device_id + " metric: "+device_metric);
					publishService.publish(message);
					state = "RequestedDeviceData";
					
			        payload = "Data is being sent from device "+device_id + " metric "+device_metric+".\n";
			        payload += "Check that the value is updated on the host application";
					message = Builders.publish().topic("SPARKPLUG_TCK/CONSOLE_PROMPT").qos(Qos.AT_LEAST_ONCE)
							.payload(ByteBuffer.wrap(payload.getBytes()))
							.build();
					logger.info("Requesting device data check for device id: "+device_id);
					publishService.publish(message);
				}
			}
			else if (state.equals("RequestedDeviceData") && words[0].equals("CONSOLE_RESPONSE")) {
				theTCK.endTest();
			}
		}
	}
}