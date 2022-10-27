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

package org.eclipse.sparkplug.tck.test.host;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_PROMPT_TOPIC;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.tahu.message.model.MessageType;
import org.eclipse.tahu.message.model.MetricDataType;
import org.eclipse.tahu.message.model.Topic;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is the primary host Sparkplug receive data test.  Data can be received from edge
 * nodes and devices.
 *
 * We manufacture some data events to be received by the primary host.
 *
 * To verify that they have been handled correctly, we have to rely on the
 * user running the tests to report the results.
 *
 * @author Ian Craggs, Anja Helmbrecht-Schaar
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
		version = "3.0.0-SNAPSHOT")
public class ReceiveDataTest extends TCKTest {
	private static final String EDGE_METRIC = "TCK_metric/Int32";
	private static final String DEVICE_METRIC = "Inputs/0";
	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull Map<String, String> testResults = new HashMap<>();
	private final @NotNull ArrayList<String> testIds = new ArrayList<>();
	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;

	private TestStatus state = TestStatus.NONE;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;

	private PublishService publishService = Services.publishService();

	final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	public ReceiveDataTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("Primary host receive data test: {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;

		if (params.length < 4) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to Host receive data test must be: groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		logger.info("Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}",
				hostApplicationId, groupId, edgeNodeId, deviceId);

		final AtomicBoolean hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);

		if (!hostOnline.get()) {
			logger.info("HostApplication {} not online - test not started.", hostApplicationId);
			return;
		}

		// The TCK_CONSOLE_TEST_CONTROL_TOPIC gets sent to the PublishInterceptor from the web ui - give it some time
		// before starting the edge node
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				logger.info("Creating the Edge Node");
				try {
					boolean created =
							utilities.getEdgeNode().edgeNodeOnline(hostApplicationId, groupId, edgeNodeId, deviceId);
					if (created) {
						logger.info("{}: Device was created", getName());

						// Now tell the EdgeNode simulator to send some data from the edge node
						logger.info("Requesting data from edgeNodeId: {}  and metric: {} ", edgeNodeId, EDGE_METRIC);
						utilities.getEdgeNode().publishEdgeData(EDGE_METRIC, MetricDataType.Int32,
								new Random().nextInt());
						state = TestStatus.REQUESTED_NODE_DATA;
					} else {
						logger.error("SendCommandTest: Failed to create the device");
					}
				} catch (Exception e) {
					throw new IllegalStateException();
				}
			}
		}, 2, TimeUnit.SECONDS);

		// First we have to connect an edge node and device.
		// We do this by sending an MQTT control message to the TCK EdgeNode utility.
		// ONLY DO THIS IF THE EDGE/DEVICE haven't already been created!!
		state = TestStatus.CONNECTING_DEVICE;
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			utilities.getEdgeNode().edgeOffline();
		} catch (Exception e) {
			logger.error("endTest", e);
		}

		testResults.putAll(results);
		state = TestStatus.NONE;
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Sparkplug Host Receive Data Test";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Host - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);

		try {
			final Topic topic = Topic.parseTopic(packet.getTopic());

			String payload = null;
			ByteBuffer byteBuffer = packet.getPayload().orElseGet(null);
			if (byteBuffer != null) {
				payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
				if (payload == null) {
					return;
				}
			}

			if (state == TestStatus.REQUESTED_NODE_DATA && topic.isType(MessageType.NDATA)) {

				logger.info("Requesting device data for device: {} and metric: {}" + deviceId, DEVICE_METRIC);
				boolean pubSuccess = utilities.getEdgeNode().publishDeviceData(DEVICE_METRIC, MetricDataType.Boolean,
						new Random().nextBoolean());
				if (pubSuccess) {
					state = TestStatus.REQUESTED_DEVICE_DATA;
				} else {
					throw new IllegalStateException();
				}
			} else if (state == TestStatus.REQUESTED_DEVICE_DATA && topic.isType(MessageType.DDATA)) {
				state = TestStatus.PUBLISHED_DEVICE_DATA;
				endTest(testResults);
			}
		} catch (Exception e) {
			logger.error("Failed to execute receive data test", e);
		}
	}

	private void publishToTckConsolePrompt(String payload) {
		Publish message = Builders.publish().topic(TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes())).build();
		logger.info("Requesting command to edge node id:{}: {} ", edgeNodeId, payload);
		publishService.publish(message);
	}
}
