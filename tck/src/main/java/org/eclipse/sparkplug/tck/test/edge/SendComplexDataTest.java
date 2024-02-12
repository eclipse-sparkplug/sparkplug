
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
package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NCMD;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_COLUMN_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_PARAMETER_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_TYPES_DEF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_TYPES_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_TYPES_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DATASET_TYPES_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_DATATYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NAME_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DATASET_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_REF_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_REF_INSTANCE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_VERSION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_COLUMN_NUM_HEADERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_COLUMN_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_PARAMETER_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_TYPES_DEF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_TYPES_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_TYPES_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DATASET_TYPES_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_DATATYPE_NOT_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_DATATYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_DATATYPE_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NAME_REQUIREMENT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DATASET_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_IS_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_VALUE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_REF_DEFINITION;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_REF_INSTANCE;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_VERSION;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResultIfNotFail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

/**
 * This is the edge node Sparkplug payload validation.
 *
 * @author Anja Helmbrecht-Schaar
 */
@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class SendComplexDataTest extends TCKTest {

	private static final @NotNull Logger logger = LoggerFactory.getLogger("Sparkplug");
	public static final String PROPERTY_KEY_QUALITY = "Quality";

	public static final @NotNull List<String> testIds = List.of(ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED,
			ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE, ID_PAYLOADS_METRIC_DATATYPE_VALUE,
			ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE, ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE,
			ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE, ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE,
			ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ, ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE,
			ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE, ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT,
			ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ, ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT, ID_PAYLOADS_NAME_REQUIREMENT,
			ID_PAYLOADS_DATASET_COLUMN_SIZE, ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS, ID_PAYLOADS_DATASET_TYPES_DEF,
			ID_PAYLOADS_DATASET_TYPES_TYPE, ID_PAYLOADS_DATASET_TYPES_VALUE, ID_PAYLOADS_DATASET_TYPES_NUM,
			ID_PAYLOADS_DATASET_PARAMETER_TYPE_REQ, ID_PAYLOADS_TEMPLATE_DATASET_VALUE,
			ID_PAYLOADS_TEMPLATE_IS_DEFINITION, ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION,
			ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION, ID_PAYLOADS_TEMPLATE_DEFINITION_REF,
			ID_PAYLOADS_TEMPLATE_INSTANCE_REF, ID_PAYLOADS_TEMPLATE_REF_DEFINITION,
			ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION, ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE,
			ID_PAYLOADS_TEMPLATE_VERSION, ID_PAYLOADS_TEMPLATE_REF_INSTANCE,
			ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED, ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE,
			ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE, ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE,
			ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ, ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE);

	private final @NotNull TCK theTCK;
	private @NotNull Utilities utilities = null;

	private @NotNull String deviceId;
	private @NotNull String groupId;
	private @NotNull String edgeNodeId;
	private @NotNull String hostApplicationId;
	private @NotNull long seqUnassigned = -1;

	// Host Application variables
	private boolean hostCreated = false;
	private @NotNull String testClientId = null;

	public SendComplexDataTest(TCK aTCK, Utilities utilities, String[] parms, Results.Config config) {
		logger.info("{}. Parameters: {} ", getName(), Arrays.asList(parms));
		theTCK = aTCK;
		this.utilities = utilities;

		if (parms.length < 4) {
			log("Not enough parameters: " + Arrays.toString(parms));
			log(getName() + " Parameters must be: hostId groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = parms[0];
		groupId = parms[1];
		edgeNodeId = parms[2];
		deviceId = parms[3];
		logger.info("Parameters are HostId: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}", hostApplicationId, groupId,
				edgeNodeId, deviceId);

		if (Utils.checkHostApplicationIsOnline(hostApplicationId).get()) {
			logger.info("Host Application is online, so using that");
		} else {
			logger.info("Creating host application");
			try {
				utilities.getHostApps().hostOnline(hostApplicationId, true);
			} catch (MqttException m) {
				throw new IllegalStateException();
			}
			hostCreated = true;
		}
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		if (hostCreated) {
			try {
				utilities.getHostApps().hostOffline();
			} catch (MqttException m) {
				logger.error("endTest", m);
			}
		}
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Edge SendComplexData";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(final @NotNull String clientId, final @NotNull ConnectPacket packet) {
		/* Determine if this the connect packet for the Edge node under test.
		 * Set the clientid if so. */
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			String willTopic = willPublishPacket.getTopic();
			if (willTopic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDEATH + "/" + edgeNodeId)) {
				testClientId = clientId;
				logger.info("Send data test - connect - client id is " + clientId);
			}
		}
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {

	}

	@Override
	public void subscribe(final @NotNull String clientId, final @NotNull SubscribePacket packet) {

	}

	@Override
	public void publish(final @NotNull String clientId, final @NotNull PublishPacket packet) {
		final String topic = packet.getTopic();
		logger.info("Edge - Payload validation test - publish - topic: {}", topic);

		// ignore messages from clients we're not interested in
		if (testClientId != null && !clientId.equals(testClientId)) {
			logger.info("Ignoring message from {} when expecting {}", clientId, testClientId);
			return;
		}

		boolean isSparkplugTopic = topic.startsWith(TOPIC_ROOT_SP_BV_1_0);
		if (!isSparkplugTopic) {
			logger.error("Skip Edge payload validation - no sparkplug payload.");
			return;
		}
		boolean isDataTopic =
				isSparkplugTopic && (topic.contains(TOPIC_PATH_DDATA) || topic.contains(TOPIC_PATH_NDATA));
		boolean isCommandTopic =
				isSparkplugTopic && (topic.contains(TOPIC_PATH_NCMD) || topic.contains(TOPIC_PATH_DCMD));

		checkPropertiesValidType(packet, topic);
		checkSequenceNumberIncluded(packet, topic);

		if (isDataTopic) {
			checkDataTopicPayload(clientId, packet, topic);
		} else if (isCommandTopic) {
			checkCommandTopicPayload(clientId, packet, topic);
		} else {
			checkDatatypeValidType(packet);
		}

		checkDataSet(packet);
		checkTemplate(packet);

		// if we have results for every assertion, end the test
		// otherwise the user will have to end it
		if (testResults.size() == testIds.size()) {
			theTCK.endTest();
		} else {
			log(getName() + " " + testResults.size() + " results obtained out of " + testIds.size() + " total.");
		}
	}

	private void checkDataTopicPayload(final @NotNull String clientId, final @NotNull PublishPacket packet,
			final @NotNull String topic) {
		if (clientId.contentEquals(deviceId) || topic.contains(groupId) && topic.contains(edgeNodeId)) {
			final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);
			if (sparkplugPayload != null) {
				checkPayloadsNameRequirement(sparkplugPayload);
				checkAliasInData(sparkplugPayload, topic);
				checkMetricsDataTypeNotRec(sparkplugPayload, topic);
				checkPayloadsNameInDataRequirement(sparkplugPayload);
			} else {
				logger.error("Skip Edge payload validation - no sparkplug payload.");
			}
		}
	}

	private void checkCommandTopicPayload(final @NotNull String clientId, final @NotNull PublishPacket packet,
			final @NotNull String topic) {
		if (clientId.contentEquals(deviceId) || topic.contains(groupId) && topic.contains(edgeNodeId)) {
			final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);
			if (sparkplugPayload != null) {
				checkAliasInData(sparkplugPayload, topic);
				checkMetricsDataTypeNotRec(sparkplugPayload, topic);
				checkPayloadsNameRequirement(sparkplugPayload);
			} else {
				logger.error("Skip Edge payload validation - no sparkplug payload.");
			}
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_PAYLOAD,
			id = ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED)
	public void checkSequenceNumberIncluded(final @NotNull PublishPacket packet, String topic) {
		logger.debug(
				"Check Req: {} A sequence number MUST be included in the payload of every Sparkplug MQTT message except NDEATH messages.",
				ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED);
		boolean isValid = false;
		final PayloadOrBuilder result = Utils.getSparkplugPayload(packet);
		if (result == null) {
			isValid = false;
			logger.error("Check req set for : {}", ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED);
		} else {
			if (result.getSeq() >= 0) {
				isValid = true;
			} else if (result.getSeq() == seqUnassigned && topic.contains(TOPIC_PATH_NDEATH)) {
				isValid = true;
			}
		}
		testResults.put(ID_PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED,
				setResult(isValid, PAYLOADS_SEQUENCE_NUM_ALWAYS_INCLUDED));
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_DATATYPE_VALUE)
	public void checkDatatypeValidType(final @NotNull PublishPacket packet) {
		boolean isValid_DataType = true;
		boolean isValid_DataTypeValue = true;

		logger.debug("Check Req: {} The datatype MUST be an unsigned 32-bit integer representing the datatype.",
				ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE);

		PayloadOrBuilder result = Utils.getSparkplugPayload(packet);

		if (result == null) {
			isValid_DataType = false;
			isValid_DataTypeValue = false;
			logger.error("Check req set for : {}", ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE);
		}

		logger.debug(
				"Check Req: The datatype MUST be one of the enumerated values as shown in the valid Sparkplug Data Types.");
		if (result != null) {
			for (Metric m : result.getMetricsList()) {
				if(!Utils.hasValidDatatype(m)){
					isValid_DataType = false;
					isValid_DataTypeValue = false;
					break;
				}
			}
		}

		setResultIfNotFail(testResults,isValid_DataType,ID_PAYLOADS_METRIC_DATATYPE_VALUE_TYPE,PAYLOADS_METRIC_DATATYPE_VALUE_TYPE);

		setResultIfNotFail(testResults,isValid_DataTypeValue,ID_PAYLOADS_METRIC_DATATYPE_VALUE,PAYLOADS_METRIC_DATATYPE_VALUE);
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_PROPERTYSET,
			id = ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PROPERTYSET,
			id = ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PROPERTYVALUE,
			id = ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PROPERTYVALUE,
			id = ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_PROPERTYVALUE,
			id = ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_QUALITY_CODES,
			id = ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_QUALITY_CODES,
			id = ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE)
	public void checkPropertiesValidType(final @NotNull PublishPacket packet, String topic) {
		boolean isValid_KeyArraySize = true;
		boolean isValid_PropertyValueType = true;
		boolean isValid_PropertyValueTypeValue = true;
		boolean isValid_PropertyValueTypeReq = true;
		boolean qualityCodeSettingIsUsed = false;

		logger.debug("Check Req: {} The datatype MUST be an unsigned 32-bit integer representing the datatype.",
				ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE);

		PayloadOrBuilder result = Utils.getSparkplugPayload(packet);

		if (result == null) {
			isValid_KeyArraySize = false;
			isValid_PropertyValueType = false;
			isValid_PropertyValueTypeReq = false;
			logger.error("Check req set for : {}", Sections.PAYLOADS_B_PROPERTYVALUE);
		} else {
			logger.debug(
					"Check Req: {} The array of keys in a PropertySet MUST contain the same number of values included in the array of PropertyValue objects.",
					PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE);
			logger.debug(
					"Check Req: {} The array of values in a PropertySet MUST contain the same number of items that are in the keys array.",
					PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE);

			logger.debug("Check Req: {} This MUST be an unsigned 32-bit integer representing the datatype.",
					ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE);
			logger.debug(
					"Check Req: {} This value MUST be one of the enumerated values as shown in the Sparkplug Basic Data Types or the Sparkplug Property Value Data Types.",
					ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE);
			logger.debug(
					"Check Req: {} This MUST be included in Property Value Definitions in NBIRTH and DBIRTH messages.",
					ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ);

			for (Metric m : result.getMetricsList()) {
				if (m.hasProperties()
						&& m.getProperties().getValuesList().size() != m.getProperties().getKeysList().size()) {
					isValid_KeyArraySize = false;
				}
				// execute always, but set only if one is true
				qualityCodeSettingIsUsed = checkQualityCodeRequirement(m) || qualityCodeSettingIsUsed;

				for (int i = 0; i < m.getProperties().getValuesCount(); i++) {
					final Payload.PropertyValue propertyValue = m.getProperties().getValues(i);
					if(!Utils.hasValidDatatype(propertyValue)){
						isValid_PropertyValueType = false;
						isValid_PropertyValueTypeValue = false;
					}
					if ((topic.contains(TOPIC_PATH_NBIRTH) || topic.contains(TOPIC_PATH_DBIRTH))
							&& Payload.PropertyValue.ValueCase.VALUE_NOT_SET == propertyValue.getValueCase()) {
						isValid_PropertyValueTypeReq = false;
					}
				}
			}
		}
		if (!qualityCodeSettingIsUsed) {
			// option was not used -so test is than passed by default - otherwise the result is set in the subroutine
			testResults.put(ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE,
					setResult(true, PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE));
			testResults.put(ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE,
					setResult(true, PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE));

		}
		testResults.put(ID_PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE,
				setResult(isValid_KeyArraySize, PAYLOADS_PROPERTYSET_KEYS_ARRAY_SIZE));
		testResults.put(ID_PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE,
				setResult(isValid_KeyArraySize, PAYLOADS_PROPERTYSET_VALUES_ARRAY_SIZE));

		setResultIfNotFail(testResults,isValid_PropertyValueType,ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE,PAYLOADS_METRIC_PROPERTYVALUE_TYPE_TYPE);
		setResultIfNotFail(testResults,isValid_PropertyValueTypeValue,ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE,PAYLOADS_METRIC_PROPERTYVALUE_TYPE_VALUE);
		setResultIfNotFail(testResults,isValid_PropertyValueTypeReq,ID_PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ,PAYLOADS_METRIC_PROPERTYVALUE_TYPE_REQ);

	}

	private boolean checkQualityCodeRequirement(Metric m) {
		// optional key - but if it is used - it must fit to requirements
		boolean qualityCodeSettingIsUsed = false;
		for (int i = 0; i < m.getProperties().getValuesCount(); i++) {
			final String key = m.getProperties().getKeys(i);
			if (key.equals(PROPERTY_KEY_QUALITY)) {
				final Payload.PropertyValue propertyValue = m.getProperties().getValues(i);
				logger.debug(
						"Check: Req: Property Value MUST be a value of 3 which represents a Signed 32-bit Integer.");
				if (!(propertyValue.getType() == Payload.PropertyValue.ValueCase.LONG_VALUE.getNumber())) {
					testResults.put(ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE,
							setResult(false, PAYLOADS_PROPERTYSET_QUALITY_VALUE_TYPE));
				}
				logger.debug(
						"Check: Req: 'value' of the Property Value MUST be an int_value and be one of the valid quality codes of 0, 192, or 500.");
				if (!(propertyValue.getLongValue() == 0 || propertyValue.getLongValue() == 192
						|| propertyValue.getLongValue() == 500)) {
					testResults.put(ID_PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE,
							setResult(false, PAYLOADS_PROPERTYSET_QUALITY_VALUE_VALUE));
				}
				qualityCodeSettingIsUsed = true;
			}
		}
		return qualityCodeSettingIsUsed;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT)
	public void checkAliasInData(final @NotNull PayloadOrBuilder sparkplugPayload, String topic) {
		logger.debug(
				"Check Req: NDATA, DDATA, NCMD, and DCMD messages MUST only include an alias and the metric name MUST be excluded.");

		boolean isValid = false;
		if (topic.contains(TOPIC_PATH_NDATA) || topic.contains(TOPIC_PATH_DDATA) || topic.contains(TOPIC_PATH_NCMD)
				|| topic.contains(TOPIC_PATH_DCMD)) {
			for (Metric m : sparkplugPayload.getMetricsList()) {
				if (!m.getIsNull() && ((m.hasAlias() && (!m.hasName() || m.getName().length() == 0))
						|| (!m.hasAlias() && (m.hasName() || m.getName().length() > 0))))
					isValid = true;
				break;
			}
			testResults.put(ID_PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT,
					setResult(isValid, PAYLOADS_ALIAS_DATA_CMD_REQUIREMENT));
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ)
	public void checkMetricsDataTypeNotRec(final @NotNull PayloadOrBuilder sparkplugPayload, String topic) {
		logger.debug(
				"Check Req: The datatype SHOULD NOT be included with metric definitions in NDATA, NCMD, DDATA, and DCMD messages.");
		boolean isValid = true;
		if (topic.contains(TOPIC_PATH_NDATA) || topic.contains(TOPIC_PATH_DDATA) || topic.contains(TOPIC_PATH_NCMD)
				|| topic.contains(TOPIC_PATH_DCMD)) {
			for (Metric m : sparkplugPayload.getMetricsList()) {
				if (m.hasDatatype()) {
					logger.info("On topic={} - Invalid payload: {}", topic, sparkplugPayload);
					isValid = false;
					break;
				}
			}
			testResults.put(ID_PAYLOADS_METRIC_DATATYPE_NOT_REQ,
					Utils.setShouldResult(isValid, PAYLOADS_METRIC_DATATYPE_NOT_REQ));
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_NAME_REQUIREMENT)
	public void checkPayloadsNameRequirement(final @NotNull PayloadOrBuilder sparkplugPayload) {
		logger.debug("Check Req: The name MUST be included with every metric unless aliases are being used.");
		boolean isValid = true;
		for (Metric m : sparkplugPayload.getMetricsList()) {
			if (m.getIsNull() || !m.hasName() && !m.hasAlias()) {
				isValid = false;
				break;
			}
		}
		testResults.put(ID_PAYLOADS_NAME_REQUIREMENT, setResult(isValid, PAYLOADS_NAME_REQUIREMENT));
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT)
	public void checkPayloadsNameInDataRequirement(final @NotNull PayloadOrBuilder sparkplugPayload) {
		logger.debug(
				"Check Req: The timestamp MUST be included with every metric in all NBIRTH, DBIRTH, NDATA, and DDATA messages.");
		boolean isValid = true;
		for (Metric m : sparkplugPayload.getMetricsList()) {
			if (!m.hasTimestamp()) {
				isValid = false;
				break;
			}
		}
		testResults.put(ID_PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT,
				setResult(isValid, PAYLOADS_NAME_BIRTH_DATA_REQUIREMENT));
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_COLUMN_SIZE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_TYPES_DEF)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_TYPES_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_TYPES_VALUE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_TYPES_NUM)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET,
			id = ID_PAYLOADS_DATASET_PARAMETER_TYPE_REQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DATASET_DATASETVALUE,
			id = ID_PAYLOADS_TEMPLATE_DATASET_VALUE)
	public boolean checkDataSet(final @NotNull PublishPacket packet) {
		final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);
		boolean hasDataSet = false;
		for (Metric m : sparkplugPayload.getMetricsList()) {
			if (m.hasDatatype()) {
				DataType datatype = DataType.forNumber(m.getDatatype());

				if (datatype == DataType.DataSet && m.hasDatasetValue()) {
					hasDataSet = true;
					Payload.DataSet d = m.getDatasetValue();

					testResults.put(ID_PAYLOADS_DATASET_COLUMN_SIZE,
							setResult(d.hasNumOfColumns() && (d.getNumOfColumns() >= 0), PAYLOADS_DATASET_COLUMN_SIZE));

					testResults.put(ID_PAYLOADS_DATASET_COLUMN_NUM_HEADERS,
							setResult(d.getColumnsCount() == d.getTypesCount(), PAYLOADS_DATASET_COLUMN_NUM_HEADERS));

					// the types allowed in dataset elements
					HashSet<DataType> valueTypes = new HashSet<DataType>(Arrays.asList(DataType.UInt32, DataType.UInt64,
							DataType.Float, DataType.Double, DataType.Boolean, DataType.String));

					// check that the types are valid
					List<Integer> types = (List<Integer>) d.getTypesList();
					boolean validtypes = true;
					boolean uint32types = true;
					if (types.size() == d.getTypesCount()) {
						for (int i = 0; i < types.size(); ++i) {
							int curtype = d.getTypes(i);
							if (curtype <= 0 && curtype >= DataType.Text.getNumber()) {
								logger.warn("Bad uint32type DataSet type '{}' in Types: {}", curtype, types);
								uint32types = false;
							}
							if (!types.contains(curtype)) {
								logger.warn("Invalid DataSet type '{}' in Types: {}", curtype, types);
								validtypes = false;
							}
						}
					}
					testResults.put(ID_PAYLOADS_DATASET_TYPES_DEF, setResult(uint32types, PAYLOADS_DATASET_TYPES_DEF));
					testResults.put(ID_PAYLOADS_DATASET_TYPES_TYPE,
							setResult(uint32types, PAYLOADS_DATASET_TYPES_TYPE));
					testResults.put(ID_PAYLOADS_DATASET_TYPES_VALUE,
							setResult(validtypes, PAYLOADS_DATASET_TYPES_VALUE));

					testResults.put(ID_PAYLOADS_DATASET_TYPES_NUM,
							setResult(d.getTypesCount() == d.getColumnsCount(), PAYLOADS_DATASET_TYPES_NUM));

					final String topic = packet.getTopic();
					String[] topicParts = topic.split("/");
					if (topicParts.length > 2
							&& (topicParts[2].equals(TOPIC_PATH_NBIRTH) || topicParts[2].equals(TOPIC_PATH_DBIRTH))) {
						testResults.put(ID_PAYLOADS_DATASET_PARAMETER_TYPE_REQ,
								setResult(validtypes, PAYLOADS_DATASET_PARAMETER_TYPE_REQ));
					}

					boolean valuetypes = true;
					for (int i = 0; i < d.getRowsCount(); ++i) {
						Payload.DataSet.Row row = d.getRows(i);
						for (int j = 0; j < row.getElementsCount(); ++j) {
							Payload.DataSet.DataSetValue val = row.getElements(j);
							if (!val.hasIntValue() && !val.hasLongValue() && !val.hasFloatValue()
									&& !val.hasDoubleValue() && !val.hasBooleanValue() && !val.hasStringValue()) {
								valuetypes = false;
							}
						}
					}
					testResults.put(ID_PAYLOADS_TEMPLATE_DATASET_VALUE,
							setResult(valuetypes, PAYLOADS_TEMPLATE_DATASET_VALUE));

				}
			}
		}
		return hasDataSet;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_IS_DEFINITION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_REF)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_REF_DEFINITION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_REF)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_REF_INSTANCE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_VERSION)

	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE_PARAMETER,
			id = ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE)
	public boolean checkTemplate(final @NotNull PublishPacket packet) {
		final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);
		boolean hasTemplate = false;
		for (Metric m : sparkplugPayload.getMetricsList()) {
			if (m.hasDatatype()) {
				DataType datatype = DataType.forNumber(m.getDatatype());

				if (datatype == DataType.Template && m.hasTemplateValue()) {
					Payload.Template t = m.getTemplateValue();

					hasTemplate = true;

					// Template definitions must be in NBIRTH messages
					testResults.put(ID_PAYLOADS_TEMPLATE_IS_DEFINITION,
							setResult(t.hasIsDefinition(), PAYLOADS_TEMPLATE_IS_DEFINITION));

					if (t.hasIsDefinition()) {
						if (t.getIsDefinition()) {
							testResults.put(ID_PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION,
									setResult(t.hasIsDefinition(), PAYLOADS_TEMPLATE_DEFINITION_IS_DEFINITION));
							testResults.put(ID_PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION,
									setResult(t.hasIsDefinition() && t.getIsDefinition(),
											PAYLOADS_TEMPLATE_IS_DEFINITION_DEFINITION));
							// templateRef must be omitted
							testResults.put(ID_PAYLOADS_TEMPLATE_DEFINITION_REF,
									setResult(t.hasTemplateRef() == false, PAYLOADS_TEMPLATE_DEFINITION_REF));
							testResults.put(ID_PAYLOADS_TEMPLATE_REF_DEFINITION,
									setResult(t.hasTemplateRef() == false, PAYLOADS_TEMPLATE_REF_DEFINITION));
						} else {
							testResults.put(ID_PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION,
									setResult(t.hasIsDefinition(), PAYLOADS_TEMPLATE_INSTANCE_IS_DEFINITION));
							testResults.put(ID_PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE,
									setResult(t.hasIsDefinition() && (t.getIsDefinition() == false),
											PAYLOADS_TEMPLATE_IS_DEFINITION_INSTANCE));
							// templateRef must be included
							testResults.put(ID_PAYLOADS_TEMPLATE_INSTANCE_REF,
									setResult(t.hasTemplateRef(), PAYLOADS_TEMPLATE_INSTANCE_REF));
							boolean ref = false;
							if (t.hasTemplateRef()) {
								ref = Utils.checkUTF8String(t.getTemplateRef());
							}
							testResults.put(ID_PAYLOADS_TEMPLATE_REF_INSTANCE,
									setResult(ref, PAYLOADS_TEMPLATE_REF_INSTANCE));
						}
					}

					boolean version = true;
					if (t.hasVersion()) {
						version = Utils.checkUTF8String(t.getVersion());
					}
					testResults.put(ID_PAYLOADS_TEMPLATE_VERSION, setResult(version, PAYLOADS_TEMPLATE_VERSION));

					if (t.getParametersCount() > 0) {
						for (Payload.Template.Parameter p : t.getParametersList()) {

							if (t.getIsDefinition()) {
								testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED,
										setResult(p.hasName(), PAYLOADS_TEMPLATE_PARAMETER_NAME_REQUIRED));
							}
							if (p.hasName()) {
								testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE, setResult(
										Utils.checkUTF8String(p.getName()), PAYLOADS_TEMPLATE_PARAMETER_NAME_TYPE));
							}

							if (p.hasType()) {
								int curtype = p.getType();
								boolean isBasicType = true;
								if (curtype < 0 || curtype > DataType.Text.getNumber()) {
									logger.info("FOR {} the TYPE is: {} AND {}", p.getName(), curtype, p);
									isBasicType = false;
								}
								testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE,
										setResult(isBasicType, PAYLOADS_TEMPLATE_PARAMETER_VALUE_TYPE));
								testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE,
										setResult(isBasicType, PAYLOADS_TEMPLATE_PARAMETER_TYPE_VALUE));

							}

							final String topic = packet.getTopic();
							String[] topicParts = topic.split("/");
							if (topicParts.length > 2 && (topicParts[2].equals(TOPIC_PATH_NBIRTH)
									|| topicParts[2].equals(TOPIC_PATH_DBIRTH))) {

								if (t.getIsDefinition()) {
									testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ,
											setResult(p.hasType(), PAYLOADS_TEMPLATE_PARAMETER_TYPE_REQ));
								}
							}

							boolean valuetype = true;
							if (!p.hasIntValue() && !p.hasLongValue() && !p.hasFloatValue() && !p.hasDoubleValue()
									&& !p.hasBooleanValue() && !p.hasStringValue()) {
								valuetype = false;
							}
							testResults.put(ID_PAYLOADS_TEMPLATE_PARAMETER_VALUE,
									setResult(valuetype, PAYLOADS_TEMPLATE_PARAMETER_VALUE));
						}
					}
				}
			}
		}
		return hasTemplate;
	}
}
