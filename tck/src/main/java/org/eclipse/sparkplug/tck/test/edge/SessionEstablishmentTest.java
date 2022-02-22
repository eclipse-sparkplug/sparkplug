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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.general.UserProperties;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.MetricDataType;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;

/**
 * This is the edge node Sparkplug session establishment.
 *
 * @author Ian Craggs
 * @author Mitchell McPartland
 */
@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class SessionEstablishmentTest extends TCKTest {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	private static final @NotNull String PASS = "PASS";
	private static final @NotNull String FAIL = "FAIL";

	private final @NotNull Map<String, String> testResults = new HashMap<>();
	private final @NotNull List<String> testIds = List.of("principles-birth-certificates-order",
			"principles-persistence-clean-session", "payloads-ndeath-will-message-qos", "payloads-ndeath-seq",
			"topics-ndeath-seq", "topics-ndeath-payload", "payloads-ndeath-will-message-retain",
			"payloads-ndeath-will-message", "payloads-nbirth-qos", "payloads-nbirth-retain", "payloads-nbirth-seq",
			"payloads-sequence-num-zero-nbirth", "payloads-nbirth-bdseq", "payloads-nbirth-timestamp",
			"payloads-nbirth-rebirth-req", "payloads-ndeath-bdseq", "edge-subscribe-ncmd", "edge-subscribe-dcmd",
			"message-flow-edge-node-birth-publish-subscribe", "topics-nbirth-mqtt", "topics-nbirth-seq-num",
			"topics-nbirth-timestamp", "topics-nbirth-bdseq-included", "topics-nbirth-bdseq-matching",
			"topics-nbirth-rebirth-metric", "payloads-dbirth-qos", "payloads-dbirth-retain", "topics-dbirth-mqtt",
			"topics-dbirth-timestamp", "payloads-dbirth-timestamp", "payloads-dbirth-seq", "topics-dbirth-seq",
			"payloads-dbirth-seq-inc", "payloads-dbirth-order", "operational-behavior-data-commands-rebirth-name",
			"operational-behavior-data-commands-rebirth-datatype", "operational-behavior-data-commands-rebirth-value");

	private final @NotNull TCK theTCK;
	private final @NotNull Map<String, Boolean> deviceIds = new HashMap<>();

	private @NotNull String testClientId = null;
	private @NotNull String hostApplicationId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull boolean ncmdFound = false;
	private @NotNull boolean dcmdFound = false;
	private @NotNull boolean stateFound = false;
	private @NotNull boolean ndataFound = false;
	private @NotNull boolean ddataFound = false;
	private @NotNull long seq = -1;
	private @NotNull long deathBdSeq = -1;
	private @NotNull long birthBdSeq = -1;

	public SessionEstablishmentTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
		logger.info(
				"Edge Node session establishment test. Parameters: host_application_id group_id edge_node_id [device_ids]");
		theTCK = aTCK;

		for (final String testId : testIds) {
			testResults.put(testId, "");
		}

		if (parms.length < 3) {
			logger.info(
					"Parameters to edge session establishment test must be: hostApplicationId groupId edgeNodeId [deviceIds]");
			return;
		}

		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];

		// there is at least one device
		if (parms.length > 3) {
			for (int i = 3; i < parms.length; i++) {
				deviceIds.put(parms[i], false);
			}
		} else {
			// no devices
			testResults.put("payloads-dbirth-qos", PASS);
			testResults.put("payloads-dbirth-retain", PASS);
			testResults.put("payloads-dbirth-timestamp", PASS);
			testResults.put("payloads-dbirth-seq", PASS);
			testResults.put("topics-dbirth-mqtt", PASS);
			testResults.put("topics-dbirth-timestamp", PASS);
		}

		logger.info("Host application id is " + hostApplicationId);
		logger.info("Group id is " + groupId);
		logger.info("Edge node id is " + edgeNodeId);
		logger.info("Device ids are " + Arrays.toString(deviceIds.keySet().toArray()));
	}

	public void endTest() {
		testClientId = null;
		reportResults(testResults);
	}

	public String getName() {
		return "SessionEstablishment";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@SpecAssertion(
			section = Sections.PRINCIPLES_PERSISTENT_VS_NON_PERSISTENT_CONNECTIONS,
			id = "principles-persistence-clean-session")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = "payloads-ndeath-will-message")
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {	
		/* Determine if this the connect packet for the Edge node under test.
		 * Set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals("spBv1.0/" + groupId + "/NDEATH/" + edgeNodeId)) {
				testClientId = clientId;
				logger.info("Edge session establishment test - connect - client id is "+clientId);
			}
		}
		
		if (testClientId != null) {
			final String isCleanSession;
			if (packet.getCleanStart()) {
				isCleanSession = PASS;
			} else {
				isCleanSession = FAIL + " (Clean session should be set to true.)";
			}
			testResults.put("principles-persistence-clean-session", isCleanSession);

			String willPresent = FAIL + " (NDEATH not registered as Will in connect packet)";
			// Optional<WillPublishPacket> willPublishPacketOptional = null;
			try {
				willPublishPacketOptional = checkWillMessage(packet);
				if (willPublishPacketOptional.isPresent()) {
					willPresent = PASS;
				}
				testResults.put("payloads-ndeath-will-message", willPresent);
			} catch (Exception e) {
				logger.info("Exception", e);
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub

	}

	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
		logger.info("Edge session establishment test - subscribe");

		String topic = "";

		List<Subscription> subscriptions = packet.getSubscriptions();
		for (Subscription s : subscriptions) {
			topic = s.getTopicFilter();
			if (topic.startsWith("STATE/" + hostApplicationId)) {
				stateFound = true;
			} else if (testClientId != null && testClientId.equals(clientId)) {
				if (topic.startsWith("spBv1.0/" + groupId + "/NCMD/" + edgeNodeId)) {
					ncmdFound = true;
				} else if (topic.startsWith("spBv1.0/" + groupId + "/DCMD/" + edgeNodeId)) {
					dcmdFound = true;
				}
			}
		}
	}

	@SpecAssertion(
			section = Sections.PRINCIPLES_BIRTH_AND_DEATH_CERTIFICATES,
			id = "principles-birth-certificates-order")
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		logger.info("Edge session establishment test - publish");

		if (testClientId != null && testClientId.equals(clientId)) {
			String topic = packet.getTopic();
			if (topic.equals("spBv1.0/" + groupId + "/NBIRTH/" + edgeNodeId)) {
				checkNBirth(packet);
				if (ndataFound || ddataFound) {
					testResults.put("principles-birth-certificates-order", FAIL + " Birth certificates must be first");
				} else {
					testResults.put("principles-birth-certificates-order", PASS);
				}
			} else if (topic.startsWith("spBv1.0/" + groupId + "/DBIRTH/")) {
				String[] topicParts = topic.split("/");
				String device = topicParts[topicParts.length - 1];
				deviceIds.put(device, true);
				checkDBirth(packet);
			} else if (topic.startsWith("spBv1.0/" + groupId + "/NDATA")) {
				ndataFound = true;
			} else if (topic.startsWith("spBv1.0/" + groupId + "/DDATA")) {
				ddataFound = true;
			}
			logger.info("topic " + packet.getTopic());

			if (deviceIds.size() == 0) {
				checkSubscribeTopics();
				theTCK.endTest();
			}
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = "payloads-ndeath-will-message-qos")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = "payloads-ndeath-seq")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDEATH,
			id = "topics-ndeath-seq")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = "payloads-ndeath-will-message-retain")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDEATH,
			id = "topics-ndeath-payload")
	public Optional<WillPublishPacket> checkWillMessage(final @NotNull ConnectPacket packet) {
		final Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String topic = willPublishPacket.getTopic();

			// NDEATH message must set MQTT Will QoS to 1
			String isQos1 = FAIL + " (NDEATH message must have Qos set to 1)";
			if (willPublishPacket.getQos().getQosNumber() == 1) {
				isQos1 = PASS;
			}
			testResults.put("payloads-ndeath-will-message-qos", isQos1);

			ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
			SparkplugBPayload sparkplugPayload = decode(payload);

			List<Metric> metrics = sparkplugPayload.getMetrics();

			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					deathBdSeq = (long) m.getValue();
					break;
				}
			}

			// NDEATH message contains a very simple payload that MUST only
			// include a single metric, the bdseq number
			String onlyBdSeq = FAIL + " (NDEATH payload must only include a single metric, the bdSeq number)";
			if (deathBdSeq != -1 && metrics.size() == 1) {
				onlyBdSeq = PASS;
			}
			testResults.put("topics-ndeath-payload", onlyBdSeq);

			// death message must not include a sequence number
			String noSeq = FAIL + " (NDEATH must not include a sequence number)";
			if (sparkplugPayload.getSeq() == -1) {
				noSeq = PASS;
			}
			testResults.put("payloads-ndeath-seq", noSeq);
			testResults.put("topics-ndeath-seq", noSeq);

			// retained flag must be false
			String retainedFalse = FAIL + " (NDEATH retained flag must be false)";
			if (!willPublishPacket.getRetain()) {
				retainedFalse = PASS;
			}
			testResults.put("payloads-ndeath-will-message-retain", retainedFalse);

		}
		return willPublishPacketOptional;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-mqtt")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-seq-num")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-timestamp")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-bdseq-included")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-bdseq-matching")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = "topics-nbirth-rebirth-metric")

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-name")
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-datatype")
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = "operational-behavior-data-commands-rebirth-value")

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-bdseq")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-qos")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-retain")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-seq")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = "payloads-sequence-num-zero-nbirth")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-timestamp")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = "payloads-nbirth-rebirth-req")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = "payloads-ndeath-bdseq")
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = "message-flow-edge-node-birth-publish-subscribe")
	public void checkNBirth(final @NotNull PublishPacket packet) {
		Date receivedBirth = new Date();
		long millisReceivedBirth = receivedBirth.getTime();
		long millisPastFiveMin = millisReceivedBirth - (5 * 60 * 1000);

		// edge node must subscribe to state before publishing nbirth
		String stateBeforeNBirth = FAIL + " (Edge node must subscribe to state before publishing NBIRTH)";
		if (stateFound == true) {
			stateBeforeNBirth = PASS;
		}
		testResults.put("message-flow-edge-node-birth-publish-subscribe", stateBeforeNBirth);

		ByteBuffer payload = packet.getPayload().orElseGet(null);
		SparkplugBPayload sparkplugPayload = decode(payload);

		// qos must be 0
		String isQos0 = FAIL + " (NBIRTH message must have Qos set to 0)";
		if (packet.getQos().getQosNumber() == 0) {
			isQos0 = PASS;
		}
		testResults.put("payloads-nbirth-qos", isQos0);
		testResults.put("topics-nbirth-mqtt", isQos0);

		// retained must be false
		String retainedFalse = FAIL + " (NBIRTH retained flag must be false)";
		if (!packet.getRetain()) {
			retainedFalse = PASS;
		}
		testResults.put("payloads-nbirth-retain", retainedFalse);

		// sequence number must be 0
		String isSeq0 = FAIL + " (NBIRTH sequence number must be 0)";
		seq = sparkplugPayload.getSeq();
		if (seq == 0) {
			isSeq0 = PASS;
		}
		testResults.put("payloads-nbirth-seq", isSeq0);
		testResults.put("payloads-sequence-num-zero-nbirth", isSeq0);
		testResults.put("topics-nbirth-seq-num", isSeq0);

		// making sure that the payload timestamp is greater than (recievedBirthTime - 5 min) and less than the
		// receivedBirthTime
		String publishedTs = FAIL
				+ " (NBIRTH must include payload timestamp that denotes the time at which the message was published)";
		Date ts = sparkplugPayload.getTimestamp();
		if (ts != null) {
			long millisPayload = ts.getTime();
			if (millisPayload > millisPastFiveMin && millisPayload < (millisReceivedBirth)) {
				publishedTs = PASS;
			}
		}
		testResults.put("payloads-nbirth-timestamp", publishedTs);
		testResults.put("topics-nbirth-timestamp", publishedTs);

		boolean rebirthFound = false;
		boolean bdSeqFound = false;

		MetricDataType datatype = null;
		boolean rebirthVal = true;

		if (sparkplugPayload != null) {
			List<Metric> metrics = sparkplugPayload.getMetrics();
			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					bdSeqFound = true;
					birthBdSeq = (long) m.getValue();
				} else if (m.getName().equals("Node Control/Rebirth")) {
					rebirthFound = true;
					datatype = m.getDataType();
					rebirthVal = (boolean) m.getValue();
				} else if (bdSeqFound == true && rebirthFound == true) {
					// if bdseq and rebirth were already found, then we have
					// the info we need and we can break out of this loop
					break;
				}
			}
		}
		// every nbirth must include a bdSeq
		String bdSeqIncluded = FAIL + " (NBIRTH must include a bdSeq)";
		if (birthBdSeq != -1) {
			bdSeqIncluded = PASS;
		}
		testResults.put("payloads-nbirth-bdseq", bdSeqIncluded);
		testResults.put("topics-nbirth-bdseq-included", bdSeqIncluded);

		// the birth bdSeq "must match the bdseq number provided in the MQTT CONNECT packets Will Message payload"
		String bdSeqMatches =
				FAIL + " (NBIRTH bdSeq must match bdSeq provided in Will Message payload of connect packet)";
		if (birthBdSeq != -1 && deathBdSeq != -1 && birthBdSeq == deathBdSeq) {
			bdSeqMatches = PASS;
		}
		testResults.put("payloads-ndeath-bdseq", bdSeqMatches);
		testResults.put("topics-nbirth-bdseq-matching", bdSeqMatches);

		// nbirth message must include 'node control/rebirth' metric
		String rebirthIncluded = FAIL + " (NBIRTH must include a 'node control/rebirth' metric)";
		if (rebirthFound == true) {
			rebirthIncluded = PASS;
		}
		testResults.put("payloads-nbirth-rebirth-req", rebirthIncluded);
		testResults.put("topics-nbirth-rebirth-metric", rebirthIncluded);
		testResults.put("operational-behavior-data-commands-rebirth-name", rebirthIncluded);

		String rebirthBoolean = FAIL + " (NBIRTH 'node control/rebirth' metric must be boolean)";
		if (rebirthFound == true && datatype == MetricDataType.Boolean) {
			rebirthBoolean = PASS;
		}
		testResults.put("operational-behavior-data-commands-rebirth-datatype", rebirthBoolean);

		logger.info("4 metric value "+rebirthVal+" type "+datatype + " " +(datatype == MetricDataType.Boolean));
		String rebirthValue = FAIL + " (NBIRTH 'node control/rebirth' metric must == false)";
		if (rebirthFound == true && datatype == MetricDataType.Boolean && rebirthVal == false) {
			rebirthValue = PASS;
		}
		testResults.put("operational-behavior-data-commands-rebirth-value", rebirthValue);

	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-qos")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-retain")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-timestamp")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-seq")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-seq-inc")
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = "payloads-dbirth-order")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = "topics-dbirth-mqtt")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = "topics-dbirth-timestamp")
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = "topics-dbirth-seq")
	public void checkDBirth(final @NotNull PublishPacket packet) {
		Date receivedBirth = new Date();
		long millisReceivedBirth = receivedBirth.getTime();
		long millisPastFiveMin = millisReceivedBirth - (5 * 60 * 1000);

		ByteBuffer payload = packet.getPayload().orElseGet(null);
		SparkplugBPayload sparkplugPayload = decode(payload);

		// qos must be 0
		String isQos0 = FAIL + " (NBIRTH message must have Qos set to 0)";
		String prevResult = testResults.getOrDefault("payloads-dbirth-qos", "");

		if (!prevResult.contains(FAIL)) {
			if (packet.getQos().getQosNumber() == 0) {
				isQos0 = PASS;
			}
			if (prevResult.equals("")) {
				testResults.put("payloads-dbirth-qos", isQos0);
			}
		}

		// retained must be be false
		String retainedFalse = FAIL + " (NBIRTH retained flag must be false)";
		prevResult = testResults.getOrDefault("payloads-dbirth-retain", "");

		if (!prevResult.contains(FAIL)) {
			if (!packet.getRetain()) {
				retainedFalse = PASS;
			}
			if (prevResult.equals("")) {
				testResults.put("payloads-dbirth-retain", retainedFalse);
			}
		}

		// for topics-dbirth-mqtt, qos must be 0 and retained must be false
		String isQos0AndRetainedFalse = FAIL + " (DBIRTH Qos must be 0 and retained must be false)";
		prevResult = testResults.getOrDefault("topics-dbirth-mqtt", "");

		if (!prevResult.contains(FAIL)) {
			if (testResults.get("payloads-dbirth-qos") == PASS && testResults.get("payloads-dbirth-retain") == PASS
					&& !prevResult.contains(FAIL)) {
				isQos0AndRetainedFalse = PASS;
			}
			if (prevResult.equals("")) {
				testResults.put("topics-dbirth-mqtt", isQos0AndRetainedFalse);
			}
		}

		// making sure that the payload timestamp is greater than (recievedBirthTime - 5 min) and less than the
		// receivedBirthTime
		String publishedTs = FAIL
				+ " (NBIRTH must include payload timestamp that denotes the time at which the message was published)";
		prevResult = testResults.getOrDefault("topics-dbirth-timestamp", "");

		if (!prevResult.contains(FAIL)) {
			Date ts = sparkplugPayload.getTimestamp();
			if (ts != null) {
				long millisPayload = ts.getTime();
				if (millisPayload > millisPastFiveMin && millisPayload < (millisReceivedBirth)) {
					publishedTs = PASS;
				}
			}
			if (prevResult.equals("")) {
				testResults.put("topics-dbirth-timestamp", publishedTs);
				testResults.put("payloads-dbirth-timestamp", publishedTs);
			}
		}

		// every dbirth message must include a sequence number
		String seqIncluded = FAIL + " (DBIRTH must include a sequence number)";
		prevResult = testResults.getOrDefault("payloads-dbirth-seq", "");

		if (!prevResult.contains(FAIL)) {
			if (sparkplugPayload.getSeq() != -1) {
				seqIncluded = PASS;
			}
			if (prevResult.equals("")) {
				testResults.put("payloads-dbirth-seq", seqIncluded);
			}
		}

		// the sequence number of the dbirth must have a value of one greater than the previous MQTT message from the
		// edge node unless
		// the previous MQTT message contained a value of 255; in this case, the sequence number must be 0
		String seqValue = FAIL
				+ " (DBIRTH sequence number must have a value of one greater than the previous MQTT message from the"
				+ "edge node unless the previous MQTT message contained a value of 255; in this case, sequence number must be 0)";
		prevResult = testResults.getOrDefault("topics-dbirth-seq", "");

		if (!prevResult.contains(FAIL)) {
			if (seq != 255) {
				if (sparkplugPayload.getSeq() == (seq + 1)) {
					seqValue = PASS;
					seq = sparkplugPayload.getSeq();
				}
			} else {
				if (sparkplugPayload.getSeq() == 0) {
					seqValue = PASS;
					seq = sparkplugPayload.getSeq();
				}
			}
			if (prevResult.equals("")) {
				testResults.put("topics-dbirth-seq", seqValue);
				testResults.put("payloads-dbirth-seq-inc", seqValue);
			}
		}

		// if this was the final dbirth to check, then we can end the test
		if (!Arrays.asList(deviceIds.values().toArray()).contains(false)) {
			// dbirth messages must be sent after nbirth and before any ndata or ddata messages are published by the
			// edge node
			String birthBeforeData =
					FAIL + " (DBIRTH must be sent before any NDATA/DDATA messages are published by the edge node)";
			if (ndataFound == false || ddataFound == false) {
				birthBeforeData = PASS;
			}
			testResults.put("payloads-dbirth-order", birthBeforeData);

			checkSubscribeTopics();
			theTCK.endTest();
		}
	}

	public SparkplugBPayload decode(ByteBuffer payload) {
		byte[] bytes = new byte[payload.remaining()];
		payload.get(bytes);
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
		SparkplugBPayload sparkplugPayload = null;
		try {
			sparkplugPayload = decoder.buildFromByteArray(bytes);
		} catch (Exception e) {
			logger.info("Exception", e);
			return sparkplugPayload;
		}
		return sparkplugPayload;
	}

	/*@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-subscribe-ncmd")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-subscribe-dcmd")*/
	public void checkSubscribeTopics() {

		// making sure edge node subscribes to ncmd and dcmd

		String ncmdSubscribe =
				FAIL + "(Edge node should subscribe to NCMD level topics to ensure Edge node targeted message from"
						+ "the primary host application are delivered)";
		if (ncmdFound == true) {
			ncmdSubscribe = PASS;
		}
		testResults.put("edge-subscribe-ncmd", ncmdSubscribe);

		String dcmdSubscribe =
				FAIL + "(Edge node should subscribe to DCMD level topics to ensure device targeted message from the"
						+ "primary host application are delivered)";
		if (dcmdFound == true) {
			dcmdSubscribe = PASS;
		}
		testResults.put("edge-subscribe-dcmd", dcmdSubscribe);

	}
}
