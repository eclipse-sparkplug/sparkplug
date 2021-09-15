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

/*
 * This is the edge node Sparkplug send data test.  Data can be sent from edge
 * nodes and devices.
 * 
 * We will need to prompt the user to initiate sending some data messages from
 * an edge node and device, and then check that those messages adhere to the 
 * Sparkplug standard.
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

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class SendDataTest extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
        	"topics-ndata-mqtt",
        	"topics-ndata-seq-num",
        	"topics-ndata-timestamp",
        	"topics-ndata-payload",
        	"topics-ddata-mqtt",
        	"topics-ddata-seq-num",
        	"topics-ddata-timestamp",
        	"topics-ddata-payload"
        };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    private String host_application_id = null;
    private String edge_node_id = null;
    private String device_id = null;
    private boolean edge_node_checked = false,
    		device_checked = false;
    
    public SendDataTest(TCK aTCK, String[] parms) {
        logger.info(getName());
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        if (parms.length < 3) {
        	logger.info("Parameters to edge send data test must be: host_application_id edge_node_id device_id");
        	return;
        }
        
        host_application_id = parms[0];
        logger.info("Host application id is "+host_application_id);
        
        edge_node_id = parms[1];
        logger.info("Edge node id is "+edge_node_id);
        
        device_id = parms[2];
        logger.info("Device id is "+device_id);
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
    	return "Sparkplug Edge Node Send Data Test";
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
		String cmd = "";

		String[] levels = packet.getTopic().split("/");
		if (levels.length >= 3) {
			cmd = levels[2];
			
		}
		
		if (cmd.equals("NDATA")) {
			// namespace/group_id/NDATA/edge_node_id
			checkNodeData(clientId, packet);
		} else if (cmd.equals("DDATA")) {
			// namespace/group_id/DDATA/edge_node_id/device_id
			checkDeviceData(clientId, packet);
		}

		if (edge_node_checked && device_checked) {
			theTCK.endTest();
		}
	}
	
	
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NDATA,
    		id = "topics-ndata-mqtt") 
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NDATA,
    		id = "topics-ndata-seq-num") 
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NDATA,
    		id = "topics-ndata-timestamp")
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_NDATA,
    		id = "topics-ndata-payload")
	public void checkNodeData(String clientId, PublishPacket packet) {
		String result = "FAIL";
		if (packet.getQos() == Qos.AT_MOST_ONCE && 
				packet.getRetain() == false) {
			result = "PASS";
		}
		testResults.put("topics-ndata-mqtt", result);
		
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
		logger.info("Send data test inboundpayload "+inboundPayload);
		
		result = "FAIL";
		if (inboundPayload != null) {
			long seqno = inboundPayload.getSeq();
			if (seqno >= 0) {
				result = "PASS";
			}	
		}
		testResults.put("topics-ndata-seq-num", result);
		
		result = "FAIL";
		if (inboundPayload != null) {
			Date ts = inboundPayload.getTimestamp();
			if (ts != null) {
				result = "PASS";
			}	
		}
		testResults.put("topics-ndata-timestamp", result);
		
		result = "FAIL";
		if (inboundPayload != null) {
			List<Metric> metrics = inboundPayload.getMetrics();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();
				// TODO: Must include metrics that have changed
				//if (current.getName().equals(edge_metric)) {
					result = "PASS"; 
				//}
			}
		}
		testResults.put("topics-ndata-payload", result);
		logger.info("Send data test payload "+result);
		edge_node_checked = true;
	}
	
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DDATA,
    		id = "topics-ddata-mqtt") 
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DDATA,
    		id = "topics-ddata-seq-num")
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DDATA,
    		id = "topics-ddata-timestamp")
	@SpecAssertion(
    		section = Sections.PAYLOADS_DESC_DDATA,
    		id = "topics-ddata-payload")
	public void checkDeviceData(String clientId, PublishPacket packet) {
		String result = "FAIL";
		if (packet.getQos() == Qos.AT_MOST_ONCE && 
				packet.getRetain() == false) {
			result = "PASS";
		}
		testResults.put("topics-ddata-mqtt", result);
		
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
		logger.info("Send data test inboundpayload "+inboundPayload);
		
		result = "FAIL";
		if (inboundPayload != null) {
			long seqno = inboundPayload.getSeq();
			if (seqno >= 0) {
				result = "PASS";
			}	
		}
		testResults.put("topics-ddata-seq-num", result);
		
		result = "FAIL";
		if (inboundPayload != null) {
			Date ts = inboundPayload.getTimestamp();
			if (ts != null) {
				result = "PASS";
			}	
		}
		testResults.put("topics-ddata-timestamp", result);
		
		result = "FAIL";
		if (inboundPayload != null) {
			List<Metric> metrics = inboundPayload.getMetrics();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				// TODO: Must include metrics that have changed - how do we check that? 
				Metric current = metricIterator.next();
				//if (current.getName().equals(device_metric)) {
					result = "PASS"; 
				//}
			}
		}
		testResults.put("topics-ddata-payload", result);
		logger.info("Send data test payload "+result);
		device_checked = true;
	}

}