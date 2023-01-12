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

import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL;
import static org.eclipse.sparkplug.tck.test.common.Requirements.OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.common.HostUtils;
import org.eclipse.sparkplug.tck.test.common.StatePayload;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

/**
 * This is the primary host Sparkplug session termination test.
 * <p>
 * We do know the host application id, but there is no requirement on the MQTT client id, which means the first that we
 * know we are dealing with the host application is the receipt of the STATE message.
 * <p>
 * There are two ways of running this test: 1. pass in the host application clientid as a parameter. Start the test with
 * the host application running, then stop the host application. The clientid parameter must match the MQTT clientid
 * being used by the host application. 2. start with the host application disconnected. Connect so we can get the MQTT
 * clientid, then shutdown the host application. The clientid parameter to the test doesn't matter.
 *
 * @author Ian Craggs, Anja Helmbrecht-Schaar
 */
@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0")
public class SessionTerminationTest extends TCKTest {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");

	public static final @NotNull List<String> testIds = List.of(
			ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD, ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED,
			ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION,
			ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN);

	private final @NotNull TCK theTCK;
	private @NotNull String hostApplicationId;
	private Constants.TestStatus state = Constants.TestStatus.NONE;
	private Results.Config config = null;

	private @Nullable String hostClientId = null;

	private TestStatus testStatus = TestStatus.STARTED;
	private long deathTimestamp = -1;

	public SessionTerminationTest(final @NotNull TCK aTCK, final @NotNull String[] params, Results.Config config) {
		logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.config = config;

		if (params.length < 1) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to host session termination test must be: hostApplicationId <hostClientId> where the hostClientid is optional");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		if (params.length == 2) {
			hostClientId = params[1];
		}

		logger.info("Parameters are HostApplicationId: {}, HostClientId: {}", hostApplicationId, hostClientId);

