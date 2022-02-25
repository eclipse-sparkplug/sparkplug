
/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Anja Helmbrecht-Schaar - initial implementation and documentation
 */
package org.eclipse.sparkplug.tck.test.edge;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.*;
import static org.eclipse.sparkplug.tck.test.common.Utils.extractSparkplugPayload;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

/**
 * This is the edge node Sparkplug payload validation.
 *
 * @author Anja Helmbrecht-Schaar
 */
@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0-SNAPSHOT")
public class PayloadTest extends TCKTest {

    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    private final @NotNull Map<String, String> testResults = new HashMap<>();
    private final @NotNull List<String> testIds = List.of(
            ID_PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED,
            ID_PAYLOADS_NAME_REQUIREMENT,
            ID_PAYLOADS_ALIAS_BIRTH_REQUIREMENT,
            ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ,
            ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT);

    private final @NotNull TCK theTCK;

    private @NotNull String deviceId;
    private @NotNull String groupId;
    private @NotNull String edgeNodeId;
    private @NotNull String hostApplicationId;
    private @NotNull long seq = -1;

    public PayloadTest(final @NotNull TCK aTCK, final @NotNull String[] parms) {
        logger.info("Edge Node payload validation test. Parameters: {} ", Arrays.asList(parms));
        theTCK = aTCK;

        if (parms.length < 4) {
            logger.error("Parameters to edge payload test must be: {hostId}, groupId edgeNodeId deviceId");
            return;
        }

        hostApplicationId = parms[0];
        groupId = parms[1];
        edgeNodeId = parms[2];
        deviceId = parms[3];
        logger.info("Parameters are HostId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId, edgeNodeId, deviceId);
    }

    public void endTest() {
        Utils.setEndTest(getName(), testIds, testResults);
        reportResults(testResults);
    }

    public String getName() {
        return "PayloadTest";
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

    @Override
    public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
        final String topic = packet.getTopic();
        logger.info("Edge - Payload validation test - publish - topic: {}", topic);

        boolean isDataTopic = topic.startsWith(TOPIC_ROOT_SP_BV_1_0) &&
                (topic.contains(TOPIC_PATH_DDATA) || topic.contains(TOPIC_PATH_NDATA));
        if (!isDataTopic) {
            logger.error("Skip Edge payload validation - no sparkplug payload.");
        }

        if (clientId.contentEquals(deviceId)
                || topic.contains(groupId) && topic.contains(edgeNodeId)) {
            final SparkplugBPayload sparkplugPayload = extractSparkplugPayload(packet);
            if (sparkplugPayload != null) {
                checkSequenceNumberIncluded(sparkplugPayload, topic);
                checkPayloadsNameRequirement(sparkplugPayload);
                checkAliasInData(sparkplugPayload, topic);
                checkMetricsDataTypeNotRec(sparkplugPayload, topic);
                checkPayloadsNameInDataRequirement(sparkplugPayload);
            } else {
                logger.error("Skip Edge payload validation - no sparkplug payload.");
            }
            theTCK.endTest();
        }
    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_PAYLOAD,
            id = ID_PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED)
    public void checkSequenceNumberIncluded(final @NotNull SparkplugBPayload sparkplugPayload, String topic) {
        logger.debug("Check Req: A sequence number MUST be included in the payload of every Sparkplug MQTT message except NDEATH messages.");
        boolean isValid = false;
        if (topic.contains(TOPIC_PATH_NDEATH) && sparkplugPayload.getSeq() == seq) {
            isValid = true;
        } else if (sparkplugPayload.getSeq() >= 0) {
            isValid = true;
        }
        testResults.put(ID_PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED, setResult(isValid, PAYLOAD_SEQUENCE_NUM_ALWAYS_INCLUDED));
    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_METRIC,
            id = ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT)
    public void checkAliasInData(final @NotNull SparkplugBPayload sparkplugPayload, String topic) {
        logger.debug("Check Req: NDATA, DDATA, NCMD, and DCMD messages MUST only include an alias and the metric name MUST be excluded.");

        boolean isValid = false;
        if (topic.contains(TOPIC_PATH_NDATA) || topic.contains(TOPIC_PATH_DDATA)) {
            for (Metric m : sparkplugPayload.getMetrics()) {
                if (!m.getIsNull()
                        && (m.hasAlias() && m.getName().length() == 0))
                    isValid = true;
                break;
            }
            testResults.put(ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT, setResult(isValid, PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT));
        }
    }

    @SpecAssertion(
            section = Sections.PAYLOADS_B_METRIC,
            id = ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ)
    public void checkMetricsDataTypeNotRec(final @NotNull SparkplugBPayload sparkplugPayload, String topic) {
        logger.debug("Check Req: The datatype SHOULD NOT be included with metric definitions in NDATA, NCMD, DDATA, and DCMD messages.");
        boolean isValid = true;
        if (topic.contains(TOPIC_PATH_NDATA) || topic.contains(TOPIC_PATH_DDATA)) {
            for (Metric m : sparkplugPayload.getMetrics()) {
                if (m.getDataType() != null) {
                    isValid = false;
                    break;
                }
            }
            testResults.put(ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ, setResult(isValid, PAYLOADS_METRIC_DATATYPE_NOT_REQ));
        }
    }


    @SpecAssertion(
            section = Sections.PAYLOADS_B_METRIC,
            id = ID_PAYLOADS_NAME_REQUIREMENT)
    public void checkPayloadsNameRequirement(final @NotNull SparkplugBPayload sparkplugPayload) {
        logger.debug("Check Req: The name MUST be included with every metric unless aliases are being used.");
        boolean isValid = true;
        for (Metric m : sparkplugPayload.getMetrics()) {
            if (m.getIsNull() || m.getName().isEmpty()) {
                isValid = false;
                break;
            }
        }
        testResults.put(ID_PAYLOADS_NAME_REQUIREMENT, setResult(isValid, PAYLOADS_NAME_REQUIREMENT));
    }


    @SpecAssertion(
            section = Sections.PAYLOADS_B_METRIC,
            id = ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT)
    public void checkPayloadsNameInDataRequirement(final @NotNull SparkplugBPayload sparkplugPayload) {
        logger.debug("Check Req: The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.");
        boolean isValid = true;
        for (Metric m : sparkplugPayload.getMetrics()) {
            if (m.getTimestamp() == null) {
                isValid = false;
                break;
            }
        }
        testResults.put(ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT, setResult(isValid, PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT));
    }
}
