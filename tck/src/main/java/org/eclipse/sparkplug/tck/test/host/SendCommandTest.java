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
 * This is the primary host Sparkplug send command test: 
 * 
 * to check that a command from a primary host under test is correct to both an
 * edge node (NCMD) and a device (DCMD).
 * 
 * There will be a prompt to the person executing the test to send a command to 
 * a device and edge node we will connect.
 * 
 * The host application under test must be connected and online prior to starting this test.
 * The id of the host application must be passed as the firt parameter to this test.
 * The second parameter is the id of the edge node to be used.
 * The third parameter is the id of the device to be used.
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
import com.hivemq.extension.sdk.api.services.publish.*;

import org.eclipse.tahu.SparkplugException;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.SparkplugBPayloadEncoder;
import org.eclipse.tahu.message.model.MessageType;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.MetricDataType;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.message.model.Topic;
import org.eclipse.tahu.message.model.Metric.MetricBuilder;
import org.eclipse.tahu.message.model.SparkplugBPayload.SparkplugBPayloadBuilder;
import org.eclipse.tahu.util.TopicUtil;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class SendCommandTest extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
    	"topics-ncmd-mqtt",
    	"topics-ncmd-timestamp",
    	"topics-ncmd-payload",
    	"topics-dcmd-mqtt",
    	"topics-dcmd-timestamp",
    	"topics-dcmd-payload"
    };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    private String host_application_id = null;
    private String edge_node_id = null;
    private String edge_metric = "TCK_metric/Boolean";
    private String device_id = null;
    private String device_metric = "Inputs/0";
	private PublishService publishService = Services.publishService();
    
    public SendCommandTest(TCK aTCK, String[] parms) {
        logger.info("Primary host send command test");
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        if (parms.length < 3) {
        	logger.info("Parameters to send command test must be: host_application_id edge_node_id device_id");
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
		if (packet.getTopic().equals("SPARKPLUG_TCK/LOG")) {
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			
			if (payload.equals("Device "+device_id+" successfully created")) {
				logger.info("SendCommandTest: Device was created");
				
		        payload = "Send an edge command to edge node "+edge_node_id + " metric "+edge_metric;
				Publish message = Builders.publish().topic("SPARKPLUG_TCK/CONSOLE_PROMPT").qos(Qos.AT_LEAST_ONCE)
						.payload(ByteBuffer.wrap(payload.getBytes()))
						.build();
				logger.info("Requesting command to edge node id: "+edge_node_id);
				publishService.publish(message);
				state = "EXPECT NODE COMMAND";
			}
		}
		else if (packet.getTopic().equals("spBv1.0/SparkplugTCK/NCMD/"+edge_node_id))
		{
			if (state.equals("EXPECT NODE COMMAND")) {
				checkNodeCommand(clientId, packet);
				
		        String payload = "Send an edge command to device "+device_id+" at edge node "+edge_node_id + " metric "+device_metric;
				Publish message = Builders.publish().topic("SPARKPLUG_TCK/CONSOLE_PROMPT").qos(Qos.AT_LEAST_ONCE)
						.payload(ByteBuffer.wrap(payload.getBytes()))
						.build();
				logger.info("Requesting command to device id: "+device_id);
				publishService.publish(message);
				
				state = "EXPECT DEVICE COMMAND";
			}
		}
		else if (packet.getTopic().equals("spBv1.0/SparkplugTCK/DCMD/"+edge_node_id+"/"+device_id))
		{
			if (state.equals("EXPECT DEVICE COMMAND")) {
				checkDeviceCommand(clientId, packet);
				theTCK.endTest();
			}
		}
	}
	
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NCMD,
    		id = "topics-ncmd-mqtt") 
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NCMD,
    		id = "topics-ncmd-timestamp")
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NCMD,
    		id = "topics-ncmd-payload")
	public void checkNodeCommand(String clientId, PublishPacket packet) {
		String result = "FAIL";
		if (packet.getQos() == Qos.AT_MOST_ONCE && 
				packet.getRetain() == false) {
			result = "PASS";
		}
		testResults.put("topics-ncmd-mqtt", result);
		
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();				
		ByteBuffer bpayload = packet.getPayload().orElseGet(null);
		
		SparkplugBPayload inboundPayload = null;
		if (bpayload != null) {
			try {
				byte[] array = new byte[bpayload.remaining()];
				bpayload.get(array);
				inboundPayload = decoder.buildFromByteArray(array);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("Send command test inboundpayload "+inboundPayload);
		
		result = "FAIL";
		if (inboundPayload != null) {
			Date ts = inboundPayload.getTimestamp();
			if (ts != null) {
				result = "PASS";
			}	
		}
		testResults.put("topics-ncmd-timestamp", result);
		
		result = "FAIL";
		if (inboundPayload != null) {
			List<Metric> metrics = inboundPayload.getMetrics();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();
				if (current.getName().equals(edge_metric)) {
					result = "PASS"; 
				}
			}
		}
		testResults.put("topics-ncmd-payload", result);
		logger.info("Send command test payload "+result);
	}
	
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DCMD,
    		id = "topics-dcmd-mqtt") 
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DCMD,
    		id = "topics-dcmd-timestamp")
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DCMD,
    		id = "topics-dcmd-payload")
	public void checkDeviceCommand(String clientId, PublishPacket packet) {
		String result = "FAIL";
		if (packet.getQos() == Qos.AT_MOST_ONCE && 
				packet.getRetain() == false) {
			result = "PASS";
		}
		testResults.put("topics-dcmd-mqtt", result);
		
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();				
		ByteBuffer bpayload = packet.getPayload().orElseGet(null);
		
		SparkplugBPayload inboundPayload = null;
		if (bpayload != null) {
			try {
				byte[] array = new byte[bpayload.remaining()];
				bpayload.get(array);
				inboundPayload = decoder.buildFromByteArray(array);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("Send command test inboundpayload "+inboundPayload);
		
		result = "FAIL";
		if (inboundPayload != null) {
			Date ts = inboundPayload.getTimestamp();
			if (ts != null) {
				result = "PASS";
			}	
		}
		testResults.put("topics-dcmd-timestamp", result);
		
		// Check for metric Inputs/0
		result = "FAIL";
		if (inboundPayload != null) {
			List<Metric> metrics = inboundPayload.getMetrics();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();
				if (current.getName().equals(device_metric)) {
					result = "PASS"; 
				}
			}
		}
		testResults.put("topics-dcmd-payload", result);
		logger.info("Send command test payload "+result);
	}
	
}