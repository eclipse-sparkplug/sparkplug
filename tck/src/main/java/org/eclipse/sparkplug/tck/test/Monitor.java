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

package org.eclipse.sparkplug.tck.test;

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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.*;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.*;

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
public class Monitor extends TCKTest implements ClientLifecycleEventListener {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private static final @NotNull String PASS = "PASS";
	private static final @NotNull String FAIL = "FAIL";
	private static final @NotNull String NAMESPACE = "spBv1.0";
	private HashMap testResults;
	String[] testIds = { "topic-structure-namespace-unique-edge-node-descriptor" };

	// edge_node_id to clientid
	private HashMap edge_nodes = new HashMap<String, String>();

	// clientid to edge_node_id
	private HashMap clientids = new HashMap<String, String>();

	public Monitor() {
		logger.info("Sparkplug message monitor 1.0");

		testResults = new HashMap<String, String>();
		edge_nodes = new HashMap<String, String>();

		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], "");
		}
	}

	public void endTest() {
		reportResults(testResults);
		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], "");
		}
	}

	public String getName() {
		return "SparkplugMonitor";
	}

	public String[] getTestIds() {
		return testIds;
	}

	public HashMap<String, String> getResults() {
		return testResults;
	}

	@Override
	public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
		logger.info("Monitor: Client {} connects.", connectionStartInput.getConnectPacket().getClientId());
	}

	@Override
	public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
		logger.info("Monitor: Client {} authenticated successfully.",
				authenticationSuccessfulInput.getClientInformation().getClientId());
	}

	@Override
	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		String clientid = disconnectEventInput.getClientInformation().getClientId();
		logger.info("Monitor: Client {} disconnected.", clientid);

		String edge_node_id = (String) clientids.get(clientid);
		if (edge_node_id != null) {
			logger.info("Monitor: removing edge node {} for client id {} on disconnect", edge_node_id, clientid);
			if (clientids.remove(clientid) == null) {
				logger.info("Monitor: Error removing clientid {} on disconnect", clientid);
			}
			if (edge_nodes.remove(edge_node_id) == null) {
				logger.info("Monitor: Error removing edge_node_id {} on disconnect", edge_node_id);
			}
		}
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {

	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {

	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {

	}

	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = "topic-structure-namespace-unique-edge-node-descriptor")
	@Override
	public void publish(String clientId, PublishPacket packet) {

		String topic = packet.getTopic();
		if (topic.startsWith(NAMESPACE)) {
			String[] topicParts = topic.split("/");
			// topic is spBv1.0/group_id/message_type/edge_node_id/[device_id]"

			if (topicParts.length != 5 && topicParts.length != 4) {
				return;
			}
			String device_id = null;
			String group_id = topicParts[1];
			String message_type = topicParts[2];
			String edge_node_id = topicParts[3];
			if (topicParts.length == 5) {
				device_id = topicParts[topicParts.length - 1];
			}

			// if we have more than one MQTT client id with the same edge node id then it's an error
			if (message_type.equals("NBIRTH")) {
				logger.info("Monitor: *** NBIRTH *** {} {}", edge_node_id, clientId);
				String client_id = (String) edge_nodes.get(edge_node_id);
				if (client_id != null && !client_id.equals(clientId)) {
					logger.error("Monitor: two clientids {} {} using the same edge_node_id {}", client_id, clientId,
							edge_node_id);
					testResults.put("topic-structure-namespace-unique-edge-node-descriptor", FAIL);
				} else {
					logger.info("Monitor: adding edge node {} for client id {} on NBIRTH", edge_node_id, clientId);
					edge_nodes.put(edge_node_id, clientId);
					clientids.put(clientId, edge_node_id);
				}
			} else if (message_type.equals("NDEATH")) {
				logger.info("Monitor: *** NDEATH *** {} {}", edge_node_id, clientId);
				String found_client_id = (String) edge_nodes.get(edge_node_id);

				if (found_client_id != null && !found_client_id.equals(clientId)) {
					logger.error("Monitor: two clientids {} {} using the same edge_node_id {}", found_client_id,
							clientId, edge_node_id);
					testResults.put("topic-structure-namespace-unique-edge-node-descriptor", FAIL);
				} else {
					logger.info("Monitor: removing edge node {} for client id {} on NDEATH", edge_node_id, clientId);
					if (clientids.remove(clientId) == null) {
						logger.info("Monitor: Error removing clientid {} on NDEATH", clientId);
					}
					if (edge_nodes.remove(edge_node_id) == null) {
						logger.info("Monitor: Error removing edge_node_id {} on NDEATH", edge_node_id);
					}
				}
			}
		}
	}

}