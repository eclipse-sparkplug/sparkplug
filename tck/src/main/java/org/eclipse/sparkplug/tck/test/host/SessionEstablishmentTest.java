/* ******************************************************************************
 * Copyright (c) 2021 Ian Craggs
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
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the primary host Sparkplug session establishment, and re-establishment test.
 * 
 * We do know the host application id, but there is no requirement on the MQTT client id,
 * which means the first that we know we are dealing with the host application is the receipt
 * of the STATE message.  
 * 
 * Currently this test works if the first MQTT client to connect is the host application.  
 * To make it completely robust means following all connect/subscribe/publish combinations and
 * ruling out the ones that don't match.  There could be many in parallel.
 *
 * @author Ian Craggs
 * @author Lukas Brand
 */
@SpecVersion(spec = "sparkplug", version = "3.0.0-SNAPSHOT")
public class SessionEstablishmentTest extends TCKTest {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    private static final @NotNull String PASS = "PASS";
    private static final @NotNull String FAIL = "FAIL";

    private enum HostState {
        DISCONNECTED,
        CONNECTED,
        SUBSCRIBED,
        PUBLISHED
    }

    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull List<String> testIds = List.of(
    		"intro-sparkplug-host-state",
    		"principles-birth-certificates-order",
            "host-topic-phid-required",
            "host-topic-phid-birth-topic",
            "host-topic-phid-birth-payload",
            "host-topic-phid-birth-qos",
            "host-topic-phid-birth-retain",
            "host-topic-phid-birth-payload-on-off",
            "host-topic-phid-death-topic",
            "host-topic-phid-death-payload",
            "host-topic-phid-death-qos",
            "host-topic-phid-death-retain",
            "host-topic-phid-death-payload-off",
            "message-flow-phid-sparkplug-subscription",
            "message-flow-phid-sparkplug-state-publish",
            "message-flow-phid-sparkplug-clean-session",
            "operational-behavior-host-application-connect-will",
            "operational-behavior-host-application-connect-will-payload",
            "operational-behavior-host-application-connect-will-qos",
            "operational-behavior-host-application-connect-will-retained",
            "operational-behavior-host-application-connect-birth",
            "operational-behavior-host-application-connect-birth-topic",
            "operational-behavior-host-application-connect-birth-payload",
            "operational-behavior-host-application-connect-birth-qos",
            "operational-behavior-host-application-connect-birth-retained",
            "components-ph-state",
            "host-topic-phid-birth-message",
            "payloads-state-will-message",
            "payloads-state-will-message-qos",
            "payloads-state-subscribe",
            "payloads-state-will-message-retain",
            "payloads-state-will-message-payload",
            "payloads-state-birth"
    );

    private final @NotNull TCK theTCK;
    private final @NotNull String hostApplicationId;
    private final @NotNull List<String> subscriptions = new ArrayList<>();


    private @NotNull HostState state = HostState.DISCONNECTED;
    private @Nullable String hostClientId = null;

    public SessionEstablishmentTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
        logger.info("Primary host session establishment test. Parameter: host_application_id");
        theTCK = aTCK;

        for (final String testId : testIds) {
            testResults.put(testId, "");
        }

