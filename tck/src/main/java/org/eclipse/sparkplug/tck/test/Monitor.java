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
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_NAME;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_METRIC_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NBIRTH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NBIRTH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_TEMPLATES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_TEMPLATES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_BDSEQ_INCREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_BDSEQ_INCREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PRINCIPLES_RBE_RECOMMENDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PRINCIPLES_RBE_RECOMMENDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_STATE;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DCMD;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DDEATH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Utils.getSparkplugPayload;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setShouldResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResultIfNotFail;
import static org.eclipse.sparkplug.tck.test.common.Utils.setShouldResultIfNotFail;
import static org.eclipse.sparkplug.tck.test.common.Utils.getNextSeq;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Template;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

/*
 * A utility to check MQTT Sparkplug messages at any time for conformance to
 * the spec. It runs in parallel to any Sparkplug SDK test, providing checks
 * for additional assertions which apply at all times.
 *
 */

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class Monitor extends TCKTest implements ClientLifecycleEventListener {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	protected static final String TEST_FAILED_FOR_ASSERTION = "Monitor: Test failed for assertion ";
	private static final @NotNull String NAMESPACE = TOPIC_ROOT_SP_BV_1_0;
	private final TreeMap<String, String> testResults = new TreeMap<>();
	String[] testIds = {ID_INTRO_EDGE_NODE_ID_UNIQUENESS,
			ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE,
			ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR, ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID,
			ID_PAYLOADS_DBIRTH_SEQ_INC, ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR, ID_TOPICS_DBIRTH_METRIC_REQS,
			ID_TOPICS_NBIRTH_METRIC_REQS, ID_TOPICS_NBIRTH_TEMPLATES, ID_TOPICS_NBIRTH_BDSEQ_INCREMENT,
			ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ, ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE, ID_PRINCIPLES_RBE_RECOMMENDED,
			ID_PAYLOADS_STATE_BIRTH_PAYLOAD, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID };

	// edge_node_id to clientid
	private HashMap<String, String> edge_nodes = new HashMap<>();

	// clientid to edge_node_id
	private HashMap<String, String> clientids = new HashMap<>();

	// edge_node_id to device_id
	private HashMap<String, HashSet<String>> edge_to_devices = new HashMap<>();

	// edge_node_id to sequence number
	private HashMap<String, Long> edgeBdSeqs = new HashMap<String, Long>();

	// edge_node_id to last sequence number
	private HashMap<String, Long> edgeSeqs = new HashMap<>();

	private HashMap<String, List<Metric>> edgeBirthMetrics = new HashMap<String, List<Metric>>();
	private HashMap<String, List<Metric>> deviceBirthMetrics = new HashMap<String, List<Metric>>();

	private HashMap<String, List<Metric>> edgeLastMetrics = new HashMap<String, List<Metric>>();
	private HashMap<String, List<Metric>> deviceLastMetrics = new HashMap<String, List<Metric>>();

	// host application id to sequence number
	private HashMap<String, Long> hostBdSeqs = new HashMap<String, Long>();
	
	// host application id to MQTT client id
	HashMap<String, String> hostClientids = new HashMap<String, String>();

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

	public TreeMap<String, String> getResults() {
		TreeMap<String, String> labelledResults = new TreeMap<>();
		for (int i = 0; i < testIds.length; ++i) {
			if (testResults.containsKey(testIds[i])) {
				labelledResults.put("Monitor:" + testIds[i], testResults.get(testIds[i]));
			}
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

	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_BIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_BDSEQ_INCREMENT)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_DEATH,
			id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ)
	@Override
	public void connect(String clientId, ConnectPacket packet) {

		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			String[] levels = willTopic.split("/");
			if (levels.length >= 3 && levels[2].equals(TOPIC_PATH_NDEATH)) {

				// this is an edge node connect
				PayloadOrBuilder payload = getSparkplugPayload(willPublishPacket);

				List<Metric> metrics = payload.getMetricsList();
				String id = levels[1] + "/" + levels[3]; // group_id + edge_node_id
				ListIterator<Metric> metricIterator = metrics.listIterator();
				while (metricIterator.hasNext()) {
					Metric current = metricIterator.next();
					if (current.getName().equals("bdSeq") && current.hasLongValue()) {
						long bdseq = current.getLongValue();
						if (edgeBdSeqs.get(id) != null) {
							if (!setResultIfNotFail(testResults, bdseq == getNextSeq(edgeBdSeqs.get(id)),
									ID_TOPICS_NBIRTH_BDSEQ_INCREMENT, TOPICS_NBIRTH_BDSEQ_INCREMENT)) {
								log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_NBIRTH_BDSEQ_INCREMENT + ": edge id: " + id);
								log("INFO: Actual bdseq: " + bdseq + " expected bdseq: " + getNextSeq(edgeBdSeqs.get(id)));
							}
						}
						edgeBdSeqs.put(id, bdseq);
					}
				}
			} else if (levels[0].equals(TOPIC_ROOT_STATE)) {
				String hostid = levels[1];
				ObjectMapper mapper = new ObjectMapper();
				String payloadString = StandardCharsets.UTF_8.decode(willPublishPacket.getPayload().get()).toString();
				boolean isValidPayload = true;
				JsonNode json = null;
				try {
					json = mapper.readTree(payloadString);
				} catch (Exception e) {
					isValidPayload = false;
				}

				if (!isValidPayload) {
					setResultIfNotFail(testResults, isValidPayload, ID_PAYLOADS_STATE_BIRTH_PAYLOAD,
							PAYLOADS_STATE_BIRTH_PAYLOAD);
				} else {
					if (json.has("bdSeq")) {
						JsonNode bdseqnode = json.get("bdSeq");
						if (bdseqnode.isShort()) {
							short bdseq = bdseqnode.shortValue();
							if (hostBdSeqs.get(hostid) != null) {
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(hostBdSeqs.get(hostid)),
										ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ,
										PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION + ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD_BDSEQ
											+ ": host id: " + hostid);
								}
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(hostBdSeqs.get(hostid)),
										ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ, HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION + ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_BDSEQ
											+ ": host id: " + hostid);
								}
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(hostBdSeqs.get(hostid)),
										ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ,
										MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION
											+ ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ
											+ ": host id: " + hostid);
								}
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(hostBdSeqs.get(hostid)),
										ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ,
										OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION
											+ ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD_BDSEQ
											+ ": host id: " + hostid);
								}
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(hostBdSeqs.get(hostid)),
										ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ,
										OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION
											+ ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD_BDSEQ
											+ ": host id: " + hostid);
								}
							}
							hostBdSeqs.put(hostid, (long) bdseq);
						} else {
							setResultIfNotFail(testResults, false,
									ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ,
									MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ);
							log("Test failed for assertion "
									+ ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ + ": host id: "
									+ hostid);
						}
					}
				}
			}
		}
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
			if (message_type.equals(TOPIC_PATH_NBIRTH)) {
				handleNBIRTH(group_id, edge_node_id, clientId, payload);
			} else if (message_type.equals(TOPIC_PATH_NDEATH)) {
				handleNDEATH(group_id, edge_node_id, clientId);
			} else if (message_type.equals(TOPIC_PATH_NDATA)) {
				handleNDATA(group_id, edge_node_id, payload);
			} else if (message_type.equals(TOPIC_PATH_DBIRTH)) {
				handleDBIRTH(group_id, edge_node_id, device_id, payload);
			} else if (message_type.equals(TOPIC_PATH_DDEATH)) {
				handleDDEATH(group_id, edge_node_id, device_id, payload);
			} else if (message_type.equals(TOPIC_PATH_DDATA)) {
				handleDDATA(group_id, edge_node_id, device_id, payload);
			}
		} else if (topic.startsWith(TOPIC_ROOT_STATE)) {
			if (packet.getPayload().isPresent()) {
				String payloadString = StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString();
				handleSTATE(clientId, topic, payloadString);
			}
		}
	}

	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)
	private void handleNBIRTH(String group_id, String edge_node_id, String clientId, PayloadOrBuilder payload) {
		logger.info("Monitor: *** NBIRTH *** {}/{} {}", group_id, edge_node_id, clientId);
		String client_id = (String) edge_nodes.get(edge_node_id);
		if (client_id != null && !client_id.equals(clientId)) {
			logger.error("Monitor: two clientids {} {} using the same group_id/edge_node_id {}", client_id, clientId,
					edge_node_id);
			testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR,
					setResult(false, TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR));

			testResults.put(ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR,
					setResult(false, PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR));
		} else {
			logger.info("Monitor: adding edge node {} for client id {} on NBIRTH", edge_node_id, clientId);
			edge_nodes.put(edge_node_id, clientId);
			clientids.put(clientId, edge_node_id);
			edge_to_devices.put(edge_node_id, new HashSet<String>());
		}

		String id = group_id + "/" + edge_node_id;
		if (payload.hasSeq()) {
			if (payload.getSeq() > 255 || payload.getSeq() < 0) {
				testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(false, PAYLOADS_NBIRTH_SEQ));
			}
			edgeSeqs.put(id, payload.getSeq());
			testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(true, PAYLOADS_NBIRTH_SEQ));
		} else {
			testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult(false, PAYLOADS_NBIRTH_SEQ));
		}

		if (payload != null) {
			edgeBirthMetrics.put(id, payload.getMetricsList());

			long lastHistoricalTimestamp = 0L;
			List<Metric> metrics = payload.getMetricsList();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();

				if (current.hasIsHistorical() && current.getIsHistorical() == false) {
					if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
							ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
							OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)) {
						log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER
								+ ": metric name: " + current.getName());
					}
					lastHistoricalTimestamp = current.getTimestamp();
				}
			}
		}
	}

	@SpecAssertion(
			section = Sections.TOPICS_EDGE_NODE_ID_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR)

	private void handleNDEATH(String group_id, String edge_node_id, String clientId) {
		logger.info("Monitor: *** NDEATH *** {}/{} {}", group_id, edge_node_id, clientId);
		String found_client_id = (String) edge_nodes.get(edge_node_id);

		if (found_client_id != null && !found_client_id.equals(clientId)) {
			logger.error("Monitor: two clientids {} {} using the same groups_id/edge_node_id {}", found_client_id,
					clientId, edge_node_id);
			testResults.put(ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR,
					setResult(false, TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR));

			testResults.put(ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR,
					setResult(false, PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR));
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
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_METRIC_REQS)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_TEMPLATES)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE)
	@SpecAssertion(
			section = Sections.PRINCIPLES_REPORT_BY_EXCEPTION,
			id = ID_PRINCIPLES_RBE_RECOMMENDED)
	private void handleNDATA(String group_id, String edge_node_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** NDATA *** {}/{}", group_id, edge_node_id);
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_NDATA_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(true, PAYLOADS_NDATA_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(false, PAYLOADS_NDATA_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_NDATA_SEQ_INC, setResult(false, PAYLOADS_NDATA_SEQ_INC));
		}

		long lastHistoricalTimestamp = 0L;
		List<Metric> metrics = payload.getMetricsList();
		String id = group_id + "/" + edge_node_id;
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();

			List<Metric> birthMetrics = edgeBirthMetrics.get(id);
			if (birthMetrics != null) {
				boolean found = false;
				// look for the current metric name in the birth metrics
				for (Metric birth : birthMetrics) {
					if (birth.getName().equals(current.getName())) {
						found = true;
						break;
					}
				}

				if (!setResultIfNotFail(testResults, found, ID_TOPICS_NBIRTH_METRIC_REQS, TOPICS_NBIRTH_METRIC_REQS)) {
					log("Test failed for assertion " + ID_TOPICS_NBIRTH_METRIC_REQS + ": metric name: "
							+ current.getName());
				}
				if (!setResultIfNotFail(testResults, found, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH)) {
					log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH + ": metric name: "
							+ current.getName());
				}
			}

			if (current.getDatatype() == DataType.Template.getNumber()) {
				if (current.hasTemplateValue()) {
					Template template = current.getTemplateValue();
					// instances must have a reference
					if (template.hasTemplateRef()) {
						boolean found = false;
						String templateName = template.getTemplateRef();
						// look for the template in the birth metrics
						for (Metric birth : birthMetrics) {
							if (birth.getName().equals(templateName)) {

								if (birth.hasTemplateValue()) {
									Template templatedef = birth.getTemplateValue();
									if (templatedef.hasIsDefinition() && templatedef.hasIsDefinition()
											&& !templatedef.hasTemplateRef()) {
										found = true;
										break;
									}
								}

							}
						}
						if (!setResultIfNotFail(testResults, found, ID_TOPICS_NBIRTH_TEMPLATES,
								TOPICS_NBIRTH_TEMPLATES)) {
							log("Test failed for assertion " + ID_TOPICS_NBIRTH_TEMPLATES + ": metric name: "
									+ current.getName());
						}
					}
				}
			}

			if (current.hasIsHistorical() && current.getIsHistorical() == false) {
				if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
						ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)) {
					log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER
							+ ": metric name: " + current.getName());
				}
				lastHistoricalTimestamp = current.getTimestamp();
			}

			List<Metric> lastMetrics = edgeLastMetrics.get(id);
			if (lastMetrics != null) {
				boolean found = false;
				// look for the current metric name in the last metrics
				for (Metric last : lastMetrics) {
					if (last.getName().equals(current.getName())) {
						found = true;
						if (!setShouldResultIfNotFail(testResults, current.equals(last),
								ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE,
								OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE)) {
							log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE
									+ ": metric name: " + current.getName());
						}
						if (!setShouldResultIfNotFail(testResults, current.equals(last), ID_PRINCIPLES_RBE_RECOMMENDED,
								ID_PRINCIPLES_RBE_RECOMMENDED)) {
							log("Test failed for assertion " + ID_PRINCIPLES_RBE_RECOMMENDED + ": metric name: "
									+ current.getName());
						}
						break;
					}
				}
			}
		}
		edgeLastMetrics.put(id, metrics);
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
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)
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
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DBIRTH_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(true, PAYLOADS_DBIRTH_SEQ_INC));
					}
					if (testResults.get(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ) == null) {
						testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
								setResult(true, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
					}
				} else {
					testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(false, PAYLOADS_DBIRTH_SEQ_INC));
					testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
							setResult(false, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(false, PAYLOADS_DBIRTH_SEQ_INC));
			testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ,
					setResult(false, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ));
		}

		if (payload != null) {
			String id = group_id + "/" + edge_node_id + "/" + device_id;
			deviceBirthMetrics.put(id, payload.getMetricsList());
			deviceLastMetrics.put(id, payload.getMetricsList());

			long lastHistoricalTimestamp = 0L;
			List<Metric> metrics = payload.getMetricsList();
			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();

				if (current.hasIsHistorical() && current.getIsHistorical() == false) {
					if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
							ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER,
							OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)) {
						log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER
								+ ": metric name: " + current.getName());
					}
					lastHistoricalTimestamp = current.getTimestamp();
				}
			}
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
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DDEATH_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(true, PAYLOADS_DDEATH_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(false, PAYLOADS_DDEATH_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(false, PAYLOADS_DDEATH_SEQ_INC));
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_SEQ_INC)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = ID_TOPICS_DBIRTH_METRIC_REQS)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_TEMPLATES)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE)
	@SpecAssertion(
			section = Sections.PRINCIPLES_REPORT_BY_EXCEPTION,
			id = ID_PRINCIPLES_RBE_RECOMMENDED)
	private void handleDDATA(String group_id, String edge_node_id, String device_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** DDATA *** {}/{}/{}", group_id, edge_node_id, device_id);
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;

			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					if (testResults.get(ID_PAYLOADS_DDATA_SEQ_INC) == null) {
						testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(true, PAYLOADS_DDATA_SEQ_INC));
					}
				} else {
					testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(false, PAYLOADS_DDATA_SEQ_INC));
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		} else {
			testResults.put(ID_PAYLOADS_DDATA_SEQ_INC, setResult(false, PAYLOADS_DDATA_SEQ_INC));
		}

		long lastHistoricalTimestamp = 0L;
		List<Metric> metrics = payload.getMetricsList();
		String id = group_id + "/" + edge_node_id + "/" + device_id;
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();

			List<Metric> birthMetrics = deviceBirthMetrics.get(id);
			if (birthMetrics != null) {
				boolean found = false;
				// look for the current metric name in the birth metrics
				for (Metric birth : birthMetrics) {
					if (birth.getName().equals(current.getName())) {
						found = true;
						break;
					}
				}

				if (!setResultIfNotFail(testResults, found, ID_TOPICS_DBIRTH_METRIC_REQS, TOPICS_DBIRTH_METRIC_REQS)) {
					log("Test failed for assertion " + ID_TOPICS_DBIRTH_METRIC_REQS + ": metric name: "
							+ current.getName());
				}
				if (!setResultIfNotFail(testResults, found, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH)) {
					log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH + ": metric name: "
							+ current.getName());
				}
			}

			List<Metric> eBirthMetrics = edgeBirthMetrics.get(id);
			if (current.getDatatype() == DataType.Template.getNumber()) {
				if (current.hasTemplateValue()) {
					Template template = current.getTemplateValue();
					// instances must have a reference
					if (template.hasTemplateRef()) {
						boolean found = false;
						String templateName = template.getTemplateRef();
						// look for the template in the birth metrics
						for (Metric birth : eBirthMetrics) {
							if (birth.getName().equals(templateName)) {

								if (birth.hasTemplateValue()) {
									Template templatedef = birth.getTemplateValue();
									if (templatedef.hasIsDefinition() && templatedef.hasIsDefinition()
											&& !templatedef.hasTemplateRef()) {
										found = true;
										break;
									}
								}

							}
						}
						if (!setResultIfNotFail(testResults, found, ID_TOPICS_NBIRTH_TEMPLATES,
								TOPICS_NBIRTH_TEMPLATES)) {
							log("Test failed for assertion " + ID_TOPICS_NBIRTH_TEMPLATES + ": metric name: "
									+ current.getName());
						}
					}
				}
			}

			if (current.hasIsHistorical() && current.getIsHistorical() == false) {
				if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
						ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)) {
					log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER
							+ ": metric name: " + current.getName());
				}
				lastHistoricalTimestamp = current.getTimestamp();
			}

			List<Metric> lastMetrics = deviceLastMetrics.get(id);
			if (lastMetrics != null) {
				boolean found = false;
				// look for the current metric name in the last metrics
				for (Metric last : lastMetrics) {
					if (last.getName().equals(current.getName())) {
						found = true;
						if (!setShouldResultIfNotFail(testResults, current.equals(last),
								ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE,
								OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE)) {
							log("Test failed for assertion " + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE
									+ ": metric name: " + current.getName());
						}
						if (!setShouldResultIfNotFail(testResults, current.equals(last), ID_PRINCIPLES_RBE_RECOMMENDED,
								PRINCIPLES_RBE_RECOMMENDED)) {
							log("Test failed for assertion " + ID_PRINCIPLES_RBE_RECOMMENDED + ": metric name: "
									+ current.getName());
						}
						break;
					}
				}
			}
		}
		deviceLastMetrics.put(id, metrics);
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID)
	private void handleSTATE(String clientId, String topic, String payload) {
		String hostid = null;
		String[] topicParts = topic.split("/");
		if (topicParts.length > 1) {
			hostid = topicParts[1];
		} else {
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		boolean isValidPayload = true;

		JsonNode json = null;
		try {
			json = mapper.readTree(payload);
		} catch (Exception e) {
			isValidPayload = false;
		}

		if (isValidPayload && json != null) {
			if (json.has("online")) {
				JsonNode online = json.get("online");
				if (online.isBoolean()) {
					boolean check = true;
					boolean state = online.booleanValue();
					String hostClientid = hostClientids.get(hostid);
					if (state) {
						if (hostClientid != null) {
							if (hostClientid.equals(clientId)) {
								check = false; // two different clientids with the same hostid online
							}
						}
						hostClientids.put(hostid, clientId);
					} else {
						if (hostClientid != null) {
							hostClientids.remove(hostid);
						} else {
							// didn't find host online but it might not be an error
						}
					}
					setShouldResultIfNotFail(testResults, check, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID,
							OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID);
				}
			}
		}
	}
}