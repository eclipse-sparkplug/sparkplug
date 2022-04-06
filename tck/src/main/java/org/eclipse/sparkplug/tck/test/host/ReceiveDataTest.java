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

/*
 * This is the primary host Sparkplug receive data test.  Data can be received from edge
 * nodes and devices.
 *
 * We manufacture some data events to be received by the primary host.
 *
 * To verify that they have been handled correctly, we have to rely on the
 * user running the tests to report the results.
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
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
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

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class ReceiveDataTest extends TCKTest {
    private static final String EDGE_METRIC = "TCK_metric/Int32";
    private static final String DEVICE_METRIC = "Inputs/0";
    private static Logger logger = LoggerFactory.getLogger("Sparkplug");
    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull ArrayList<String> testIds = new ArrayList<>();
    private @NotNull String deviceId;
    private @NotNull String groupId;
    private @NotNull String edgeNodeId;
    private @NotNull String hostApplicationId;


    private TestStatus state = TestStatus.NONE;
    private TCK theTCK = null;

    private PublishService publishService = Services.publishService();

    public ReceiveDataTest(TCK aTCK, String[] params) {
        logger.info("Primary host receive data test: {} Parameters: {} ", getName(), Arrays.asList(params));
        theTCK = aTCK;

        if (params.length < 4) {
            logger.error("Parameters to Host receive data test must be: groupId edgeNodeId deviceId");
            return;
        }
        hostApplicationId = params[0];
        groupId = params[1];
        edgeNodeId = params[2];
        deviceId = params[3];
        logger.info("Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId, edgeNodeId, deviceId);

        final AtomicBoolean hostOnline = Utils.checkHostApplicationIsOnline(hostApplicationId);

        if (!hostOnline.get()) {
            logger.info("HostApplication {} not online - test not started.", hostApplicationId);
            return;
        }

        // First we have to connect an edge node and device.
        // We do this by sending an MQTT control message to the TCK device utility.
        // ONLY DO THIS IF THE EDGE/DEVICE haven't already been created!!
        state = TestStatus.CONNECTING_DEVICE;
        String payload = "NEW DEVICE " + hostApplicationId + " " + groupId + " " + edgeNodeId + " " + deviceId;
        Publish message = Builders.publish()
                .topic(TopicConstants.TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
                .payload(ByteBuffer.wrap(payload.getBytes()))
                .build();
        logger.info("Requesting new device creation. GroupId: {}, EdgeNodeId: {}, DeviceId: {}", groupId, edgeNodeId, deviceId);
        publishService.publish(message);
    }

    public void endTest() {
        state = TestStatus.NONE;
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "Sparkplug Host Receive Data Test";
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

        if (packet.getTopic().equals(TopicConstants.TCK_LOG_TOPIC)) {
            String payload = null;
            ByteBuffer byteBuffer = packet.getPayload().orElseGet(null);
            if (byteBuffer != null) {
                payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
            }

            if (payload == null) {
                return;
            }

            final String[] split = payload.split(" ");
            logger.info("{}: Payload contains: {} in state: {} ", getName(), split, state);

            if (state == TestStatus.CONNECTING_DEVICE
                    && payload.equals("Device " + deviceId + " successfully created")) {

                logger.info("{}: Device was created", getName());

                // Now tell the device simulator to send some data from the edge node
                logger.info("Requesting data from edgeNodeId: {}  and metric: {} ", edgeNodeId, EDGE_METRIC);

                String message = "SEND_EDGE_DATA " + hostApplicationId + " " + edgeNodeId + " " + EDGE_METRIC;
                Publish requestEdgeNodeData = Builders.publish().topic(TopicConstants.TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
                        .payload(ByteBuffer.wrap(message.getBytes()))
                        .build();
                publishService.publish(requestEdgeNodeData);
                state = TestStatus.REQUESTED_NODE_DATA;

                String message2 = "Data is being sent from edge node "
                        + edgeNodeId + " metric " + DEVICE_METRIC + ".\n"
                        + "Check that the value is updated on the host application";
                Publish requestUpdate = Builders.publish().topic(TopicConstants.TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
                        .payload(ByteBuffer.wrap(message2.getBytes()))
                        .build();
                logger.info("Requesting edge node data check for edge id: " + edgeNodeId);
                publishService.publish(requestUpdate);

            } else if (state == TestStatus.REQUESTED_NODE_DATA
                    && split[0].equals(TestStatus.CONSOLE_RESPONSE.toString())
                    && split[1].equals("OK")) {

                // Now tell the device simulator to send some data from the device
                logger.info("Requesting data from device id: " + deviceId + " metric: " + DEVICE_METRIC);
                String message = "SEND_DEVICE_DATA " + hostApplicationId + " " + edgeNodeId + " " + deviceId + " " + DEVICE_METRIC;
                Publish requestDeviceData = Builders.publish().topic(TopicConstants.TCK_DEVICE_CONTROL_TOPIC).qos(Qos.AT_LEAST_ONCE)
                        .payload(ByteBuffer.wrap(message.getBytes()))
                        .build();

                publishService.publish(requestDeviceData);


                logger.info("Requesting device data check for device id: " + deviceId);
                state = TestStatus.REQUESTED_DEVICE_DATA;
                String message2 = "Data is being sent from device " + deviceId
                        + " metric " + DEVICE_METRIC + ".\n"
                        + "Check that the value is updated on the host application";
                Publish checkDeviceData = Builders.publish().topic(TopicConstants.TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
                        .payload(ByteBuffer.wrap(message2.getBytes()))
                        .build();
                publishService.publish(checkDeviceData);

            } else if (state == TestStatus.REQUESTED_NODE_DATA
                    && split[0].equals(TestStatus.CONSOLE_RESPONSE.toString())) {
                theTCK.endTest();
            }
        }
    }
}