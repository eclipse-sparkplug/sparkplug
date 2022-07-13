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
 *    Anja Helmbrecht-Schaar
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.PASS;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DCMD;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_STATE;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;

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

	private final @NotNull Map<String, String> testResults = new HashMap<>();

	private final @NotNull List<String> testIds = List.of(ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER,
			ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION, ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS, ID_PAYLOADS_NDEATH_SEQ,
			ID_TOPICS_NDEATH_SEQ, ID_TOPICS_NDEATH_PAYLOAD, ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN,
			ID_PAYLOADS_NDEATH_WILL_MESSAGE, ID_PAYLOADS_NBIRTH_QOS, ID_PAYLOADS_NBIRTH_RETAIN, ID_PAYLOADS_NBIRTH_SEQ,
			ID_PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH, ID_PAYLOADS_NBIRTH_BDSEQ, ID_PAYLOADS_NBIRTH_TIMESTAMP,
			ID_PAYLOADS_NBIRTH_REBIRTH_REQ, ID_PAYLOADS_NDEATH_BDSEQ, ID_MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE,
			ID_TOPICS_NBIRTH_MQTT, ID_TOPICS_NBIRTH_SEQ_NUM, ID_TOPICS_NBIRTH_TIMESTAMP,
			ID_TOPICS_NBIRTH_BDSEQ_INCLUDED, ID_TOPICS_NBIRTH_BDSEQ_MATCHING, ID_TOPICS_NBIRTH_REBIRTH_METRIC,
			ID_PAYLOADS_DBIRTH_QOS, ID_PAYLOADS_DBIRTH_RETAIN, ID_TOPICS_DBIRTH_MQTT, ID_TOPICS_DBIRTH_TIMESTAMP,
			ID_PAYLOADS_DBIRTH_TIMESTAMP, ID_PAYLOADS_DBIRTH_SEQ, ID_TOPICS_DBIRTH_SEQ, ID_PAYLOADS_DBIRTH_SEQ_INC,
			ID_PAYLOADS_DBIRTH_ORDER, ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME,
			ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE, ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE,
			ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES,
			ID_TOPICS_NBIRTH_METRICS, ID_TOPICS_DBIRTH_METRICS, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT,
			ID_MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE, ID_MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ,
			ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC, ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD,
			ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS, ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED,
			ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT,
			ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC);

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
	private @NotNull boolean nbirthFound = false;
	private @NotNull boolean nbirthTopic = false;
	private @NotNull long seq = -1;
	private @NotNull long deathBdSeq = -1;
	private @NotNull long birthBdSeq = -1;

	public SessionEstablishmentTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
		logger.info("Edge Node session establishment test. Parameters: {} ", Arrays.asList(parms));
		theTCK = aTCK;

		if (parms.length < 3) {
			String errmsg =
					"Parameters to edge session establishment test must be: hostApplicationId groupId edgeNodeId [deviceIds]";
			logger.error(errmsg);
			prompt(errmsg);
			throw new IllegalArgumentException(errmsg);
		}

		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];

		// Initialize DeviceIds - Param: 0=host_application_id 1=group_id 2=edge_node_id 3=[device_ids]
		if (parms.length > 3) {
			for (int i = 3; i < parms.length; i++) {
				logger.debug("Add to deviceIds device: {} ", parms[i]);
				deviceIds.put(parms[i], false);
			}
		} else {
			// no devices
			testResults.put(ID_PAYLOADS_DBIRTH_QOS, NOT_EXECUTED);
			testResults.put(ID_PAYLOADS_DBIRTH_RETAIN, NOT_EXECUTED);
			testResults.put(ID_PAYLOADS_DBIRTH_TIMESTAMP, NOT_EXECUTED);
			testResults.put(ID_PAYLOADS_DBIRTH_SEQ, NOT_EXECUTED);
			testResults.put(ID_TOPICS_DBIRTH_MQTT, NOT_EXECUTED);
			testResults.put(ID_TOPICS_DBIRTH_TIMESTAMP, NOT_EXECUTED);
		}
		logger.info("Host application id: {}, Group id: {}, Edge node id: {}, Device ids: {}", hostApplicationId,
				groupId, edgeNodeId, deviceIds.keySet());
	}

	public String getName() {
		return "Edge SessionEstablishment";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		testClientId = null;
		nbirthFound = false;
		nbirthTopic = false;
		deathBdSeq = -1;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@SpecAssertion(
			section = Sections.PRINCIPLES_PERSISTENT_VS_NON_PERSISTENT_CONNECTIONS,
			id = ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC)
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		logger.info("Edge session establishment test - connect");

		/* Determine if this the connect packet for the Edge node under test.
		 * Set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				testClientId = clientId;
				logger.info("Edge session establishment test - connect - client id is " + clientId);
				testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC,
						setResult(true, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC));
			}
		}

		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT,
				setResult(testClientId != null, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_CONNECT));

		if (testClientId != null) {
			logger.debug("Check Req: Clean session should be set to true.");
			testResults.put(ID_PRINCIPLES_PERSISTENCE_CLEAN_SESSION,
					setResult(packet.getCleanStart(), PRINCIPLES_PERSISTENCE_CLEAN_SESSION));

			try {
				willPublishPacketOptional = checkWillMessage(packet);
				logger.debug("Check Req: NDEATH not registered as Will in connect packet");
				testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE,
						setResult(willPublishPacketOptional.isPresent(), PAYLOADS_NDEATH_WILL_MESSAGE));

				testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE, setResult(
						willPublishPacketOptional.isPresent(), MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE));
			} catch (Exception e) {
				logger.error("Exception in Edge session establishment test: ", e);
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub

	}

	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
		logger.info("Edge session establishment test - subscribe");
		// Example: spBv1.0/Group1/DBIRTH/Edge1/Device1
		List<Subscription> subscriptions = packet.getSubscriptions();
		for (Subscription s : subscriptions) {
			String[] levels = s.getTopicFilter().split("/");

			if (levels[0].equals(TOPIC_ROOT_STATE) && levels[1].equals(hostApplicationId)) {
				stateFound = true;
			} else if (testClientId != null && testClientId.equals(clientId)) {
				if (levels[0].equals(TOPIC_ROOT_SP_BV_1_0) && levels[1].equals(groupId)) {
					if (levels[2].equals(TOPIC_PATH_NCMD) && levels[3].equals(edgeNodeId)) {
						ncmdFound = true;
					}
					if (levels[2].equals(TOPIC_PATH_DCMD) && levels[3].equals(edgeNodeId)) {
						dcmdFound = true;
					}
				}
			}
		}
	}

	@SpecAssertion(
			section = Sections.PRINCIPLES_BIRTH_AND_DEATH_CERTIFICATES,
			id = ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER)
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		if (testClientId != null && testClientId.equals(clientId)) {
			logger.info("Edge session establishment test - publish -  to topic: {} ", packet.getTopic());
			String topic = packet.getTopic();
			String[] topicLevels = topic.split("/");

			if (!(topicLevels[0].equals(TOPIC_ROOT_SP_BV_1_0) && topicLevels[1].equals(groupId))) {
				logger.info("Skip - Edge session establishment test for this topic");
				return;
			}

			// Example: spBv1.0/Group0/DBIRTH/Edge0/Device0
			if (topicLevels[2].equals(TOPIC_PATH_NBIRTH)) {
				if (topicLevels[3].equals(edgeNodeId)) {
					checkNBirth(packet);
					logger.debug("Check Req: Birth certificates must be first");
					boolean bValid = !(ndataFound || ddataFound);
					testResults.put(ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER,
							setResult(bValid, PRINCIPLES_BIRTH_CERTIFICATES_ORDER));
					nbirthFound = true;
				}
			}
			if (topicLevels[2].equals(TOPIC_PATH_DBIRTH) && topicLevels[3].equals(edgeNodeId)) {
				String device = topicLevels[topicLevels.length - 1];
				logger.debug("Start check for Device {} ", device);
				deviceIds.put(device, true);
				checkDBirth(packet);
			} else if (topicLevels[2].equals(TOPIC_PATH_NDATA)) {
				ndataFound = true;
			} else if (topicLevels[2].equals(TOPIC_PATH_DDATA)) {
				ddataFound = true;
			}

			if (deviceIds.size() == 0) {
				checkSubscribeTopics();
				theTCK.endTest();
			}
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT)
	public void checkPayloadsNameInDataRequirement(final @NotNull PayloadOrBuilder sparkplugPayload) {
		logger.debug(
				"Check Req: The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.");

		boolean isValid = true;
		List<Metric> metrics = sparkplugPayload.getMetricsList();
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();
			if (!current.hasTimestamp()) {
				isValid = false;
				break;
			}
		}
		testResults.put(ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT,
				setResult(isValid, PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT));
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT)
	public void checkPayloadsAliasAndNameRequirement(final @NotNull PayloadOrBuilder sparkplugPayload) {
		logger.debug("Check Req: "
				+ "if alias is included, NBIRTH and DBIRTH messages MUST include both a metric name and alias.");

		boolean isValid = true;
		List<Metric> metrics = sparkplugPayload.getMetricsList();
		ListIterator<Metric> metricIterator = metrics.listIterator();
		while (metricIterator.hasNext()) {
			Metric current = metricIterator.next();
			if (current.hasAlias() && !current.hasName()) {
				isValid = false;
				break;
			}
		}
		testResults.put(ID_PAYLOADS_NAME_REQUIREMENT, setResult(isValid, PAYLOADS_ALIAS_BIRTH_REQUIREMENT));
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDEATH,
			id = ID_TOPICS_NDEATH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDEATH,
			id = ID_TOPICS_NDEATH_PAYLOAD)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS)
	public Optional<WillPublishPacket> checkWillMessage(final @NotNull ConnectPacket packet) {
		final Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {

			logger.debug("Check Req: NDEATH message must set MQTT Will QoS to 1");
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			boolean bValid = (willPublishPacket.getQos().getQosNumber() == 1);
			testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_QOS, setResult(bValid, PAYLOADS_NDEATH_WILL_MESSAGE_QOS));
			testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS,
					setResult((willPublishPacket.getQos().getQosNumber() == 1),
							MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_QOS));

			PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(willPublishPacket);

			testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD,
					setResult(sparkplugPayload != null, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_PAYLOAD));

			if (sparkplugPayload != null && sparkplugPayload.getMetricsList() != null) {
				List<Metric> metrics = sparkplugPayload.getMetricsList();
				for (Metric m : metrics) {
					if (m.getName().equals("bdSeq") && m.hasLongValue()) {
						deathBdSeq = m.getLongValue();
						break;
					}
				}
				logger.debug("Check Req: NDEATH payload must only include a single metric, the bdSeq number");
				bValid = (deathBdSeq != -1 && metrics.size() == 1);
				testResults.put(ID_TOPICS_NDEATH_PAYLOAD, setResult(bValid, TOPICS_NDEATH_PAYLOAD));

				logger.debug("Check Req: NDEATH must not include a sequence number");
				bValid = !sparkplugPayload.hasSeq();
				testResults.put(ID_PAYLOADS_NDEATH_SEQ, setResult(bValid, PAYLOADS_NDEATH_SEQ));
				testResults.put(ID_TOPICS_NDEATH_SEQ, setResult(bValid, TOPICS_NDEATH_SEQ));
				testResults.put(ID_TOPICS_NDEATH_PAYLOAD, setResult(bValid, TOPICS_NDEATH_PAYLOAD));

				logger.debug("Check Req: NDEATH retained flag must be false");
				bValid = !willPublishPacket.getRetain();
				testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN,
						setResult(bValid, PAYLOADS_NDEATH_WILL_MESSAGE_RETAIN));

				testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED,
						setResult(bValid, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_WILL_RETAINED));
			}
		}
		return willPublishPacketOptional;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_MQTT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_SEQ_NUM)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_BDSEQ_INCLUDED)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_BDSEQ_MATCHING)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_REBIRTH_METRIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NBIRTH,
			id = ID_TOPICS_NBIRTH_METRICS)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE)

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_BDSEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NBIRTH,
			id = ID_PAYLOADS_NBIRTH_REBIRTH_REQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_BDSEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_DATATYPE_REQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ)
	public void checkNBirth(final @NotNull PublishPacket packet) {
		Date receivedBirth = new Date();
		long millisReceivedBirth = receivedBirth.getTime();
		long millisPastFiveMin = millisReceivedBirth - (5 * 60 * 1000);

		// TODO: if the edge node is configured to wait for a primary host application:
		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
				setResult(stateFound, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT));

		logger.debug("Check Req: NBIRTH message must have Qos set to 0.");
		testResults.put(ID_PAYLOADS_NBIRTH_QOS, setResult(packet.getQos().getQosNumber() == 0, PAYLOADS_NBIRTH_QOS));
		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS,
				setResult(packet.getQos().getQosNumber() == 0, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_QOS));

		logger.debug("Check Req: NBIRTH messages MUST be published.");
		testResults.put(ID_TOPICS_NBIRTH_MQTT, setResult(true, TOPICS_NBIRTH_MQTT));

		logger.debug("Check Req: NBIRTH retained flag must be false.");
		testResults.put(ID_PAYLOADS_NBIRTH_RETAIN, setResult(!packet.getRetain(), PAYLOADS_NBIRTH_RETAIN));
		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED,
				setResult(!packet.getRetain(), MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_RETAINED));

		// Topic check
		String topic = packet.getTopic();
		nbirthTopic = topic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NBIRTH + "/" + edgeNodeId);
		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC,
				setResult(nbirthTopic, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_TOPIC));

		// Payload checks
		PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);

		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD,
				setResult(sparkplugPayload != null, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD));

		if (sparkplugPayload != null) {
			logger.debug("Check Req: NBIRTH message must have Qos set to 0.");
			logger.debug(
					"Check Req: Every NBIRTH message MUST include a sequence number and it MUST have a value of 0.");
			seq = sparkplugPayload.getSeq();
			testResults.put(ID_PAYLOADS_NBIRTH_SEQ, setResult((seq == 0), PAYLOADS_NBIRTH_SEQ));
			testResults.put(ID_TOPICS_NBIRTH_SEQ_NUM, setResult((seq == 0), TOPICS_NBIRTH_SEQ_NUM));
			testResults.put(ID_PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH,
					setResult((seq == 0), PAYLOADS_SEQUENCE_NUM_ZERO_NBIRTH));

			testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ,
					setResult((seq >= 0 && seq <= 255), MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_SEQ));

			// receivedBirthTime::making sure that the payload timestamp is greater than (receivedBirthTime - 5 min) and
			// less than the
			logger.debug(
					"Check Req: NBIRTH messages MUST include a payload timestamp that denotes the time at which the message was published.");
			boolean bHasTimeStamp = false;
			if (sparkplugPayload.hasTimestamp()) {
				long ts = sparkplugPayload.getTimestamp();
				bHasTimeStamp = (ts > millisPastFiveMin && ts < (millisReceivedBirth));
			}
			testResults.put(ID_PAYLOADS_NBIRTH_TIMESTAMP, setResult(bHasTimeStamp, PAYLOADS_NBIRTH_TIMESTAMP));
			testResults.put(ID_TOPICS_NBIRTH_TIMESTAMP, setResult(bHasTimeStamp, TOPICS_NBIRTH_TIMESTAMP));

			logger.debug("Check Req: NBIRTH must include a bdSeq");
			boolean rebirthFound = false;
			boolean bdSeqFound = false;
			boolean rebirthVal = true;
			DataType datatype = null;
			List<Metric> metrics = sparkplugPayload.getMetricsList();
			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					bdSeqFound = true;
					birthBdSeq = m.getLongValue();
				} else if (m.getName().equals("Node Control/Rebirth")) {
					rebirthFound = true;
					datatype = DataType.valueOf(m.getDatatype());
					rebirthVal = m.getBooleanValue();
				}

				if (!m.hasName() || !Utils.hasValue(m) || !m.hasDatatype()) {
					testResults.put(ID_TOPICS_NBIRTH_METRICS, setResult(false, TOPICS_NBIRTH_METRICS));
				} else if (testResults.get(ID_TOPICS_NBIRTH_METRICS) == null) {
					testResults.put(ID_TOPICS_NBIRTH_METRICS, setResult(true, TOPICS_NBIRTH_METRICS));
				}
				if (!Utils.hasValue(m)) {
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES,
							setResult(false, OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES));
				} else {
					if (testResults.get(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES) == null) {
						testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES,
								setResult(true, ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_NBIRTH_VALUES));
					}
				}
			}

			testResults.put(ID_PAYLOADS_NBIRTH_BDSEQ, setResult(birthBdSeq != -1, PAYLOADS_NBIRTH_BDSEQ));
			testResults.put(ID_TOPICS_NBIRTH_BDSEQ_INCLUDED, setResult(birthBdSeq != -1, TOPICS_NBIRTH_BDSEQ_INCLUDED));
			testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ,
					setResult(birthBdSeq == deathBdSeq, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_NBIRTH_PAYLOAD_BDSEQ));

			if (testResults.get(ID_PAYLOADS_METRIC_DATATYPE_REQ) == null
					|| testResults.get(ID_PAYLOADS_METRIC_DATATYPE_REQ).contains(TopicConstants.PASS)) {
				logger.debug(
						"Check req: The datatype MUST be included with each metric definition in NBIRTH and DBIRTH messages.");
				int hasDataTypeCnt = countDataType(metrics);
				testResults.put(ID_PAYLOADS_METRIC_DATATYPE_REQ,
						setResult(hasDataTypeCnt == metrics.size(), PAYLOADS_METRIC_DATATYPE_REQ));
			}
			logger.debug("Check Req: NBIRTH bdSeq must match bdSeq provided in Will Message payload of connect packet");
			testResults.put(ID_PAYLOADS_NBIRTH_BDSEQ, setResult(birthBdSeq != -1, PAYLOADS_NBIRTH_BDSEQ));
			testResults.put(ID_TOPICS_NBIRTH_BDSEQ_INCLUDED, setResult(birthBdSeq != -1, TOPICS_NBIRTH_BDSEQ_INCLUDED));

			logger.debug("Check Req: NBIRTH bdSeq must match bdSeq provided in Will Message payload of connect packet");
			boolean bMatches = (birthBdSeq != -1 && deathBdSeq != -1 && birthBdSeq == deathBdSeq);
			testResults.put(ID_PAYLOADS_NDEATH_BDSEQ, setResult(bMatches, PAYLOADS_NDEATH_BDSEQ));
			testResults.put(ID_TOPICS_NBIRTH_BDSEQ_MATCHING, setResult(bMatches, TOPICS_NBIRTH_BDSEQ_MATCHING));

			logger.debug("Check Req: NBIRTH must include a 'Node Control/Rebirth' metric.");
			testResults.put(ID_PAYLOADS_NBIRTH_REBIRTH_REQ, setResult(rebirthFound, PAYLOADS_NBIRTH_REBIRTH_REQ));
			testResults.put(ID_TOPICS_NBIRTH_REBIRTH_METRIC, setResult(rebirthFound, TOPICS_NBIRTH_REBIRTH_METRIC));

			logger.debug("Check Req: An NBIRTH message MUST include a metric with a name of 'Node Control/Rebirth'.");
			testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME,
					setResult(rebirthFound, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_NAME));

			logger.debug(
					"Check Req: The 'Node Control/Rebirth' metric in the NBIRTH message MUST have a datatype of 'Boolean'.");
			boolean bIsBoolean = (rebirthFound && datatype == DataType.Boolean);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE,
					setResult(bIsBoolean, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_DATATYPE));

			logger.debug("Check Req: NBIRTH 'node control/rebirth' metric must == false.");
			boolean bRebirthMetric = (rebirthFound && datatype == DataType.Boolean && !rebirthVal);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE,
					setResult(bRebirthMetric, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_VALUE));

			checkPayloadsNameInDataRequirement(sparkplugPayload);
			checkPayloadsAliasAndNameRequirement(sparkplugPayload);
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_SEQ_INC)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DBIRTH,
			id = ID_PAYLOADS_DBIRTH_ORDER)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = ID_TOPICS_DBIRTH_MQTT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = ID_TOPICS_DBIRTH_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = ID_TOPICS_DBIRTH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DBIRTH,
			id = ID_TOPICS_DBIRTH_METRICS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DATA_PUBLISH,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC)
	public void checkDBirth(final @NotNull PublishPacket packet) {
		Date receivedBirth = new Date();
		long millisReceivedBirth = receivedBirth.getTime();
		long millisPastFiveMin = millisReceivedBirth - (5 * 60 * 1000);

		testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT,
				setResult(nbirthFound, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_NBIRTH_WAIT));

		logger.debug("Check Req: NBIRTH message must have Qos set to 0");
		String prevResult = testResults.getOrDefault(ID_PAYLOADS_DBIRTH_QOS, "");
		if (!prevResult.contains(FAIL)) {
			boolean bValid = (packet.getQos().getQosNumber() == 0);
			if (prevResult.equals("")) {
				testResults.put(ID_PAYLOADS_DBIRTH_QOS, setResult(bValid, PAYLOADS_DBIRTH_QOS));
				testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS,
						setResult(bValid, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_QOS));
			}
		}

		logger.debug("Check Req: NBIRTH retained flag must be false");
		prevResult = testResults.getOrDefault(ID_PAYLOADS_DBIRTH_RETAIN, "");
		if (!prevResult.contains(FAIL)) {
			if (prevResult.equals("")) {
				testResults.put(ID_PAYLOADS_DBIRTH_RETAIN, setResult(!packet.getRetain(), PAYLOADS_DBIRTH_RETAIN));
				testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED,
						setResult(!packet.getRetain(), MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_RETAINED));
			}
		}

		logger.debug("Check Req: DBIRTH Qos must be 0 and retained must be false");
		prevResult = testResults.getOrDefault(ID_TOPICS_DBIRTH_MQTT, "");
		if (!prevResult.contains(FAIL)) {
			boolean bValid = (testResults.get(ID_PAYLOADS_DBIRTH_QOS).equals(PASS)
					&& testResults.get(ID_PAYLOADS_DBIRTH_RETAIN).equals(PASS) && !prevResult.contains(FAIL));
			if (prevResult.equals("")) {
				testResults.put(ID_TOPICS_DBIRTH_MQTT, setResult(bValid, TOPICS_DBIRTH_MQTT));
			}
		}

		// Topic check
		String topic = packet.getTopic();
		String[] topicLevels = topic.split("/");

		boolean goodTopic = topicLevels.length >= 5 && topicLevels[0].equals(TOPIC_ROOT_SP_BV_1_0)
				&& topicLevels[1].equals(groupId) && topicLevels[2].equals(TOPIC_PATH_DBIRTH)
				&& topicLevels[3].equals(edgeNodeId) && deviceIds.keySet().contains(topicLevels[4]);

		testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC,
				setResult(goodTopic, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC));

		testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC,
				setResult(goodTopic && nbirthTopic, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_TOPIC));

		// Payload checks
		PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);

		testResults.put(ID_MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC,
				setResult(sparkplugPayload != null, MESSAGE_FLOW_DEVICE_BIRTH_PUBLISH_DBIRTH_MATCH_EDGE_NODE_TOPIC));

		if (sparkplugPayload != null) {

			// making sure that the payload timestamp is greater than (recievedBirthTime - 5 min) and less than the
			// receivedBirthTime
			logger.debug(
					"Check Req: NBIRTH must include payload timestamp that denotes the time at which the message was published");
			prevResult = testResults.getOrDefault(ID_TOPICS_DBIRTH_TIMESTAMP, NOT_EXECUTED);
			boolean bValid = false;
			if (!prevResult.contains(FAIL)) {
				if (sparkplugPayload.hasTimestamp()) {
					long millisPayload = sparkplugPayload.getTimestamp();
					bValid = (millisPayload > millisPastFiveMin && millisPayload < (millisReceivedBirth));
				}
				if (prevResult.equals(NOT_EXECUTED)) {
					testResults.put(ID_TOPICS_DBIRTH_TIMESTAMP, setResult(bValid, TOPICS_DBIRTH_TIMESTAMP));
					testResults.put(ID_PAYLOADS_DBIRTH_TIMESTAMP, setResult(bValid, PAYLOADS_DBIRTH_TIMESTAMP));
				}
			}

			logger.debug("Check Req: DBIRTH must include a sequence number");
			prevResult = testResults.getOrDefault(ID_PAYLOADS_DBIRTH_SEQ, NOT_EXECUTED);
			if (!prevResult.contains(FAIL)) {
				boolean bContains = (sparkplugPayload.getSeq() != -1);
				if (prevResult.equals(NOT_EXECUTED)) {
					testResults.put(ID_PAYLOADS_DBIRTH_SEQ, setResult(bContains, PAYLOADS_DBIRTH_SEQ));
				}
			}

			logger.debug(
					"Check Req: DBIRTH sequence number must have a value of one greater than the previous MQTT message from the "
							+ " edge node unless the previous MQTT message contained a value of 255; in this case, sequence number must be 0.");
			prevResult = testResults.getOrDefault(ID_TOPICS_DBIRTH_SEQ, "");
			if (!prevResult.contains(FAIL)) {
				boolean bSeqValid = false;
				if (seq != 255) {
					if (sparkplugPayload.getSeq() == (seq + 1)) {
						bSeqValid = true;
						seq = sparkplugPayload.getSeq();
					}
				} else {
					if (sparkplugPayload.getSeq() == 0) {
						bSeqValid = true;
						seq = sparkplugPayload.getSeq();
					}
				}
				if (prevResult.equals("")) {
					testResults.put(ID_TOPICS_DBIRTH_SEQ, setResult(bSeqValid, TOPICS_DBIRTH_SEQ));
					testResults.put(ID_PAYLOADS_DBIRTH_SEQ_INC, setResult(bSeqValid, PAYLOADS_DBIRTH_SEQ_INC));
				}
			}

			if (testResults.get(ID_PAYLOADS_METRIC_DATATYPE_REQ) == null
					|| testResults.get(ID_PAYLOADS_METRIC_DATATYPE_REQ).contains(PASS)) {
				logger.debug(
						"Check req: The datatype MUST be included with each metric definition in NBIRTH and DBIRTH messages.");
				List<Metric> metrics = sparkplugPayload.getMetricsList();
				int hasDataTypeCnt = countDataType(metrics);
				testResults.put(ID_PAYLOADS_METRIC_DATATYPE_REQ,
						setResult(hasDataTypeCnt == metrics.size(), PAYLOADS_METRIC_DATATYPE_REQ));
			}

			checkPayloadsNameInDataRequirement(sparkplugPayload);
			checkPayloadsAliasAndNameRequirement(sparkplugPayload);

			List<Metric> metrics = sparkplugPayload.getMetricsList();
			for (Metric m : metrics) {
				if (!m.hasName() || !Utils.hasValue(m) || !m.hasDatatype()) {
					testResults.put(ID_TOPICS_DBIRTH_METRICS, setResult(false, TOPICS_DBIRTH_METRICS));
				} else if (testResults.get(ID_TOPICS_DBIRTH_METRICS) == null) {
					testResults.put(ID_TOPICS_DBIRTH_METRICS, setResult(true, TOPICS_DBIRTH_METRICS));
				}

				if (!Utils.hasValue(m)) {
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES,
							setResult(false, OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES));
				} else {
					if (testResults.get(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES) == null) {
						testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES,
								setResult(true, OPERATIONAL_BEHAVIOR_DATA_PUBLISH_DBIRTH_VALUES));
					}
				}
			}
		}

		// if this was the final dbirth to check, then we can end the test
		if (!Arrays.asList(deviceIds.values().toArray()).contains(false)) {

			logger.debug(
					"Check Req: DBIRTH must be sent before any NDATA/DDATA messages are published by the edge node");
			boolean bValid = !(ndataFound || ddataFound);
			testResults.put(ID_PAYLOADS_DBIRTH_ORDER, setResult(bValid, PAYLOADS_DBIRTH_ORDER));
			checkSubscribeTopics();
			theTCK.endTest();
		}
	}

	private int countDataType(List<Metric> metrics) {
		if (metrics != null) {
			final AtomicInteger hasDataTypeCnt = new AtomicInteger();
			metrics.forEach(m -> {
				if (m.hasDatatype()) {
					hasDataTypeCnt.incrementAndGet();
				}
			});
			return hasDataTypeCnt.get();
		}
		return 0;
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE)
	public void checkSubscribeTopics() {
		testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE,
				setResult(ncmdFound, MESSAGE_FLOW_EDGE_NODE_NCMD_SUBSCRIBE));

		// TODO: this assertion only applies if the device supports writing to outputs (an input parameter?
		testResults.put(ID_MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE,
				setResult(dcmdFound, MESSAGE_FLOW_DEVICE_DCMD_SUBSCRIBE));
	}
}
