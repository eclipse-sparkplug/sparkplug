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
 * This is the Sparkplug primary host message reordering test. It tests the following:
 * 
 * - Sparkplug Host Applications SHOULD provide a configurable 'Reorder Timeout' parameter.
 * - If a message arrives with an out of order sequence number, the Host Application SHOULD 
 *   start a timer denoting the start of the Reorder Timeout window.
 * - If the Reorder Timeout elapses and the missing message(s) have not been received, the
 *   Sparkplug Host Application SHOULD send an NCMD to the Edge Node with a 'Node Control/Rebirth' request
 * - If the missing messages that triggered the start of the Reorder Timeout timer arrive before
 *   the reordering timer elapses, the timer can be terminated and normal operation in the
 *    Host Application can continue
 *
 * @author Ian Craggs
 */

package org.eclipse.sparkplug.tck.test.host;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_REORDERING_START;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkHostApplicationIsOnline;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.sparkplug.impl.exception.model.MetricDataType;
import org.eclipse.sparkplug.impl.exception.model.SparkplugBPayload;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants.TestStatus;
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
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.publish.PublishService;

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class MessageOrderingTest extends TCKTest {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull Map<String, String> testResults = new HashMap<>();
	public static final @NotNull List<String> testIds =
			List.of(ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START,
					ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS);
	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull int reorderTimeout;

	private @NotNull String testClientId;
	private TestStatus state = null;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;

	private boolean rebirth_received = false;
	private int reorder_count = 0;

	private PublishService publishService = Services.publishService();
	private final ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	public MessageOrderingTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("{}: Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;

		// Ignore sequence number failures because we're intentionally triggering out of order messages
		utilities.getMonitor().setIgnoreSeqNumCheck(true);

		if (params.length < 5) {
			log("Not enough parameters: " + Arrays.toString(params));
			log(getName() + "Parameters must be: hostApplicationId, groupId edgeNodeId deviceId reorderTimeout");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		reorderTimeout = Integer.valueOf(params[4]); // in milliseconds
		logger.info(
				"Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {} Reorder Timeout: {}",
				hostApplicationId, groupId, edgeNodeId, deviceId, reorderTimeout);

		final AtomicBoolean hostOnline = checkHostApplicationIsOnline(hostApplicationId);

		if (!hostOnline.get()) {
			log(String.format("HostApplication %s not online - test not started.", hostApplicationId));
			throw new IllegalStateException();
		}

		// First start the simulated edge node
		// Delay the start because
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					utilities.getEdgeNode().edgeNodeOnline(hostApplicationId, groupId, edgeNodeId, deviceId);
				} catch (Exception e) {
					logger.error("Failed to start simulated edge node", e);
					theTCK.endTest();
				}
			}
		}, 1, TimeUnit.SECONDS);
		state = TestStatus.EXPECT_NODE_BIRTH;
	}

	@Override
	public void endTest(Map<String, String> results) {
		try {
			utilities.getEdgeNode().edgeOffline();
		} catch (Exception e) {
			logger.error("endTest", e);
		}

		utilities.getMonitor().setIgnoreSeqNumCheck(false);
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@Override
	public String getName() {
		return "Host MessageOrdering";
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

		/* Get the simulated edge node connecting - set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				testClientId = clientId;
				logger.info("{} Connect - edge client id is {} ", getName(), clientId);
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
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS)
	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.info("{} - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);

		if (packet.getTopic()
				.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NBIRTH + "/" + edgeNodeId)) {
			// the edge node birth
			if (state == TestStatus.EXPECT_NODE_BIRTH) {
				logger.info("{} node birth received", getName());
				state = TestStatus.EXPECT_DEVICE_BIRTH;
			} else if (state == TestStatus.EXPECT_NODE_REBIRTH) {
				logger.info("{} node birth received", getName());
				state = TestStatus.EXPECT_DEVICE_REBIRTH;
			} else {
				logger.error("{} node birth received at wrong time", getName());
				theTCK.endTest();
			}
		} else if (packet.getTopic().equals(
				TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_DBIRTH + "/" + edgeNodeId + "/" + deviceId)) {
			// device id birth
			if (state == TestStatus.EXPECT_DEVICE_BIRTH) {
				logger.info("{} device birth received", getName());
				state = TestStatus.PUBLISH_DEVICE_DATA;
				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						sendDeviceData(3); // end a good data sequence
					}
				}, 1, TimeUnit.SECONDS);
			} else if (state == TestStatus.EXPECT_DEVICE_REBIRTH) {
				logger.info("{} device birth received", getName());
				state = TestStatus.PUBLISH_DEVICE_DATA;
				executorService.schedule(new Runnable() {
					@Override
					public void run() {
						sendDeviceData(3); // end a good data sequence
					}
				}, 1, TimeUnit.SECONDS);
			} else {
				logger.error("{} device birth received at wrong time", getName());
				theTCK.endTest();
			}
		} else if (packet.getTopic().equals(
				TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_DDATA + "/" + edgeNodeId + "/" + deviceId)) {

		} else if (packet.getTopic()
				.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NCMD + "/" + edgeNodeId)) {
			if (state == TestStatus.EXPECT_NODE_REBIRTH) {
				// this should be a device rebirth as a result of a missing message

				// look through metrics
				final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);

				for (Metric metric : sparkplugPayload.getMetricsList()) {
					if (metric.hasName() && metric.getName().equals("Node Control/Rebirth")) {
						if (reorder_count == 1) {
							// should not get rebirth here
							Utils.setShouldResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS,
									OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS);
						} else {
							rebirth_received = true;
							reorder_count++;
							Utils.setShouldResult(testResults, true, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM,
									OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM);
							Utils.setShouldResult(testResults, true, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START,
									OPERATIONAL_BEHAVIOR_HOST_REORDERING_START);
							Utils.setShouldResult(testResults, true, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH,
									OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH);
							logger.info("{} rebirth for Edge Node", getName());
							try {
								utilities.getEdgeNode().edgeOffline();
								// in a short while do a rebirth
								executorService.schedule(new Runnable() {
									@Override
									public void run() {
										try {
											utilities.getEdgeNode().edgeNodeOnline(hostApplicationId, groupId,
													edgeNodeId, deviceId);
										} catch (Exception e) {
											logger.error("Failed to start simulated edge node", e);
											theTCK.endTest();
										}
									}
								}, 1, TimeUnit.SECONDS);
							} catch (Exception e) {
								theTCK.endTest();
							}
						}
					}
				}
			}
		}
	}

	public void sendDeviceData(int count) {
		int mycount = count - 1;
		try {
			utilities.getEdgeNode().publishDeviceData("Temperature", MetricDataType.Int16, (short) count);
		} catch (Exception e) {
			e.printStackTrace();
		}

		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				if (mycount > 0) {
					sendDeviceData(mycount);
				} else {
					sendMissingDeviceData();
				}
			}
		}, 1, TimeUnit.SECONDS);
	}

	SparkplugBPayload delayed = null;

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_MESSAGE_ORDERING,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS)
	public void sendMissingDeviceData() {
		try {
			delayed = utilities.getEdgeNode().getNextDeviceData("Temperature", MetricDataType.Int16, (short) 23);
			utilities.getEdgeNode().publishDeviceData("Temperature", MetricDataType.Int16, (short) 24);
			state = TestStatus.EXPECT_NODE_REBIRTH;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (reorder_count == 1) {
			// send the missing data within the reorder interval
			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						utilities.getEdgeNode().publishDeviceData(delayed);
						state = TestStatus.PUBLISHED_DEVICE_DATA;
						executorService.schedule(new Runnable() {
							@Override
							public void run() {
								Utils.setShouldResultIfNotFail(testResults, true,
										ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS,
										OPERATIONAL_BEHAVIOR_HOST_REORDERING_SUCCESS);
								theTCK.endTest();
							}
						}, reorderTimeout * 2, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						logger.error("Error publishing device data", e);
						theTCK.endTest();
					}
				}
			}, reorderTimeout / 2, TimeUnit.MILLISECONDS);
		} else {
			// wait long enough to get a REBIRTH
			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					if (!rebirth_received) {
						Utils.setShouldResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM,
								OPERATIONAL_BEHAVIOR_HOST_REORDERING_PARAM);
						Utils.setShouldResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_START,
								OPERATIONAL_BEHAVIOR_HOST_REORDERING_START);
						Utils.setShouldResult(testResults, false, ID_OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH,
								OPERATIONAL_BEHAVIOR_HOST_REORDERING_REBIRTH);
						theTCK.endTest();
					}
				}
			}, reorderTimeout * 2, TimeUnit.MILLISECONDS);
		}
	}
}
