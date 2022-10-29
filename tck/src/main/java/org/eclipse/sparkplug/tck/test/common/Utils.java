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

import static org.eclipse.sparkplug.tck.test.common.Constants.FAIL;
import static org.eclipse.sparkplug.tck.test.common.Constants.MAYBE;
import static org.eclipse.sparkplug.tck.test.common.Constants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.Constants.PASS;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Template.Parameter;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		testIds.forEach(test -> {
			if (!testResults.containsKey(test)) {
				// logger.info("Test {} - {} not yet executed. ", name, test);
				testResults.put(test, NOT_EXECUTED);
			}
		});
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
		if (results.get(req_id) == null || !results.get(req_id).equals(FAIL)) {
			results.put(req_id, setResultWithStackTrace(result, req_desc, 2));
		}
		return result;
	}

	public static @NotNull boolean setShouldResultIfNotFail(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		if (results.get(req_id) == null || !results.get(req_id).equals(MAYBE)) {
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

	public static String getRetained(String topic) {
		String payload = null;
		final CompletableFuture<Optional<RetainedPublish>> getFuture =
				Services.retainedMessageStore().getRetainedMessage(topic);
		try {
			Optional<RetainedPublish> retainedPublishOptional = getFuture.get();
			if (retainedPublishOptional.isPresent()) {
				final RetainedPublish retainedPublish = retainedPublishOptional.get();
				ByteBuffer bpayload = retainedPublish.getPayload().orElseGet(null);
				if (bpayload != null) {
					payload = StandardCharsets.UTF_8.decode(bpayload).toString();
				}
			} else {
				logger.info("No retained message for topic: " + topic);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return payload;
	}

	public static AtomicBoolean checkHostApplicationIsOnline(String hostApplicationId) {
		// Check that the host application status is online
		AtomicBoolean hostOnline = new AtomicBoolean(false);
		String topic = Constants.TOPIC_ROOT_STATE + "/" + hostApplicationId;
		String payload = getRetained(topic);
		if (payload == null) {
			logger.info("No retained message for topic: " + topic);
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				StatePayload statePayload = mapper.readValue(payload, StatePayload.class);
				if (statePayload != null && statePayload.isOnline()) {
					hostOnline.set(true);
				}
			} catch (Exception e) {
				logger.error("Failed to handle state topic payload: {}", payload);
			}
		}
		logger.info("Is Host online? {}", hostOnline);
		return hostOnline;
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

	public static StatePayload getHostPayload(String payloadString, boolean expectOnline, boolean logMismatchErrors) {
		logger.debug("Incoming potential Host STATE payload: {}", payloadString);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = null;
		try {
			json = mapper.readTree(payloadString);
		} catch (Exception e) {

		}

		boolean isValidPayload = false;
		boolean retOnline = false;
		long retTimestamp = -1;

		if (json != null) {
			if (json.has("timestamp")) {
				JsonNode timestamp = json.get("timestamp");
				if (timestamp.isLong()) {
					int timestamp_max_diff = 60000; // milliseconds difference allowed
					Date now = new Date();
					long diff = now.getTime() - timestamp.longValue();
					if (diff >= 0 && diff <= timestamp_max_diff) {
						isValidPayload = true;
						retTimestamp = timestamp.longValue();
					} else {
						logger.warn("StatePayload is invalid - Timestamp diff " + diff);
					}
				}
			}

			if (json.has("online")) {
				JsonNode online = json.get("online");
				if (online.isBoolean() && online.booleanValue() == expectOnline) {
					// valid - don't set isValidPayload as it might be false
				} else {
					isValidPayload = false;
					if (logMismatchErrors) {
						logger.warn("StatePayload is invalid - online={} - expected={}", online, expectOnline);
					} else {
						logger.debug("StatePayload is invalid - online={} - expected={}", online, expectOnline);
					}
				}
			} else {
				isValidPayload = false;
				logger.warn("StatePayload is invalid - online field is missing");
			}
		}

		if (isValidPayload) {
			logger.debug("Returning StatePaload with online={} timestamp={}", retOnline, retTimestamp);
			return new StatePayload(retOnline, retTimestamp);
		} else {
			return null;
		}
	}

	public static boolean checkUTC(long timestamp, long UTCwindow) {
		boolean result = false;

		Date now = new Date();
		long diff = now.getTime() - timestamp;

		if (diff == 0) {
			result = true; // Exactly the same so we're good.
		} else if (diff > 0) {
			// The timestamp is within the previous allowed interval.
			result = diff <= UTCwindow;
		} else if (diff < 0) {
			// The timestamp is within the next allowed interval.
			// This shouldn't happen unless clocks differ between machines
			// but we should allow for it.
			result = diff >= UTCwindow;
		}

		if (result == false) {
			logger.info("CheckUTC: timestamp diff " + diff);
		}
		return result;
	}

	public static String addQuotes(String string) {
		String result = string;
		if (result.contains(" ")) {
			result = "\"" + result + "\"";
		}
		return result;
	}

	public static String[] tokenize(String payload) {
		// find all tokens which are either plain tokens or
		// containing whitespaces and therefore surrounded with double quotes
		final Pattern tokenPattern = Pattern.compile("(\"[^\"]+\")|\\S+");
		final Matcher matcher = tokenPattern.matcher(payload.trim());
		final List<String> tokens = new ArrayList<>();
		while (matcher.find()) {
			tokens.add(matcher.group());
		}
		final String[] strings = tokens.stream().map(token -> {
			if (token.startsWith("\"") && token.endsWith("\"")) {
				return token.substring(1, token.length() - 1);
			} else {
				return token;
			}
		}).toArray(String[]::new);

		return strings;
	}
}
