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

import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DBIRTH_METRIC_REQS;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.MAYBE;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.PASS;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Template.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.publish.RetainedPublish;

public class Utils {
	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");

	/**
	 * Set the not executed text for any test with a blank status
	 */
	public static @NotNull void setEndTest(final String name, final List<String> testIds,
			final Map<String, String> testResults) {
		if (!(testIds.size() == testResults.size())) {
			testIds.forEach(test -> {
				if (!testResults.containsKey(test)) {
					// logger.info("Test {} - {} not yet executed. ", name, test);
					testResults.put(test, NOT_EXECUTED);
				}
			});
		}
	}

	public static @NotNull String setResult(boolean bValid, String requirement) {
		return setResultWithStackTrace(bValid, requirement, 2);
	}

	private static @NotNull String setResultWithStackTrace(boolean bValid, String requirement, int element) {
		if (!bValid) {
			StackTraceElement[] elements = (new Exception()).getStackTrace();
			if (elements != null && elements.length > element) {
				final String result = FAIL + " " + requirement + " (" + elements[element] + ")";
				logger.debug(result);
				return result;
			} else {
				return FAIL + " " + requirement;
			}
		}
		return PASS;
	}

	public static @NotNull String setShouldResult(boolean bValid, String requirement) {
		return bValid ? PASS : MAYBE + " " + requirement;
	}

	public static @NotNull boolean setResultIfNotFail(Map<String, String> results, boolean result, String req_id,
													  String req_desc) {
		if (!results.get(req_id).equals(FAIL)) {
			results.put(req_id, setResultWithStackTrace(result, req_desc, 2));
		}
		return result;
	}
	
	public static @NotNull boolean setShouldResultIfNotFail(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		if (!results.get(req_id).equals(MAYBE)) {
			results.put(req_id, setShouldResult(result, req_desc));
		}
		return result;
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

	public static long getNextSeq(long seq) {
		assert seq >= 0 && seq <= 255;
		return (seq == 255) ? 0 : seq + 1;
	}

	public static boolean hasValue(Metric m) {
		if (m.hasIsNull() && m.getIsNull()) {
			// A null value is valid
			return true;
		}

		switch (DataType.forNumber(m.getDatatype())) {
			case Unknown:
				return false;
			case Int8:
			case Int16:
			case Int32:
			case UInt8:
			case UInt16:
				return m.hasIntValue();
			case Int64:
			case UInt32:
			case UInt64:
			case DateTime:
				return m.hasLongValue();
			case Float:
				return m.hasFloatValue();
			case Double:
				return m.hasDoubleValue();
			case Boolean:
				return m.hasBooleanValue();
			case String:
			case Text:
			case UUID:
				return m.hasStringValue();
			case Bytes:
			case File:
			case Int8Array:
			case Int16Array:
			case Int32Array:
			case Int64Array:
			case UInt8Array:
			case UInt16Array:
			case UInt32Array:
			case UInt64Array:
			case FloatArray:
			case DoubleArray:
			case BooleanArray:
			case StringArray:
			case DateTimeArray:
				return m.hasBytesValue();
			case DataSet:
				return m.hasDatasetValue();
			case Template:
				return m.hasTemplateValue();
			default:
				return false;
		}
	}
	
	public static boolean hasValue(Parameter p) {
		if (!p.hasType()) {
			return false;
		}
		switch (p.getType()) {
			case Parameter.INT_VALUE_FIELD_NUMBER:
				return p.hasIntValue();
			case Parameter.LONG_VALUE_FIELD_NUMBER:
				return p.hasLongValue();
			case Parameter.FLOAT_VALUE_FIELD_NUMBER:
				return p.hasFloatValue();
			case Parameter.DOUBLE_VALUE_FIELD_NUMBER:
				return p.hasDoubleValue();
			case Parameter.BOOLEAN_VALUE_FIELD_NUMBER:
				return p.hasBooleanValue();
			case Parameter.STRING_VALUE_FIELD_NUMBER:
				return p.hasStringValue();
			default:
				return false;
		}
	}

	public static AtomicBoolean checkHostApplicationIsOnline(String hostApplicationId) {
		// Check that the host application status is ONLINE

		AtomicBoolean hostOnline = new AtomicBoolean(false);
		String topic = TopicConstants.TOPIC_ROOT_STATE + "/" + hostApplicationId;
		final CompletableFuture<Optional<RetainedPublish>> getFuture =
				Services.retainedMessageStore().getRetainedMessage(topic);
		try {
			Optional<RetainedPublish> retainedPublishOptional = getFuture.get();
			if (retainedPublishOptional.isPresent()) {
				final RetainedPublish retainedPublish = retainedPublishOptional.get();
				String payload = null;
				ByteBuffer bpayload = retainedPublish.getPayload().orElseGet(null);
				if (bpayload != null) {
					payload = StandardCharsets.UTF_8.decode(bpayload).toString();
				}
				if (!payload.equals(HostState.ONLINE.toString())) {
					logger.info("Host status payload: " + payload);
				} else {
					hostOnline.set(true);
				}
			} else {
				logger.info("No retained message for topic: " + topic);
			}
		} catch (InterruptedException | ExecutionException e) {

		}
		return hostOnline;
	}

	private enum HostState {
		ONLINE,
		OFFLINE
	}

	public enum Profile {
		HOST,
		EDGE,
		BROKER
	}

	public enum TestStatus {
		NONE,
		CONSOLE_RESPONSE,
		CONNECTING_DEVICE,
		REQUESTED_NODE_DATA,
		REQUESTED_DEVICE_DATA,
		KILLING_DEVICE,
		EXPECT_NODE_REBIRTH,
		EXPECT_NODE_COMMAND,
		EXPECT_DEVICE_REBIRTH,
		EXPECT_DEVICE_COMMAND,
		DEATH_MESSAGE_RECEIVED
	}

	public static StringBuilder getSummary(final @NotNull Map<String, String> results) {
		final StringBuilder summary = new StringBuilder();

		String overall = results.entrySet().isEmpty() ? TopicConstants.EMPTY : TopicConstants.NOT_EXECUTED;

		for (final Map.Entry<String, String> reportResult : results.entrySet()) {
			if (reportResult.getValue().equals(NOT_EXECUTED)) {
				//skip
				continue;
			}

			summary.append(reportResult.getKey()).append(": ").append(reportResult.getValue()).append(";")
					.append(System.lineSeparator());

			if (!overall.equals(TopicConstants.FAIL)) { // don't overwrite an overall fail status
				if (reportResult.getValue().startsWith(TopicConstants.PASS)) {
					overall = TopicConstants.PASS;
				} else if (reportResult.getValue().startsWith(TopicConstants.FAIL)) {
					overall = TopicConstants.FAIL;
				}
			}
		}
		summary.append("OVERALL: ").append(overall).append(";").append(System.lineSeparator());

		return summary;
	}

	public static boolean checkUTF8String(String inString) {
		// MUST be a valid UTF-8 string

		byte[] bytes = inString.getBytes(StandardCharsets.UTF_8);

		String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);

		boolean rc = false;
		if (inString.equals(utf8EncodedString)) {
			rc = true;
		}
		return rc;
	}
}
