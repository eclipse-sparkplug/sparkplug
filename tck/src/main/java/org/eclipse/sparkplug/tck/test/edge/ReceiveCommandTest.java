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
 * This is the edge node Sparkplug receive command test.  
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
public class ReceiveCommandTest extends TCKTest {

	private final String NAMESPACE = "spBv1.0";
    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {

        };
    private String myClientId = null;
    private String state = null;
    private TCK theTCK = null;
    private String host_application_id = null;
    private String edge_node_id = null;
    private String group_id = null;
	private PublishService publishService = Services.publishService();
	private boolean nbirth = false, 
			dbirth = false;
    
    public ReceiveCommandTest(TCK aTCK, String[] parms) {
        logger.info(getName());
        theTCK = aTCK;
         
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        
        if (parms.length < 2) {
        	logger.info("Parameters to receive command test must be: group_id edge_node_id");
        	return;
        }

        group_id = parms[0];
        logger.info("Group id is "+group_id);
        
        edge_node_id = parms[1];
        logger.info("Edge node id is "+edge_node_id);
        
        state = "SendingCommand";
        String topicName = NAMESPACE + "/" + group_id + "/NCMD/" + edge_node_id;
        
        byte[] payload = null;
        try {
        	payload = new SparkplugBPayloadEncoder().getBytes(new SparkplugBPayloadBuilder()
				.addMetric(new MetricBuilder("Node Control/Rebirth", MetricDataType.Boolean, true)
						.createMetric())
				.createPayload());
        } catch (Exception e) {
        	logger.info("Error building edge node rebirth command. Aborting test.");
        	endTest();
        }
        
		Publish message = Builders.publish().topic(topicName).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload))
				.build();
		logger.info("Requesting edge rebirth. Edge node id: "+edge_node_id);
		publishService.publish(message);
    }
    
    public void endTest() {
    	state = null;
    	myClientId = null;
    	reportResults(testResults);
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
        logger.info("Ending test "+getName());
    }
    
    public String getName() {
    	return "Sparkplug Edge Receive Command Test";
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
		if (state.equals("SendingCommand"))
		{
			String[] levels = packet.getTopic().split("/");
			if (levels.length == 4 && levels[2].equals("NBIRTH")) {
				if (levels[0].equals(NAMESPACE) && 
						levels[1].equals(group_id) &&
						levels[3].equals(edge_node_id)) {
					logger.info("Node birth received");
					nbirth = true;
				}
			} else if (levels.length == 5 && levels[2].equals("DBIRTH")) {
				if (levels[0].equals(NAMESPACE) && 
						levels[1].equals(group_id) &&
						levels[3].equals(edge_node_id)) {
					logger.info("Device birth received for "+levels[4]);
					dbirth = true;
				}
			}
		}
		if (nbirth && dbirth) {
			endTest();
		}
	}

}