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
 * This is the Sparkplug Edge Node multiple MQTT Broker test
 * 
 * It tests:
 * 
 * When an edge node is configured for two MQTT brokers
 *  - it must not connect to more than one at a time
 *  - it must subscribe to the Host Application state message
 *  - if it receives a host application offline message, it must move to the next MQTT broker
 *  - the edge node must wait to publish its birth sequence until the online state message is received
 * 
 * @author Ian Craggs
 */

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_STATE;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkHostApplicationIsOnline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.eclipse.sparkplug.tck.utility.HostApplication;
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
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class MultipleBrokerTest extends TCKTest {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull Map<String, String> testResults = new HashMap<>();
	public static final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER,
					ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
					ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);

	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull String brokerURL;

	private HostApplication broker2 = new HostApplication("tcp://localhost:1884");

	private TestStatus state = null;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;
	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	private int births_on = 0;
	private String edgeNodeTestClientId;
	private boolean nbirthReceived = false;
	private boolean dbirthReceived = false;

	public MultipleBrokerTest(TCK aTCK, Utilities utilities, String[] parms, Results.Config config) {
		logger.info("{}: Parameters: {} ", getName(), Arrays.asList(parms));
		theTCK = aTCK;
		this.utilities = utilities;

		// Ignore BD sequence number failures because we're connecting to two MQTT Servers and the monitor will only see
		// a portion of the bdSeq numbers
		utilities.getMonitor().setIgnoreBdSeqNumCheck(true);

		if (parms.length < 5) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log("Parameters to edge multiple broker test must be: hostApplicationId, groupId edgeNodeId deviceId brokerURL");
			throw new IllegalArgumentException();
		}
		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];
		deviceId = parms[3];
		brokerURL = parms[4];
		logger.info("Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {} BrokerURL: {}",
				hostApplicationId, groupId, edgeNodeId, deviceId, brokerURL);

		// check host is offline
		final AtomicBoolean hostOnline = checkHostApplicationIsOnline(hostApplicationId);
		if (hostOnline.get()) {
			log(String.format("HostApplication %s is online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		// subscribe to second server, and check Host is online on both
		broker2 = new HostApplication(brokerURL);

		state = TestStatus.NONE;
		// start fake host on server 1 and 2, check that edge node connects and subscribes
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				setHost1Online();
			}
		}, 1, TimeUnit.SECONDS);
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			utilities.getHostApps().hostOffline();
			broker2.hostOffline();
		} catch (Exception e) {
			logger.error("endTest", e);
		}

		utilities.getMonitor().setIgnoreBdSeqNumCheck(false);
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
		edgeNodeTestClientId = null;
	}

	public void setHost1Online() {
		logger.info("{} setHost1Online", getName());
		// start fake host on server 1 check that edge node connects and subscribes
		try {
			Utils.setResult(testResults, false,
					ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
					OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS);
			state = TestStatus.HOST_ONLINE;
			utilities.getHostApps().hostOnline(hostApplicationId, true);
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
	}

	public void setHost2Online() {
		logger.info("{} setHost2Online", getName());
		// now we should have received the birth messages for server 1
		if (nbirthReceived == false) {
			logger.info("{} no NBIRTH received in state {}", getName(), state.toString());
		} else {
			nbirthReceived = false;
		}
		if (dbirthReceived == false) {
			logger.info("{} no DBIRTH received in state {}", getName(), state.toString());
		} else {
			dbirthReceived = false;
		}

		// start fake host on server 2 - edge node should not connect
		try {
			state = TestStatus.HOSTS_ONLINE;
			broker2.hostOnline(hostApplicationId, true);
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				hostsOnline();
			}
		}, 2, TimeUnit.SECONDS);
	}

	private void hostsOnline() {
		logger.info("{} HostsOnline", getName());

		// after a while send a host application offline message,
		// check for node deaths on server 1
		// check for node births on server 2
		births_on = 2;
		state = TestStatus.EXPECT_DEATHS_AND_BIRTHS;
		try {
			logger.info("{} setting host 1 offline", getName());
			utilities.getHostApps().hostOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				host1Offline();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void host1Offline() {
		logger.info("{} Host1Offline", getName());
		// now we should have received the death and birth messages

		checkBirths();

		// send online host application to server 1
		state = TestStatus.DONT_EXPECT_BIRTHS;
		try {
			utilities.getHostApps().hostOnline(hostApplicationId, true);
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				host1Online();
			}
		}, 1, TimeUnit.SECONDS);
	}

	private void host1Online() {
		logger.info("{} Host1Online", getName());
		// now we should have received the death and birth messages

		Utils.setResultIfNotFail(testResults, true,
				ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER,
				OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER);

		// send offline host application on server 2,
		// check disconnects from server 2
		// check reconnects to server 1
		births_on = 1;
		state = TestStatus.EXPECT_DEATHS_AND_BIRTHS;
		try {
			broker2.hostOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		// the test will end when DBIRTH is received on server 1
	}

	@Override
	public String getName() {
		return "Edge MultipleBroker";
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
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				edgeNodeTestClientId = clientId;
				logger.info("{} - connect - client id is {}", getName(), clientId);
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		logger.info("{} - DISCONNECT {}, {} ", getName(), clientId, state);
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS)
	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("{} - SUBSCRIBE {}, {} ", getName(), clientId, state);

		if (edgeNodeTestClientId == null || !edgeNodeTestClientId.equals(clientId)) {
			return; // ignore subscriptions from any other client
		}

		List<Subscription> subscriptions = packet.getSubscriptions();
		for (Subscription s : subscriptions) {
			String[] levels = s.getTopicFilter().split("/");

			logger.debug(">>>> subscription to " + s.getTopicFilter());

			if (levels[0].equals(TOPIC_ROOT_SP_BV_1_0) && levels[1].equals(TOPIC_PATH_STATE)
					&& levels[2].equals(hostApplicationId)) {

				if (state == TestStatus.HOST_ONLINE) {
					Utils.setResult(testResults, true,
							ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS,
							OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_STATE_SUBS);
				}
			}
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT)
	@Override
	public void publish(String clientId, PublishPacket packet) {
		final String topic = packet.getTopic();
		logger.info("{} test - PUBLISH - topic: {}, state: {} ", getName(), topic, state);

		if (!topic.startsWith(TOPIC_ROOT_SP_BV_1_0)) {
			// ignore non Sparkplug messages
			return;
		}

		if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NBIRTH + "/"
				+ edgeNodeId)) {
			// found the edge NBIRTH
			nbirthReceived = true;
			if (state == TestStatus.HOST_ONLINE) {
				Utils.setResultIfNotFail(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);

			} else if (state == TestStatus.EXPECT_DEATHS_AND_BIRTHS && births_on == 1) {
				Utils.setResultIfNotFail(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);

			} else {
				// any other state is wrong
				logger.error("{} error received NBIRTH in state {}", getName(), state.toString());

				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER,
						OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER);

				Utils.setResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);
			}
		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_DBIRTH + "/"
				+ edgeNodeId + "/" + deviceId)) {
			// found the device DBIRTH
			dbirthReceived = true;
			if (state == TestStatus.HOST_ONLINE) {
				Utils.setResultIfNotFail(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);

				// we've received NBIRTH and DBIRTH so set the second host online
				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						setHost2Online();
					}
				}, 1, TimeUnit.SECONDS);

			} else if (state == TestStatus.EXPECT_DEATHS_AND_BIRTHS && births_on == 1) {
				Utils.setResultIfNotFail(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);

				// the edge node has reconnected to server 1 so that's the end of the test
				state = TestStatus.ENDING;
				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						theTCK.endTest();
					}
				}, 1, TimeUnit.SECONDS);

			} else {
				// any other state is wrong
				logger.error("{} error received DBIRTH in state {}", getName(), state.toString());

				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER,
						OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_SINGLE_SERVER);

				Utils.setResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_BIRTH_SEQUENCE_WAIT);
			}
		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NDEATH + "/"
				+ edgeNodeId)) {

			if (state == TestStatus.ENDING) {

			} else if (state == TestStatus.EXPECT_DEATHS_AND_BIRTHS && births_on == 2) {

			} else {
				// any other state is wrong
				logger.error("{} error received NDEATH in state {}", getName(), state.toString());

				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK,
						OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK);
			}
		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_DDEATH + "/"
				+ edgeNodeId + "/" + deviceId)) {

			if (state == TestStatus.ENDING) {

			} else if (state == TestStatus.EXPECT_DEATHS_AND_BIRTHS && births_on == 2) {

			} else {
				// any other state is wrong
				logger.error("{} error received DDEATH in state {}", getName(), state.toString());

				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK,
						OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK);
			}
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_IN_MULTIPLE_MQTT_SERVER_TOPOLOGIES,
			id = ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK)
	private void checkBirths() {
		// check births on broker2
		boolean nbirth_found = false;
		boolean dbirth_found = false;
		HostApplication.Message msg = broker2.getNextMessage();
		while (msg != null) {
			logger.info("Message found {}", msg.getTopic()); // , new String(msg.getMqttMessage().getPayload()));
			String topic = msg.getTopic();
			if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NBIRTH + "/"
					+ edgeNodeId)) {
				nbirth_found = true;
			} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_DBIRTH
					+ "/" + edgeNodeId + "/" + deviceId)) {
				dbirth_found = true;
			}
			msg = broker2.getNextMessage();
		}
		Utils.setResultIfNotFail(testResults, nbirth_found && dbirth_found,
				ID_OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK,
				OPERATIONAL_BEHAVIOR_PRIMARY_APPLICATION_STATE_WITH_MULTIPLE_SERVERS_WALK);
	}
}
