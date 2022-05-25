/********************************************************************************
 * Copyright (c) 2014, 2022 Cirrus Link Solutions and others
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
import org.eclipse.paho.client.mqttv3.MqttException;

import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.*;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.*;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Date;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_LOG_TOPIC;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class MQTTListener implements MqttCallbackExtended {
	private final static Logger logger = LoggerFactory.getLogger(MQTTListener.class);
	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "Sparkplug MQTT Listener";
	private String username = "admin";
	private String password = "changeme";

	private MqttTopic log_topic = null;
	private MqttClient client = null;

	private String primary_host_application_id = null;

	private HashMap testResults = new HashMap<String, String>();
	String[] testIds = { ID_INTRO_GROUP_ID_STRING, ID_INTRO_GROUP_ID_CHARS, ID_INTRO_EDGE_NODE_ID_STRING,
			ID_INTRO_EDGE_NODE_ID_CHARS, ID_INTRO_DEVICE_ID_STRING, ID_INTRO_DEVICE_ID_CHARS, ID_TOPIC_STRUCTURE,
			ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES,
			ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES,
			ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID, ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID,
			ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID, ID_PAYLOADS_TIMESTAMP_IN_UTC };

	public void log(String message) {
		try {
			MqttMessage mqttmessage = new MqttMessage(("MQTTListener: " + message).getBytes());
			log_topic.publish(mqttmessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MQTTListener listener = new MQTTListener();
		listener.run(args);
	}

	public void clearResults() {
		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], NOT_EXECUTED);
		}
	}

	public HashMap<String, String> getResults() {
		HashMap labelledResults = new HashMap<String, String>();
		for (int i = 0; i < testIds.length; ++i) {
			labelledResults.put("MQTTListener:" + testIds[i], testResults.get(testIds[i]));
		}
		return labelledResults;
	}

	public void run(String[] args) {
		if (client != null) {
			return;
		}

		logger.info("*** Sparkplug TCK MQTT Listener ***");
		clearResults();
		try {
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			// options.setUserName(username);
			// options.setPassword(password.toCharArray());
			client = new MqttClient(serverUrl, clientId);
			client.setTimeToWait(10000); // short timeout on failure to connect
			client.setCallback(this);
			log_topic = client.getTopic(TCK_LOG_TOPIC);
			client.connect(options);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		System.out.println("Connected!");

		try {
			// Just listen to all DDATA messages on spBv1.0 topics and wait for inbound messages
			client.subscribe(
					new String[] { "spAv1.0/#", TOPIC_ROOT_SP_BV_1_0 + "/#", TOPIC_ROOT_STATE + "/#", TCK_LOG_TOPIC },
					new int[] { 2, 2, 2, 2 });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("The MQTT Connection was lost! - will auto-reconnect");
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_TIMESTAMP_IN_UTC)
	@SpecAssertion(
			section = Sections.TOPICS_NAMESPACE_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_A)
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if (topic.startsWith(TOPIC_ROOT_STATE+"/")) {
				System.out.println("Sparkplug message: " + topic + " " + new String(message.getPayload()));
			} else if (topic.equals(TCK_LOG_TOPIC)) {
				System.out.println("TCK log: " + new String(message.getPayload()));
			} else {
				if (topic.startsWith("spAv1.0/")) {
					log("Warning - non-standard Sparkplug A message received");
					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_A, setResult(false, TOPIC_STRUCTURE_NAMESPACE_A));
				} else {
					System.out.println("Message arrived on Sparkplug topic " + topic);
					checkTopic(topic.split("/"));

					PayloadOrBuilder inboundPayload = Payload.parseFrom(message.getPayload());
					//System.out.println(inboundPayload.toString());

					if (inboundPayload.hasTimestamp()) {
						Date now = new Date();
						long diff = now.getTime() - inboundPayload.getTimestamp();
						testResult(ID_PAYLOADS_TIMESTAMP_IN_UTC, setResult(
								diff >= 0 && diff <= 20000, PAYLOADS_TIMESTAMP_IN_UTC));
						if (diff < 0 || diff > 20000) {
							System.out.println("Timestamp diff " + diff);
						}
					}
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
		if (!((String)testResults.get(id)).startsWith(FAIL)) {
			testResults.put(id, state);
		}
	}

	@SpecAssertion(
			section = Sections.TOPICS_SPARKPLUG_TOPIC_NAMESPACE_ELEMENTS,
			id = ID_TOPIC_STRUCTURE)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_GROUP_ID_STRING)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_GROUP_ID_CHARS)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_EDGE_NODE_ID_STRING)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_EDGE_NODE_ID_CHARS)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_DEVICE_ID_STRING)
	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_DEVICE_ID_CHARS)
	@SpecAssertion(
			section = Sections.TOPICS_GROUP_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID)
	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID)
	@SpecAssertion(
			section = Sections.TOPICS_DEVICE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID)
	@SpecAssertion(
			section = Sections.TOPICS_DEVICE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES)
	@SpecAssertion(
			section = Sections.TOPICS_DEVICE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES)
	public void checkTopic(String[] elements) {
		Boolean result = false;
		if (elements[0].equals(TOPIC_ROOT_STATE)) {
			if (elements.length == 2) {
				result = true;
			}	
			testResult(ID_TOPIC_STRUCTURE, setResult(result, TOPIC_STRUCTURE));
		} else {
			if (elements.length < 4) {
				testResult(ID_TOPIC_STRUCTURE, setResult(false, "(too few topic elements)"));
			} else {
				String namespace = elements[0];
				String group_id = elements[1];
				String message_type = elements[2];
				String edge_node_id = elements[3];
				String device_id = null;
				if (elements.length >= 5) {
					device_id = elements[4];
				}

				if (message_type.equals("DBIRTH") || message_type.equals("DDEATH") || message_type.equals("DDATA")
						|| message_type.equals("DCMD")) {				
					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES, 
							setResult(elements.length == 5, TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES));	
					result = (elements.length == 5) ? true : false;
				}

				if (message_type.equals("NBIRTH") || message_type.equals("NDEATH") || message_type.equals("NDATA")
						|| message_type.equals("NCMD")) {
					
					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES, 
							setResult(elements.length == 4, TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES));					
					result = (elements.length == 4) ? true : false;
				}
				testResult(ID_TOPIC_STRUCTURE, setResult(result, TOPIC_STRUCTURE));

				result = true;
				if (!checkUTF8String(group_id)) {
					result = false;
					testResult(ID_INTRO_GROUP_ID_STRING, setResult(false, INTRO_GROUP_ID_STRING));
				}

				if (!checkMQTTChars(group_id)) {
					result = false;
					testResult(ID_INTRO_GROUP_ID_CHARS, setResult(false, INTRO_GROUP_ID_CHARS));
				}
				testResult(ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID,
						setResult(result, TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID));

				result = true;
				if (!checkUTF8String(edge_node_id)) {
					result = false;
					testResult(ID_INTRO_EDGE_NODE_ID_STRING, setResult(false, INTRO_EDGE_NODE_ID_STRING));
				}

				if (!checkMQTTChars(edge_node_id)) {
					result = false;
					testResult(ID_INTRO_EDGE_NODE_ID_CHARS, setResult(false, INTRO_EDGE_NODE_ID_CHARS));
				}
				testResult(ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID,
						setResult(result, TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID));

				if (device_id != null) {
					result = true;
					if (!checkUTF8String(device_id)) {
						result = false;
						testResult(ID_INTRO_DEVICE_ID_STRING, setResult(false, INTRO_DEVICE_ID_STRING));
					}

					if (!checkMQTTChars(device_id)) {
						result = false;
						testResult(ID_INTRO_DEVICE_ID_CHARS, setResult(false, INTRO_DEVICE_ID_STRING));
					}
					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID,
							setResult(result, TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID));
				}
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
		if ((inString.indexOf('+') != -1) || (inString.indexOf('/') != -1) || (inString.indexOf('#') != -1)) {
			rc = false;
		}
		return rc;
	}
}