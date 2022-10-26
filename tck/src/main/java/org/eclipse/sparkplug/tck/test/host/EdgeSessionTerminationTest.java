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

package org.eclipse.sparkplug.tck.test.host;

/*
 * This is the primary host Sparkplug edge node death test.
 *
 * On receiving an edge node death message, the host application should set the data
 * for the edge node and connected devices to stale.
 *
 * @author Ian Craggs, Anja Helmbrecht-Schaar
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Monitor;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_PROMPT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_DEVICE_CONTROL_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TestStatus.KILLING_DEVICE;
import static org.eclipse.sparkplug.tck.test.common.Constants.TestStatus.NDEATH_MESSAGE_RECEIVED;

import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NCMD_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class EdgeSessionTerminationTest extends TCKTest {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE);
	private @NotNull String deviceId;
	private @NotNull String edgeNodeId;
	private @NotNull String groupId;
	private @NotNull String hostApplicationId;
	private int assertion_count = 0;

	private TestStatus state = TestStatus.NONE;
	private TCK theTCK = null;

	private PublishService publishService = Services.publishService();

	public EdgeSessionTerminationTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("Primary host {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;

		if (params.length < 3) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to host edge node death test must be: host_application_id edge_node_id device_id");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		logger.info("Parameters are HostApplicationId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, edgeNodeId,
				deviceId);

		final AtomicBoolean hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);

		if (!hostOnline.get()) {
			log(String.format("HostApplication %s not online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		if (utilities.getMonitor().hasDevice(groupId, edgeNodeId, deviceId)) {
			logger.info("Edge node {} and device {} already connected, using those.", edgeNodeId, deviceId);
			state = TestStatus.KILLING_DEVICE;
		} else {
			// First we .........have to connect an edge node and device.
			// We do this by sending an MQTT control message to the TCK EdgeNode utility.
			state = TestStatus.CONNECTING_DEVICE;
			String payload = "NEW DEVICE " + hostApplicationId + " " + edgeNodeId + " " + deviceId;
			Publish message = Builders.publish().topic(TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
					.payload(ByteBuffer.wrap(payload.getBytes())).build();
			logger.info("Requesting new device creation.  Edge node id: " + edgeNodeId + " device id: " + deviceId);
			publishService.publish(message);
		}
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		state = TestStatus.NONE;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
		assertion_count = 0;
	}

	public String getName() {
		return "Sparkplug Host EdgeNode Death Test";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		// Get the edge node MQTT clientid from the connect packet
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		// TODO Auto-generated method stub

	}

	private void consolePrompt(String payload) {
		Publish message = Builders.publish().topic(TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes())).build();
		publishService.publish(message);
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE)
	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Test {} - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);
		if (packet.getTopic().equals(TCK_LOG_TOPIC)) {
			String payload = null;
			ByteBuffer bpayload = packet.getPayload().orElseGet(null);
			if (bpayload != null) {
				payload = StandardCharsets.UTF_8.decode(bpayload).toString();
			}
			if (payload == null) {
				logger.error("EdgeNodeDeathTest: no payload");
				return;
			}

			if (payload.equals("Device " + deviceId + " successfully created")) {
				logger.info("EdgeNodeDeathTest: Device was created");

				// Now force an NDEATH from the edge node.
				// We do this by sending an MQTT control message to the TCK EdgeNode utility.
				state = KILLING_DEVICE;
				String disconnectMsg = "DISCONNECT_EDGE_NODE " + hostApplicationId + " " + edgeNodeId;
				Publish publish = Builders.publish().topic(TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
						.payload(ByteBuffer.wrap(disconnectMsg.getBytes())).build();
				logger.info("Requesting edge node death. Edge node id: " + edgeNodeId);
				publishService.publish(publish);
			}
		} else if (packet.getTopic().equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/"
				+ Constants.TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
			logger.info("Test: {} expected NDEATH found", getName());

			consolePrompt("Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark the "
					+ "Edge Node as offline using the current Host Application's system UTC time.");

			state = NDEATH_MESSAGE_RECEIVED;
			assertion_count = 1;
		} else if (packet.getTopic().equals(Constants.TCK_CONSOLE_REPLY_TOPIC)) {
			if (packet.getPayload().isPresent()) {
				final ByteBuffer payloadByteBuffer = packet.getPayload().get();
				final String payload = StandardCharsets.UTF_8.decode(payloadByteBuffer).toString();

				if (assertion_count == 1) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all metrics "
									+ "that were included in the previous NBIRTH as STALE using the current Host Application's system UTC time.");

					assertion_count = 2;
				} else if (assertion_count == 2) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all "
									+ "Sparkplug Devices associated with the Edge Node as offline using the current Host Application's system UTC time");

					assertion_count = 3;
				} else if (assertion_count == 3) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all "
									+ "Sparkplug Devices associated with the Edge Node as offline using the current Host Application's system UTC time");

					assertion_count = 4;
				} else if (assertion_count == 4) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE);

					theTCK.endTest();
				}
			}
		}
	}
}