        hostApplicationId = parms[0];
        logger.info("Host application id is " + hostApplicationId);
    }

    @Override
    public void endTest() {
        reportResults(testResults);
    }

    @Override
    public String getName() {
        return "SessionEstablishment";
    }

    @Override
    public String[] getTestIds() {
        return testIds.toArray(new String[0]);
    }

    public Map<String, String> getResults() {
        return testResults;
    }

    @Override
    public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
        logger.info("Primary host session establishment test - connect");

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
    @SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
            id = "message-flow-phid-sparkplug-subscription")
    public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {
        logger.info("Primary host session establishment test - subscribe");

        //ignore messages before connect
        if (hostClientId == null) {
        	return;
        }

        if (hostClientId.equals(clientId)) {
            //Subscribe is after connect (and allow additional subscriptions)
            if (state != HostState.CONNECTED && state != HostState.SUBSCRIBED) {
                final String notConnected = FAIL +
                        " (Host application needs to subscribe after connect. Is in state: " + state + ")";
                testResults.put("message-flow-phid-sparkplug-subscription", notConnected);
                theTCK.endTest();
                return;
            }

            subscriptions.addAll(packet.getSubscriptions().stream()
                    .map(Subscription::getTopicFilter)
                    .collect(Collectors.toList()));

            checkSubscribes(false);
        }
    }

    @Override
    @SpecAssertion(
    		section = Sections.PRINCIPLES_BIRTH_AND_DEATH_CERTIFICATES,
    		id = "principles-birth-certificates-order")    
    
    @SpecAssertion(
    		section = Sections.INTRODUCTION_HOST_APPLICATIONS,
    		id = "intro-sparkplug-host-state")
    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT,
            id = "message-flow-phid-sparkplug-state-publish")
    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-birth")
    
    @SpecAssertion(section = Sections.COMPONENTS_SPARKPLUG_HOST_APPLICATION,
            id = "components-ph-state")
    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        logger.info("Primary host session establishment test - publish");

        //ignore messages before connect
        if (hostClientId == null) {
        	return;
        }

        if (hostClientId.equals(clientId)) {
            //Check if subscribe completed
            checkSubscribes(true);

            //Publish is after subscribe (and theoretically allow additional publishes)
            if (state != HostState.SUBSCRIBED && state != HostState.PUBLISHED) {
                final String notConnected = FAIL +
                        " (Host application needs to publish after subscribe. Is in state: " + state + ")";
                testResults.put("message-flow-phid-sparkplug-state-publish", notConnected);
                theTCK.endTest();
                return;
            }

            final boolean overallPass = checkBirthMessage(packet);

            if (overallPass) {
                state = HostState.PUBLISHED;
                testResults.put("principles-birth-certificates-order", PASS);
                testResults.put("message-flow-phid-sparkplug-state-publish", PASS);
                testResults.put("operational-behavior-host-application-connect-birth", PASS);
            } else {
            	testResults.put("principles-birth-certificates-order", FAIL);
            	testResults.put("operational-behavior-host-application-connect-birth", FAIL);
                logger.error("Test failed on published.");
                theTCK.endTest();
            }

            //TODO: test reconnect
            testResults.put("components-ph-state", PASS);
            testResults.put("intro-sparkplug-host-state", PASS);
        }

        // TODO: now we can disconnect the client and allow it to reconnect and go throught the
        // session re-establishment phases.  It would be nice to be able to do this at after a
        // short arbitrary interval, but I haven't worked out a good way of doing that yet (assuming
        // that a sleep here is not a good idea).  Using a PING interceptor could be one way but
        // we probably can't rely on any particular keepalive interval values.

        theTCK.endTest();
    }


    @SpecAssertion(
    		section = Sections.TOPICS_DEATH_MESSAGE_STATE, 
    		id = "host-topic-phid-required")
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-will") 
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_PRIMARY_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "message-flow-phid-sparkplug-clean-session")
    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE, 
    		id = "payloads-state-will-message")
    private boolean checkConnectMessage(final @NotNull ConnectPacket packet) {
        boolean overallResult = true;

        //Clean session is enabled
        final String isCleanSession;
        if (packet.getCleanStart()) {
            isCleanSession = PASS;
        } else {
            isCleanSession = FAIL + " (Clean session should be set to true.)";
            overallResult = false;
        }
        testResults.put("message-flow-phid-sparkplug-clean-session", isCleanSession);

        //Will exists
        final String willExists;
        if (packet.getWillPublish().isPresent()) {
            willExists = PASS;
        } else {
            willExists = FAIL + " (Will message is needed.)";
            overallResult = false;
        }
        testResults.put("host-topic-phid-required", willExists);
        testResults.put("operational-behavior-host-application-connect-will", willExists);
        testResults.put("payloads-state-will-message", willExists);
        return overallResult;
    }

    @SpecAssertion(
    		section = Sections.TOPICS_DEATH_MESSAGE_STATE, 
    		id = "host-topic-phid-death-topic")
    @SpecAssertion(
    		section = Sections.TOPICS_DEATH_MESSAGE_STATE, 
    		id = "host-topic-phid-death-payload")
    @SpecAssertion(
    		section = Sections.PAYLOADS_DESC_STATE_DEATH,
    		id = "host-topic-phid-death-payload-off")
    @SpecAssertion(
    		section = Sections.TOPICS_DEATH_MESSAGE_STATE, 
    		id = "host-topic-phid-death-qos")
    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-will-topic") 
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-will-payload") 
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-will-qos")   
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-will-retained")      
    
    @SpecAssertion(
    		section = Sections.TOPICS_DEATH_MESSAGE_STATE,
    		id = "host-topic-phid-death-retain")
    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE, 
    		id = "payloads-state-will-message-qos")
    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE, 
    		id = "payloads-state-will-message-retain")
    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE, 
    		id = "payloads-state-will-message-payload")
    
    private boolean checkDeathMessage(final @NotNull ConnectPacket packet) {
        boolean overallResult = true;

        final Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
        if (willPublishPacketOptional.isPresent()) {
            final WillPublishPacket willPublishPacket = willPublishPacketOptional.get();

            //Topic is STATE/{host_application_id}
            final String wrongTopic;
            if (willPublishPacket.getTopic().equals("STATE/" + hostApplicationId)) {
                wrongTopic = PASS;
            } else {
                wrongTopic = FAIL + " (Birth topic should be STATE/{host_application_id})";
                overallResult = false;
            }
            testResults.put("operational-behavior-host-application-connect-will-topic", wrongTopic);
            testResults.put("host-topic-phid-death-topic", wrongTopic);

            //Payload exists
            final String payloadExists;
            if (willPublishPacket.getPayload().isPresent()) {
                payloadExists = PASS;
            } else {
                payloadExists = FAIL + " (Will message does not contain a payload with UTF-8 string \"OFFLINE\".)";
                overallResult = false;
            }
            testResults.put("host-topic-phid-death-payload", payloadExists);

            //Payload message exists
            if (willPublishPacket.getPayload().isPresent()) {
                final String payloadIsOffline;
                final ByteBuffer payload = willPublishPacket.getPayload().get();
                if ("OFFLINE".equals(StandardCharsets.UTF_8.decode(payload).toString())) {
                    payloadIsOffline = PASS;
                } else {
                    payloadIsOffline = FAIL + " (Payload of will message needs to be a UTF-8 encoded string \"OFFLINE\".)";
                    overallResult = false;
                }
                testResults.put("host-topic-phid-death-payload-off", payloadIsOffline);
                testResults.put("operational-behavior-host-application-connect-will-payload", payloadIsOffline);
                testResults.put("payloads-state-will-message-payload", payloadIsOffline);
            }

            //Will publish is QoS 1
            final String isQos1;
            if (willPublishPacket.getQos() == Qos.AT_LEAST_ONCE) {
                isQos1 = PASS;
            } else {
                isQos1 = FAIL + " (Will message must have QoS set to 1.)";
                overallResult = false;
            }
            testResults.put("host-topic-phid-death-qos", isQos1);
            testResults.put("operational-behavior-host-application-connect-will-qos", isQos1);
            testResults.put("payloads-state-will-message-qos", isQos1);

            //Retain flag is set
            final String isRetain;
            if (willPublishPacket.getRetain()) {
                isRetain = PASS;
            } else {
                isRetain = FAIL + " (Will message must have the Retain Flag set to true.)";
                overallResult = false;
            }
            testResults.put("host-topic-phid-death-retain", isRetain);
            testResults.put("operational-behavior-host-application-connect-will-retained", isRetain);
            testResults.put("payloads-state-will-message-retain", isRetain);
        } else {
            overallResult = false;
        }
        return overallResult;
    }

    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE,
            id = "payloads-state-subscribe")
    private void checkSubscribes(final boolean shouldBeSubscribed) {
        final List<String> namespaceTopicFilter = List.of("spBv1.0/#");
        final List<String> stateTopicFilter = List.of("STATE/" + hostApplicationId, "STATE/+", "STATE/#");

        if (!Collections.disjoint(namespaceTopicFilter, subscriptions)
                && !Collections.disjoint(stateTopicFilter, subscriptions)) {
            testResults.put("message-flow-phid-sparkplug-subscription", PASS);
            testResults.put("message-state-subscribe", PASS);
            state = HostState.SUBSCRIBED;
        } else if (shouldBeSubscribed) {
            final String missingSubscribe;
            if (Collections.disjoint(namespaceTopicFilter, subscriptions)) {
                missingSubscribe = FAIL + " (Namespace topic filter is missing: " + namespaceTopicFilter + ")";
            } else {
                missingSubscribe = FAIL + " (STATE topic filter is missing. Possibilities: " + stateTopicFilter + ")";
            }
            testResults.put("message-flow-phid-sparkplug-subscription", missingSubscribe);
            testResults.put("payloads-state-subscribe", missingSubscribe);
            theTCK.endTest();
        }
    }

    @SpecAssertion(
    		section = Sections.BIRTH_MESSAGE_STATE,
    		id = "host-topic-phid-birth-message")
    @SpecAssertion(
    		section = Sections.TOPICS_BIRTH_MESSAGE_STATE,
    		id = "host-topic-phid-birth-topic")
    @SpecAssertion(
    		section = Sections.TOPICS_BIRTH_MESSAGE_STATE, 
    		id = "host-topic-phid-birth-payload")
    @SpecAssertion(
    		section = Sections.TOPICS_BIRTH_MESSAGE_STATE, 
    		id = "host-topic-phid-birth-qos")
    @SpecAssertion(
    		section = Sections.TOPICS_BIRTH_MESSAGE_STATE, 
    		id = "host-topic-phid-birth-retain")
    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-birth-topic")
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-birth-payload")    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-birth-qos")    
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_ESTABLISHMENT, 
    		id = "operational-behavior-host-application-connect-birth-retained")
    
    @SpecAssertion(
    		section = Sections.PAYLOADS_B_STATE, 
    		id = "payloads-state-birth")
    @SpecAssertion(
    		section = Sections.PAYLOADS_DESC_STATE_BIRTH, 
    		id = "host-topic-phid-birth-payload-on-off")
    private boolean checkBirthMessage(final @NotNull PublishPacket packet) {
        boolean overallResult = true;

        //Topic is STATE/{host_application_id}
        final String wrongTopic;
        if (packet.getTopic().equals("STATE/" + hostApplicationId)) {
            wrongTopic = PASS;
        } else {
            wrongTopic = FAIL + " (Birth topic should be STATE/{host_application_id})";
            overallResult = false;
        }
        testResults.put("host-topic-phid-birth-topic", wrongTopic);
        testResults.put("operational-behavior-host-application-connect-birth-topic", wrongTopic);
        
        //Payload exists
        final String payloadExists;
        if (packet.getPayload().isPresent()) {
            payloadExists = PASS;
        } else {
            payloadExists = FAIL + " (Birth message does not contain a payload with UTF-8 string \"ONLINE\".)";
            overallResult = false;
        }
        testResults.put("host-topic-phid-birth-payload", payloadExists);
        testResults.put("host-topic-phid-birth-message", payloadExists);
        testResults.put("birth_message_state host-topic-phid-birth-payload", payloadExists);

        //Payload message is ONLINE
        if (packet.getPayload().isPresent()) {
            final String payloadIsOnline;
            final ByteBuffer payload = packet.getPayload().get();
            if ("ONLINE".equals(StandardCharsets.UTF_8.decode(payload).toString())) {
                payloadIsOnline = PASS;
            } else {
                payloadIsOnline = FAIL + " (Payload of birth message needs to be a UTF-8 encoded string \"ONLINE\".)";
                overallResult = false;
            }
            testResults.put("host-topic-phid-birth-payload-on-off", payloadIsOnline);
            testResults.put("operational-behavior-host-application-connect-birth-payload", payloadIsOnline);
        }

        //Will publish is QoS 1
        final String isQos1;
        if (packet.getQos() == Qos.AT_LEAST_ONCE) {
            isQos1 = PASS;
        } else {
            isQos1 = FAIL + " (Birth message must have QoS set to 1.)";
            overallResult = false;
        }
        testResults.put("host-topic-phid-birth-qos", isQos1);
        testResults.put("operational-behavior-host-application-connect-birth-qos", isQos1);

        //Retain flag is set
        final String isRetain;
        if (packet.getRetain()) {
            isRetain = PASS;
        } else {
            isRetain = FAIL + " (Birth message must have the Retain Flag set to true.)";
            overallResult = false;
        }
        testResults.put("host-topic-phid-birth-retain", isRetain);
        testResults.put("operational-behavior-host-application-connect-birth-retained", isRetain);
        
        testResults.put("payloads-state-birth", overallResult ? "TRUE" : "FAIL");
        return overallResult;
    }

}