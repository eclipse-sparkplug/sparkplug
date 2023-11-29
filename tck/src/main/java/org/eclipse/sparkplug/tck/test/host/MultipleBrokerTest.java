/*******************************************************************************
 * Copyright (c) 2021, 2023 Ian Craggs
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
 * This is the Sparkplug host application multiple broker test. It tests the following:
 * 
 * WHen host applications are connected to more than 1 MQTT broker:
 * - they must send an online status message to all brokers
 * - every time they connect they must send an online state message
 * - the host application must have a separate state timestamp for each MQTT broker
 *
 * @author Ian Craggs
 */

package org.eclipse.sparkplug.tck.test.host;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkHostApplicationIsOnline;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
import org.eclipse.sparkplug.tck.test.common.HostUtils;
import org.eclipse.sparkplug.tck.test.common.StatePayload;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.utility.HostApplication;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.session.ClientService;

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class MultipleBrokerTest extends TCKTest {

	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull Map<String, String> testResults = new HashMap<>();
	public static final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
					ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE,
					ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP);
	private @NotNull String hostApplicationId;
	private @NotNull String brokerURL;

	private HostApplication broker2 = null;

	private TestStatus state = null;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;
	private Results.Config config = null;

	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();
	private final ClientService clientService = Services.clientService();
	private String hostClientId = null;
	private long deathTimestamp = -1;
	private boolean checkHostIsOnLine_hasRun = false;

	public MultipleBrokerTest(TCK aTCK, Utilities utilities, String[] parms, Results.Config config) {
		logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(parms));
		theTCK = aTCK;
		this.utilities = utilities;
		this.config = config;

		// Ignore duplicate STATE publish failures because we're connecting to two MQTT Servers and the monitor will
		// detect a collision that isn't really a collision
		utilities.getMonitor().setIgnoreDupHostCheck(true);

		if (parms.length < 2) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log("Parameters to host multiple broker test must be: hostApplicationId, brokerURL");
			throw new IllegalArgumentException();
		}
		hostApplicationId = parms[0];
		brokerURL = parms[1];
		logger.info("Parameters are HostApplicationId: {}, BrokerURL: {}", hostApplicationId, brokerURL);

		final AtomicBoolean hostOnline = checkHostApplicationIsOnline(hostApplicationId);

		if (hostOnline.get()) {
			log(String.format("HostApplication %s online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		state = TestStatus.HOST_ONLINE;
		// subscribe to second server, and check Host is online on both
		broker2 = new HostApplication(brokerURL);
		try {
			broker2.connect();
		} catch (MqttException e) {
			logger.error("Connecting to broker", e);
			theTCK.endTest();
		}
		log(getName() + " waiting for host application to be started");
	}

	private void checkHostIsOnline() {
		logger.info("{} checkHostIsOnline", getName());
		// check host is now online, and timestamp is different
		checkHost2Online();

		// forcibly disconnect the host application on this server, check it reconnects ok
		state = TestStatus.EXPECT_HOST_RECONNECT;
		clientService.disconnectClient(hostClientId);
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				hostReconnect();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void hostReconnect() {
		logger.info("{} hostReconnect", getName());

		state = TestStatus.NONE;
		theTCK.endTest();
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS)
	private void checkHost2Online() {
		HostApplication.Message msg = broker2.getNextMessage();
		while (msg != null) {
			String topic = msg.getTopic();
			if (topic.equals(Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId)) {
				StatePayload statePayload = Utils.getHostPayload(new String(msg.getMqttMessage().getPayload()));

				Utils.setResult(testResults, statePayload.isOnline() && (statePayload.getTimestamp() >= deathTimestamp),
						ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP,
						OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MULTI_SERVER_TIMESTAMP);

				Utils.setResult(testResults, statePayload.isOnline(),
						ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
						OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS);
			}
			msg = broker2.getNextMessage();
		}
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			broker2.disconnect();
		} catch (Exception e) {
			logger.error("endTest", e);
		}

		utilities.getMonitor().setIgnoreDupHostCheck(false);
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@Override
	public String getName() {
		return "Host MultipleBroker";
	}

	@Override
	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	@Override
	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		if (HostUtils.isHostApplication(hostApplicationId, packet)) {
			logger.debug("Got a connect from {}", hostApplicationId);

			String payloadString =
					StandardCharsets.UTF_8.decode(packet.getWillPublish().get().getPayload().get()).toString();
			logger.debug("Will message STATE payload={}", payloadString);
			StatePayload statePayload = Utils.getHostPayload(payloadString);
			if (statePayload != null
					&& (state == TestStatus.HOST_ONLINE || state == TestStatus.EXPECT_HOST_RECONNECT)) {
				deathTimestamp = statePayload.getTimestamp().longValue();
				hostClientId = clientId;
				logger.info("{} : setting clientid to {}", getName(), hostClientId);
			} else {
				logger.error("Test failed on connect.");
				theTCK.endTest();
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		logger.info("{} - DISCONNECT {}, {} ", getName(), clientId, state);
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("{} - SUBSCRIBE {}, {} ", getName(), clientId, state);
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS)
	@Override
	public void publish(String clientId, PublishPacket packet) {
		final String topic = packet.getTopic();
		logger.info("{} test - PUBLISH - topic: {}, state: {} ", getName(), topic, state);

		if (!topic.startsWith(TOPIC_ROOT_SP_BV_1_0)) {
			// ignore non Sparkplug messages
			return;
		}

		if (hostClientId.equals(clientId) && topic.equals(Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId)) {
			if (packet.getPayload().isPresent()) {
				String payloadString = StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString();
				StatePayload statePayload = Utils.getHostPayload(payloadString);

				if (statePayload == null) {

				} else if (state == TestStatus.HOST_ONLINE) {
					Utils.setResultIfNotFail(testResults, statePayload.isOnline(),
							ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE,
							OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE);

					Utils.setResultIfNotFail(testResults, statePayload.isOnline(),
							ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
							OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS);

					if (!checkHostIsOnLine_hasRun) {
						checkHostIsOnLine_hasRun = true;
						// now we've received the subscribe messages on this broker, schedule the check
						// for the other broker
						executorService.schedule(new Runnable() {
							@Override
							public void run() {
								checkHostIsOnline();
							}
						}, 1, TimeUnit.SECONDS);
					}
				} else if (state == TestStatus.EXPECT_HOST_RECONNECT) {
					// we should get an offline, followed by an online

					if (!statePayload.isOnline()) {
						Utils.setResultIfNotFail(testResults, true,
								ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE,
								OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE);
					}
				}
			}
		}
	}
}
