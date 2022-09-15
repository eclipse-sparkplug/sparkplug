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
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import com.hivemq.extension.sdk.api.services.session.ClientService;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.Constants.*;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class ReceiveCommandTest extends TCKTest {

	private static final String BD_SEQ = "bdSeq";
	private static final String NODE_CONTROL_REBIRTH = "Node Control/Rebirth";
	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull TCK theTCK;

	private final @NotNull List<String> testIds = List.of(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1,
			ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2,
			ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_3, ID_PAYLOADS_NDEATH_WILL_MESSAGE);

	private @NotNull status state;
	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull long deathBdSeq;
	private String edgeNodeClientId;
	private boolean bNBirth = false, bDBirth = false;
	private PublishService publishService = Services.publishService();

	public ReceiveCommandTest(TCK aTCK, String[] params) {
		logger.info("{} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;

		if (params.length < 3) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to edge receive command test must be: groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		state = status.START;
		deathBdSeq = -1;
		groupId = params[0];
		edgeNodeId = params[1];
		deviceId = params[2];
		logger.info("Parameters are  GroupId: {}, EdgeNodeId: {}, DeviceId: {}", groupId, edgeNodeId, deviceId);

		sendCommand(true);

		// indicate we are testing the receipt of NBIRTH and DBIRTH messages after a rebirth command
		// set this assertion value to false by default, then track the receipt of both NBIRTH and DBIRTH messages for
		// device attached to an edge node.
		testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2,
				setResult(false, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2));

		// this will fail if we receive a data message
		testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_1, PASS);
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
			}
		});
	}

	private void sendCommand(boolean isNode) {
		String topicName = TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/";
		if (isNode) {
			state = status.SENDING_NODE_REBIRTH;
			topicName += TOPIC_PATH_NCMD + "/" + edgeNodeId;
		} else {
			state = status.SENDING_DEVICE_REBIRTH;
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
		state = status.END;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribe(final String clientId, final SubscribePacket packet) {
		// TODO Auto-generated method stub
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

		logger.info("Edge - Receive Command test - PUBLISH - topic: {}, state: {} ", packet.getTopic(), state);
		if (state == status.SENDING_NODE_REBIRTH || state == status.DISCONNECTING_CLIENT) {
			String[] levels = packet.getTopic().split("/");
			if (levels[0].equals(TOPIC_ROOT_SP_BV_1_0) && levels[1].equals(groupId) && levels[3].equals(edgeNodeId)) {
				if (levels.length == 4 && levels[2].equals(TOPIC_PATH_NBIRTH)) {
					logger.info("Node birth received - ClientId {} on Topic {} ", clientId, packet.getTopic());
					edgeNodeClientId = clientId;
					bNBirth = true;
					if (state == status.DISCONNECTING_CLIENT) {
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
					logger.debug("Device birth received for device: {}", levels[4]);
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

				if (bNBirth && bDBirth && state == status.SENDING_NODE_REBIRTH) {
					logger.debug(
							"Check Req: {} After an Edge Node stops sending DATA messages, it MUST send a complete BIRTH sequence including the NBIRTH and DBIRTH(s) if applicable.",
							ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2);
					testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_REBIRTH_ACTION_2, PASS);
					state = status.DISCONNECTING_CLIENT;
					bNBirth = false;
					bDBirth = false;
					disconnectClient(clientId);
				}

				if (bNBirth && bDBirth && state == status.DISCONNECTING_CLIENT) {
					state = status.END;
					theTCK.endTest();
				}
			}
		}
	}

	private enum status {
		START,
		DISCONNECTING_CLIENT,
		SENDING_NODE_REBIRTH,
		SENDING_DEVICE_REBIRTH,
		END
	}
}
