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

import static org.eclipse.sparkplug.tck.test.common.Constants.PASS;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDEATH_WILL_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDEATH_WILL_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 5. Check bdSeq on nbirth
 *
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import com.hivemq.extension.sdk.api.services.session.ClientService;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0")
public class ReceiveCommandTest extends TCKTest {

	private static final String BD_SEQ = "bdSeq";
	private static final String NODE_CONTROL_REBIRTH = "Node Control/Rebirth";
	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull TCK theTCK;
	private @NotNull Utilities utilities = null;

	public static final @NotNull List<String> testIds = List.of(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1,
			ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2,
			ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3, ID_PAYLOADS_NDEATH_WILL_MESSAGE);

	private @NotNull TestStatus state;
	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull long deathBdSeq = -1;
	private String edgeNodeClientId;
	private boolean bNBirth = false, bDBirth = false;
	private PublishService publishService = Services.publishService();
	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	// Host Application variables
	private boolean hostCreated = false;

	public ReceiveCommandTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("{} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;

		if (params.length < 4) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to edge receive command test must be: hostApplicationId groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		state = TestStatus.NONE;
		deathBdSeq = -1;
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		logger.info("Host application id: {}, Group id: {}, Edge node id: {}, Device id: {}", hostApplicationId,
				groupId, edgeNodeId, deviceId);

		// indicate we are testing the receipt of NBIRTH and DBIRTH messages after a rebirth command
		// set this assertion value to false by default, then track the receipt of both NBIRTH and DBIRTH messages for
		// device attached to an edge node.
		testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2,
				setResult(false, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2));

