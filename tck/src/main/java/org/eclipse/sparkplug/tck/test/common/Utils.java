/**
 * Copyright (c) 2022, 2023 Anja Helmbrecht-Schaar, Ian Craggs
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

	private static @NotNull String setShouldResultWithStackTrace(boolean bValid, String requirement, int element) {
		if (!bValid) {
			StackTraceElement[] elements = (new Exception()).getStackTrace();
			if (elements != null && elements.length > element) {
				final String result = MAYBE + " " + requirement + " (" + elements[element] + ")";
				logger.debug(result);
				return result;
			} else {
				return MAYBE + " " + requirement;
			}
		}
		return PASS;
	}

	public static @NotNull String setShouldResult(boolean bValid, String requirement) {
		return bValid ? PASS : MAYBE + " " + requirement;
	}

	public static @NotNull boolean setShouldResult(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		results.put(req_id, setShouldResultWithStackTrace(result, req_desc, 2));
		return result;
	}

	public static @NotNull boolean setResult(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		results.put(req_id, setResultWithStackTrace(result, req_desc, 2));
		return result;
	}

	public static @NotNull boolean setResultIfNotFail(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		if (results.get(req_id) == null || !results.get(req_id).equals(FAIL)) {
			results.put(req_id, setResultWithStackTrace(result, req_desc, 2));
		}
		return result;
	}

	public static @NotNull boolean setResultIfNotPass(Map<String, String> results, boolean result, String req_id,
			String req_desc) {
		if (results.get(req_id) == null || !results.get(req_id).equals(PASS)) {
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

	public static boolean hasValidDatatype(Metric m) {
        // Checks that the datatype is valid for Metric and that the value case corresponds with the datatype
        Metric.ValueCase expectedValueCase = Metric.ValueCase.VALUE_NOT_SET;

        if(DataType.forNumber(m.getDatatype()) != null) {
            switch (DataType.forNumber(m.getDatatype())) {
                case Int8:
                case Int16:
                case Int32:
                case UInt8:
                case UInt16:
                case UInt32:
                    expectedValueCase = Metric.ValueCase.INT_VALUE;
                    break;
                case Int64:
                case UInt64:
                case DateTime:
                    expectedValueCase = Metric.ValueCase.LONG_VALUE;
                    break;
                case Float:
                    expectedValueCase = Metric.ValueCase.FLOAT_VALUE;
                    break;
                case Double:
                    expectedValueCase = Metric.ValueCase.DOUBLE_VALUE;
                    break;
                case Boolean:
                    expectedValueCase = Metric.ValueCase.BOOLEAN_VALUE;
                    break;
                case String:
                case Text:
                case UUID:
                    expectedValueCase = Metric.ValueCase.STRING_VALUE;
                    break;
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
                    expectedValueCase = Metric.ValueCase.BYTES_VALUE;
                    break;
                case DataSet:
                    expectedValueCase = Metric.ValueCase.DATASET_VALUE;
                    break;
                case Template:
                    expectedValueCase = Metric.ValueCase.TEMPLATE_VALUE;
                    break;
                default:
                    break;
            }
        }else {
            // If Metric does not have datatype, assumed valid
            return true;

        }

		if(m.getIsNull()){
			// If Metric is null, Value case will not be set, so just confirms datatype is valid
			return expectedValueCase != Metric.ValueCase.VALUE_NOT_SET;
		}

        return DataType.forNumber(m.getDatatype()) != null && expectedValueCase != Metric.ValueCase.VALUE_NOT_SET && m.getValueCase() == expectedValueCase;
    }

	public static boolean hasValidDatatype(Payload.PropertyValue p) {
        // Checks that the datatype is valid for Property Values and that the value case corresponds with the datatype
        Payload.PropertyValue.ValueCase expectedValueCase = Payload.PropertyValue.ValueCase.VALUE_NOT_SET;
        if (DataType.forNumber(p.getType()) != null) {
            switch (DataType.forNumber(p.getType())) {
                case Int8:
                case Int16:
                case Int32:
                case UInt8:
                case UInt16:
                case UInt32:
                    expectedValueCase = Payload.PropertyValue.ValueCase.INT_VALUE;
                    break;
                case Int64:
                case UInt64:
                    expectedValueCase = Payload.PropertyValue.ValueCase.LONG_VALUE;
                    break;
                case Float:
                    expectedValueCase = Payload.PropertyValue.ValueCase.FLOAT_VALUE;
                    break;
                case Double:
                    expectedValueCase = Payload.PropertyValue.ValueCase.DOUBLE_VALUE;
                    break;
                case Boolean:
                    expectedValueCase = Payload.PropertyValue.ValueCase.BOOLEAN_VALUE;
                    break;
                case String:
                case Text:
                case UUID:
                    expectedValueCase = Payload.PropertyValue.ValueCase.STRING_VALUE;
                    break;
                default:
                    break;
            }
        } else {
            // If Property Value does not have type, assumed valid
            return true;

        }
		if(p.getIsNull()){
			// If Property Value is null, Value case will not be set, so just confirms datatype is valid
			return expectedValueCase != Payload.PropertyValue.ValueCase.VALUE_NOT_SET;
		}
        return DataType.forNumber(p.getType()) != null && expectedValueCase != Payload.PropertyValue.ValueCase.VALUE_NOT_SET && p.getValueCase() == expectedValueCase;
    }

	public static boolean hasValue(Metric m) {
		if (m.hasIsNull() && m.getIsNull()) {
			// A null value is valid
			return true;
		}

		switch(m.getValueCase()){
			case INT_VALUE:
				return m.hasIntValue();
			case LONG_VALUE:
				return m.hasLongValue();
			case FLOAT_VALUE:
				return m.hasFloatValue();
			case DOUBLE_VALUE:
				return m.hasDoubleValue();
			case BOOLEAN_VALUE:
				return m.hasBooleanValue();
			case STRING_VALUE:
				return m.hasStringValue();
			case BYTES_VALUE:
				return m.hasBytesValue();
			case DATASET_VALUE:
				return m.hasDatasetValue();
			case TEMPLATE_VALUE:
				return m.hasTemplateValue();
			case EXTENSION_VALUE:
				return m.hasExtensionValue();
			default:
				return false;
		}
	}

	public static boolean hasValue(Parameter p) {
		if (!p.hasType()) {
			return false;
		}

		switch (p.getValueCase()){
			case INT_VALUE:
				return p.hasIntValue();
			case LONG_VALUE:
				return p.hasLongValue();
			case FLOAT_VALUE:
				return p.hasFloatValue();
			case DOUBLE_VALUE:
				return p.hasDoubleValue();
			case BOOLEAN_VALUE:
				return p.hasBooleanValue();
			case STRING_VALUE:
				return p.hasStringValue();
			case EXTENSION_VALUE:
				return p.hasExtensionValue();
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

	public static StatePayload getHostPayload(String payloadString) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = null;
		try {
			json = mapper.readTree(payloadString);
			long retTimestamp = -1;
			if (json.has("timestamp")) {
				JsonNode timestamp = json.get("timestamp");
				if (timestamp.isLong()) {
					retTimestamp = timestamp.longValue();
					if (json.has("online")) {
						JsonNode online = json.get("online");
						if (online.isBoolean()) {
							return new StatePayload(online.booleanValue(), retTimestamp);
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static StatePayload getHostPayload(String payloadString, boolean expectOnline, boolean logMismatchErrors,
			long UTCWindow) {
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
					if (checkUTC(timestamp.longValue(), UTCWindow)) {
						isValidPayload = true;
						retTimestamp = timestamp.longValue();
					} else {
						logger.warn("StatePayload is invalid - Timestamp diff");
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
