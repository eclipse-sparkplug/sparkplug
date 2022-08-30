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
 *    Anja Helmbrecht-Schaar
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.host;

/*
 * This is the primary host Sparkplug edge node death test.
 *
 * When an edge node dies, the host application should set the data
 * for the edge node and connected devices to stale.
 *
 * @author Ian Craggs, Anja Helmbrecht-Schaar
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import com.hivemq.extension.sdk.api.services.publish.Publish;
import com.hivemq.extension.sdk.api.services.publish.PublishService;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.common.Utils.TestStatus;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_DEVICE_CONTROL_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Utils.TestStatus.KILLING_DEVICE;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class EdgeNodeDeathTest extends TCKTest {

    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull ArrayList<String> testIds = new ArrayList<>();
    private @NotNull String deviceId;
    private @NotNull String edgeNodeId;
    private @NotNull String hostApplicationId;

    private TestStatus state = TestStatus.NONE;
    private TCK theTCK = null;

    private PublishService publishService = Services.publishService();

	public EdgeNodeDeathTest(TCK aTCK, String[] params) {
		logger.info("Primary host {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;

		if (params.length < 3) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to host edge node death test must be: host_application_id edge_node_id device_id");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		edgeNodeId = params[1];
		deviceId = params[2];
		logger.info("Parameters are HostApplicationId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, edgeNodeId,
				deviceId);

		final AtomicBoolean hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);

		if (!hostOnline.get()) {
			logger.info("HostApplication {} not online - test not started.", hostApplicationId);
			return;
		}

		// First we have to connect an edge node and device.
		// We do this by sending an MQTT control message to the TCK device utility.
		state = TestStatus.CONNECTING_DEVICE;
		String payload = "NEW DEVICE " + hostApplicationId + " " + edgeNodeId + " " + deviceId;
		Publish message = Builders.publish().topic(TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
				.payload(ByteBuffer.wrap(payload.getBytes())).build();
		logger.info("Requesting new device creation.  Edge node id: " + edgeNodeId + " device id: " + deviceId);
		publishService.publish(message);
	}

    @Override
    public void endTest(Map<String, String> results) {
    	testResults.putAll(results);
        state = TestStatus.NONE;
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "Sparkplug Host EdgeNode Death Test";
    }

    public String[] getTestIds() {
        return testIds.toArray(new String[0]);
    }

    public Map<String, String> getResults() {
        return testResults;
    }

    @Override
    public void connect(String clientId, ConnectPacket packet) {
        // TODO Auto-generated method stub
    }

    @Override
    public void disconnect(String clientId, DisconnectPacket packet) {
        // TODO Auto-generated method stub
    }

    @Override
    public void subscribe(String clientId, SubscribePacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void publish(String clientId, PublishPacket packet) {
        logger.info("Host - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);
        if (packet.getTopic().equals(TCK_LOG_TOPIC)) {
            String payload = null;
            ByteBuffer bpayload = packet.getPayload().orElseGet(null);
            if (bpayload != null) {
                payload = StandardCharsets.UTF_8.decode(bpayload).toString();
            }
            if (payload == null) {
                logger.error("EdgeNodeDeathTest: no payload");
                return;
            }

            if (payload.equals("Device " + deviceId + " successfully created")) {
                logger.info("EdgeNodeDeathTest: Device was created");

                // We do this by sending an MQTT control message to the TCK device utility.
                state = KILLING_DEVICE;
                String disconnectMsg = "DISCONNECT_EDGE_NODE " + hostApplicationId + " " + edgeNodeId;
                Publish publish = Builders.publish().topic(TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
                        .payload(ByteBuffer.wrap(disconnectMsg.getBytes()))
                        .build();
                logger.info("Requesting edge node death. Edge node id: " + edgeNodeId);
                publishService.publish(publish);
            }
        }
    }
}