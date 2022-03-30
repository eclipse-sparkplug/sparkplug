/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar, Ian Craggs
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

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.*;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;

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
    
    public static PayloadOrBuilder decode(ByteBuffer payload) {
		byte[] array = new byte[payload.remaining()];
		payload.get(array);
		try {
			return Payload.parseFrom(array);
		} catch (InvalidProtocolBufferException e) {
			logger.error("Payload Exception", e);
			return null;
		}
    }
    
    public static PayloadOrBuilder getSparkplugPayload(PublishPacket packet) {
        final ByteBuffer payload = packet.getPayload().orElseGet(null);
        if (payload != null && packet.getTopic().startsWith(TOPIC_ROOT_SP_BV_1_0)) {
        	return decode(payload);
        }
        return null;
    }
    
    public static boolean hasValue(Metric m) {
    	switch (DataType.valueOf(m.getDatatype())) {
            case Unknown: return false;
            case Int32: return m.hasIntValue();
            case Int64: return m.hasLongValue();
            case Float: return m.hasFloatValue();
            case Double: return m.hasDoubleValue();
            case Boolean: return m.hasBooleanValue();
            case String: return m.hasStringValue();
            //case : return m.hasExtensionValue();
    	}
    	return false;
    }
}
