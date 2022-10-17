/* ******************************************************************************
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
 ****************************************************************************** */

package org.eclipse.sparkplug.tck.test.host;

import static org.eclipse.sparkplug.tck.test.common.Requirements.COMPONENTS_PH_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_PRIMARY_HOST;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_BIRTH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_COMPONENTS_PH_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_PRIMARY_HOST;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_BIRTH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_INTRO_SPARKPLUG_HOST_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_SUBSCRIBE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Requirements.INTRO_SPARKPLUG_HOST_STATE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_BIRTH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_SUBSCRIBE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_STATE_WILL_MESSAGE_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PRINCIPLES_BIRTH_CERTIFICATES_ORDER;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.StatePayload;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.MqttVersion;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;

/**
 * This is the primary host Sparkplug session establishment, and re-establishment test.
 * <p>
 * We do know the host application id, but there is no requirement on the MQTT client id, which means the first that we
 * know we are dealing with the host application is the receipt of the STATE message.
 * <p>
 * Currently this test works if the first MQTT client to connect is the host application. To make it completely robust
 * means following all connect/subscribe/publish combinations and ruling out the ones that don't match. There could be
 * many in parallel.
 *
 * @author Ian Craggs
 * @author Lukas Brand
 */
@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class SessionEstablishmentTest extends TCKTest {
	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	private final @NotNull List<String> testIds = List.of(ID_CONFORMANCE_PRIMARY_HOST,
			ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH,
			ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH,
			ID_COMPONENTS_PH_STATE, ID_INTRO_SPARKPLUG_HOST_STATE, ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311,
			ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL,
			ID_PAYLOADS_STATE_WILL_MESSAGE, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC,
			ID_HOST_TOPIC_PHID_DEATH_TOPIC, ID_HOST_TOPIC_PHID_DEATH_PAYLOAD,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD, ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD,
			ID_HOST_TOPIC_PHID_DEATH_QOS, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS,
			ID_PAYLOADS_STATE_WILL_MESSAGE_QOS, ID_HOST_TOPIC_PHID_DEATH_RETAIN,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED, ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN,
			ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, ID_PAYLOADS_STATE_SUBSCRIBE, ID_HOST_TOPIC_PHID_BIRTH_TOPIC,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC, ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD,
			ID_HOST_TOPIC_PHID_BIRTH_MESSAGE, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD,
			ID_HOST_TOPIC_PHID_BIRTH_QOS, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS,
			ID_HOST_TOPIC_PHID_BIRTH_RETAIN, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED,
			ID_PAYLOADS_STATE_BIRTH, ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ,
			ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD, ID_PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ,
			ID_PAYLOADS_STATE_BIRTH_PAYLOAD, ID_HOST_TOPIC_PHID_BIRTH_REQUIRED, ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ,
			ID_HOST_TOPIC_PHID_DEATH_REQUIRED, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ,
			ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED);

	private @NotNull String hostApplicationId;

	private final @NotNull List<String> subscriptions = new ArrayList<>();
	private @NotNull HostState state = HostState.DISCONNECTED;
	private @Nullable String hostClientId = null;
	private TCK theTCK = null;

	private short deathBdSeq = -1;

	public SessionEstablishmentTest(final @NotNull TCK aTCK, final @NotNull String[] params) {
		logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;

		if (params.length != 1) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to Host Session Establishment test must be: hostId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		logger.info("{} Parameters are HostApplicationId: {}", getName(), hostApplicationId);
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Host SessionEstablishment";
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
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		if (HostUtils.isHostApplication(hostApplicationId, packet)) {
			logger.debug("Primary {} - connect", getName());

			boolean overallPass = checkConnectMessage(packet);
			overallPass = overallPass && checkDeathMessage(packet);

			if (overallPass) {
				hostClientId = clientId;
				log(getName() + ": host clientId is " + hostClientId);
				state = HostState.CONNECTED;
			} else {
				logger.error("Test failed on connect.");
				theTCK.endTest();
			}
		}
	}

	@Override
	public void disconnect(final @NotNull String clientId, final @NotNull DisconnectPacket packet) {
		// TODO Auto-generated method stub
	}

	@Override
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION)
	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {

		// ignore messages before connect
		if (hostClientId == null) {
			return;
		}

		if (hostClientId.equals(clientId)) {
			logger.debug("Primary {} - subscribe", getName());
			// Subscribe is after connect (and allow additional subscriptions) ;
			if (state != HostState.CONNECTED && state != HostState.SUBSCRIBED) {
				logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION,
						MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION);
				testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION,
						setResult(false, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION));
				theTCK.endTest();
				return;
			}
			subscriptions.addAll(
					packet.getSubscriptions().stream().map(Subscription::getTopicFilter).collect(Collectors.toList()));
			checkSubscribes(false);
		}
	}

	@Override
	@SpecAssertion(
			section = Sections.PRINCIPLES_BIRTH_AND_DEATH_CERTIFICATES,
			id = ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER)
	@SpecAssertion(
			section = Sections.INTRODUCTION_HOST_APPLICATIONS,
			id = ID_INTRO_SPARKPLUG_HOST_STATE)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH)
	@SpecAssertion(
			section = Sections.COMPONENTS_SPARKPLUG_HOST_APPLICATION,
			id = ID_COMPONENTS_PH_STATE)
	@SpecAssertion(
			section = Sections.CONFORMANCE_SPARKPLUG_HOST_APPLICATION,
			id = ID_CONFORMANCE_PRIMARY_HOST)
	@SpecAssertion(
			section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_REQUIRED)
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		// ignore messages before connect
		if (hostClientId == null) {
			return;
		}

		if (hostClientId.equals(clientId)) {
			logger.info("Primary - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);
			// Check if subscribe completed
			checkSubscribes(true);

			// Publish is after subscribe (and theoretically allow additional publishes)
			logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH,
					MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH);

			if (state != HostState.SUBSCRIBED && state != HostState.PUBLISHED) {
				testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH,
						setResult(false, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH));
				theTCK.endTest();
				return;
			}

			final boolean overallPass = checkBirthMessage(packet);

			logger.debug("Check Req: {}:{}.", ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER,
					PRINCIPLES_BIRTH_CERTIFICATES_ORDER);
			testResults.put(ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER,
					setResult(overallPass, PRINCIPLES_BIRTH_CERTIFICATES_ORDER));

			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH,
					setResult(overallPass, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH));

			testResults.put(ID_HOST_TOPIC_PHID_BIRTH_REQUIRED, setResult(overallPass, HOST_TOPIC_PHID_BIRTH_REQUIRED));

			if (overallPass) {
				state = HostState.PUBLISHED;
				testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH,
						setResult(true, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH));

				testResults.put(ID_CONFORMANCE_PRIMARY_HOST, setResult(true, CONFORMANCE_PRIMARY_HOST));
			} else {
				logger.error("Test failed on published.");
				theTCK.endTest();
			}

			// TODO: test reconnect
			logger.debug("Check Req: {}:{}.", ID_COMPONENTS_PH_STATE, COMPONENTS_PH_STATE);
			testResults.put(ID_COMPONENTS_PH_STATE, setResult(true, COMPONENTS_PH_STATE));

			logger.debug("Check Req: {}:{}.", ID_INTRO_SPARKPLUG_HOST_STATE, INTRO_SPARKPLUG_HOST_STATE);
			testResults.put(ID_INTRO_SPARKPLUG_HOST_STATE, setResult(true, INTRO_SPARKPLUG_HOST_STATE));
		}

		// TODO: now we can disconnect the client and allow it to reconnect and go throught the
		// session re-establishment phases. It would be nice to be able to do this at after a
		// short arbitrary interval, but I haven't worked out a good way of doing that yet (assuming
		// that a sleep here is not a good idea). Using a PING interceptor could be one way but
		// we probably can't rely on any particular keepalive interval values.

		theTCK.endTest();
	}

	/*@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_REQUIRED)*/
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_WILL_MESSAGE)
	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_DEATH_REQUIRED)
	private boolean checkConnectMessage(final @NotNull ConnectPacket packet) {
		logger.info("Primary - {} test - CONNECT - state: {}, checkConnectMessage  ", getName(), state);

		boolean overallResult = false;

		if (packet.getMqttVersion() == MqttVersion.V_5) {
			overallResult = packet.getCleanStart() && (packet.getSessionExpiryInterval() == 0);
			logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50,
					MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50);
			testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50,
					setResult(overallResult, MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_50));
		} else {
			overallResult = packet.getCleanStart();
			logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311,
					MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311);

			testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311,
					setResult(overallResult, MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION_311));
		}

		// Will exists
		final boolean willExists = packet.getWillPublish().isPresent();
		overallResult &= willExists;

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL,
				setResult(willExists, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL));

		testResults.put(ID_HOST_TOPIC_PHID_DEATH_REQUIRED, setResult(willExists, HOST_TOPIC_PHID_DEATH_REQUIRED));

		logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE, PAYLOADS_STATE_WILL_MESSAGE);
		testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE, setResult(willExists, PAYLOADS_STATE_WILL_MESSAGE));
		return overallResult;
	}

	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_DEATH_TOPIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_DEATH,
			id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD)
	@SpecAssertion(
			section = Sections.DEATH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_DEATH_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED)

	@SpecAssertion(
			section = Sections.DEATH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_DEATH_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_WILL_MESSAGE_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD)

	private boolean checkDeathMessage(final @NotNull ConnectPacket packet) {
		logger.info("Primary - {} test - CONNECT - state: {}, checkDeathMessage  ", getName(), state);

		boolean overallResult = false;
		final Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();

		if (willPublishPacketOptional.isPresent()) {
			final WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			logger.info("   checkDeathMessage willPublishPacket: topic={}", willPublishPacket.getTopic(), state);

			// Topic is spBv1.0/STATE/{host_application_id}
			final boolean topicIsValid =
					willPublishPacket.getTopic().equals(Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId);
			overallResult = topicIsValid;

			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC,
					setResult(topicIsValid, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC));

			logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_TOPIC, HOST_TOPIC_PHID_DEATH_TOPIC);
			testResults.put(ID_HOST_TOPIC_PHID_DEATH_TOPIC, setResult(topicIsValid, HOST_TOPIC_PHID_DEATH_TOPIC));

			// Payload exists
			final boolean payloadExists = willPublishPacket.getPayload().isPresent();
			overallResult &= payloadExists;

			if (payloadExists) {
				String payloadString = StandardCharsets.UTF_8.decode(willPublishPacket.getPayload().get()).toString();
				StatePayload deathPayload = Utils.getHostPayload(payloadString, false);

				boolean isValidPayload = false;
				if (deathPayload != null) {
					deathBdSeq = deathPayload.getBdSeq().shortValue();
					isValidPayload = true;
				}

				logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_PAYLOAD, HOST_TOPIC_PHID_DEATH_PAYLOAD);
				testResults.put(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD,
						setResult(isValidPayload, HOST_TOPIC_PHID_DEATH_PAYLOAD));

				logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD,
						OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD);
				testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD,
						setResult(isValidPayload, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD));

				logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD,
						PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD);
				testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD,
						setResult(isValidPayload, PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD));
			}

			// Will publish is QoS 1
			final boolean isQos1 = willPublishPacket.getQos() == Qos.AT_LEAST_ONCE;
			overallResult &= isQos1;

			logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_QOS, HOST_TOPIC_PHID_DEATH_QOS);
			testResults.put(ID_HOST_TOPIC_PHID_DEATH_QOS, setResult(isQos1, HOST_TOPIC_PHID_DEATH_QOS));

			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS,
					setResult(isQos1, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS));

			logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_QOS, PAYLOADS_STATE_WILL_MESSAGE_QOS);
			testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_QOS, setResult(isQos1, PAYLOADS_STATE_WILL_MESSAGE_QOS));

			// Retain flag is set
			final boolean isRetain = willPublishPacket.getRetain();
			overallResult &= isRetain;

			logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_RETAIN, HOST_TOPIC_PHID_DEATH_RETAIN);
			testResults.put(ID_HOST_TOPIC_PHID_DEATH_RETAIN, setResult(isRetain, HOST_TOPIC_PHID_DEATH_RETAIN));

			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED,
					setResult(isRetain, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED));

			logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN,
					PAYLOADS_STATE_WILL_MESSAGE_RETAIN);
			testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN,
					setResult(isRetain, PAYLOADS_STATE_WILL_MESSAGE_RETAIN));

		}
		return overallResult;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_SUBSCRIBE)
	@SpecAssertion(
			section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED)
	private void checkSubscribes(final boolean shouldBeSubscribed) {
		final List<String> namespaceTopicFilter = List.of(Constants.TOPIC_ROOT_SP_BV_1_0 + "/#");
		String prefix = Constants.TOPIC_ROOT_STATE + "/";
		final List<String> stateTopicFilter = List.of(prefix + hostApplicationId, prefix + "+", prefix + "#");
		boolean isSubscribed = false;
		final boolean nameSpaceMissing = Collections.disjoint(namespaceTopicFilter, subscriptions);
		final boolean stateFilterMissing = Collections.disjoint(stateTopicFilter, subscriptions);
		String addInformation = "";

		if (!nameSpaceMissing && !stateFilterMissing) {
			isSubscribed = true;
			state = HostState.SUBSCRIBED;
		} else {
			if (shouldBeSubscribed) {
				if (nameSpaceMissing) {
					addInformation = " (Namespace topic filter is missing: " + namespaceTopicFilter + ")";
				}
				if (stateFilterMissing) {
					addInformation = " (STATE topic filter is missing. Possibilities: " + stateTopicFilter + ")";
				}
			}
		}
		logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION,
				MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION + addInformation);
		testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION,
				setResult(isSubscribed, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION + addInformation));

		logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_SUBSCRIBE, PAYLOADS_STATE_SUBSCRIBE + addInformation);
		testResults.put(ID_PAYLOADS_STATE_SUBSCRIBE,
				setResult(isSubscribed, PAYLOADS_STATE_SUBSCRIBE + addInformation));

		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED,
				HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED + addInformation);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED,
				setResult(isSubscribed, HOST_TOPIC_PHID_BIRTH_SUB_REQUIRED + addInformation));

		if (shouldBeSubscribed) {
			theTCK.endTest();
		}

	}

	@SpecAssertion(
			section = Sections.BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_MESSAGE)
	@SpecAssertion(
			section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_TOPIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_BIRTH,
			id = ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_QOS)
	@SpecAssertion(
			section = Sections.BIRTH_MESSAGE_STATE,
			id = ID_HOST_TOPIC_PHID_BIRTH_RETAIN)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED)

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD)

	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_BIRTH)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_STATE,
			id = ID_PAYLOADS_STATE_BIRTH_PAYLOAD)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_BIRTH,
			id = ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ)

	private boolean checkBirthMessage(final @NotNull PublishPacket packet) {

		boolean overallResult = false;

		// Topic is STATE/{host_application_id}
		final boolean topicIsValid = packet.getTopic().equals(Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId);
		overallResult = topicIsValid;

		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_TOPIC, HOST_TOPIC_PHID_BIRTH_TOPIC);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_TOPIC, setResult(topicIsValid, HOST_TOPIC_PHID_BIRTH_TOPIC));

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC,
				setResult(topicIsValid, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC));

		// Payload exists
		final boolean payloadExists = packet.getPayload().isPresent();
		overallResult &= payloadExists;

		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD, HOST_TOPIC_PHID_BIRTH_PAYLOAD);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD, setResult(payloadExists, HOST_TOPIC_PHID_BIRTH_PAYLOAD));

		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_MESSAGE, HOST_TOPIC_PHID_BIRTH_MESSAGE);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_MESSAGE, setResult(payloadExists, HOST_TOPIC_PHID_BIRTH_MESSAGE));

		if (payloadExists) {
			ObjectMapper mapper = new ObjectMapper();
			String payloadString = StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString();

			boolean isValidPayload = true;

			JsonNode json = null;
			try {
				json = mapper.readTree(payloadString);
			} catch (Exception e) {
				isValidPayload = false;
			}

			if (json != null) {
				if (json.has("timestamp")) {
					JsonNode timestamp = json.get("timestamp");
					if (timestamp.isLong()) {
						int timestamp_max_diff = 60000; // milliseconds difference allowed
						Date now = new Date();
						long diff = now.getTime() - timestamp.longValue();
						if (diff >= 0 && diff <= timestamp_max_diff) {
							// valid - don't set isValidPayload as it might be false
						} else {
							logger.info("Timestamp diff " + diff);
							isValidPayload = false;
						}
					} else {
						isValidPayload = false;
					}
				} else {
					isValidPayload = false;
				}

				if (json.has("bdSeq")) {
					JsonNode bdseq = json.get("bdSeq");
					if (bdseq.isShort() && bdseq.shortValue() >= 0 || bdseq.shortValue() <= 255) {
						testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ,
								setResult(bdseq.shortValue() == deathBdSeq,
										MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD_BDSEQ));

						testResults.put(ID_PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ,
								setResult(bdseq.shortValue() == deathBdSeq, PAYLOADS_STATE_BIRTH_PAYLOAD_BDSEQ));

						testResults.put(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ,
								setResult(bdseq.shortValue() == deathBdSeq, HOST_TOPIC_PHID_BIRTH_PAYLOAD_BDSEQ));

						testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ,
								setResult(bdseq.shortValue() == deathBdSeq,
										OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD_BDSEQ));
					} else {
						isValidPayload = false;
					}
				} else {
					isValidPayload = false;
				}

				if (json.has("online")) {
					JsonNode online = json.get("online");
					if (online.isBoolean() && online.booleanValue() == true) {
						// valid - don't set isValidPayload as it might be false
					} else {
						isValidPayload = false;
					}
				} else {
					isValidPayload = false;
				}
			}

			testResults.put(ID_PAYLOADS_STATE_BIRTH_PAYLOAD, setResult(isValidPayload, PAYLOADS_STATE_BIRTH_PAYLOAD));

			testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD,
					setResult(isValidPayload, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH_PAYLOAD));

			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD,
					setResult(isValidPayload, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD));

			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD,
					setResult(isValidPayload, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD));
		}

		// Will publish is QoS 1
		final boolean isQos1 = (packet.getQos() == Qos.AT_LEAST_ONCE);
		overallResult &= isQos1;
		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_QOS, HOST_TOPIC_PHID_BIRTH_QOS);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_QOS, setResult(isQos1, HOST_TOPIC_PHID_BIRTH_QOS));

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS,
				setResult(isQos1, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS));

		// Retain flag is set
		final boolean isRetain = (packet.getRetain());
		overallResult &= isRetain;
		logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_RETAIN, HOST_TOPIC_PHID_BIRTH_RETAIN);
		testResults.put(ID_HOST_TOPIC_PHID_BIRTH_RETAIN, setResult(isRetain, HOST_TOPIC_PHID_BIRTH_RETAIN));

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED,
				setResult(isRetain, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED));

		logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_BIRTH, ID_PAYLOADS_STATE_BIRTH);
		testResults.put(ID_PAYLOADS_STATE_BIRTH, setResult(overallResult, ID_PAYLOADS_STATE_BIRTH));
		return overallResult;
	}

	private enum HostState {
		DISCONNECTED,
		CONNECTED,
		SUBSCRIBED,
		PUBLISHED
	}
}
