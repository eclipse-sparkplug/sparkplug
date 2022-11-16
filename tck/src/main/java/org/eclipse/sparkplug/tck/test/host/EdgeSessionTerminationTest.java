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

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_PROMPT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TestStatus.DDEATH_MESSAGE_RECEIVED;
import static org.eclipse.sparkplug.tck.test.common.Constants.TestStatus.KILLING_DEVICE;
import static org.eclipse.sparkplug.tck.test.common.Constants.TestStatus.NDEATH_MESSAGE_RECEIVED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is the primary host Sparkplug edge node death test.
 *
 * On receiving an edge node death message, the host application should set the data
 * for the edge node and connected devices to stale.
 *
 * @author Ian Craggs
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0")
public class EdgeSessionTerminationTest extends TCKTest {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE);
	private @NotNull String deviceId;
	private @NotNull String edgeNodeId;
	private @NotNull String groupId;
	private @NotNull String hostApplicationId;
	private int assertion_count = 0;
	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	private TestStatus state = TestStatus.NONE;
	private TCK theTCK = null;
	private Utilities utilities = null;
	private boolean realDevice = false;

	private PublishService publishService = Services.publishService();

	public EdgeSessionTerminationTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("Primary host {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;

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
			realDevice = true;
			state = TestStatus.KILLING_DEVICE;
		} else {
			// If no real edge node is available, we use a simulated one
			state = TestStatus.CONNECTING_DEVICE;
			try {
				utilities.getEdgeNode().edgeNodeOnline(hostApplicationId, groupId, edgeNodeId, deviceId);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			logger.info("{}: Edge node {} and device {} created.", getName(), edgeNodeId, deviceId);
		}
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			utilities.getEdgeNode().edgeOffline();
		} catch (Exception e) {

		}
		testResults.putAll(results);
		state = TestStatus.NONE;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
		assertion_count = 0;
	}

	public String getName() {
		return "Sparkplug Host Edge Node Session Termination Test";
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
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_DEVICE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE)
	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.debug("Test {} - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);

		if (packet.getTopic().equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NBIRTH
				+ "/" + edgeNodeId)) {
			logger.info("{} NBIRTH received", getName());
		} else if (packet.getTopic().equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/"
				+ Constants.TOPIC_PATH_DBIRTH + "/" + edgeNodeId + "/" + deviceId)) {
			logger.info("{} DBIRTH received", getName());

			// Now force an DDEATH from the edge node.
			state = KILLING_DEVICE;
			if (!realDevice) {
				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							utilities.getEdgeNode().deviceDeath();
						} catch (Exception e) {
							logger.error("{} error", getName(), e);
							theTCK.endTest();
						}
					}
				}, 2, TimeUnit.SECONDS);
			}
		} else if (packet.getTopic().equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/"
				+ Constants.TOPIC_PATH_DDEATH + "/" + edgeNodeId + "/" + deviceId)) {
			logger.info("Test: {} expected DDEATH found", getName());

			consolePrompt("Immediately after receiving an DDEATH from an Edge Node, Host Applications "
					+ "MUST mark the Sparkplug Device associated with the Edge Node as offline using "
					+ "the timestamp in the DDEATH payload.");

			state = DDEATH_MESSAGE_RECEIVED;
			assertion_count = 1;
		} else if (packet.getTopic().equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/"
				+ Constants.TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
			logger.info("Test: {} expected NDEATH found", getName());

			consolePrompt("Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark the "
					+ "Edge Node as offline using the current Host Application's system UTC time.");

			state = NDEATH_MESSAGE_RECEIVED;
			assertion_count = 3;
		} else if (packet.getTopic().equals(Constants.TCK_CONSOLE_REPLY_TOPIC)) {
			logger.info("Assertion number " + assertion_count);
			if (packet.getPayload().isPresent()) {
				final ByteBuffer payloadByteBuffer = packet.getPayload().get();
				final String payload = StandardCharsets.UTF_8.decode(payloadByteBuffer).toString();

				if (assertion_count == 1) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_OFFLINE);

					consolePrompt(
							"Immediately after receiving an DDEATH from an Edge Node, Host Applications MUST mark all "
									+ "of the metrics that were included with the associated Sparkplug Device DBIRTH messages as "
									+ "STALE using the timestamp in the DDEATH payload.");

					assertion_count = 2;
				} else if (assertion_count == 2) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_DDEATH_DEVICES_TAGS_STALE);

					if (!realDevice) {
						executorService.schedule(new Runnable() {
							@Override
							public void run() {
								try {
									utilities.getEdgeNode().edgeOffline();
								} catch (Exception e) {

								}
							}
						}, 3, TimeUnit.SECONDS);
					}
					assertion_count = 0;
				} else if (assertion_count == 3) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_OFFLINE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all metrics "
									+ "that were included in the previous NBIRTH as STALE using the current Host Application's system UTC time.");

					assertion_count = 4;
				} else if (assertion_count == 4) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_NODE_TAGS_STALE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all "
									+ "Sparkplug Devices associated with the Edge Node as offline using the current Host Application's system UTC time");

					assertion_count = 5;
				} else if (assertion_count == 5) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_OFFLINE);

					consolePrompt(
							"Immediately after receiving an NDEATH from an Edge Node, Host Applications MUST mark all "
									+ "Sparkplug Devices associated with the Edge Node as offline using the current Host Application's system UTC time");

					assertion_count = 6;
				} else if (assertion_count == 6) {
					Utils.setResultIfNotFail(testResults, payload.equals("PASS"),
							ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE,
							OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_ACTION_NDEATH_DEVICES_TAGS_STALE);
					assertion_count = 0;
					theTCK.endTest();
				}
			}
		}
	}
}
