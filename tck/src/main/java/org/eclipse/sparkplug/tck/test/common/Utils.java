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
import com.hivemq.extension.sdk.api.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.*;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.*;

public class Utils {
	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	public static @NotNull void setEndTest(final String name, final List<String> testIds,
			final Map<String, String> testResults) {
		if (!(testIds.size() == testResults.size())) {
			testIds.forEach(test -> {
				if (!testResults.containsKey(test)) {
					logger.info("Test {} - {} not yet executed. ", name, test);
					testResults.put(test, NOT_EXECUTED);
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
			case Unknown:
				return false;
			case Int32:
				return m.hasIntValue();
			case Int64:
				return m.hasLongValue();
			case Float:
				return m.hasFloatValue();
			case Double:
				return m.hasDoubleValue();
			case Boolean:
				return m.hasBooleanValue();
			case String:
				return m.hasStringValue();
			// case : return m.hasExtensionValue();
		}
		return false;
	}
	
    public static AtomicBoolean checkHostApplicationIsOnline(String hostApplicationId) {
        AtomicBoolean hostOnline = new AtomicBoolean(false);
        String topic = TopicConstants.TOPIC_ROOT_STATE + "/" + hostApplicationId;
        // Check that the host application status is ONLINE, ready for the test
        Services.retainedMessageStore().getRetainedMessage(topic)
                .whenComplete((retainedPublish, throwable) -> {
                    if (throwable != null) {
                        logger.error("Error getting retained message for HostApplication Status: {}", throwable.getMessage());
                    } else if (retainedPublish.isPresent()) {
                        ByteBuffer byteBuffer = retainedPublish.get().getPayload().orElseGet(null);
                        if (byteBuffer != null) {
                            String payload = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                            if (HostState.ONLINE.toString().equals(payload)) {
                                hostOnline.set(true);
                            }
                            logger.info("checkHostApplicationIsOnline - " +
                                            "Retained message for HostApplication: {} {} {} ",
                                    hostApplicationId, hostOnline.get(), payload);
                        }
                    } else {
                        logger.info("No retained message for topic: {} ", topic);
                    }
                });
        return hostOnline;
    }

    private enum HostState {
        ONLINE, OFFLINE
    }


    public enum TestStatus {
        NONE,
        CONSOLE_RESPONSE, CONNECTING_DEVICE, REQUESTED_NODE_DATA, REQUESTED_DEVICE_DATA,
        KILLING_DEVICE, EXPECT_NODE_REBIRTH, EXPECT_NODE_COMMAND, EXPECT_DEVICE_REBIRTH, EXPECT_DEVICE_COMMAND,
        DEATH_MESSAGE_RECEIVED
    }

	public static StringBuilder getSummary(final @NotNull Map<String, String> results) {
		final StringBuilder summary = new StringBuilder();

		String overall = results.entrySet().isEmpty() ? TopicConstants.EMPTY : TopicConstants.PASS;

		for (final Map.Entry<String, String> reportResult : results.entrySet()) {
			summary.append(reportResult.getKey()).append(": ").append(reportResult.getValue()).append(";")
					.append(System.lineSeparator());

			if (reportResult.getValue().startsWith(TopicConstants.FAIL)) {
				overall = TopicConstants.FAIL;
			}
		}
		summary.append("OVERALL: ").append(overall).append(";").append(System.lineSeparator());

		return summary;
	}
}
