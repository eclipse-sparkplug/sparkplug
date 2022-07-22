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

package org.eclipse.sparkplug.tck.test;

import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NBIRTH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Utils.getSparkplugPayload;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

/*
 * A utility to check MQTT Sparkplug messages at any time for conformance to
 * the spec. It runs in parallel to any Sparkplug SDK test, providing checks
 * for additional assertions which apply at all times.
 * 
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class Monitor extends TCKTest implements ClientLifecycleEventListener {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private static final @NotNull String NAMESPACE = TOPIC_ROOT_SP_BV_1_0;
	private HashMap<String, String> testResults = new HashMap<>();
	String[] testIds =
			{ ID_INTRO_EDGE_NODE_ID_UNIQUENESS, ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE,
					ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR,
					ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID, ID_PAYLOADS_DBIRTH_SEQ_INC };

	// edge_node_id to clientid
	private HashMap<String, String> edge_nodes = new HashMap<>();

	// clientid to edge_node_id
	private HashMap<String, String> clientids = new HashMap<>();

	// edge_node_id to device_id
	private HashMap<String, HashSet<String>> edge_to_devices = new HashMap<>();

	// edge_node_id to last messsage (NBIRTH/NDEATH) + sequence number
	private HashMap edgeBdSeqs = new HashMap<String, ArrayList<Object>>();

	// edge_node_id to last sequence number
	private HashMap<String, Long> edgeSeqs = new HashMap<>();

	public Monitor() {
		logger.info("Sparkplug TCK message monitor 1.0");

		clearResults();
	}

	public void clearResults() {
		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], NOT_EXECUTED);
		}
	}

	public void startTest() {
		clearResults();
	}

	public void endTest(Map<String, String> results) {
		clearResults();
	}

	public String getName() {
		return "SparkplugMonitor";
	}

	public String[] getTestIds() {
		return testIds;
	}

	public HashMap<String, String> getResults() {
		HashMap<String, String> labelledResults = new HashMap<>();
		for (int i = 0; i < testIds.length; ++i) {
			labelledResults.put("Monitor:" + testIds[i], testResults.get(testIds[i]));
		}
		return labelledResults;
	}

	@Override
	public void onMqttConnectionStart(ConnectionStartInput connectionStartInput) {
		logger.debug("Monitor: Client {} connects.", connectionStartInput.getConnectPacket().getClientId());
	}

	@Override
	public void onAuthenticationSuccessful(AuthenticationSuccessfulInput authenticationSuccessfulInput) {
		logger.debug("Monitor: Client {} authenticated successfully.",
				authenticationSuccessfulInput.getClientInformation().getClientId());
	}

	// onDisconnect is called whenever an MQTT client disconnects from the server,
	// whether that be through an MQTT disconnect or a simple TCP connection break, so
	// under all circumstances.
	@Override
	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		String clientid = disconnectEventInput.getClientInformation().getClientId();
		logger.debug("Monitor: Client {} disconnected.", clientid);

		String edge_node_id = (String) clientids.get(clientid);
		if (edge_node_id != null) {
			logger.info("Monitor: removing edge node {} for client id {} on disconnect", edge_node_id, clientid);
			if (clientids.remove(clientid) == null) {
				logger.error("Monitor: Error removing clientid {} on disconnect", clientid);
			}
			if (edge_nodes.remove(edge_node_id) == null) {
				logger.error("Monitor: Error removing edge_node_id {} on disconnect", edge_node_id);
			}

			HashSet<String> devices = (HashSet<String>) edge_to_devices.get(edge_node_id);
			logger.debug("Monitor: devices for edge_node_id {} were {}", edge_node_id, devices);
			if (edge_to_devices.remove(edge_node_id) == null) {
				logger.error("Monitor: Error removing edge_node_id {} from edge_to_devices on disconnect",
						edge_node_id);
			}
		}
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {

	}

	// disconnect is only called for the receipt of an MQTT disconnect packet
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {

	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {

	}

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

			// Uniqueness for edge node ids is within group id, so
			// we add the group_id to the edge node id as a shortcut to make it so.
			edge_node_id = group_id + ":" + edge_node_id;

			PayloadOrBuilder payload = getSparkplugPayload(packet);

			// if we have more than one MQTT client id with the same edge node id then it's an error
			if (message_type.equals("NBIRTH")) {
				handleNBIRTH(group_id, edge_node_id, clientId, payload);
			} else if (message_type.equals("NDEATH")) {
				handleNDEATH(group_id, edge_node_id, clientId);
			} else if (message_type.equals("NDATA")) {
				handleNDATA(group_id, edge_node_id, payload);
			} else if (message_type.equals("DBIRTH")) {
				handleDBIRTH(group_id, edge_node_id, device_id, payload);
			} else if (message_type.equals("DDEATH")) {
				handleDDEATH(group_id, edge_node_id, device_id, payload);
			} else if (message_type.equals("DDATA")) {
				handleDDATA(group_id, edge_node_id, device_id, payload);
			}
		}
	}

	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_SEQ)
	private void handleNBIRTH(String group_id, String edge_node_id, String clientId, PayloadOrBuilder payload) {
		logger.info("Monitor: *** NBIRTH *** {}/{} {}", group_id, edge_node_id, clientId);
		String client_id = (String) edge_nodes.get(edge_node_id);
		if (client_id != null && !client_id.equals(clientId)) {
			logger.error("Monitor: two clientids {} {} using the same group_id/edge_node_id {}", client_id, clientId,
					edge_node_id);
			testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR,
					setResult(false, TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR));
		} else {
			logger.info("Monitor: adding edge node {} for client id {} on NBIRTH", edge_node_id, clientId);
			edge_nodes.put(edge_node_id, clientId);
			clientids.put(clientId, edge_node_id);
			edge_to_devices.put(edge_node_id, new HashSet<String>());
		}

		if (payload.hasSeq()) {
			if (payload.getSeq() > 255 || payload.getSeq() < 0) {
				testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(false, ID_PAYLOADS_NBIRTH_SEQ));
			}

			String id = group_id + "/" + edge_node_id;
			edgeSeqs.put(id, payload.getSeq());
			testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(true, ID_PAYLOADS_NBIRTH_SEQ));
		} else {
			testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(false, ID_PAYLOADS_NBIRTH_SEQ));
		}
	}

	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR)
	private void handleNDEATH(String group_id, String edge_node_id, String clientId) {
		logger.info("Monitor: *** NDEATH *** {}/{} {}", group_id, edge_node_id, clientId);
		String found_client_id = (String) edge_nodes.get(edge_node_id);

		if (found_client_id != null && !found_client_id.equals(clientId)) {
			logger.error("Monitor: two clientids {} {} using the same groups_id/edge_node_id {}", found_client_id,
					clientId, edge_node_id);
			testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR,
					setResult(false, TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR));
		} else {
			logger.info("Monitor: removing edge node {} for client id {} on NDEATH", edge_node_id, clientId);
			if (clientids.remove(clientId) == null) {
				logger.info("Monitor: Error removing clientid {} on NDEATH", clientId);
			}
			if (edge_nodes.remove(edge_node_id) == null) {
				logger.info("Monitor: Error removing edge_node_id {} on NDEATH", edge_node_id);
			}

			HashSet<String> devices = (HashSet<String>) edge_to_devices.get(edge_node_id);
			logger.info("Monitor: devices for edge_node_id {} were {}", edge_node_id, devices);
			if (edge_to_devices.remove(edge_node_id) == null) {
				logger.error("Monitor: Error removing edge_node_id {} from edge_to_devices on disconnect",
						edge_node_id);
			}
		}

		String id = group_id + "/" + edge_node_id;
		edgeSeqs.remove(id);
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDATA,
			id = ID_PAYLOADS_NDATA_SEQ_INC)
	private void handleNDATA(String group_id, String edge_node_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** NDATA *** {}/{}", group_id, edge_node_id);
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getExpectedSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_NDATA_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(true, ID_PAYLOADS_NDATA_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(false, ID_PAYLOADS_NDATA_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(false, ID_PAYLOADS_NDATA_SEQ_INC));
		}
	}

	@SpecAssertion(
			section = Sections.INTRODUCTION_SPARKPLUG_IDS,
			id = ID_INTRO_EDGE_NODE_ID_UNIQUENESS)
	@SpecAssertion(
			section = Sections.TOPICS_DEVICE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID)
	@SpecAssertion(
			section = Sections.TOPICS_DEVICE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_SEQ_INC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ)
	private void handleDBIRTH(String group_id, String edge_node_id, String device_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** DBIRTH *** {}/{}/{}", group_id, edge_node_id, device_id);
		if (!edge_to_devices.keySet().contains(edge_node_id)) {
			logger.error("Monitor: DBIRTH before NBIRTH");
		} else {
			HashSet<String> devices = (HashSet<String>) edge_to_devices.get(edge_node_id);
			if (devices.contains(device_id)) {
				logger.error("Monitor: edge_node {} using device_id {} twice", edge_node_id, device_id);
				testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID,
						setResult(false, TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID));
				testResults.put(ID_INTRO_EDGE_NODE_ID_UNIQUENESS, setResult(false, INTRO_EDGE_NODE_ID_UNIQUENESS));
			} else {
				// the following is true as it's a MAY clause. So record a +ve result
				testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE,
						setResult(true, TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE));
				logger.info("Monitor: adding device id {} for edge node id {} on DBIRTH", device_id, edge_node_id);
				devices.add(device_id);
			}
		}

		// record sequence numbers for checking
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getExpectedSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DBIRTH_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(true, PAYLOADS_DBIRTH_SEQ_INC));
					}
					if (testResults.get(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ) == null) {
						testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
								setResult(true, ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
					}
				} else {
					testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(false, PAYLOADS_DBIRTH_SEQ_INC));
					testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
							setResult(false, ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(false, PAYLOADS_DBIRTH_SEQ_INC));
			testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
					setResult(false, ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDEATH,
			id = ID_PAYLOADS_DDEATH_SEQ_INC)
	private void handleDDEATH(String group_id, String edge_node_id, String device_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** DDEATH *** {}/{}/{}", group_id, edge_node_id, device_id);
		if (!edge_to_devices.keySet().contains(edge_node_id)) {
			logger.error("Monitor: DDEATH received but no edge_node_id recorded");
		} else {
			HashSet<String> devices = (HashSet<String>) edge_to_devices.get(edge_node_id);
			if (!devices.contains(device_id)) {
				logger.error("Monitor: DDEATH before DBIRTH for device {} on edge {}", device_id, edge_node_id);
			} else {
				logger.info("Monitor: removing device id {} for edge node id {} on DDEATH", device_id, edge_node_id);
				devices.remove(device_id);
			}
		}

		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getExpectedSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DDEATH_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(true, ID_PAYLOADS_DDEATH_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(false, ID_PAYLOADS_DDEATH_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(false, ID_PAYLOADS_DDEATH_SEQ_INC));
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_SEQ_INC)
	private void handleDDATA(String group_id, String edge_node_id, String device_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** DDATA *** {}/{}/{}", group_id, edge_node_id, device_id);
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getExpectedSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DDATA_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(true, ID_PAYLOADS_DDATA_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(false, ID_PAYLOADS_DDATA_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(false, ID_PAYLOADS_DDATA_SEQ_INC));
		}
	}

	private long getExpectedSeq(long lastSeq) {
		if (lastSeq == 255) {
			return 0;
		} else {
			return lastSeq + 1;
		}
	}
}