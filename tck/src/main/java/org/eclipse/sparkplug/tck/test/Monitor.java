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

import static org.eclipse.sparkplug.tck.test.common.Constants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.Constants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_STATE;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CASE_SENSITIVITY_SPARKPLUG_IDS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CASE_SENSITIVITY_SPARKPLUG_IDS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_DEVICE_ID_CHARS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_DEVICE_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_EDGE_NODE_ID_CHARS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_EDGE_NODE_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_GROUP_ID_CHARS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_GROUP_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NBIRTH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TIMESTAMP_IN_UTC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PRINCIPLES_RBE_RECOMMENDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_BDSEQ_INCREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NBIRTH_TEMPLATES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_A;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_DEVICE_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_CHARS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_EDGE_NODE_ID_UNIQUENESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_GROUP_ID_CHARS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_GROUP_ID_STRING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DBIRTH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NBIRTH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_SEQUENCE_NUM_INCREMENTING;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TIMESTAMP_IN_UTC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PRINCIPLES_RBE_RECOMMENDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_BDSEQ_INCREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NBIRTH_TEMPLATES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_A;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkUTC;
import static org.eclipse.sparkplug.tck.test.common.Utils.getNextSeq;
import static org.eclipse.sparkplug.tck.test.common.Utils.getSparkplugPayload;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResultIfNotFail;
import static org.eclipse.sparkplug.tck.test.common.Utils.setShouldResultIfNotFail;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Template;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * This Monitor class holds tests for assertions that don't neatly fit into a single test scenario,
 * or apply all the time, so it runs alongside all tests in the Host and Edge profiles. It does not
 * apply to the Broker profile, and is switched off in that case.
 */

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0")
public class Monitor extends TCKTest implements ClientLifecycleEventListener {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	protected static final String TEST_FAILED_FOR_ASSERTION = "Monitor: Test failed for assertion ";
	private static final @NotNull String NAMESPACE = TOPIC_ROOT_SP_BV_1_0;
	private final TreeMap<String, String> testResults = new TreeMap<>();
	public static final @NotNull List<String> testIds = List.of(ID_INTRO_EDGE_NODE_ID_UNIQUENESS,
			ID_TOPIC_STRUCTURE_NAMESPACE_DUPLICATE_DEVICE_ID_ACROSS_EDGE_NODE,
			ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_EDGE_NODE_DESCRIPTOR, ID_TOPIC_STRUCTURE_NAMESPACE_UNIQUE_DEVICE_ID,
			ID_PAYLOADS_DBIRTH_SEQ_INC, ID_PAYLOADS_NBIRTH_EDGE_NODE_DESCRIPTOR, ID_TOPICS_DBIRTH_METRIC_REQS,
			ID_TOPICS_NBIRTH_METRIC_REQS, ID_TOPICS_NBIRTH_TEMPLATES, ID_TOPICS_NBIRTH_BDSEQ_INCREMENT,
			ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD, ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE, ID_PRINCIPLES_RBE_RECOMMENDED,
			ID_PAYLOADS_STATE_BIRTH_PAYLOAD, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_HOST_ID,
			ID_CASE_SENSITIVITY_SPARKPLUG_IDS, ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING, ID_PAYLOADS_TIMESTAMP_IN_UTC,
			ID_INTRO_GROUP_ID_STRING, ID_INTRO_GROUP_ID_CHARS, ID_INTRO_EDGE_NODE_ID_STRING,
			ID_INTRO_EDGE_NODE_ID_CHARS, ID_INTRO_DEVICE_ID_STRING, ID_INTRO_DEVICE_ID_CHARS, ID_TOPIC_STRUCTURE,
			ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES,
			ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES,
			ID_TOPIC_STRUCTURE_NAMESPACE_VALID_GROUP_ID, ID_TOPIC_STRUCTURE_NAMESPACE_VALID_EDGE_NODE_ID,
			ID_TOPIC_STRUCTURE_NAMESPACE_VALID_DEVICE_ID,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ, ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD,
			ID_PAYLOADS_NDATA_SEQ_INC, ID_PAYLOADS_DDATA_SEQ_INC, ID_TOPIC_STRUCTURE_NAMESPACE_A,
			ID_PAYLOADS_DDEATH_SEQ_INC, ID_PAYLOADS_NBIRTH_SEQ,
			ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD_SEQ);

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

