/*******************************************************************************
 * Copyright (c) 2022 Ian Craggs
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

import static org.eclipse.sparkplug.tck.test.common.Constants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_SEQ_NUMBER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDEATH_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDEATH_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDEATH_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DEVICE_DDEATH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_SEQ_INC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_SEQ_NUMBER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDEATH_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDEATH_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDEATH_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectReasonCode;
import com.hivemq.extension.sdk.api.packets.general.MqttVersion;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

/**
 * This is the edge node Sparkplug session termination test
 * 
 * The purpose is to test NDEATH and DDEATH messages
 *
 * @author Ian Craggs
 */
@SpecVersion(
		spec = "sparkplug",
		version = "3.0.1-SNAPSHOT")
public class SessionTerminationTest extends TCKTest {
	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	public static final @NotNull List<String> testIds = List.of(ID_TOPICS_DDEATH_MQTT, ID_TOPICS_DDEATH_SEQ_NUM,
			ID_PAYLOADS_DDEATH_TIMESTAMP, ID_PAYLOADS_DDEATH_SEQ, ID_PAYLOADS_DDEATH_SEQ_INC,
			ID_PAYLOADS_DDEATH_SEQ_NUMBER, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET, ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH,
			ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER, ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311,
			ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50, ID_TOPICS_NDEATH_TOPIC, ID_TOPICS_DDEATH_TOPIC,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC);

	private final @NotNull TCK theTCK;
	private @NotNull Utilities utilities = null;
	private final @NotNull Map<String, Boolean> deviceIds = new HashMap<>();

	private @NotNull String testClientId = null;
	private @NotNull MqttVersion testMqttVersion = null;
	private @NotNull String hostApplicationId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String deviceId;

	private @NotNull boolean ndeathFound = false;
	private @NotNull boolean ddeathFound = false;
	private @NotNull boolean disconnected = false;

	// Host Application variables
	private boolean hostCreated = false;

	public SessionTerminationTest(final @NotNull TCK aTCK, Utilities utilities, String[] parms, Results.Config config) {
		logger.info("Edge Node session termination test. Parameters: {} ", Arrays.asList(parms));
		theTCK = aTCK;
		this.utilities = utilities;

		if (parms.length < 4) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log("Parameters to edge session termination test must be: hostApplicationId groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}

		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];
		deviceId = parms[3];

		logger.info("Host application id: {}, Group id: {}, Edge node id: {}, Device id: {}", hostApplicationId,
				groupId, edgeNodeId, deviceId);

		if (Utils.checkHostApplicationIsOnline(hostApplicationId).get()) {
			logger.info("Host Application is online, so using that");
		} else {
			logger.info("Creating host application");
			try {
				utilities.getHostApps().hostOnline(hostApplicationId, true);
			} catch (MqttException m) {
				throw new IllegalStateException();
			}
			hostCreated = true;
		}
	}

