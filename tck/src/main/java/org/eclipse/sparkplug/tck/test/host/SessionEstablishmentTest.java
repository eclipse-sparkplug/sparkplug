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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

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
    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull ArrayList<String> testIds = new ArrayList<>();
    private @NotNull String hostApplicationId;

    private final @NotNull List<String> subscriptions = new ArrayList<>();
    private @NotNull HostState state = HostState.DISCONNECTED;
    private @Nullable String hostClientId = null;
    private TCK theTCK = null;

    public SessionEstablishmentTest(final @NotNull TCK aTCK, final @NotNull String[] params) {
        logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(params));
        theTCK = aTCK;

        hostApplicationId = params[0];
        logger.info("{} Parameters are HostApplicationId: {}", getName(), hostApplicationId);
    }

    @Override
    public void endTest() {
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "SessionEstablishment";
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
        logger.info("Primary {} - connect", getName());

        boolean overallPass = checkConnectMessage(packet);
        overallPass = overallPass && checkDeathMessage(packet);

        if (overallPass) {
            hostClientId = clientId;
            state = HostState.CONNECTED;
        } else {
            logger.error("Test failed on connect.");
            theTCK.endTest();
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
        logger.info("Primary {} - subscribe", getName());

        // ignore messages before connect
        if (hostClientId == null) {
            return;
        }

        if (hostClientId.equals(clientId)) {
            // Subscribe is after connect (and allow additional subscriptions) ;
            if (state != HostState.CONNECTED && state != HostState.SUBSCRIBED) {
                logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION);
                testIds.add(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION);
                testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, setResult(false, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION));
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
    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        logger.info("Primary - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);

        // ignore messages before connect
        if (hostClientId == null) {
            return;
        }

        if (hostClientId.equals(clientId)) {
            // Check if subscribe completed
            checkSubscribes(true);

            // Publish is after subscribe (and theoretically allow additional publishes)
            logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH);
            testIds.add(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH);

            if (state != HostState.SUBSCRIBED && state != HostState.PUBLISHED) {
                testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH, setResult(false, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH));
                theTCK.endTest();
                return;
            }

            final boolean overallPass = checkBirthMessage(packet);

            logger.debug("Check Req: {}:{}.", ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER, PRINCIPLES_BIRTH_CERTIFICATES_ORDER);
            testIds.add(ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER);
            testResults.put(ID_PRINCIPLES_BIRTH_CERTIFICATES_ORDER, setResult(overallPass, PRINCIPLES_BIRTH_CERTIFICATES_ORDER));

            logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH);
            testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH);
            testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH, setResult(overallPass, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH));

            if (overallPass) {
                state = HostState.PUBLISHED;
                testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH, setResult(true, MESSAGE_FLOW_PHID_SPARKPLUG_STATE_PUBLISH));
            } else {
                logger.error("Test failed on published.");
                theTCK.endTest();
            }

            // TODO: test reconnect
            logger.debug("Check Req: {}:{}.", ID_COMPONENTS_PH_STATE, COMPONENTS_PH_STATE);
            testIds.add(ID_COMPONENTS_PH_STATE);
            testResults.put(ID_COMPONENTS_PH_STATE, setResult(true, COMPONENTS_PH_STATE));

            logger.debug("Check Req: {}:{}.", ID_INTRO_SPARKPLUG_HOST_STATE, INTRO_SPARKPLUG_HOST_STATE);
            testIds.add(ID_INTRO_SPARKPLUG_HOST_STATE);
            testResults.put(ID_INTRO_SPARKPLUG_HOST_STATE, setResult(true, INTRO_SPARKPLUG_HOST_STATE));
        }

        // TODO: now we can disconnect the client and allow it to reconnect and go throught the
        // session re-establishment phases. It would be nice to be able to do this at after a
        // short arbitrary interval, but I haven't worked out a good way of doing that yet (assuming
        // that a sleep here is not a good idea). Using a PING interceptor could be one way but
        // we probably can't rely on any particular keepalive interval values.

        theTCK.endTest();
    }

    @SpecAssertion(
            section = Sections.TOPICS_DEATH_MESSAGE_STATE,
            id = ID_HOST_TOPIC_PHID_REQUIRED)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT,
            id = ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
            id = ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_STATE,
            id = ID_PAYLOADS_STATE_WILL_MESSAGE)
    private boolean checkConnectMessage(final @NotNull ConnectPacket packet) {
        logger.info("Primary - {} test - CONNECT - state: {}, checkConnectMessage  ", getName(), state);

        boolean overallResult = false;

        // Clean session is enabled
        final boolean isCleanSession = packet.getCleanStart();
        overallResult = isCleanSession;
        logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION, MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION);
        testIds.add(ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION);
        testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_CLEAN_SESSION, setResult(isCleanSession, INTRO_SPARKPLUG_HOST_STATE));

        // Will exists
        final boolean willExists = packet.getWillPublish().isPresent();
        overallResult &= willExists;

        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_REQUIRED, HOST_TOPIC_PHID_REQUIRED);
        testIds.add(ID_HOST_TOPIC_PHID_REQUIRED);
        testResults.put(ID_HOST_TOPIC_PHID_REQUIRED, setResult(willExists, HOST_TOPIC_PHID_REQUIRED));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL, setResult(willExists, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE, PAYLOADS_STATE_WILL_MESSAGE);
        testIds.add(ID_PAYLOADS_STATE_WILL_MESSAGE);
        testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE, setResult(willExists, PAYLOADS_STATE_WILL_MESSAGE));
        return overallResult;
    }

    @SpecAssertion(
            section = Sections.TOPICS_DEATH_MESSAGE_STATE,
            id = ID_HOST_TOPIC_PHID_DEATH_TOPIC)
    @SpecAssertion(
            section = Sections.TOPICS_DEATH_MESSAGE_STATE,
            id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_STATE_DEATH,
            id = ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF)
    @SpecAssertion(
            section = Sections.TOPICS_DEATH_MESSAGE_STATE,
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
            section = Sections.TOPICS_DEATH_MESSAGE_STATE,
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

            // Topic is STATE/{host_application_id}
            final boolean topicIsValid = willPublishPacket.getTopic().equals(TopicConstants.TOPIC_ROOT_STATE + "/" + hostApplicationId);
            overallResult = topicIsValid;

            logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC);
            testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC);
            testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC, setResult(topicIsValid, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_TOPIC));

            logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_TOPIC, HOST_TOPIC_PHID_DEATH_TOPIC);
            testIds.add(ID_HOST_TOPIC_PHID_DEATH_TOPIC);
            testResults.put(ID_HOST_TOPIC_PHID_DEATH_TOPIC, setResult(topicIsValid, HOST_TOPIC_PHID_DEATH_TOPIC));

            // Payload exists
            final boolean payloadExists = willPublishPacket.getPayload().isPresent();
            overallResult &= payloadExists;

            logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_PAYLOAD, HOST_TOPIC_PHID_DEATH_PAYLOAD);
            testIds.add(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD);
            testResults.put(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD, setResult(payloadExists, HOST_TOPIC_PHID_DEATH_PAYLOAD));


            // Payload is OFFLINE Message
            if (payloadExists) {
                final boolean payloadIsOffline = "OFFLINE".equals(StandardCharsets.UTF_8.decode(willPublishPacket.getPayload().get()).toString());
                overallResult &= payloadIsOffline;

                logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF, ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF);
                testIds.add(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF);
                testResults.put(ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF, setResult(payloadIsOffline, ID_HOST_TOPIC_PHID_DEATH_PAYLOAD_OFF));

                logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD);
                testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD);
                testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD, setResult(payloadIsOffline, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_PAYLOAD));

                logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD, PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD);
                testIds.add(ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD);
                testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD, setResult(payloadIsOffline, PAYLOADS_STATE_WILL_MESSAGE_PAYLOAD));
            }

            // Will publish is QoS 1
            final boolean isQos1 = willPublishPacket.getQos() == Qos.AT_LEAST_ONCE;
            overallResult &= isQos1;

            logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_QOS, HOST_TOPIC_PHID_DEATH_QOS);
            testIds.add(ID_HOST_TOPIC_PHID_DEATH_QOS);
            testResults.put(ID_HOST_TOPIC_PHID_DEATH_QOS, setResult(isQos1, HOST_TOPIC_PHID_DEATH_QOS));

            logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS);
            testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS);
            testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS, setResult(isQos1, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_QOS));

            logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_QOS, PAYLOADS_STATE_WILL_MESSAGE_QOS);
            testIds.add(ID_PAYLOADS_STATE_WILL_MESSAGE_QOS);
            testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_QOS, setResult(isQos1, PAYLOADS_STATE_WILL_MESSAGE_QOS));

            // Retain flag is set
            final boolean isRetain = willPublishPacket.getRetain();
            overallResult &= isRetain;

            logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_DEATH_RETAIN, HOST_TOPIC_PHID_DEATH_RETAIN);
            testIds.add(ID_HOST_TOPIC_PHID_DEATH_RETAIN);
            testResults.put(ID_HOST_TOPIC_PHID_DEATH_RETAIN, setResult(isRetain, HOST_TOPIC_PHID_DEATH_RETAIN));

            logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED);
            testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED);
            testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED, setResult(isRetain, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_WILL_RETAINED));

            logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN, PAYLOADS_STATE_WILL_MESSAGE_RETAIN);
            testIds.add(ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN);
            testResults.put(ID_PAYLOADS_STATE_WILL_MESSAGE_RETAIN, setResult(isRetain, PAYLOADS_STATE_WILL_MESSAGE_RETAIN));

        }
        return overallResult;
    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_STATE,
            id = ID_PAYLOADS_STATE_SUBSCRIBE)
    private void checkSubscribes(final boolean shouldBeSubscribed) {
        final List<String> namespaceTopicFilter = List.of("spBv1.0/#");
        final List<String> stateTopicFilter = List.of(
                TopicConstants.TOPIC_ROOT_STATE + "/" + hostApplicationId,
                TopicConstants.TOPIC_ROOT_STATE + "/+",
                TopicConstants.TOPIC_ROOT_STATE + "/#");

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
        logger.debug("Check Req: {}:{}.", ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION + addInformation);
        testIds.add(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION);
        testResults.put(ID_MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION, setResult(isSubscribed, MESSAGE_FLOW_PHID_SPARKPLUG_SUBSCRIPTION + addInformation));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_SUBSCRIBE, PAYLOADS_STATE_SUBSCRIBE + addInformation);
        testIds.add(ID_PAYLOADS_STATE_SUBSCRIBE);
        testResults.put(ID_PAYLOADS_STATE_SUBSCRIBE, setResult(isSubscribed, PAYLOADS_STATE_SUBSCRIBE + addInformation));

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
            section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
            id = ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD)
    @SpecAssertion(
            section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
            id = ID_HOST_TOPIC_PHID_BIRTH_QOS)
    @SpecAssertion(
            section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
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
            section = Sections.PAYLOADS_B_STATE,
            id = ID_PAYLOADS_STATE_BIRTH)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_STATE_BIRTH,
            id = ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF)
    private boolean checkBirthMessage(final @NotNull PublishPacket packet) {

        boolean overallResult = false;

        // Topic is STATE/{host_application_id}
        final boolean topicIsValid = packet.getTopic().equals(TopicConstants.TOPIC_ROOT_STATE + "/" + hostApplicationId);
        overallResult = topicIsValid;

        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_TOPIC, HOST_TOPIC_PHID_BIRTH_TOPIC);
        testIds.add(ID_HOST_TOPIC_PHID_BIRTH_TOPIC);
        testResults.put(ID_HOST_TOPIC_PHID_BIRTH_TOPIC, setResult(topicIsValid, HOST_TOPIC_PHID_BIRTH_TOPIC));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC, setResult(topicIsValid, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_TOPIC));

        // Payload exists
        final boolean payloadExists = packet.getPayload().isPresent();
        overallResult &= payloadExists;

        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD, HOST_TOPIC_PHID_BIRTH_PAYLOAD);
        testIds.add(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD);
        testResults.put(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD, setResult(payloadExists, HOST_TOPIC_PHID_BIRTH_PAYLOAD));

        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_MESSAGE, HOST_TOPIC_PHID_BIRTH_MESSAGE);
        testIds.add(ID_HOST_TOPIC_PHID_BIRTH_MESSAGE);
        testResults.put(ID_HOST_TOPIC_PHID_BIRTH_MESSAGE, setResult(payloadExists, HOST_TOPIC_PHID_BIRTH_MESSAGE));


        // Payload message is ONLINE
        // Payload is OFFLINE Message
        if (payloadExists) {
            final boolean payloadIsOnline = "ONLINE".equals(StandardCharsets.UTF_8.decode(packet.getPayload().get()).toString());
            overallResult &= payloadIsOnline;

            logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF, HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF);
            testIds.add(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF);
            testResults.put(ID_HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF, setResult(payloadIsOnline, HOST_TOPIC_PHID_BIRTH_PAYLOAD_ON_OFF));

            logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD);
            testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD);
            testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD, setResult(payloadIsOnline, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_PAYLOAD));
        }

        // Will publish is QoS 1
        final boolean isQos1 = (packet.getQos() == Qos.AT_LEAST_ONCE);
        overallResult &= isQos1;
        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_QOS, HOST_TOPIC_PHID_BIRTH_QOS);
        testIds.add(ID_HOST_TOPIC_PHID_BIRTH_QOS);
        testResults.put(ID_HOST_TOPIC_PHID_BIRTH_QOS, setResult(isQos1, HOST_TOPIC_PHID_BIRTH_QOS));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS, setResult(isQos1, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_QOS));


        // Retain flag is set
        final boolean isRetain = (packet.getRetain());
        overallResult &= isRetain;
        logger.debug("Check Req: {}:{}.", ID_HOST_TOPIC_PHID_BIRTH_RETAIN, HOST_TOPIC_PHID_BIRTH_RETAIN);
        testIds.add(ID_HOST_TOPIC_PHID_BIRTH_RETAIN);
        testResults.put(ID_HOST_TOPIC_PHID_BIRTH_RETAIN, setResult(isRetain, HOST_TOPIC_PHID_BIRTH_RETAIN));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED, setResult(isRetain, OPERATIONAL_BEHAVIOR_HOST_APPLICATION_CONNECT_BIRTH_RETAINED));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_STATE_BIRTH, ID_PAYLOADS_STATE_BIRTH);
        testIds.add(ID_PAYLOADS_STATE_BIRTH);
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