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

/**
 * This is the Sparkplug edge node test when dependent on a primary host application
 *
 * States:
 *  1. WRONG_HOST_ONLINE - set another host online, make sure the edge doesn't send births
 *  2. HOST_OFFLINE - set the correct host offline, make sure the edge doesn't send births
 *  3. HOST_ONLINE - set the correct host online, expect the edge births
 *  4. DONT_EXPECT_DEATHS - send an old timestamp offline message - ensure the edge doesn't send deaths
 *  5. EXPECT_DEATHS - set the correct host offline, expect deaths
 *  6. HOST_WRONG_TIMESTAMP - send an online message with the wrong timestamp, ensure the edge doesn't send births
 *  7. HOST_ONLINE_AGAIN - set the host online properly, expect births
 *  8. HOST_OFFLINE - set the host offline again, to end
 *
 * @author Ian Craggs
 **/

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.sparkplug.tck.test.common.Utils;
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

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class PrimaryHostTest extends TCKTest {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	public static final String PROPERTY_KEY_QUALITY = "Quality";

	public static final @NotNull List<String> testIds = List.of(ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
			ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT,
			ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP);

	private final @NotNull TCK theTCK;
	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull long seqUnassigned = -1;
	private Utilities utilities = null;

	private TestStatus state = TestStatus.NONE;

	public PrimaryHostTest(TCK aTCK, Utilities utilities, String[] parms, Results.Config config) {
		logger.info("Edge Node payload validation test. Parameters: {} ", Arrays.asList(parms));
		theTCK = aTCK;
		this.utilities = utilities;

		if (parms.length < 4) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log("Parameters to edge primary host test must be: hostId groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];
		deviceId = parms[3];
		logger.info("Parameters are HostId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId,
				edgeNodeId, deviceId);

		// try setting simulated host offline if it's marked online
		AtomicBoolean hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);
		if (hostOnline.get()) {
			try {
				utilities.getHostApps().hostPrepare(hostApplicationId, false);
				utilities.getHostApps().hostSendOffline();
			} catch (Exception e) {
				logger.error("{} error", getName(), e);
				theTCK.endTest();
			}
		}

		hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);
		if (hostOnline.get()) {
			log(String.format("HostApplication %s is online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		// set the wrong host online to ensure the edge node doesn't send an NBIRTH
		try {
			state = TestStatus.WRONG_HOST_ONLINE;
			utilities.getHostApps().hostOnline(hostApplicationId + "_WRONG", false);
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		logger.info("{} set wrong host online", getName());
		// wait a little while to ensure the edge node doesn't send an NBIRTH
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				wrongHostOnline();
			}
		}, 2, TimeUnit.SECONDS);
	}

	private void wrongHostOnline() {
		logger.info("{} wrongHostOffline", getName());
		// now set the wrong host offline, and set the right one offline explicitly
		// no births should arrive when the (correct) host is marked offline
		try {
			utilities.getHostApps().hostOffline();
			utilities.getHostApps().hostPrepare(hostApplicationId, false);
			state = TestStatus.HOST_OFFLINE;
			utilities.getHostApps().hostSendOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				rightHostOffline();
			}
		}, 2, TimeUnit.SECONDS);
	}

	private void rightHostOffline() {
		logger.info("{} rightHostOffline", getName());
		// now set the right one online
		try {
			state = TestStatus.HOST_ONLINE;
			utilities.getHostApps().hostSendOnline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				rightHostOnline();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void rightHostOnline() {
		logger.info("{} rightHostOnline", getName());
		// now we should have received the birth messages

		// send an offline message with an old timestamp (less than previous)
		state = TestStatus.DONT_EXPECT_DEATHS;

		// this will be set to false if any deaths are received in this state
		Utils.setResult(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP,
				OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP);
		try {
			utilities.getHostApps().hostSendOldOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		// we should get death message(s)
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				dontExpectDeaths();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void dontExpectDeaths() {
		logger.info("{} dontExpectDeaths", getName());
		// no deaths should have been received

		// set the host offline again
		state = TestStatus.EXPECT_DEATHS;
		try {
			utilities.getHostApps().hostSendOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		// we should get death message(s)
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				expectDeathMessages();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void expectDeathMessages() {
		logger.info("{} expectDeathMessages", getName());
		// now we should have received the death messages

		// send the online message but with a lower timestamp - should be wrong

		// should not get any births in this state
		Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
				MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);
		try {
			state = TestStatus.HOST_WRONG_TIMESTAMP;
			utilities.getHostApps().hostSendOldOnline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				wrongTimestamp();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void wrongTimestamp() {
		logger.info("{} wrongTimestamp", getName());
		// should not have received re-birth messages

		// this will not set the condition to true if it has already failed
		Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
				MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);

		// this will be set to true if the births arrive
		Utils.setResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT,
				OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT);

		// Set the host online properly again
		try {
			state = TestStatus.HOST_ONLINE_AGAIN;
			utilities.getHostApps().hostOffline();
			utilities.getHostApps().hostOnline(hostApplicationId, false);
		} catch (Exception e) {
			logger.error("{} wrongTimestamp error", getName(), e);
			theTCK.endTest();
		}
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				hostOnlineAgain();
			}
		}, 3, TimeUnit.SECONDS);
	}

	private void hostOnlineAgain() {
		logger.info("{} hostOnlineAgain", getName());

		// Set the host offline properly again
		try {
			state = TestStatus.HOST_OFFLINE;
			utilities.getHostApps().hostOffline();
		} catch (Exception e) {
			logger.error("{} error", getName(), e);
			theTCK.endTest();
		}
		theTCK.endTest();
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			utilities.getHostApps().hostOffline();
		} catch (MqttException e) {
			// e.printStackTrace();
		}
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Edge PrimaryHost";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
		// TODO Auto-generated method stub
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP)
	@Override
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		final String topic = packet.getTopic();
		logger.info("{} - publish - topic: {}", getName(), topic);

		if (!topic.startsWith(TOPIC_ROOT_SP_BV_1_0)) {
			// ignore non Sparkplug messages
			return;
		}

		if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NBIRTH + "/"
				+ edgeNodeId)) {
			// found the edge NBIRTH
			if (state == TestStatus.WRONG_HOST_ONLINE) {
				// received NBIRTH for wrong host
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID);
			} else if (state == TestStatus.HOST_OFFLINE) {
				// received NBIRTH when host is offline
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT);
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE);

			} else if (state == TestStatus.HOST_WRONG_TIMESTAMP) {
				// received NBIRTH for wrong host timestamp
				Utils.setResultIfNotFail(testResults, false,
						ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);

			} else if (state == TestStatus.HOST_ONLINE) {

				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT);
				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID);
				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE);

			} else if (state == TestStatus.HOST_ONLINE_AGAIN) {

				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);
				Utils.setResult(testResults, true, ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT);
			} else {
				// any other state is wrong
				logger.error("{} error received NBIRTH in state {}", getName(), state.toString());
			}
		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_DBIRTH + "/"
				+ edgeNodeId + "/" + deviceId)) {
			// found the device DBIRTH
			if (state == TestStatus.WRONG_HOST_ONLINE) {
				// received DBIRTH for wrong host
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID);
			} else if (state == TestStatus.HOST_OFFLINE) {
				// received DBIRTH when host is offline
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT);
				Utils.setResultIfNotFail(testResults, false, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE);

			} else if (state == TestStatus.HOST_WRONG_TIMESTAMP) {
				// received DBIRTH for wrong host timestamp
				Utils.setResultIfNotFail(testResults, false,
						ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);

			} else if (state == TestStatus.HOST_ONLINE) {

				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT);
				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ID);
				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_ONLINE);

			} else if (state == TestStatus.HOST_ONLINE_AGAIN) {

				Utils.setResultIfNotFail(testResults, true, ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP,
						MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_WAIT_TIMESTAMP);
				Utils.setResultIfNotFail(testResults, true,
						ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_RECONNECT);
			} else {
				// any other state is wrong
				logger.error("{} error received DBIRTH in state {}", getName(), state.toString());
			}
		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_NDEATH + "/"
				+ edgeNodeId)) {

			logger.info("Received NDEATH in state " + state.name());
			Utils.setResultIfNotFail(testResults, state == TestStatus.EXPECT_DEATHS || state == TestStatus.HOST_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE,
					OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE);

			Utils.setResultIfNotFail(testResults, state == TestStatus.EXPECT_DEATHS || state == TestStatus.HOST_OFFLINE,
					ID_MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE,
					MESSAGE_FLOW_EDGE_NODE_BIRTH_PUBLISH_PHID_OFFLINE);

			if (state == TestStatus.DONT_EXPECT_DEATHS) {
				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP);
			}

		} else if (topic.equals(Constants.TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + Constants.TOPIC_PATH_DDEATH + "/"
				+ edgeNodeId + "/" + deviceId)) {

			logger.info("Received DDEATH in state " + state.name());
			Utils.setResultIfNotFail(testResults, state == TestStatus.EXPECT_DEATHS || state == TestStatus.HOST_OFFLINE,
					ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE,
					OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE);

			if (state == TestStatus.DONT_EXPECT_DEATHS) {
				Utils.setResult(testResults, false,
						ID_OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP,
						OPERATIONAL_BEHAVIOR_EDGE_NODE_TERMINATION_HOST_OFFLINE_TIMESTAMP);
			}
		}
	}
}
