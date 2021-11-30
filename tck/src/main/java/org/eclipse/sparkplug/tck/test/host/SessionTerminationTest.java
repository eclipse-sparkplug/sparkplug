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
 * This is the primary host Sparkplug session termination test.
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
 */
@SpecVersion(spec = "sparkplug", version = "3.0.0-SNAPSHOT")
public class SessionTerminationTest extends TCKTest {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    private static final @NotNull String PASS = "PASS";
    private static final @NotNull String FAIL = "FAIL";

    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull List<String> testIds = List.of(
    		"operational-behavior-host-application-death-topic",
    		"operational-behavior-host-application-death-payload",
    		"operational-behavior-host-application-death-qos",
    		"operational-behavior-host-application-death-retained",
    		"operational-behavior-host-application-disconnect-intentional"
    );

    private final @NotNull TCK theTCK;
    private final @NotNull String hostApplicationId;
    private final @NotNull List<String> subscriptions = new ArrayList<>();
    
    private String state = "";

    private @Nullable String hostClientId = null;

    public SessionTerminationTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
        logger.info("Primary host session termination test. Parameter: host_application_id host_client_id");
        theTCK = aTCK;

        for (final String testId : testIds) {
            testResults.put(testId, "");
        }

        hostApplicationId = parms[0];
        logger.info("Host application id is " + hostApplicationId);
        
        hostClientId = parms[1];
        logger.info("Host client id is " + hostClientId);
    }

    @Override
    public void endTest() {
        reportResults(testResults);
    }

    @Override
    public String getName() {
        return "SessionTerminationTest";
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

    }

    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
    		id = "operational-behavior-host-application-disconnect-intentional")   
    @Override
    public void disconnect(final @NotNull String clientId, final @NotNull DisconnectPacket packet) {

        if (clientId.equals(hostClientId)) {
        	if (state.equals("death message received")) {
        		testResults.put("operational-behavior-host-application-disconnect-intentional", PASS);
        	}
        	theTCK.endTest();
        }
    }

    @Override
    public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {

        if (clientId.equals(hostClientId)) {
        	if (state.equals("death message received")) {
        		testResults.put("operational-behavior-host-application-disconnect-intentional", FAIL);
        		theTCK.endTest();
        	}
        }
    }

    @Override
    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        logger.info("Primary host session termination test - publish");

        if (clientId.equals(hostClientId)) {
        	if (state.equals("death message received")) {
        		testResults.put("operational-behavior-host-application-disconnect-intentional", FAIL);
        		theTCK.endTest();
        	}
        	else if (checkDeathMessage(packet)) {
        		state = "death message received";
        	}
        }
    }

    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
    		id = "operational-behavior-host-application-death-topic")
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
    		id = "operational-behavior-host-application-death-payload")
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
    		id = "operational-behavior-host-application-death-qos")
    @SpecAssertion(
    		section = Sections.OPERATIONAL_BEHAVIOR_SPARKPLUG_HOST_APPLICATION_SESSION_TERMINATION,
    		id = "operational-behavior-host-application-death-retained")
    private boolean checkDeathMessage(final @NotNull PublishPacket packet) {
    	boolean overallResult = true;

    	//Topic is STATE/{host_application_id}
    	final String wrongTopic;
    	if (packet.getTopic().equals("STATE/" + hostApplicationId)) {
    		wrongTopic = PASS;
    	} else {
    		wrongTopic = FAIL + " (Death topic should be STATE/{host_application_id})";
    		overallResult = false;
    	}
    	testResults.put("operational-behavior-host-application-death-topic", wrongTopic);

    	//Payload exists
    	final String payloadExists;
    	if (packet.getPayload().isPresent()) {
    		payloadExists = PASS;
    	} else {
    		payloadExists = FAIL + " (Death message does not contain a payload with UTF-8 string \"OFFLINE\".)";
    		overallResult = false;
    	}
    	testResults.put("operational-behavior-host-application-death-payload", payloadExists);

    	//Payload message exists
    	if (packet.getPayload().isPresent()) {
    		final String payloadIsOffline;
    		final ByteBuffer payload = packet.getPayload().get();
    		if ("OFFLINE".equals(StandardCharsets.UTF_8.decode(payload).toString())) {
    			payloadIsOffline = PASS;
    		} else {
    			payloadIsOffline = FAIL + " (Payload of death message needs to be a UTF-8 encoded string \"OFFLINE\".)";
    			overallResult = false;
    		}
    		testResults.put("operational-behavior-host-application-death-payload", payloadIsOffline);
    	}

    	//Will publish is QoS 1
    	final String isQos1;
    	if (packet.getQos() == Qos.AT_LEAST_ONCE) {
    		isQos1 = PASS;
    	} else {
    		isQos1 = FAIL + " (Death message must have QoS set to 1.)";
    		overallResult = false;
    	}
    	testResults.put("operational-behavior-host-application-death-qos", isQos1);

    	//Retain flag is set
    	final String isRetain;
    	if (packet.getRetain()) {
    		isRetain = PASS;
    	} else {
    		isRetain = FAIL + " (Death message must have the Retain Flag set to true.)";
    		overallResult = false;
    	}
    	testResults.put("operational-behavior-host-application-death-retained", isRetain);
    	return overallResult;
    }

}