	public String getName() {
		return "Edge SessionTermination";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public void clearResults() {
		testResults.clear();
		testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER, FAIL);
		testResults.put(ID_TOPICS_DDEATH_MQTT, FAIL);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		if (hostCreated) {
			try {
				utilities.getHostApps().hostOffline();
			} catch (MqttException m) {
				logger.error("endTest", m);
			}
		}
		testClientId = null;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC)
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		/* Determine if this the connect packet for the Edge node under test.
		 * Set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				testClientId = clientId;
				testMqttVersion = packet.getMqttVersion();
				logger.info("Edge session termination test - connect - client id is " + clientId);
				testResults.put(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC,
						setResult(true, MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_WILL_MESSAGE_TOPIC));
			}
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET)
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		logger.info("{} - disconnect clientid: {} ", getName(), clientId);

		if (testClientId == null || !clientId.equals(testClientId)) {
			// ignore disconnect packets from other clients
			return;
		}

		// An NDEATH must be received - whether by publish or retained message
		// If we get a disconnect packet, the NDEATH should already have been received (MQTT 3.1.1)

		testResults.put(ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET,
				setResult(ndeathFound, OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET));

		if (testMqttVersion == MqttVersion.V_5) {
			testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50,
					setResult(packet.getReasonCode() == DisconnectReasonCode.DISCONNECT_WITH_WILL_MESSAGE,
							PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT50));
		} else {
			testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311,
					setResult(ndeathFound, PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER_DISCONNECT_MQTT311));
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET)
	@Override
	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		// on disconnecting the TCP connection, the NDEATH might not have been received

		String clientId = disconnectEventInput.getClientInformation().getClientId();
		logger.info("{} - onDisconnect clientid: {} ", getName(), clientId);
		if (testClientId == null || !clientId.equals(testClientId)) {
			// ignore disconnections from other clients
			return;
		}

		testResults.put(ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET,
				setResult(ndeathFound, OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_PACKET));

		disconnected = true;
		if (disconnected && ndeathFound) {
			theTCK.endTest();
		}
	}

	@Override
	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {

	}

	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_NDEATH,
			id = ID_TOPICS_NDEATH_TOPIC)
	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_DDEATH,
			id = ID_TOPICS_DDEATH_TOPIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDEATH,
			id = ID_TOPICS_DDEATH_MQTT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDEATH,
			id = ID_TOPICS_DDEATH_SEQ_NUM)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDEATH,
			id = ID_PAYLOADS_DDEATH_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDEATH,
			id = ID_PAYLOADS_DDEATH_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDEATH,
			id = ID_PAYLOADS_DDEATH_SEQ_INC)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDEATH,
			id = ID_PAYLOADS_DDEATH_SEQ_NUMBER)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH)

	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		// topics namespace/group_id/NDEATH/edge_node_id
		// namespace/group_id/DDEATH/edge_node_id/device_id

		logger.info("Edge session termination test - publish - to topic: {} ", packet.getTopic());

		if (testClientId == null || !clientId.equals(testClientId)) {
			// ignore disconnect packets from other clients
			return;
		}

		String topic = packet.getTopic();
		String[] topicLevels = topic.split("/");

		if (topicLevels.length == 4 && topicLevels[0].equals(TOPIC_ROOT_SP_BV_1_0) && topicLevels[1].equals(groupId)
				&& topicLevels[2].equals("NDEATH") && topicLevels[3].equals(edgeNodeId)) {
			ndeathFound = true;

			testResults.put(ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH,
					setResult(ndeathFound, OPERATIONAL_BEHAVIOR_EDGE_NODE_INTENTIONAL_DISCONNECT_NDEATH));

			// should receive NDEATH before disconnection
			testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER,
					setResult(!disconnected, PAYLOADS_NDEATH_WILL_MESSAGE_PUBLISHER));

			testResults.put(ID_TOPICS_NDEATH_TOPIC, setResult(ndeathFound, TOPICS_NDEATH_TOPIC));
		}

		if (topicLevels.length == 5 && topicLevels[0].equals(TOPIC_ROOT_SP_BV_1_0) && topicLevels[1].equals(groupId)
				&& topicLevels[2].equals("DDEATH") && topicLevels[3].equals(edgeNodeId)
				&& topicLevels[4].equals(deviceId)) {
			ddeathFound = true;

			testResults.put(ID_OPERATIONAL_BEHAVIOR_DEVICE_DDEATH,
					setResult(ddeathFound, OPERATIONAL_BEHAVIOR_DEVICE_DDEATH));
			testResults.put(ID_TOPICS_DDEATH_TOPIC, setResult(ddeathFound, TOPICS_DDEATH_TOPIC));

			// DDEATH messages MUST be published with MQTT QoS equal to 0 and retain equal to false.
			boolean isValidMQTT = (packet.getQos() == Qos.AT_MOST_ONCE && packet.getRetain() == false);
			testResults.put(ID_TOPICS_DDEATH_MQTT, setResult(isValidMQTT, TOPICS_DDEATH_MQTT));

			// payload related tests
			PayloadOrBuilder payload = Utils.getSparkplugPayload(packet);

			// The DDEATH MUST include a sequence number in the payload and it MUST have a value of one
			// greater than the previous MQTT message from the Edge Node contained unless the previous
			// MQTT message contained a value of 255. In this case the sequence number MUST be 0.

			boolean isValidSeq = false; // TODO check sequence increment
			if (payload.hasSeq()) {
				long seq = payload.getSeq();
				if (seq >= 0 && seq <= 255) {
					isValidSeq = true;
				}
			}
			testResults.put(ID_TOPICS_DDEATH_SEQ_NUM, setResult(isValidSeq, TOPICS_DDEATH_SEQ_NUM));
			testResults.put(ID_PAYLOADS_DDEATH_SEQ_INC, setResult(isValidSeq, PAYLOADS_DDEATH_SEQ_INC));

			testResults.put(ID_PAYLOADS_DDEATH_TIMESTAMP, setResult(payload.hasTimestamp(), PAYLOADS_DDEATH_TIMESTAMP));

			testResults.put(ID_PAYLOADS_DDEATH_SEQ, setResult(payload.hasSeq(), PAYLOADS_DDEATH_SEQ));

			testResults.put(ID_PAYLOADS_DDEATH_SEQ_NUMBER, setResult(payload.hasSeq(), PAYLOADS_DDEATH_SEQ_NUMBER));
		}

		if (disconnected && ndeathFound && ddeathFound) {
			theTCK.endTest();
		}
	}
}