	private HashMap<String, Map<Long, String>> edgeAliasMaps = new HashMap<>();

	// host application id to sequence number
	private HashMap<String, Long> hostTimestamps = new HashMap<String, Long>();

	// host application id to MQTT client id
	HashMap<String, String> hostClientids = new HashMap<String, String>();

	// device/edge ids lowercase to original
	HashMap<String, String> lowerGroupIds = new HashMap<String, String>();
	HashMap<String, String> lowerEdgeIds = new HashMap<String, String>();
	HashMap<String, String> lowerDeviceIds = new HashMap<String, String>();

	private Results results = null;

	private boolean ignoreBdSeqNumCheck = false;

	private boolean ignoreSeqNumCheck = false;

	private boolean ignoreDupHostCheck = false;

	public Monitor(Results results) {
		logger.info("Sparkplug TCK message monitor 1.0");
		this.results = results;
		clearResults();
	}

	public void clearResults() {
		for (int i = 0; i < testIds.size(); ++i) {
			testResults.put(testIds.get(i), NOT_EXECUTED);
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
		return testIds.toArray(new String[0]);
	}

	public void setIgnoreBdSeqNumCheck(boolean ignoreBdSeqNumCheck) {
		this.ignoreBdSeqNumCheck = ignoreBdSeqNumCheck;
	}

	public void setIgnoreSeqNumCheck(boolean ignoreSeqNumCheck) {
		this.ignoreSeqNumCheck = ignoreSeqNumCheck;
	}

	public void setIgnoreDupHostCheck(boolean ignoreDupHostCheck) {
		this.ignoreDupHostCheck = ignoreDupHostCheck;
	}

	public boolean hasEdgeNode(String groupId, String edgeNodeId) {
		return edge_nodes.containsKey(groupId + ":" + edgeNodeId);
	}

	public boolean hasDevice(String groupId, String edgeNodeId, String deviceId) {
		logger.info("Monitor edge {} ", edge_nodes.keySet().toString());
		String edgeId = groupId + ":" + edgeNodeId;
		return edge_to_devices.containsKey(edgeId) && edge_to_devices.get(edgeId).contains(deviceId);
	}

	public TreeMap<String, String> getResults() {
		TreeMap<String, String> labelledResults = new TreeMap<>();
		for (int i = 0; i < testIds.size(); ++i) {
			if (testResults.containsKey(testIds.get(i))) {
				labelledResults.put("Monitor:" + testIds.get(i), testResults.get(testIds.get(i)));
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

		if (hostClientids.values().contains(clientid)) {
			// remove hostid - clientid relation
			for (String hostid : hostClientids.keySet()) {
				if (hostClientids.get(hostid).equals(clientid)) {
					hostClientids.remove(hostid);
					break;
				}
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
			id = ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_DEATH,
			id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_BIRTH,
			id = ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ)
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
							if (!ignoreBdSeqNumCheck) {
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(edgeBdSeqs.get(id)),
										ID_TOPICS_NBIRTH_BDSEQ_INCREMENT, TOPICS_NBIRTH_BDSEQ_INCREMENT)) {
									log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_NBIRTH_BDSEQ_INCREMENT + ": edge id: "
											+ id);
									log("INFO: Actual bdseq: " + bdseq + " expected bdseq: "
											+ getNextSeq(edgeBdSeqs.get(id)));
								}
								if (!setResultIfNotFail(testResults, bdseq == getNextSeq(edgeBdSeqs.get(id)),
										ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ,
										MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ)) {
									log(TEST_FAILED_FOR_ASSERTION
											+ ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD_BDSEQ
											+ ": edge id: " + id);
									log("INFO: Actual bdseq: " + bdseq + " expected bdseq: "
											+ getNextSeq(edgeBdSeqs.get(id)));
								}
							}
						}
						edgeBdSeqs.put(id, bdseq);
					}
				}
			} else if (levels[0].equals(TOPIC_ROOT_SP_BV_1_0) && levels[1].equals(TOPIC_PATH_STATE)) {
				String hostid = levels[2];
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
					if (json.has("timestamp")) {
						JsonNode timestampNode = json.get("timestamp");
						long timestamp = -1;
						if (timestampNode.isLong()
								&& Utils.checkUTC(timestampNode.longValue(), results.getConfig().UTCwindow)) {
							timestamp = timestampNode.longValue();
						} else {
							setResultIfNotFail(testResults, false, ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD,
									HOST_TOPIC_PHID_BIRTH_PAYLOAD);
							log(TEST_FAILED_FOR_ASSERTION + ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD + ": host id: " + hostid
									+ " with timestamp=" + timestamp);
							return;
						}

						if (!setResultIfNotFail(testResults,
								timestampNode.isLong()
										&& Utils.checkUTC(timestampNode.longValue(), results.getConfig().UTCwindow),
								ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT,
								HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT)) {
							log(TEST_FAILED_FOR_ASSERTION + ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT
									+ ": host id: " + hostid + " with timestamp=" + timestamp);
						}

						if (hostTimestamps.get(hostid) != null) {
							if (!setResultIfNotFail(testResults, timestamp >= hostTimestamps.get(hostid),
									ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD, PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD)) {
								log(TEST_FAILED_FOR_ASSERTION + ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD + ": host id: "
										+ hostid + " received timestamp=" + timestamp + " expected >= "
										+ hostTimestamps.get(hostid));
							}
							if (!setResultIfNotFail(testResults, timestamp >= hostTimestamps.get(hostid),
									ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD,
									HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_CONNECT)) {
								log(TEST_FAILED_FOR_ASSERTION + ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD + ": host id: "
										+ hostid + " received timestamp=" + timestamp + " expected >= "
										+ hostTimestamps.get(hostid));
							}
							if (!setResultIfNotFail(testResults, timestamp >= hostTimestamps.get(hostid),
									ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD, HOST_TOPIC_PHID_BIRTH_PAYLOAD)) {
								log(TEST_FAILED_FOR_ASSERTION + ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD + ": host id: "
										+ hostid + " received timestamp=" + timestamp + " expected >= "
										+ hostTimestamps.get(hostid));
							}
							if (!setResultIfNotFail(testResults, timestamp >= hostTimestamps.get(hostid),
									ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD,
									OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD)) {
								log(TEST_FAILED_FOR_ASSERTION
										+ ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD + ": host id: "
										+ hostid + " received timestamp=" + timestamp + " expected >= "
										+ hostTimestamps.get(hostid));
							}
						}
						hostTimestamps.put(hostid, (long) timestamp);

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

	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_TIMESTAMP_IN_UTC)
	@SpecAssertion(
			section = Sections.TOPICS_NAMESPACE_ELEMENT,
			id = ID_TOPIC_STRUCTURE_NAMESPACE_A)
	@Override
	public void publish(String clientId, PublishPacket packet) {

		String topic = packet.getTopic();

		if (topic.startsWith("spAv1.0/")) {
			log("Warning - non-standard Sparkplug A message received");
			testResult(ID_TOPIC_STRUCTURE_NAMESPACE_A, setResult(false, TOPIC_STRUCTURE_NAMESPACE_A));
		} else if (topic.startsWith(NAMESPACE)) {
			String[] topicParts = topic.split("/");
			// topic is spBv1.0/group_id/message_type/edge_node_id/[device_id]"
			// or spBv1.0/STATE/hostid

			checkTopic(topicParts);

			if (topicParts.length > 5 || topicParts.length < 3) {
				return;
			}

			if (topicParts.length == 3 && Constants.TOPIC_PATH_STATE.equals(topicParts[1])) {
				if (packet.getPayload().isPresent()) {
					String payloadString = StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString();
					handleSTATE(clientId, topic, payloadString);
				}
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

			if (payload.hasTimestamp()) {
				setResultIfNotFail(testResults, checkUTC(payload.getTimestamp(), results.getConfig().UTCwindow),
						ID_PAYLOADS_TIMESTAMP_IN_UTC, PAYLOADS_TIMESTAMP_IN_UTC);
			}

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
			} else {
				logger.info("Monitor: *** {} *** {}/{} {}", message_type, group_id, edge_node_id,
						(device_id == null) ? "" : device_id);
			}
		}
	}

	private void testResult(String id, String state) {
		// Don't override a failing test fail
		if (testResults.get(id) != null && !((String) testResults.get(id)).startsWith(FAIL)) {
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
		if (elements[0].equals(TOPIC_ROOT_SP_BV_1_0) && elements[1].equals(TOPIC_PATH_STATE)) {
			if (elements.length == 3) {
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

					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES, setResult(
							elements.length == 5, TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_ASSOCIATED_MESSAGE_TYPES));
					result = (elements.length == 5) ? true : false;
				}

				if (message_type.equals("NBIRTH") || message_type.equals("NDEATH") || message_type.equals("NDATA")
						|| message_type.equals("NCMD")) {

					testResult(ID_TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES, setResult(
							elements.length == 4, TOPIC_STRUCTURE_NAMESPACE_DEVICE_ID_NON_ASSOCIATED_MESSAGE_TYPES));
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
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_CASE_SENSITIVITY,
			id = ID_CASE_SENSITIVITY_SPARKPLUG_IDS)
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

		boolean lowerResult = true;

		String lowGroupId = group_id.toLowerCase();
		if (lowerGroupIds.containsKey(lowGroupId)) {
			lowerResult = group_id.equals(lowerGroupIds.get(lowGroupId));
		} else {
			lowerGroupIds.put(lowGroupId, group_id);
		}
		setShouldResultIfNotFail(testResults, lowerResult, ID_CASE_SENSITIVITY_SPARKPLUG_IDS,
				CASE_SENSITIVITY_SPARKPLUG_IDS + " group ids: " + group_id + " " + lowerGroupIds.get(lowGroupId));

		String lowEdgeId = edge_node_id.toLowerCase();
		if (lowerEdgeIds.containsKey(lowEdgeId)) {
			lowerResult = edge_node_id.equals(lowerEdgeIds.get(lowEdgeId));
		} else {
			lowerEdgeIds.put(lowEdgeId, edge_node_id);
		}
		setShouldResultIfNotFail(testResults, lowerResult, ID_CASE_SENSITIVITY_SPARKPLUG_IDS,
				CASE_SENSITIVITY_SPARKPLUG_IDS + " edge ids: " + edge_node_id + " " + lowerEdgeIds.get(lowEdgeId));

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
			boolean aliasMapInitialized = false;
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();

				if (current.hasAlias()) {
					Map<Long, String> aliasMap = edgeAliasMaps.get(id);
					if (!aliasMapInitialized) {
						aliasMap = edgeAliasMaps.computeIfAbsent(id, (k) -> new HashMap<>());
						aliasMap.clear();
						aliasMapInitialized = true;
					}

					logger.debug("Creating alias: {} -> {}", current.getAlias(), current.getName());
					aliasMap.put(current.getAlias(), current.getName());
				}

				if (current.hasIsHistorical() && current.getIsHistorical() == false) {
					if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
							ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
							OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)) {
						log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER
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
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING)
	private void handleNDATA(String group_id, String edge_node_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** NDATA *** {}/{}", group_id, edge_node_id);

		boolean correct_seq = false;
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;
			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					correct_seq = true;
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		}
		if (ignoreSeqNumCheck) {
			setResultIfNotFail(testResults, true, ID_PAYLOADS_NDATA_SEQ_INC, PAYLOADS_NDATA_SEQ_INC);
			setResultIfNotFail(testResults, true, ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING,
					PAYLOADS_SEQUENCE_NUM_INCREMENTING);
		} else {
			setResultIfNotFail(testResults, correct_seq, ID_PAYLOADS_NDATA_SEQ_INC, PAYLOADS_NDATA_SEQ_INC);
			setResultIfNotFail(testResults, correct_seq, ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING,
					PAYLOADS_SEQUENCE_NUM_INCREMENTING);
		}

		long lastHistoricalTimestamp = 0L;
		List<Metric> metrics = payload.getMetricsList();
		String id = group_id + "/" + edge_node_id;
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();

			// Get the metric name if aliases are used and set it as needed
			String currentMetricName = current.getName();
			if (!current.hasName() && current.hasAlias()) {
				currentMetricName = edgeAliasMaps.get(id).get(current.getAlias());
				logger.debug("Got currentMetricName from alias: {} -> {}", current.getAlias(), currentMetricName);
			}

			List<Metric> birthMetrics = edgeBirthMetrics.get(id);
			if (birthMetrics != null) {
				boolean found = false;
				// look for the current metric name in the birth metrics
				for (Metric birth : birthMetrics) {
					if (birth.getName().equals(currentMetricName)) {
						found = true;
						break;
					}
				}

				if (!setResultIfNotFail(testResults, found, ID_TOPICS_NBIRTH_METRIC_REQS, TOPICS_NBIRTH_METRIC_REQS)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_NBIRTH_METRIC_REQS + ": metric name: "
							+ currentMetricName);
				}
				if (!setResultIfNotFail(testResults, found, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH + ": metric name: "
							+ currentMetricName);
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
							log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_NBIRTH_TEMPLATES + ": metric name: "
									+ currentMetricName);
						}
					}
				}
			}

			if (current.hasIsHistorical() && current.getIsHistorical() == false) {
				if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
						ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_ORDER
							+ ": metric name: " + currentMetricName);
				}
				lastHistoricalTimestamp = current.getTimestamp();
			}

			List<Metric> lastMetrics = edgeLastMetrics.get(id);
			if (lastMetrics != null) {
				boolean found = false;
				// look for the current metric name in the last metrics
				for (Metric last : lastMetrics) {
					// Get the metric name if aliases are used and set it as needed
					String lastMetricName = last.getName();
					if (!last.hasName() && last.hasAlias()) {
						lastMetricName = edgeAliasMaps.get(id).get(last.getAlias());
					}

					if (lastMetricName.equals(currentMetricName)) {
						found = true;

						if (!setShouldResultIfNotFail(testResults, !metricsEqual(current, last),
								ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE,
								OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE)) {
							log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_CHANGE
									+ ": metric name: " + currentMetricName);
						}
						if (!setShouldResultIfNotFail(testResults, !metricsEqual(current, last),
								ID_PRINCIPLES_RBE_RECOMMENDED, PRINCIPLES_RBE_RECOMMENDED)) {
							log(TEST_FAILED_FOR_ASSERTION + ID_PRINCIPLES_RBE_RECOMMENDED + ": metric name: "
									+ currentMetricName);
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
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_CASE_SENSITIVITY,
			id = ID_CASE_SENSITIVITY_SPARKPLUG_IDS)
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

		boolean lowerResult = true;

		String lowGroupId = group_id.toLowerCase();
		if (lowerGroupIds.containsKey(lowGroupId)) {
			lowerResult = group_id.equals(lowerGroupIds.get(lowGroupId));
		} else {
			lowerGroupIds.put(lowGroupId, group_id);
		}
		setShouldResultIfNotFail(testResults, lowerResult, ID_CASE_SENSITIVITY_SPARKPLUG_IDS,
				CASE_SENSITIVITY_SPARKPLUG_IDS + " group ids: " + group_id + " " + lowerGroupIds.get(lowGroupId));

		String lowEdgeId = edge_node_id.toLowerCase();
		if (lowerEdgeIds.containsKey(lowEdgeId)) {
			lowerResult = edge_node_id.equals(lowerEdgeIds.get(lowEdgeId));
		} else {
			lowerEdgeIds.put(lowEdgeId, edge_node_id);
		}
		setShouldResultIfNotFail(testResults, lowerResult, ID_CASE_SENSITIVITY_SPARKPLUG_IDS,
				CASE_SENSITIVITY_SPARKPLUG_IDS + " edge ids: " + edge_node_id + " " + lowerEdgeIds.get(lowEdgeId));

		String lowDeviceId = device_id.toLowerCase();
		if (lowerDeviceIds.containsKey(lowDeviceId)) {
			lowerResult = device_id.equals(lowerDeviceIds.get(lowDeviceId));
		} else {
			lowerDeviceIds.put(lowDeviceId, device_id);
		}
		setShouldResultIfNotFail(testResults, lowerResult, ID_CASE_SENSITIVITY_SPARKPLUG_IDS,
				CASE_SENSITIVITY_SPARKPLUG_IDS + " device ids: " + device_id + " " + lowerDeviceIds.get(lowDeviceId));

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
			boolean aliasMapInitialized = false;
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();

				if (current.hasAlias()) {
					Map<Long, String> aliasMap = edgeAliasMaps.get(id);
					if (!aliasMapInitialized) {
						aliasMap = edgeAliasMaps.computeIfAbsent(id, (k) -> new HashMap<>());
						aliasMap.clear();
						aliasMapInitialized = true;
					}

					logger.debug("Creating alias: {} -> {}", current.getAlias(), current.getName());
					aliasMap.put(current.getAlias(), current.getName());
				}

				if (current.hasIsHistorical() && current.getIsHistorical() == false) {
					if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
							ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER,
							OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)) {
						log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER
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

		if (payload != null && payload.hasSeq()) {
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
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING)
	private void handleDDATA(String group_id, String edge_node_id, String device_id, PayloadOrBuilder payload) {
		logger.info("Monitor: *** DDATA *** {}/{}/{}", group_id, edge_node_id, device_id);

		boolean correct_seq = false;
		if (payload.hasSeq()) {
			String id = group_id + "/" + edge_node_id;
			if (edgeSeqs.get(id) != null) {
				long expectedSeq = getNextSeq((Long) edgeSeqs.get(id));
				if (payload.getSeq() == expectedSeq) {
					correct_seq = true;
				}
			}
			edgeSeqs.put(id, payload.getSeq());
		}
		if (ignoreSeqNumCheck) {
			setResultIfNotFail(testResults, true, ID_PAYLOADS_NDATA_SEQ_INC, PAYLOADS_NDATA_SEQ_INC);
			setResultIfNotFail(testResults, true, ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING,
					PAYLOADS_SEQUENCE_NUM_INCREMENTING);
		} else {
			setResultIfNotFail(testResults, correct_seq, ID_PAYLOADS_DDATA_SEQ_INC, PAYLOADS_DDATA_SEQ_INC);
			setResultIfNotFail(testResults, correct_seq, ID_PAYLOADS_SEQUENCE_NUM_INCREMENTING,
					PAYLOADS_SEQUENCE_NUM_INCREMENTING);
		}

		long lastHistoricalTimestamp = 0L;
		List<Metric> metrics = payload.getMetricsList();
		String id = group_id + "/" + edge_node_id + "/" + device_id;
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();

			// Get the metric name if aliases are used and set it as needed
			String currentMetricName = current.getName();
			if (!current.hasName() && current.hasAlias()) {
				currentMetricName = edgeAliasMaps.get(id).get(current.getAlias());
				logger.debug("Got currentMetricName from alias: {} -> {}", current.getAlias(), currentMetricName);
			}

			List<Metric> birthMetrics = deviceBirthMetrics.get(id);
			if (birthMetrics != null) {
				boolean found = false;
				// look for the current metric name in the birth metrics
				for (Metric birth : birthMetrics) {
					if (birth.getName().equals(currentMetricName)) {
						found = true;
						break;
					}
				}

				if (!setResultIfNotFail(testResults, found, ID_TOPICS_DBIRTH_METRIC_REQS, TOPICS_DBIRTH_METRIC_REQS)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_DBIRTH_METRIC_REQS + ": metric name: "
							+ currentMetricName);
				}
				if (!setResultIfNotFail(testResults, found, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH + ": metric name: "
							+ currentMetricName);
				}
			}

			List<Metric> eBirthMetrics = edgeBirthMetrics.get(id);
			if (eBirthMetrics != null && current.getDatatype() == DataType.Template.getNumber()) {
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
							log(TEST_FAILED_FOR_ASSERTION + ID_TOPICS_NBIRTH_TEMPLATES + ": metric name: "
									+ currentMetricName);
						}
					}
				}
			}

			if (current.hasIsHistorical() && current.getIsHistorical() == false) {
				if (!setResultIfNotFail(testResults, current.getTimestamp() >= lastHistoricalTimestamp,
						ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER,
						OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER)) {
					log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_ORDER
							+ ": metric name: " + currentMetricName);
				}
				lastHistoricalTimestamp = current.getTimestamp();
			}

			List<Metric> lastMetrics = deviceLastMetrics.get(id);
			if (lastMetrics != null) {
				boolean found = false;
				// look for the current metric name in the last metrics
				for (Metric last : lastMetrics) {
					// Get the metric name if aliases are used and set it as needed
					String lastMetricName = last.getName();
					if (!last.hasName() && last.hasAlias()) {
						lastMetricName = edgeAliasMaps.get(id).get(last.getAlias());
					}

					if (lastMetricName.equals(currentMetricName)) {
						found = true;
						if (!setShouldResultIfNotFail(testResults, !metricsEqual(current, last),
								ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE,
								OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE)) {
							log(TEST_FAILED_FOR_ASSERTION + ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_CHANGE
									+ ": metric name: " + currentMetricName);
						}
						if (!setShouldResultIfNotFail(testResults, !metricsEqual(current, last),
								ID_PRINCIPLES_RBE_RECOMMENDED, PRINCIPLES_RBE_RECOMMENDED)) {
							log(TEST_FAILED_FOR_ASSERTION + ID_PRINCIPLES_RBE_RECOMMENDED + ": metric name: "
									+ currentMetricName);
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
		if (ignoreDupHostCheck) {
			return;
		}

		String hostid = null;
		String[] topicParts = topic.split("/");
		if (topicParts.length == 3) {
			hostid = topicParts[2];
		} else {
			return;
		}
		logger.info("Monitor: clientid {} *** STATE *** {} {}", clientId, hostid, payload);

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

	private boolean metricsEqual(Metric metricOne, Metric metricTwo) {
		/*
		 * We don't use the metric equals methods because it includes a timestamp check too.
		 * We should consider metrics equal even if the timestamp has been updated.
		 * - Wes Johnson
		 */
		if (metricOne == null && metricTwo == null) {
			logger.debug("metricOne and metricTwo are null");
			return true;
		} else if (metricOne != null && metricTwo == null) {
			logger.debug("metricOne is not null and metricTwo is null");
			return false;
		} else if (metricOne == null && metricTwo != null) {
			logger.debug("metricOne is null and metricTwo is not null");
			return false;
		} else {
			logger.debug("metricOne and metricTwo are not null");
			if (metricOne.getAlias() == metricTwo.getAlias() && metricOne.getName() == metricTwo.getName()
					&& metricOne.getBooleanValue() == metricTwo.getBooleanValue()
					&& metricOne.getBytesValue() == metricTwo.getBytesValue()
					&& metricOne.getDatasetValue() == metricTwo.getDatasetValue()
					&& metricOne.getDoubleValue() == metricTwo.getDoubleValue()
					&& metricOne.getFloatValue() == metricTwo.getFloatValue()
					&& metricOne.getIntValue() == metricTwo.getIntValue()
					&& metricOne.getLongValue() == metricTwo.getLongValue()
					&& metricOne.getStringValue() == metricTwo.getStringValue()
					&& metricOne.getTemplateValue() == metricTwo.getTemplateValue()) {
				// Metrics are considered equal if the name, alias, and values are equal
				logger.trace("Metrics match...");
				return true;
			} else {
				logger.trace("Metrics don't match...");
				return false;
			}
		}
	}
}
