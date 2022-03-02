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

package org.eclipse.sparkplug.tck.test.common;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.protobuf.SparkplugBProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.*;

public class Utils {
    private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

    public static @NotNull void setEndTest(final String name, final List<String> testIds, final Map<String, String> testResults) {
        if (!(testIds.size() == testResults.size())) {
            testIds.forEach(test -> {
                if (!testResults.containsKey(test)) {
                    logger.info("Test {} - {} not yet executed. ", name, test);
                    testResults.put(test, "");
                }
            });
        }
    }


    public static @NotNull String setResult(boolean bValid, String requirement) {
        return bValid ? PASS : FAIL + " " + requirement;
    }

    private static SparkplugBPayload decode(ByteBuffer payload) {
        byte[] bytes = new byte[payload.remaining()];
        payload.get(bytes);
        SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
        SparkplugBPayload sparkplugPayload = null;
        try {
            sparkplugPayload = decoder.buildFromByteArray(bytes);
        } catch (Exception e) {
            logger.error("Payload Exception", e);
            return sparkplugPayload;
        }
        return sparkplugPayload;
    }

    public static SparkplugBProto.Payload parseRaw(PublishPacket packet) throws InvalidProtocolBufferException {
        ByteBuffer payload = packet.getPayload().get();
        byte[] bytes = new byte[packet.getPayload().get().remaining()];
        payload.get(bytes);
        SparkplugBProto.Payload protoPayload = SparkplugBProto.Payload.parseFrom(bytes);
        return protoPayload;
    }

    public static SparkplugBPayload extractSparkplugPayload(PublishPacket packet) {
        final ByteBuffer payload = packet.getPayload().orElseGet(null);
        if (payload != null &&
                packet.getTopic().startsWith(TOPIC_ROOT_SP_BV_1_0)) {
            return decode(payload);
        }
        return null;
    }
}