		setTimestamp();
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	@Override
	public String getName() {
		return "Host SessionTermination";
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
			logger.debug("Got a connect from {}", hostApplicationId);

			// get the bdSeq number
			String payloadString =
					StandardCharsets.UTF_8.decode(packet.getWillPublish().get().getPayload().get()).toString();
			logger.debug("Will message STATE payload={}", payloadString);
			StatePayload statePayload = Utils.getHostPayload(payloadString);
			if (statePayload != null && testStatus == TestStatus.STARTED) {
				deathTimestamp = statePayload.getTimestamp().longValue();
				testStatus = TestStatus.CONNECT_RECEIVED;
				hostClientId = clientId;
				logger.info("{} : setting clientid to {}", getName(), hostClientId);
			} else {
				logger.error("Test failed on connect.");
				theTCK.endTest();
			}
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_DEATH,
			id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN)
	@Override
	public void disconnect(final @NotNull String clientId, final @NotNull DisconnectPacket packet) {
		logger.info("Host - {} test - DISCONNECT - clientId: {}, state: {} ", getName(), clientId, state);

		if (hostClientId == null) {
			logger.warn("{} host application clientid is not set", getName());
			return;
		}

		if (clientId.equals(hostClientId)) {
			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL,
					setResult(testStatus == TestStatus.DEATH_RECEIVED,
							OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL));

			testResults.put(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN, setResult(
					testStatus == TestStatus.DEATH_RECEIVED, HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_CLEAN));

			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION, setResult(
					testStatus == TestStatus.DEATH_RECEIVED, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION));
			theTCK.endTest();
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_STATE_DEATH,
			id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET)
	@Override
	public void onDisconnect(DisconnectEventInput disconnectEventInput) {
		logger.info("Host - {} test - onDISCONNECT - clientId: {}, state: {} ", getName(),
				disconnectEventInput.getClientInformation().getClientId(), state);

		if (hostClientId == null) {
			logger.warn("{} host application clientid is not set", getName());
			theTCK.endTest();
		}

		if (disconnectEventInput.getClientInformation().getClientId().equals(hostClientId)) {
			logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL,
					OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL);
			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL,
					setResult(testStatus == TestStatus.DEATH_RECEIVED,
							OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DISCONNECT_INTENTIONAL));

			testResults.put(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET,
					setResult(testStatus == TestStatus.DEATH_RECEIVED,
							HOST_TOPIC_PHID_DEATH_PAYLOAD_TIMESTAMP_DISCONNECT_WITH_NO_DISCONNECT_PACKET));

			testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION, setResult(
					testStatus == TestStatus.DEATH_RECEIVED, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_TERMINATION));
			theTCK.endTest();
		}
	}

	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH)
	@Override
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		logger.info("{} test - PUBLISH - topic: {}, clientId: {}, testStatus: {} ", getName(), packet.getTopic(),
				clientId, testStatus);

		if (hostClientId == null) {
			logger.warn("{} host application clientid is not set", getName());
			return;
		}

		if (clientId.equals(hostClientId) && packet.getTopic().startsWith(Constants.TOPIC_ROOT_STATE)) {
			if (testStatus == TestStatus.CONNECT_RECEIVED) {
				// Looking for a BIRTH - see if this is the original online STATE message after the connect and return
				// early
				StatePayload statePayload =
						Utils.getHostPayload(StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString());

				boolean receivedBirthAfterConnect = statePayload != null;
				testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH,
						setResult(receivedBirthAfterConnect, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH));

				if (statePayload != null) {
					logger.debug("Ignoring original online state message");
					testStatus = TestStatus.BIRTH_RECEIVED;
				} else {
					logger.error("Received unexpected STATE message from {}", clientId);

				}
			} else if (testStatus == TestStatus.BIRTH_RECEIVED || testStatus == TestStatus.STARTED) {
				StatePayload statePayload =
						Utils.getHostPayload(StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString());
				if (statePayload == null || statePayload.isOnline()) {
					logger.debug("Ignoring birth/online STATE message: {}",
							StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString());
					return;
				}

				if (checkDeathMessage(packet)) {
					testStatus = TestStatus.DEATH_RECEIVED;
				} else {
					logger.warn("Failed to validate the death message");
				}
			} else {
				logger.error(
						"Received unexpected message in termination flow with TestStatus={} topic={} and clientId={}",
						testStatus, packet.getTopic(), clientId);
			}
		} else {
			logger.debug("Ignoring message from {}", clientId);
		}
	}

	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS)
	@SpecAssertion(
			section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
			id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED)
	private boolean checkDeathMessage(final @NotNull PublishPacket packet) {
		logger.info("Host - {} test - checkDeathMessage - topic: {}, checkDeathMessage state: {} ", getName(),
				packet.getTopic(), state);
		boolean overallResult = true;

		// Topic is spBv1.0/STATE/{host_application_id}
		final boolean isStateTopic = (packet.getTopic().equals(Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId));
		overallResult &= isStateTopic;
		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC,
				setResult(isStateTopic, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC));

		// Payload exists
		final boolean isPayloadPresent = (packet.getPayload().isPresent());
		overallResult &= isPayloadPresent;
		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD,
				setResult(isPayloadPresent, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_TOPIC));

		// Payload message exists
		StatePayload statePayload =
				Utils.getHostPayload(StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString());
		boolean payloadIsOffline = false;
		if (statePayload != null && !statePayload.isOnline()
				&& statePayload.getTimestamp().longValue() >= deathTimestamp) {
			payloadIsOffline = true;
		}
		overallResult &= payloadIsOffline;

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD,
				setResult(payloadIsOffline, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_PAYLOAD));

		// Will publish is QoS 1
		final boolean isWillQoS1 = (packet.getQos() == Qos.AT_LEAST_ONCE);
		overallResult &= isWillQoS1;

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS,
				setResult(isStateTopic, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_QOS));

		// Retain flag is set
		final boolean isRetain = (packet.getRetain());
		overallResult &= isRetain;

		logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED,
				OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED);
		testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED,
				setResult(isRetain, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_DEATH_RETAINED));

		return overallResult;
	}

	private void setTimestamp() {
		String payload = Utils.getRetained(Constants.TOPIC_ROOT_STATE + '/' + hostApplicationId);
		if (payload != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				StatePayload statePayload = mapper.readValue(payload, StatePayload.class);
				if (statePayload != null && statePayload.isOnline()) {
					deathTimestamp = statePayload.getTimestamp().longValue();
					logger.info("{} setting timestamp to {}", getName(), deathTimestamp);
				}
			} catch (Exception e) {
				logger.error("Failed to handle state topic payload: {}", payload);
			}
		}
	}

	public enum TestStatus {
		STARTED,
		CONNECT_RECEIVED,
		BIRTH_RECEIVED,
		DEATH_RECEIVED;
	}
}