		// this will fail if we receive a data message at the wrong time
		testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1, PASS);

		if (Utils.checkHostApplicationIsOnline(hostApplicationId).get()) {
			log("Host Application is online, so using that");

			// send the node rebirth command, expecting the edge node to be online already
			log("Sending rebirth command, expecting the Edge and Device to be online already");
			sendRebirth(true);
		} else {
			log("Host application not online. Creating simulated host application");
			try {
				utilities.getHostApps().hostOnline(hostApplicationId, true);
			} catch (MqttException m) {
				throw new IllegalStateException();
			}
			hostCreated = true;
			state = TestStatus.EXPECT_NODE_BIRTH;
			// wait for the edge and device to come online
			log("Waiting for the Edge and Device to come online");
		}
	}

	private void disconnectClient(String clientId) {
		final ClientService clientService = Services.clientService();
		CompletableFuture<Boolean> disconnectFuture = clientService.disconnectClient(clientId, true);
		disconnectFuture.whenComplete(new BiConsumer<Boolean, Throwable>() {
			@Override
			public void accept(Boolean disconnected, Throwable throwable) {
				if (throwable == null) {
					if (disconnected) {
						logger.debug("Client {} was successfully disconnected and no Will message was sent", clientId);
					} else {
						logger.debug("Client {} not found", clientId);
					}
				} else {
					logger.error("Client {} disconnectClient: {} ", clientId, throwable.getMessage());
					if (logger.isDebugEnabled()) {
						logger.error("Original error:", throwable);
					}
				}
				state = TestStatus.ENDING;
				theTCK.endTest();
			}
		});
	}

	private void sendRebirth(boolean isNode) {
		String topicName = TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/";
		if (isNode) {
			state = TestStatus.SENDING_NODE_REBIRTH;
			topicName += TOPIC_PATH_NCMD + "/" + edgeNodeId;
		} else {
			state = TestStatus.SENDING_DEVICE_REBIRTH;
			topicName = TOPIC_PATH_DCMD + "/" + edgeNodeId + "/" + deviceId;
		}

		byte[] payload = null;
		try {
			payload =
					Payload.newBuilder()
							.addMetrics(Metric.newBuilder().setName(NODE_CONTROL_REBIRTH)
									.setDatatype(DataType.Boolean.getNumber()).setBooleanValue(true))
							.build().toByteArray();
		} catch (Exception e) {
			logger.error("Error building edge node rebirth command. Aborting test. {} ", e.getMessage());
			theTCK.endTest();
		}

		Publish message =
				Builders.publish().topic(topicName).qos(Qos.AT_LEAST_ONCE).payload(ByteBuffer.wrap(payload)).build();

		logger.info("Requesting edge rebirth. Edge node id: {} ", edgeNodeId);
		publishService.publish(message);
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
		state = TestStatus.ENDING;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Edge ReceiveCommand";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDEATH,
			id = ID_PAYLOADS_NDEATH_WILL_MESSAGE)
	@Override
	public void connect(final String clientId, final ConnectPacket packet) {
		// we can determine the clientId corresponding to the edge node id by
		// checking the will contents to see if the edge node id matches.
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			logger.debug("Edge - Receive Command test - CONNECT ClientId {} on and will topic {}", clientId,
					willPublishPacket.getTopic());
			String[] topicParts = willPublishPacket.getTopic().split("/");
			if (topicParts.length >= 4) {
				String will_edge_node_id = topicParts[3];
				boolean bIsEqual = edgeNodeId.equals(will_edge_node_id);
				if (bIsEqual) {
					edgeNodeClientId = clientId;
					logger.debug("ClientId for edge node id {} is {} ", edgeNodeId, edgeNodeClientId);
					ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
					deathBdSeq = getBdSeq(payload);
				}
				logger.debug("Check Req: {} NDEATH not registered as Will in connect packet.",
						ID_PAYLOADS_NDEATH_WILL_MESSAGE);
				testResults.put(ID_PAYLOADS_NDEATH_WILL_MESSAGE, setResult(bIsEqual, PAYLOADS_NDEATH_WILL_MESSAGE));
			}
		}
	}

	private long getBdSeq(final ByteBuffer payload) {
		final PayloadOrBuilder inboundPayload = Utils.decode(payload);
		if (inboundPayload != null) {
			for (Metric m : inboundPayload.getMetricsList()) {
				if (m.getName().equals(BD_SEQ)) {
					return m.getLongValue();
				}
			}
		}
		return -1L;
	}

	@Override
	public void disconnect(final String clientId, final DisconnectPacket packet) {

	}

	@Override
	public void subscribe(final String clientId, final SubscribePacket packet) {

	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
			id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3)
	@Override
	public void publish(final String clientId, final PublishPacket packet) {
		String topic = packet.getTopic();
		logger.info("Edge - Receive Command test - PUBLISH - topic: {}, state: {} ", topic, state);

		if (state == TestStatus.EXPECT_NODE_BIRTH
				&& topic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NBIRTH + "/" + edgeNodeId)) {
			log("Edge " + edgeNodeId + " is now online");
			state = TestStatus.EXPECT_DEVICE_BIRTH;
		} else if (state == TestStatus.EXPECT_DEVICE_BIRTH && topic.equals(
				TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_DBIRTH + "/" + edgeNodeId + "/" + deviceId)) {
			log("Device " + deviceId + " is now online, sending rebirth");
			sendRebirth(true);
		} else if (state == TestStatus.SENDING_NODE_REBIRTH) {
			String[] levels = topic.split("/");

			if (levels[0].equals(TOPIC_ROOT_SP_BV_1_0) && levels[1].equals(groupId) && levels[3].equals(edgeNodeId)) {
				if (levels.length == 4 && levels[2].equals(TOPIC_PATH_NBIRTH)) {
					log("Node birth received - ClientId  " + clientId);
					edgeNodeClientId = clientId;
					bNBirth = true;

					if (deathBdSeq != -1) {
						// check bdSeq
						ByteBuffer payload = packet.getPayload().orElseGet(null);
						long birthSeq = getBdSeq(payload);
						logger.debug(
								"Check Req: {} The NBIRTH MUST include the same bdSeq metric with the same value it had included in the Will Message of the previous MQTT CONNECT packet.",
								ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3);
						testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3,
								setResult(birthSeq == deathBdSeq, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3));
						if (birthSeq != deathBdSeq) {
							logger.error("*** Death sequence no: {}  nBirth seq no: {}. Expected to be equal",
									deathBdSeq, birthSeq);
						}
					}
				} else if (levels.length == 5 && levels[2].equals(TOPIC_PATH_DBIRTH)) {
					log("Device birth received for device: " + levels[4]);
					bDBirth = true;

				} else if (levels[2].equals(TOPIC_PATH_NDATA)) {
					logger.error("Data received for edge node: {}", levels[3]);
					logger.debug(
							"Check Req: {} When an Edge Node receives a Rebirth Request, it MUST immediately stop sending DATA messages.",
							ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1);
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1,
							setResult(false, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1));
				} else if (levels[2].equals(TOPIC_PATH_DDATA)) {
					logger.error("Data received for edge node: {} and device id: {} ", levels[3], levels[4]);
					logger.debug(
							"Check Req: {}When an Edge Node receives a Rebirth Request, it MUST immediately stop sending DATA messages.",
							ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1);
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1,
							setResult(false, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1));
				}

				if (bNBirth && bDBirth && state == TestStatus.SENDING_NODE_REBIRTH) {
					logger.debug(
							"Check Req: {} After an Edge Node stops sending DATA messages, it MUST send a complete BIRTH sequence including the NBIRTH and DBIRTH(s) if applicable.",
							ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2);
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2, PASS);
					state = TestStatus.DISCONNECTING_CLIENT;
					bNBirth = false;
					bDBirth = false;
					disconnectClient(clientId);
				}
			}
		}
	}
}
