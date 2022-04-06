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
 * This is the primary host Sparkplug send command test:
 *
 * to check that a command from a primary host under test is correct to both an
 * edge node (NCMD) and a device (DCMD).
 *
 * There will be a prompt to the person executing the test to send a command to
 * a device and edge node we will connect.
 *
 * The host application under test must be connected and online prior to starting this test.
 * The id of the host application must be passed as the first parameter to this test.
 * The second parameter is the id of the edge node to be used.
 * The third parameter is the id of the device to be used.
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
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.TopicConstants;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.sparkplug.tck.test.common.Utils.TestStatus;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_CONSOLE_PROMPT_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkHostApplicationIsOnline;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class SendCommandTest extends TCKTest {

    private static final String NODE_CONTROL_REBIRTH = "Node Control/Rebirth";
    private static final String EDGE_METRIC = "TCK_metric/Boolean";
    private static final String DEVICE_METRIC = "Inputs/0";

    private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull ArrayList<String> testIds = new ArrayList<>();
    private @NotNull String deviceId;
    private @NotNull String groupId;
    private @NotNull String edgeNodeId;
    private @NotNull String hostApplicationId;

    private TestStatus state = null;
    private TCK theTCK = null;

    private PublishService publishService = Services.publishService();

    public SendCommandTest(TCK aTCK, String[] params) {
        logger.info("Primary host {}: Parameters: {} ", getName(), Arrays.asList(params));
        theTCK = aTCK;

        if (params.length < 4) {
            logger.error("Parameters to host send command test must be: hostApplicationId, groupId edgeNodeId deviceId");
            return;
        }
        hostApplicationId = params[0];
        groupId = params[1];
        edgeNodeId = params[2];
        deviceId = params[3];
        logger.info("Parameters are HostApplicationId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId, edgeNodeId, deviceId);

        final AtomicBoolean hostOnline = checkHostApplicationIsOnline(hostApplicationId);

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
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "Sparkplug Host Send Command Test";
    }

    public String[] getTestIds() {
        return testIds.toArray(new String[0]);
    }

    public Map<String, String> getResults() {
        return testResults;
    }

    @Override
    public void connect(String clientId, ConnectPacket packet) {
    }

    @Override
    public void disconnect(String clientId, DisconnectPacket packet) {
    }

    @Override
    public void subscribe(String clientId, SubscribePacket packet) {
    }

    private void publishToTckConsolePrompt(String payload) {
        Publish message = Builders.publish().topic(TCK_CONSOLE_PROMPT_TOPIC).qos(Qos.AT_LEAST_ONCE)
                .payload(ByteBuffer.wrap(payload.getBytes())).build();
        logger.info("Requesting command to edge node id:{} ", edgeNodeId);
        publishService.publish(message);
    }

    @Override
    public void publish(String clientId, PublishPacket packet) {
        logger.info("Host - {} test - PUBLISH - topic: {}, state: {} ", getName(), packet.getTopic(), state);
        final String topic = packet.getTopic();
        if (topic.equals(TCK_LOG_TOPIC)) {
            ByteBuffer byteBuffer = packet.getPayload().orElseGet(null);
            if (byteBuffer != null) {
                final String payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                if (payload.equals("Device " + deviceId + " successfully created")) {
                    logger.info("SendCommandTest: Device was created");
                    publishToTckConsolePrompt("Send an edge rebirth to edge node " + edgeNodeId);
                    state = TestStatus.EXPECT_NODE_REBIRTH;
                }
            }
        } else if (topic.equals(TopicConstants.SP_BV_1_0_SPARKPLUG_TCK_NCMD_TOPIC + edgeNodeId)) {
            if (state == TestStatus.EXPECT_NODE_REBIRTH) {
                checkNodeCommand(clientId, packet);
                publishToTckConsolePrompt("Send an edge command to edge node " + edgeNodeId + " metric " + EDGE_METRIC);
                state = TestStatus.EXPECT_NODE_COMMAND;
            } else if (state == TestStatus.EXPECT_NODE_COMMAND) {
                checkNodeCommand(clientId, packet);
                publishToTckConsolePrompt("Send a device rebirth command to device " + deviceId + " at edge node " + edgeNodeId);
                state = TestStatus.EXPECT_DEVICE_REBIRTH;
            }
        } else if (topic.equals(TopicConstants.SP_BV_1_0_SPARKPLUG_TCK_DCMD_TOPIC + edgeNodeId + "/" + deviceId)) {
            if (state == TestStatus.EXPECT_DEVICE_REBIRTH) {
                checkDeviceCommand(clientId, packet);
                publishToTckConsolePrompt("Send a device command to device " + deviceId + " at edge node " + edgeNodeId + " metric " + DEVICE_METRIC);
                state = Utils.TestStatus.EXPECT_DEVICE_COMMAND;
            } else if (state == TestStatus.EXPECT_DEVICE_COMMAND) {
                checkDeviceCommand(clientId, packet);
                theTCK.endTest();
            }
        }
    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_NCMD,
            id = ID_PAYLOADS_NCMD_TIMESTAMP)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_NCMD,
            id = ID_PAYLOADS_NCMD_SEQ)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_NCMD,
            id = ID_PAYLOADS_NCMD_QOS)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_NCMD,
            id = ID_PAYLOADS_NCMD_RETAIN)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_NCMD,
            id = ID_TOPICS_NCMD_MQTT)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_NCMD,
            id = ID_TOPICS_NCMD_TIMESTAMP)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_NCMD,
            id = ID_TOPICS_NCMD_PAYLOAD)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
            id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
            id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
            id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
            id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE)
    public void checkNodeCommand(final String clientId, final @NotNull PublishPacket packet) {
        logger.info("Host - {}  - PUBLISH - checkNodeCommand {}, {}", getName(), packet.getTopic(), state);

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB, setResult(true, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_VERB));

        // QoS and not retained - related tests
        logger.debug("Check Req: {}:{}.", ID_TOPICS_NCMD_MQTT, TOPICS_NCMD_MQTT);
        testIds.add(ID_TOPICS_NCMD_MQTT);
        testResults.put(ID_TOPICS_NCMD_MQTT, setResult((packet.getQos() == Qos.AT_MOST_ONCE && !packet.getRetain()), TOPICS_NCMD_MQTT));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_NCMD_QOS, PAYLOADS_NCMD_QOS);
        testIds.add(ID_PAYLOADS_NCMD_QOS);
        testResults.put(ID_PAYLOADS_NCMD_QOS, setResult((packet.getQos() == Qos.AT_MOST_ONCE), PAYLOADS_NCMD_QOS));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_NCMD_RETAIN, PAYLOADS_NCMD_RETAIN);
        testIds.add(ID_PAYLOADS_NCMD_RETAIN);
        testResults.put(ID_PAYLOADS_NCMD_RETAIN, setResult(!packet.getRetain(), PAYLOADS_NCMD_RETAIN));

        // payload related tests
        PayloadOrBuilder inboundPayload = Utils.getSparkplugPayload(packet);
        Boolean[] bValid = checkValidCommandPayload(inboundPayload);

        logger.debug("Check Req: {}:{}.", ID_TOPICS_NCMD_TIMESTAMP, TOPICS_NCMD_TIMESTAMP);
        testIds.add(ID_TOPICS_NCMD_TIMESTAMP);
        testResults.put(ID_TOPICS_NCMD_TIMESTAMP, setResult(bValid[0], TOPICS_NCMD_TIMESTAMP));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_NCMD_SEQ, PAYLOADS_NCMD_SEQ);
        testIds.add(ID_PAYLOADS_NCMD_SEQ);
        testResults.put(ID_PAYLOADS_NCMD_SEQ, setResult(bValid[1], PAYLOADS_NCMD_SEQ));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_NCMD_TIMESTAMP, PAYLOADS_NCMD_TIMESTAMP);
        testIds.add(ID_PAYLOADS_NCMD_TIMESTAMP);
        testResults.put(ID_PAYLOADS_NCMD_TIMESTAMP, setResult(bValid[0], PAYLOADS_NCMD_TIMESTAMP));

        logger.debug("Check Req: {}:{}.", ID_TOPICS_NCMD_PAYLOAD, TOPICS_NCMD_PAYLOAD);
        testIds.add(ID_TOPICS_NCMD_PAYLOAD);
        testResults.put(ID_TOPICS_NCMD_PAYLOAD, setResult(bValid[2], TOPICS_NCMD_PAYLOAD));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB, setResult(bValid[3], OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_NAME, setResult(bValid[3], OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VERB));

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE, setResult(bValid[4], OPERATIONAL_BEHAVIOR_DATA_COMMANDS_NCMD_REBIRTH_VALUE));


    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_DCMD,
            id = ID_PAYLOADS_DCMD_TIMESTAMP)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_DCMD,
            id = ID_PAYLOADS_DCMD_SEQ)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_DCMD,
            id = ID_PAYLOADS_DCMD_QOS)
    @SpecAssertion(
            section = Sections.PAYLOADS_B_DCMD,
            id = ID_PAYLOADS_DCMD_RETAIN)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_DCMD,
            id = ID_TOPICS_DCMD_MQTT)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_DCMD,
            id = ID_TOPICS_DCMD_TIMESTAMP)
    @SpecAssertion(
            section = Sections.PAYLOADS_DESC_DCMD,
            id = ID_TOPICS_DCMD_PAYLOAD)
    @SpecAssertion(
            section = Sections.OPERATIONAL_BEHAVIOR_COMMANDS,
            id = ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB)
    public void checkDeviceCommand(String clientId, PublishPacket packet) {
        logger.info("Host - {}  - PUBLISH - checkDeviceCommand {}, {} ", getName(), packet.getTopic(), state);

        logger.debug("Check Req: {}:{}.", ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB);
        testIds.add(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB);
        testResults.put(ID_OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB, setResult(true, OPERATIONAL_BEHAVIOR_DATA_COMMANDS_DCMD_VERB));


        // QoS and not retained - related tests
        logger.debug("Check Req: {}:{}.", ID_TOPICS_DCMD_MQTT, TOPICS_DCMD_MQTT);
        testIds.add(ID_TOPICS_DCMD_MQTT);
        testResults.put(ID_TOPICS_DCMD_MQTT, setResult(packet.getQos() == Qos.AT_MOST_ONCE && !packet.getRetain(), TOPICS_DCMD_MQTT));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_DCMD_QOS, PAYLOADS_DCMD_QOS);
        testIds.add(ID_PAYLOADS_DCMD_QOS);
        testResults.put(ID_PAYLOADS_DCMD_QOS, setResult((packet.getQos() == Qos.AT_MOST_ONCE), PAYLOADS_DCMD_QOS));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_DCMD_RETAIN, PAYLOADS_DCMD_RETAIN);
        testIds.add(ID_PAYLOADS_DCMD_RETAIN);
        testResults.put(ID_PAYLOADS_DCMD_RETAIN, setResult(!packet.getRetain(), PAYLOADS_DCMD_RETAIN));


        // payload related tests
        PayloadOrBuilder inboundPayload = Utils.getSparkplugPayload(packet);
        Boolean[] bValid = checkValidDeviceCommandPayload(inboundPayload);

        logger.debug("Check Req: {}:{}.", ID_TOPICS_DCMD_TIMESTAMP, TOPICS_DCMD_TIMESTAMP);
        testIds.add(ID_TOPICS_DCMD_TIMESTAMP);
        testResults.put(ID_TOPICS_DCMD_TIMESTAMP, setResult(bValid[0], TOPICS_DCMD_TIMESTAMP));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_DCMD_TIMESTAMP, PAYLOADS_DCMD_TIMESTAMP);
        testIds.add(ID_PAYLOADS_DCMD_TIMESTAMP);
        testResults.put(ID_PAYLOADS_DCMD_TIMESTAMP, setResult(bValid[0], PAYLOADS_DCMD_TIMESTAMP));

        logger.debug("Check Req: {}:{}.", ID_PAYLOADS_DCMD_SEQ, PAYLOADS_DCMD_SEQ);
        testIds.add(ID_PAYLOADS_DCMD_SEQ);
        testResults.put(ID_PAYLOADS_DCMD_SEQ, setResult(bValid[1], PAYLOADS_DCMD_SEQ));


        logger.debug("Check Req: {}:{}.", ID_TOPICS_DCMD_PAYLOAD, TOPICS_DCMD_PAYLOAD);
        testIds.add(ID_TOPICS_DCMD_PAYLOAD);
        testResults.put(ID_TOPICS_DCMD_PAYLOAD, setResult(bValid[2], TOPICS_DCMD_PAYLOAD));
    }

    private Boolean[] checkValidDeviceCommandPayload(PayloadOrBuilder payload) {
        Boolean[] bValidPayload = new Boolean[]{false, false, false};

        if (payload != null) {
            bValidPayload[0] = (payload.hasTimestamp());
            bValidPayload[1] = (payload.getSeq() < 0);
            List<Metric> metrics = payload.getMetricsList();

            ListIterator<Metric> metricIterator = metrics.listIterator();
            while (metricIterator.hasNext()) {
                Metric current = metricIterator.next();
                if (current.getName().equals(DEVICE_METRIC)) {
                    bValidPayload[2] = true;
                }
            }
        }
        return bValidPayload;
    }

    private Boolean[] checkValidCommandPayload(PayloadOrBuilder payload) {
        Boolean[] bValidPayload = new Boolean[]{false, false, false, false, false};

        if (payload != null) {
            bValidPayload[0] = payload.hasTimestamp();
            bValidPayload[1] = (payload.getSeq() < 0);
            List<Metric> metrics = payload.getMetricsList();

            ListIterator<Metric> metricIterator = metrics.listIterator();
            while (metricIterator.hasNext()) {
                Metric current = metricIterator.next();
                if (current.getName().equals(EDGE_METRIC)) {
                    bValidPayload[2] = true;
                }
                if (current.getName().equals(NODE_CONTROL_REBIRTH)) {
                    bValidPayload[3] = true;
                    if (current.getDatatype() == DataType.Boolean.getNumber()) {
                        bValidPayload[4] = current.getBooleanValue();
                    }
                }
            }
        }
        return bValidPayload;
    }
}
