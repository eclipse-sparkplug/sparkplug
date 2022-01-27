/*******************************************************************************
 * Copyright (c) 2021, 2022 Ian Craggs
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
 * We send a rebirth command to an edge node and a device so we can
 * test the behavior of the those edge nodes and devices.
 * 
 * The parameters are the group_id, edge_node_id and device_id to use.
 * 
 * The edge node must start in the online state, potentially after running
 * the edge node session establishment test.  
 * 
 * 1. Send a node rebirth command.
 * 2. Wait for edge node and device rebirths
 * 3. Send MQTT client disconnect to the edge node
 * 4. Watch for connect to get client id
 * 5. Check bdSeq
 * 5. Then track NBIRTH and DBIRTH
 * 6. Send NBIRTH cmd to check bdSeq
 * 7. Send DBIRTH cmd
 *   
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.logging.Level;
import java.util.function.BiConsumer;

import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.*;
import com.hivemq.extension.sdk.api.services.session.ClientService;
import java.util.concurrent.CompletableFuture;

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
	private static final String PASS = "PASS";
	private static final String FAIL = "FAIL";
    
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
    		"operational-behavior-data-commands-rebirth-action-1",
    		"operational-behavior-data-commands-rebirth-action-2",
    		"operational-behavior-data-commands-rebirth-action-3"
        };
    private String state = "start";
    private TCK theTCK = null;
    private String host_application_id = null;
    private String edge_node_clientid = null;
    private String edge_node_id = null;
    private String device_id = null;
	private long deathBdSeq = -1;
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
        
        if (parms.length < 3) {
        	logger.info("Parameters to receive command test must be: group_id edge_node_id device_id");
        	return;
        }
        
        System.out.println(parms);

        group_id = parms[0];
        logger.info("Group id is "+group_id);
        
        edge_node_id = parms[1];
        logger.info("Edge node id is "+edge_node_id);
        
        device_id = parms[2];
        logger.info("Device id is "+device_id);
        
        sendCommand(true);
        // indicate we are testing the receipt of NBIRTH and DBIRTH messages after a rebirth command
        testResults.put("operational-behavior-data-commands-rebirth-action-2", FAIL); 
        
        // this will fail if we receive a data message
		testResults.put("operational-behavior-data-commands-rebirth-action-1", PASS);
    }
    
    public void disconnectClient(String clientId) {
        final ClientService clientService = Services.clientService();
        CompletableFuture<Boolean> disconnectFuture = clientService.disconnectClient(clientId, true);
        
        disconnectFuture.whenComplete(new BiConsumer<Boolean, Throwable>() {
            @Override
            public void accept(Boolean disconnected, Throwable throwable) {
                if(throwable == null) {
                    if(disconnected){
                        System.out.println("Client was successfully disconnected and no Will message was sent");
                    } else {
                        System.out.println("Client not found");
                    }
                } else {
                    logger.error("disconnectClient", throwable);
                }
            }
        });
    }
    
    public void sendCommand(boolean isNode) {
        String topicName = "";
        if (isNode) {
        	state = "SendingNodeRebirth";
        	topicName = NAMESPACE + "/" + group_id + "/NCMD/" + edge_node_id;
        } else {
        	state = "SendingDeviceRebirth";
        	topicName = NAMESPACE + "/" + group_id + "/DCMD/" + edge_node_id +"/" + device_id;
        }
        
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
    	state = "end";
    	edge_node_clientid = null;
    	deathBdSeq = -1;
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
    
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH, 
			id = "payloads-ndeath-will-message")
	@Override
	public void connect(String clientId, ConnectPacket packet) {
		// we can determine the clientid corresponding to the edge node id by
		// checking the will contents to see if the edge node id matches.
		
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String topic = willPublishPacket.getTopic();
			String[] topicParts = topic.split("/");
			if (topicParts.length >= 4) {
				String will_edge_node_id = topicParts[3];
				if (edge_node_id.equals(will_edge_node_id)) {
					edge_node_clientid = clientId;
					logger.info("Clientid for edge node id "+edge_node_id+" is "+edge_node_clientid);
					
					ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
					deathBdSeq = getBdSeq(payload);
					
					/*if (!found) {
						// some error
					}*/
				}
			}
		}		
	}
	
	private long getBdSeq(ByteBuffer payload) {
		try {
			SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
			byte[] payload_array = new byte[payload.remaining()];
			payload.get(payload_array);
			SparkplugBPayload inboundPayload = decoder.buildFromByteArray(payload_array);

			List<Metric> metrics = inboundPayload.getMetrics();
			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					return (long) m.getValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}
	
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		// TODO Auto-generated method stub
		
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-action-1")
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-action-2")
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-action-3")
	@Override
	public void publish(String clientId, PublishPacket packet) {
		if (state.equals("SendingNodeRebirth") || state.equals("DisconnectingClient")) {
			String[] levels = packet.getTopic().split("/");
			if (levels.length == 4 && levels[2].equals("NBIRTH")) {
				if (levels[0].equals(NAMESPACE) && levels[1].equals(group_id) && levels[3].equals(edge_node_id)) {
					logger.info("Node birth received - clientid " + clientId);
					edge_node_clientid = clientId;
					nbirth = true;

					if (state.equals("DisconnectingClient")) {
						// check bdSeq
						ByteBuffer payload = packet.getPayload().orElseGet(null);
						long birthseq = getBdSeq(payload);
						testResults.put("operational-behavior-data-commands-rebirth-action-3",  
								birthseq == deathBdSeq ? PASS : FAIL);
						if (birthseq != deathBdSeq) {
							logger.info("*** Death sequence no "+deathBdSeq+" nbirth seq no "+birthseq+". Expected to be equal");
						}
					}
				}
			} else if (levels.length == 5 && levels[2].equals("DBIRTH")) {
				if (levels[0].equals(NAMESPACE) && levels[1].equals(group_id) && levels[3].equals(edge_node_id)) {
					logger.info("Device birth received for " + levels[4]);
					dbirth = true;
				}
			} else if (levels[2].equals("NDATA")) {
				if (levels[0].equals(NAMESPACE) && levels[1].equals(group_id) && levels[3].equals(edge_node_id)) {		
					testResults.put("operational-behavior-data-commands-rebirth-action-1", FAIL);
					logger.info("Error: Data received for edge node" + levels[3]);	
				}
			} else if (levels[2].equals("DDATA")) {
				if (levels[0].equals(NAMESPACE) && levels[1].equals(group_id) && levels[3].equals(edge_node_id)) {		
					testResults.put("operational-behavior-data-commands-rebirth-action-1", FAIL);
					logger.info("Error: Data received for edge node" + levels[3]+ " device id "+levels[4]);	
				}
			}
			
			if (state.equals("SendingNodeRebirth") && nbirth && dbirth) {
				testResults.put("operational-behavior-data-commands-rebirth-action-2", PASS);  			
				state = "DisconnectingClient";
				nbirth = false;
				dbirth = false;
				disconnectClient(clientId);
			}
			if (state.equals("DisconnectingClient") && nbirth && dbirth) {
				endTest();
			}
		}
	}

}