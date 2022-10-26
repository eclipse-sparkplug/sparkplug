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

/*
 * This is the Sparkplug Edge Node multiple MQTT Broker test
 * 
 *
 * @author Ian Craggs
 */

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONSOLE_PROMPT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkHostApplicationIsOnline;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setShouldResult;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class MultipleBrokerTest extends TCKTest {

	private static final String NODE_CONTROL_REBIRTH = "Node Control/Rebirth";
	private static final String EDGE_METRIC = "TCK_metric/Boolean";
	private static final String DEVICE_METRIC = "Inputs/0";

	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull Map<String, String> testResults = new HashMap<>();
	private final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER,
					ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);
	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;

	private String edgeNodeTestClientId = null;

	private TestStatus state = null;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;

	private PublishService publishService = Services.publishService();

	public MultipleBrokerTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;

		if (params.length < 4) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to host send command test must be: hostApplicationId, groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		logger.info("Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}",
				hostApplicationId, groupId, edgeNodeId, deviceId);

		final AtomicBoolean hostOnline = checkHostApplicationIsOnline(hostApplicationId);

		if (!hostOnline.get()) {
			log(String.format("HostApplication %s not online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		// The TCK_CONSOLE_TEST_CONTROL_TOPIC gets sent to the PublishInterceptor from the web ui - give it some time
		// before starting the edge node
		Executors.newScheduledThreadPool(1).schedule(new Runnable() {
			@Override
			public void run() {
				logger.info("Creating the Edge Node");
				try {
					utilities.getEdgeNode().edgeNodeOnline(hostApplicationId, groupId, edgeNodeId, deviceId);
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
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@Override
	public String getName() {
		return "Host SendCommand";
	}

	@Override
	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	@Override
	public Map<String, String> getResults() {
		return testResults;
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT)
	@Override
	public void connect(String clientId, ConnectPacket packet) {
		/* Determine if this the connect packet for the Edge node under test.
		 * Set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				edgeNodeTestClientId = clientId;
				logger.info("Host Application send command test - connect - client id is " + clientId);
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		logger.info("Host - {} - DISCONNECT {}, {} ", getName(), clientId, state);
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("Host - {} - SUBSCRIBE {}, {} ", getName(), clientId, state);
	}

	private void publishToTckConsolePrompt(String payload) {
		Publish message = Builders.publish().topic(TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes())).build();
		logger.info("Requesting command to edge node id:{}: {} ", edgeNodeId, payload);
		publishService.publish(message);
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Host - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);

	}
}
