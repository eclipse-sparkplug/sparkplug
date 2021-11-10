/********************************************************************************
 * Copyright (c) 2014, 2021 Cirrus Link Solutions and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Cirrus Link Solutions - initial implementation
 *   Ian Craggs - add checks for Sparkplug TCK
 ********************************************************************************/

package org.eclipse.sparkplug.tck.test;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.message.model.Topic;
import org.eclipse.tahu.util.TopicUtil;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class MQTTListener implements MqttCallbackExtended {

	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "Sparkplug MQTT Listener";
	private String username = "admin";
	private String password = "changeme";
	private String log_topic_name = "SPARKPLUG_TCK/LOG";
	private MqttTopic log_topic = null;
	private MqttClient client;
	
	private String primary_host_application_id = null;
	
    private HashMap testResults = new HashMap<String, String>();
    String[] testIds = {
    	"intro-secondary-host-state",
    	"intro-group-id-string",
    	"intro-group-id-chars",
    	"intro-edge-node-id-string",
    	"intro-edge-node-id-chars",
    	"intro-device-id-string",
    	"intro-device-id-chars",    	
    	"topic-structure-namespace-device-id-associated-message-types",
    	"topic-structure-namespace-device-id-non-associated-message-types",
    	"topic-structure-namespace-valid-group-id",
    	"topic-structure-namespace-valid-edge-node-id",
    	"topic-structure-namespace-valid-device-id",
    };
	
	public void log(String message) {
		try {
			MqttMessage mqttmessage = new MqttMessage(("MQTTListener: "+message).getBytes());
			log_topic.publish(mqttmessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MQTTListener listener = new MQTTListener();
		listener.run(args);
	}
	
	public void run(String[] args) {
		System.out.println("*** Sparkplug TCK MQTT Listener ***");
		
        if (args.length < 1) {
        	System.out.println("Parameter must be: primary_host_application_id");
        	return;
        }
        
        primary_host_application_id = args[0];
        System.out.println("Primary host application id is "+primary_host_application_id);
        
        testResults = new HashMap<String, String>();
        
        for (int i = 0; i < testIds.length; ++i) {
            testResults.put(testIds[i], "");
        }
		
		try {
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			//options.setUserName(username);
			//options.setPassword(password.toCharArray());
			client = new MqttClient(serverUrl, clientId);
			client.setTimeToWait(5000);						// short timeout on failure to connect
			client.setCallback(this);
			log_topic = client.getTopic(log_topic_name);
			client.connect(options);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		System.out.println("Connected!");
		
		try {
			// Just listen to all DDATA messages on spBv1.0 topics and wait for inbound messages
			client.subscribe(new String[]{"spAv1.0/#", "spBv1.0/#", "STATE/#", log_topic_name}, new int[]{2, 2, 2, 2});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("The MQTT Connection was lost! - will auto-reconnect");
    }

	@SpecAssertion(
    		section = Sections.TOPICS_NAMESPACE_ELEMENT,
    		id = "topic-structure-namespace-a")
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if (topic.startsWith("STATE/") ) {
				System.out.println("Sparkplug message: "+ topic + " " + new String(message.getPayload()));
				checkState(topic, message);
			} else if (topic.equals(log_topic_name)) {
				System.out.println("TCK log: "+ new String(message.getPayload()));
			} else {

				if (topic.startsWith("spAv1.0/")) {
					log("Warning - non-standard Sparkplug A message received");
					testResult("topic-structure-namespace-a", "FAIL");
				} else {

					Topic sparkplugTopic = TopicUtil.parseTopic(topic);
					ObjectMapper mapper = new ObjectMapper();
					mapper.setSerializationInclusion(Include.NON_NULL);

					System.out.println("Message arrived on Sparkplug topic " + sparkplugTopic.toString());

					SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
					SparkplugBPayload inboundPayload = decoder.buildFromByteArray(message.getPayload());

					// Convert the message to JSON and print to system.out
					String payloadString = mapper.writeValueAsString(inboundPayload);
					System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inboundPayload));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Published message: " + token);
	}
	
	private void testResult(String id, String state) {
		// Don't override a failing test fail
		if (!testResults.get(id).equals("FAIL")) {
			testResults.put(id, state);
		}
	}
	
	@SpecAssertion(
    		section = Sections.INTRODUCTION_SECONDARY_HOST_APPLICATION,
    		id = "intro-secondary-host-state")
	public void checkState(String topic, MqttMessage message) {
		String[] words = topic.split("/");
		if (words.length == 2) {
			if (!words[1].equals(primary_host_application_id)) {
				log("Error: non-primary host "+words[1]+" publishing STATE message");
				testResult("intro-secondary-host-state", "FAIL");
			}
		} else {
			log("Error: STATE message with wrong topic "+topic);
		}
	}
	
    @SpecAssertion(
    		section = Sections.TOPICS_SPARKPLUG_TOPIC_NAMESPACE_ELEMENTS,
    		id = "topic-structure")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-group-id-string")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-group-id-chars")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-edge-node-id-string")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-edge-node-id-chars")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-device-id-string")
    @SpecAssertion(
    		section = Sections.INTRODUCTION_SPARKPLUG_IDS,
    		id = "intro-device-id-chars")
    @SpecAssertion(
    		section = Sections.TOPICS_GROUP_ID_ELEMENT,
    		id = "topic-structure-namespace-valid-group-id") 
    @SpecAssertion(
    		section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
    		id = "topic-structure-namespace-valid-edge-node-id") 
    @SpecAssertion(
    		section = Sections.TOPICS_DEVICE_ID_ELEMENT,
    		id = "topic-structure-namespace-valid-device-id") 
    @SpecAssertion(
    		section = Sections.TOPICS_DEVICE_ID_ELEMENT,
    		id = "topic-structure-namespace-device-id-associated-message-types")
    @SpecAssertion(
    		section = Sections.TOPICS_DEVICE_ID_ELEMENT,
    		id = "topic-structure-namespace-device-id-non-associated-message-types")
	public void checkTopic(String[] elements) {
		String result = "FAIL";
		if (elements[0].equals("STATE")) {
			if (elements.length == 2) {
				result = "PASS";
			}
			testResult("topic-structure", result);
		} else {
			if (elements.length < 4) {
				log("topic-structure: FAIL (too few topic elements)");
			} else {
				String namespace = elements[0];
				String group_id = elements[1];
				String message_type = elements[2];
				String edge_node_id = elements[3];
				String device_id = null;
				if (elements.length >= 5) {
					device_id = elements[4];
				}
				
				if (message_type.equals("DBIRTH") || 
					message_type.equals("DDEATH") ||
					message_type.equals("DDATA") ||
					message_type.equals("DCMD")) {
					if (elements.length != 5) {
						result = "FAIL";	
						testResult("topic-structure-namespace-device-id-associated-message-types", "FAIL");
					} else {
						testResult("topic-structure-namespace-device-id-associated-message-types", "TRUE");
					}
				}  
					
				if (message_type.equals("NBIRTH") || 
					message_type.equals("NDEATH") ||
					message_type.equals("NDATA") ||
					message_type.equals("NCMD")) {
					if (elements.length != 4) { 
						result = "FAIL";
						testResult("topic-structure-namespace-device-id-non-associated-message-types", "FAIL");
					} else {
						testResult("topic-structure-namespace-device-id-non-associated-message-types", "TRUE");
					}
				}
				testResult("topic-structure", result);
				
				result = "TRUE";
				if (!checkUTF8String(group_id)) {
					result = "FAIL";
					testResult("intro-group-id-string", "FAIL");	
					log("Group id string is invalid");
				}
				
				if (!checkMQTTChars(group_id)) {
					result = "FAIL";
					testResult("intro-group-id-chars", "FAIL");	
					log("Group id chars are invalid");
				}
				testResult("topic-structure-namespace-valid-group-id", result);
				
				result = "TRUE";
				if (!checkUTF8String(edge_node_id)) {
					result = "FAIL";
					testResult("intro-edge-node-id-string", "FAIL");	
					log("Edge node id string is invalid");
				}
				
				if (!checkMQTTChars(edge_node_id)) {
					result = "FAIL";
					testResult("intro-edge-node-id-chars", "FAIL");	
					log("Edge node id chars are invalid");
				}
				testResult("topic-structure-namespace-valid-edge-node-id", result);
				
				result = "TRUE";
				if (!checkUTF8String(device_id)) {
					result = "FAIL";
					testResult("intro-device-id-string", "FAIL");	
					log("Device id string is invalid");
				}
				
				if (!checkMQTTChars(device_id)) {
					result = "FAIL";
					testResult("intro-device-id-chars", "FAIL");	
					log("Device id chars are invalid");
				}
				testResult("topic-structure-namespace-valid-device-id", result);
				
			}
		}
	}
    
    private boolean checkUTF8String(String inString) {
    	// MUST be a valid UTF-8 string 

    	byte[] bytes = inString.getBytes(StandardCharsets.UTF_8);

    	String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);

    	boolean rc = false;
    	if (inString.equals(utf8EncodedString)) {
    		rc = true;
    	}
    	return rc;
    }
    
    private boolean checkMQTTChars(String inString) {
    	// MUST not use reserved characters of + (plus), / (forward slash), and # (number sign).
    	boolean rc = true; 
    	if ((inString.indexOf('+') != -1) ||
    		(inString.indexOf('/') != -1) ||
    		(inString.indexOf('#') != -1)) {
    		rc = false;
    	}
    	return rc;
    }
}