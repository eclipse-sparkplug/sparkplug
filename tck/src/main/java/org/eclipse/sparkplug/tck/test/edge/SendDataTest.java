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

package org.eclipse.sparkplug.tck.test.edge;

import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_DDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDATA;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_PATH_NDEATH;
import static org.eclipse.sparkplug.tck.test.common.Constants.TOPIC_ROOT_SP_BV_1_0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_DDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_NDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_DEFINITION_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_PAYLOADS_TEMPLATE_INSTANCE_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDATA_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDATA_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDATA_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_DDATA_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDATA_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDATA_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDATA_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_TOPICS_NDATA_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_DDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_METRIC_TIMESTAMP_IN_UTC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_QOS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_RETAIN;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_SEQ;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_NDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_MEMBERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_NBIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_DEFINITION_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_MEMBERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS;
import static org.eclipse.sparkplug.tck.test.common.Requirements.PAYLOADS_TEMPLATE_INSTANCE_REF;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDATA_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDATA_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDATA_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_DDATA_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDATA_MQTT;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDATA_PAYLOAD;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDATA_SEQ_NUM;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDATA_TIMESTAMP;
import static org.eclipse.sparkplug.tck.test.common.Requirements.TOPICS_NDATA_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Utils.checkUTC;
import static org.eclipse.sparkplug.tck.test.common.Utils.hasValue;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResultIfNotFail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.Results;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCK.Utilities;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.DataType;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Template.Parameter;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.PayloadOrBuilder;
import org.eclipse.sparkplug.tck.test.common.Utils;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is the edge node Sparkplug send data test.  Data can be sent from edge
 * nodes and devices.
 * 
 * We will need to prompt the user to initiate sending some data messages from
 * an edge node and device, and then check that those messages adhere to the 
 * Sparkplug standard.
 *  
 */

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class SendDataTest extends TCKTest {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private final @NotNull ArrayList<String> testIds = new ArrayList<>();
	private Results.Config config = null;

	private HashMap<String, Payload.Template> definitions = new HashMap<String, Payload.Template>();

	String[] testId = { ID_TOPICS_NDATA_MQTT, ID_TOPICS_NDATA_SEQ_NUM, ID_TOPICS_NDATA_TIMESTAMP,
			ID_TOPICS_NDATA_PAYLOAD, ID_TOPICS_DDATA_MQTT, ID_TOPICS_DDATA_SEQ_NUM, ID_TOPICS_DDATA_TIMESTAMP,
			ID_TOPICS_DDATA_PAYLOAD, ID_PAYLOADS_NDATA_TIMESTAMP, ID_PAYLOADS_NDATA_SEQ, ID_PAYLOADS_NDATA_QOS,
			ID_PAYLOADS_NDATA_RETAIN, ID_PAYLOADS_DDATA_TIMESTAMP, ID_PAYLOADS_DDATA_SEQ, ID_PAYLOADS_DDATA_QOS,
			ID_PAYLOADS_DDATA_RETAIN, ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY, ID_PAYLOADS_TEMPLATE_DEFINITION_REF,
			ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT, ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY,
			ID_PAYLOADS_TEMPLATE_INSTANCE_REF, ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS,
			ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH, ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA,
			ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH, ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS,
			ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS, ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS,
			ID_PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS, ID_TOPICS_NDATA_TOPIC, ID_TOPICS_DDATA_TOPIC,
			ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC };
	private String testClientId = null;
	private String hostApplicationId;
	private String state = null;
	private TCK theTCK = null;
	private @NotNull Utilities utilities = null;
	private String groupId = null;
	private String edgeNodeId = null;
	private String deviceId = null;
	private boolean isEdgeNodeChecked = false, isDeviceChecked = false;

	// Host Application variables
	private boolean hostCreated = false;

	private HashMap<String, Map<Long, String>> edgeAliasMaps = new HashMap<>();

	public SendDataTest(TCK aTCK, Utilities utilities, String[] params, Results.Config config) {
		logger.info("Edge Node: {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		this.utilities = utilities;
		this.config = config;
		testIds.addAll(Arrays.asList(testId));

		if (params.length < 3) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters to send data test must be: groupId edgeNodeId deviceId");
			throw new IllegalArgumentException();
		}
		hostApplicationId = params[0];
		groupId = params[1];
		edgeNodeId = params[2];
		deviceId = params[3];
		logger.info("Parameters are Host application ID: {}, GroupId: {}, EdgeNodeId: {}, DeviceId: {}",
				hostApplicationId, groupId, edgeNodeId, deviceId);

		try {
			logger.info("UTCwindow is " + config.UTCwindow);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		definitions.clear();
	}

	public String getName() {
		return "Edge SendData";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
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
		// TODO Auto-generated method stub
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Edge send data test - publish - to topic: {} ", packet.getTopic());

		// ignore messages from clients we're not interested in
		if (testClientId != null && !clientId.equals(testClientId)) {
			logger.info("Ignoring message from {} when expecting {}", clientId, testClientId);
			return;
		}

		String cmd = "";
		String[] levels = packet.getTopic().split("/");
		if (levels.length >= 3) {
			cmd = levels[2];
			logger.info("Looking for {}", cmd);
		}

		if (cmd.equals(TOPIC_PATH_NBIRTH)) {
			checkNBIRTH(clientId, packet);
		} else if (cmd.equals(TOPIC_PATH_DBIRTH)) {
			checkDBIRTH(clientId, packet);
		} else if (cmd.equals(TOPIC_PATH_NDATA)) {
			// namespace/group_id/NDATA/edge_node_id
			checkNDATA(clientId, packet);
		} else if (cmd.equals(TOPIC_PATH_DDATA)) {
			// namespace/group_id/DDATA/edge_node_id/device_id
			checkDDATA(clientId, packet);
		}

		if (isEdgeNodeChecked && isDeviceChecked) {
			theTCK.endTest();
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDATA,
			id = ID_PAYLOADS_NDATA_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDATA,
			id = ID_PAYLOADS_NDATA_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDATA,
			id = ID_PAYLOADS_NDATA_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_NDATA,
			id = ID_PAYLOADS_NDATA_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDATA,
			id = ID_TOPICS_NDATA_MQTT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDATA,
			id = ID_TOPICS_NDATA_SEQ_NUM)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDATA,
			id = ID_TOPICS_NDATA_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_NDATA,
			id = ID_TOPICS_NDATA_PAYLOAD)
	@SpecAssertion(
			section = Sections.TOPICS_DATA_MESSAGE_NDATA,
			id = ID_TOPICS_NDATA_TOPIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC)
	public void checkNDATA(String clientId, PublishPacket packet) {
		logger.info("Send data test payload::check Edge Node data - Start");
		logger.debug(
				"Check Req: {} NDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.",
				ID_TOPICS_NDATA_MQTT);
		boolean isValidMQTT = (packet.getQos() == Qos.AT_MOST_ONCE && packet.getRetain() == false);
		testResults.put(ID_TOPICS_NDATA_MQTT, setResult(isValidMQTT, TOPICS_NDATA_MQTT));

		logger.debug("Check Req: {} NDATA messages MUST be published with the MQTT QoS set to 0.",
				ID_PAYLOADS_NDATA_QOS);
		boolean isValidQOS = (packet.getQos() == Qos.AT_MOST_ONCE);
		testResults.put(ID_PAYLOADS_NDATA_QOS, setResult(isValidQOS, PAYLOADS_NDATA_QOS));

		logger.debug("Check Req: {} NDATA messages MUST be published with the MQTT retain flag set to false.",
				ID_PAYLOADS_NDATA_RETAIN);
		boolean isValidNOTRetain = (packet.getRetain() == false);
		testResults.put(ID_PAYLOADS_NDATA_RETAIN, setResult(isValidNOTRetain, PAYLOADS_NDATA_RETAIN));

		// payload related tests
		PayloadOrBuilder inboundPayload = Utils.getSparkplugPayload(packet);
		Boolean[] bValid = checkValidPayload(inboundPayload);

		logger.debug("Check Req: {} Every NDATA message MUST include a sequence number.", ID_PAYLOADS_NDATA_SEQ);
		testResults.put(ID_PAYLOADS_NDATA_SEQ, setResult(bValid[0], PAYLOADS_NDATA_SEQ));

		logger.debug(
				"Check Req: {} The NDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node "
						+ "contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.",
				ID_TOPICS_NDATA_SEQ_NUM);
		testResults.put(ID_TOPICS_NDATA_SEQ_NUM, setResult(bValid[1], TOPICS_NDATA_SEQ_NUM));

		logger.debug(
				"Check Req: {} The NDATA MUST include a timestamp denoting the date and time the message was sent from the Edge Node.",
				ID_TOPICS_NDATA_TIMESTAMP);
		testResults.put(ID_TOPICS_NDATA_TIMESTAMP, setResult(bValid[2], TOPICS_NDATA_TIMESTAMP));

		logger.debug(
				"Check Req: {} NDATA messages MUST include a payload timestamp that denotes the time at which the message was published.",
				ID_PAYLOADS_NDATA_TIMESTAMP);
		testResults.put(ID_PAYLOADS_NDATA_TIMESTAMP, setResult(bValid[3], PAYLOADS_NDATA_TIMESTAMP));

		logger.debug(
				"Check Req: {} The NDATA MUST include the Edge Node's metrics that have changed since the last NBIRTH or NDATA message.",
				ID_TOPICS_NDATA_PAYLOAD);
		testResults.put(ID_TOPICS_NDATA_PAYLOAD, setResult(bValid[4], TOPICS_NDATA_PAYLOAD));

		logger.info("Send data test payload::check Edge Node data - {} - Finished",
				Arrays.stream(bValid).allMatch(Predicate.isEqual(true)));
		isEdgeNodeChecked = true;

		// Topic check
		String topic = packet.getTopic();
		boolean goodTopic =
				topic.equals(TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_NDATA + "/" + edgeNodeId);
		testResults.put(ID_TOPICS_NDATA_TOPIC, setResult(goodTopic, TOPICS_NDATA_TOPIC));

		// check templates
		String sparkplugDescriptor = groupId + "/" + edgeNodeId;
		for (Metric metric : inboundPayload.getMetricsList()) {
			String currentMetricName = metric.getName();
			if (!metric.hasName() && metric.hasAlias()) {
				currentMetricName = edgeAliasMaps.get(sparkplugDescriptor).get(metric.getAlias());
				logger.debug("Got currentMetricName from alias: {} -> {}", metric.getAlias(), currentMetricName);
			}
			if (metric.hasDatatype()) {
				DataType datatype = DataType.forNumber(metric.getDatatype());
				if (datatype == DataType.Template && metric.hasTemplateValue()) {
					checkInstance(sparkplugDescriptor, currentMetricName, metric.getTemplateValue(), TOPIC_PATH_DDATA);
				}
			}
			if (metric.hasTimestamp()) {
				setResultIfNotFail(testResults, checkUTC(metric.getTimestamp(), config.UTCwindow),
						ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC, PAYLOADS_METRIC_TIMESTAMP_IN_UTC);
			}
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_SEQ)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_QOS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_DDATA,
			id = ID_PAYLOADS_DDATA_RETAIN)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDATA,
			id = ID_TOPICS_DDATA_MQTT)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDATA,
			id = ID_TOPICS_DDATA_SEQ_NUM)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDATA,
			id = ID_TOPICS_DDATA_TIMESTAMP)
	@SpecAssertion(
			section = Sections.PAYLOADS_DESC_DDATA,
			id = ID_TOPICS_DDATA_PAYLOAD)
	@SpecAssertion(
			section = Sections.TOPICS_DATA_MESSAGE_DDATA,
			id = ID_TOPICS_DDATA_TOPIC)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_METRIC,
			id = ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC)
	public void checkDDATA(String clientId, PublishPacket packet) {
		logger.info("Send data test payload::check Device data - Start");

		logger.debug(
				"Check Req: {} DDATA messages MUST be published with MQTT QoS equal to 0 and retain equal to false.",
				ID_TOPICS_NDATA_MQTT);
		boolean isValidMQTT = (packet.getQos() == Qos.AT_MOST_ONCE && packet.getRetain() == false);
		testResults.put(ID_TOPICS_DDATA_MQTT, setResult(isValidMQTT, TOPICS_DDATA_MQTT));

		logger.debug("Check Req: {} DDATA messages MUST be published with the MQTT QoS set to 0.",
				ID_PAYLOADS_DDATA_QOS);
		boolean isValidQOS = (packet.getQos() == Qos.AT_MOST_ONCE);
		testResults.put(ID_PAYLOADS_DDATA_QOS, setResult(isValidQOS, PAYLOADS_DDATA_QOS));

		logger.debug("Check Req: {} DDATA messages MUST be published with the MQTT retain flag set to false.",
				ID_PAYLOADS_DDATA_RETAIN);
		boolean isValidNOTRetain = (packet.getRetain() == false);
		testResults.put(ID_PAYLOADS_DDATA_RETAIN, setResult(isValidNOTRetain, PAYLOADS_DDATA_RETAIN));

		// payload related tests
		PayloadOrBuilder inboundPayload = Utils.getSparkplugPayload(packet);
		Boolean[] bValid = checkValidPayload(inboundPayload);

		logger.debug("Check Req: {} Every DDATA message MUST include a sequence number.", ID_PAYLOADS_DDATA_SEQ);
		testResults.put(ID_PAYLOADS_DDATA_SEQ, setResult(bValid[0], PAYLOADS_DDATA_SEQ));

		logger.debug(
				"Check Req: {} The DDATA MUST include a sequence number in the payload and it MUST have a value of one greater than the previous MQTT message from the Edge Node "
						+ "contained unless the previous MQTT message contained a value of 255. In this case the sequence number MUST be 0.",
				ID_TOPICS_DDATA_SEQ_NUM);
		testResults.put(ID_TOPICS_DDATA_SEQ_NUM, setResult(bValid[1], TOPICS_DDATA_SEQ_NUM));

		logger.debug(
				"Check Req: {} The DDATA MUST include a timestamp denoting the date and time the message was sent from the Edge Node.",
				ID_TOPICS_DDATA_TIMESTAMP);
		testResults.put(ID_TOPICS_DDATA_TIMESTAMP, setResult(bValid[2], TOPICS_DDATA_TIMESTAMP));

		logger.debug(
				"Check Req: {} DDATA messages MUST include a payload timestamp that denotes the time at which the message was published.",
				ID_PAYLOADS_DDATA_TIMESTAMP);
		testResults.put(ID_PAYLOADS_DDATA_TIMESTAMP, setResult(bValid[3], PAYLOADS_DDATA_TIMESTAMP));

		logger.debug(
				"Check Req: {} The DDATA MUST include the Edge Node's metrics that have changed since the last DBIRTH or DDATA message.",
				ID_TOPICS_DDATA_PAYLOAD);
		testResults.put(ID_TOPICS_DDATA_PAYLOAD, setResult(bValid[4], TOPICS_DDATA_PAYLOAD));

		logger.info("Send data test payload::check Device data - {} - Finished",
				Arrays.stream(bValid).allMatch(Predicate.isEqual(true)));
		isDeviceChecked = true;

		// Topic check
		String topic = packet.getTopic();
		boolean goodTopic = topic.equals(
				TOPIC_ROOT_SP_BV_1_0 + "/" + groupId + "/" + TOPIC_PATH_DDATA + "/" + edgeNodeId + "/" + deviceId);
		testResults.put(ID_TOPICS_DDATA_TOPIC, setResult(goodTopic, TOPICS_DDATA_TOPIC));

		// check templates
		String sparkplugDescriptor = groupId + "/" + edgeNodeId + "/" + deviceId;
		for (Metric metric : inboundPayload.getMetricsList()) {
			String currentMetricName = metric.getName();
			if (!metric.hasName() && metric.hasAlias()) {
				currentMetricName = edgeAliasMaps.get(sparkplugDescriptor).get(metric.getAlias());
				logger.debug("Got currentMetricName from alias: {} -> {}", metric.getAlias(), currentMetricName);
			}
			if (metric.hasDatatype()) {
				DataType datatype = DataType.forNumber(metric.getDatatype());
				if (datatype == DataType.Template && metric.hasTemplateValue()) {
					checkInstance(sparkplugDescriptor, metric.getName(), metric.getTemplateValue(), TOPIC_PATH_DDATA);
				}
			}
			if (metric.hasTimestamp()) {
				setResultIfNotFail(testResults, checkUTC(metric.getTimestamp(), config.UTCwindow),
						ID_PAYLOADS_METRIC_TIMESTAMP_IN_UTC, PAYLOADS_METRIC_TIMESTAMP_IN_UTC);
			}
		}
	}

	private Boolean[] checkValidPayload(PayloadOrBuilder payload) {
		Boolean[] bValidPayload = new Boolean[] { false, false, false, false, false };

		if (payload != null) {
			long seqNum = payload.getSeq();
			bValidPayload[0] = true;
			bValidPayload[1] = (seqNum >= 0 && seqNum <= 255);
			bValidPayload[2] = payload.hasTimestamp();
			bValidPayload[3] = payload.hasTimestamp();
			List<Metric> metrics = payload.getMetricsList();

			ListIterator<Metric> metricIterator = metrics.listIterator();
			while (metricIterator.hasNext()) {
				Metric current = metricIterator.next();
				// TODO: Must include metrics that have changed
				// if (current.getName().equals(edgeMetric))
				bValidPayload[4] = true;
			}
		}
		return bValidPayload;
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_REF)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS)
	private void checkInstance(String sparkplugDescriptor, String instanceName, Payload.Template instance,
			String msgtype) {
		Payload.Template definition = null;
		if (instance.hasIsDefinition() && instance.getIsDefinition()) {
			setResultIfNotFail(testResults, !msgtype.equals(TOPIC_PATH_NBIRTH),
					ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY, PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY);
		} else {
			if (!instance.hasTemplateRef()) {
				setResultIfNotFail(testResults, false, ID_PAYLOADS_TEMPLATE_INSTANCE_REF,
						PAYLOADS_TEMPLATE_INSTANCE_REF);
			} else {
				String defname = instance.getTemplateRef();
				boolean defFound = definitions.containsKey(defname);

				setResultIfNotFail(testResults, defFound, ID_PAYLOADS_TEMPLATE_INSTANCE_REF,
						PAYLOADS_TEMPLATE_INSTANCE_REF);

				setResultIfNotFail(testResults, defFound, ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH,
						PAYLOADS_TEMPLATE_DEFINITION_NBIRTH);

				if (defFound) {
					definition = definitions.get(defname);
				}
			}
		}

		if (definition != null) {
			// check all the instance metrics are in the definition
			List<Metric> defmetrics = definition.getMetricsList();
			for (Metric metric : instance.getMetricsList()) {
				boolean found = false;
				String currentMetricName = metric.getName();
				if (!metric.hasName() && metric.hasAlias()) {
					currentMetricName = edgeAliasMaps.get(sparkplugDescriptor).get(metric.getAlias());
					logger.debug("Got currentMetricName from alias: {} -> {}", metric.getAlias(), currentMetricName);
				}
				for (Metric defmetric : defmetrics) {
					String defMetricName = defmetric.getName();
					if (!defmetric.hasName() && defmetric.hasAlias()) {
						defMetricName = edgeAliasMaps.get(sparkplugDescriptor).get(defmetric.getAlias());
					}
					if (currentMetricName.equals(defMetricName)) {
						found = true; // found instance metric in definition
						break;
					}
				}
				setResultIfNotFail(testResults, found, ID_PAYLOADS_TEMPLATE_DEFINITION_MEMBERS,
						PAYLOADS_TEMPLATE_DEFINITION_MEMBERS);

				setResultIfNotFail(testResults, found, ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS,
						PAYLOADS_TEMPLATE_INSTANCE_MEMBERS);
			}

			if (msgtype.equals(TOPIC_PATH_NBIRTH) || msgtype.equals(TOPIC_PATH_NDATA)
					|| msgtype.equals(TOPIC_PATH_DDATA)) {
				// check the definition metrics are in the instance
				for (Metric defmetric : defmetrics) {
					boolean found = false;
					for (Metric metric : instance.getMetricsList()) {
						String currentMetricName = metric.getName();
						if (!metric.hasName() && metric.hasAlias()) {
							currentMetricName = edgeAliasMaps.get(sparkplugDescriptor).get(metric.getAlias());
							logger.debug("Got currentMetricName from alias: {} -> {}", metric.getAlias(),
									currentMetricName);
						}
						if (currentMetricName.equals(defmetric.getName())) {
							found = true; // found definition metric in instance
							break;
						}
					}

					setResultIfNotFail(testResults,
							found || msgtype.equals(TOPIC_PATH_NDATA) || msgtype.equals(TOPIC_PATH_DDATA),
							ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH, PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_BIRTH);
					setResultIfNotFail(testResults,
							found && (msgtype.equals(TOPIC_PATH_NDATA) || msgtype.equals(TOPIC_PATH_DDATA)),
							ID_PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA, PAYLOADS_TEMPLATE_INSTANCE_MEMBERS_DATA);
				}
			}

			// now check parameters
			if (instance.getParametersCount() > 0) {
				logger.debug("Found parameters in {}: {}", instanceName, instance.getParametersCount());
				for (Parameter parm : instance.getParametersList()) {
					if (parm.hasName()) {
						String instance_parm_name = parm.getName();
						setResultIfNotFail(testResults, true, ID_PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS,
								PAYLOADS_TEMPLATE_INSTANCE_PARAMETERS);
						boolean parm_found = false;
						if (definition.getParametersCount() > 0) {
							for (Parameter def_parm : definition.getParametersList()) {
								if (def_parm.hasName() && def_parm.getName().equals(instance_parm_name)) {
									parm_found = true;
									break;
								}
							}
							setResultIfNotFail(testResults, parm_found, ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS,
									PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS);
						}
					}
				}
			}
		} else {
			logger.warn("Definition is null for {}", instanceName);
		}
	}

	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_REF)
	@SpecAssertion(
			section = Sections.PAYLOADS_B_TEMPLATE,
			id = ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT)
	public void checkNBIRTH(String clientId, PublishPacket packet) {

		setResultIfNotFail(testResults, true, ID_PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY,
				PAYLOADS_TEMPLATE_DEFINITION_NBIRTH_ONLY);

		final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);

		// record definitions
		String id = groupId + "/" + edgeNodeId;
		boolean aliasMapInitialized = false;
		for (Metric metric : sparkplugPayload.getMetricsList()) {
			if (metric.hasAlias()) {
				Map<Long, String> aliasMap = edgeAliasMaps.get(id);
				if (!aliasMapInitialized) {
					aliasMap = edgeAliasMaps.computeIfAbsent(id, (k) -> new HashMap<>());
					aliasMap.clear();
					aliasMapInitialized = true;
				}

				logger.debug("Creating alias: {} -> {}", metric.getAlias(), metric.getName());
				aliasMap.put(metric.getAlias(), metric.getName());
			}

			if (metric.hasDatatype()) {
				DataType datatype = DataType.forNumber(metric.getDatatype());
				if (datatype == DataType.Template && metric.hasTemplateValue()) {
					Payload.Template template = metric.getTemplateValue();
					if (template.hasIsDefinition() && template.getIsDefinition()) {
						// this is a definition
						definitions.put(metric.getName(), template);

						setResultIfNotFail(testResults, !template.hasTemplateRef(), ID_PAYLOADS_TEMPLATE_DEFINITION_REF,
								PAYLOADS_TEMPLATE_DEFINITION_REF);

						// now check parameters for values
						if (template.getParametersCount() > 0) {
							for (Parameter parm : template.getParametersList()) {
								logger.debug("Parameter hasValue(): {}->{}", parm.getName(), hasValue(parm));
								// Whether or not there are parameter values this test should pass
								setResult(testResults, true, ID_PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT,
										PAYLOADS_TEMPLATE_DEFINITION_PARAMETERS_DEFAULT);
							}
						}
					}
				}
			}
		}

		// now check any instances included
		for (Metric metric : sparkplugPayload.getMetricsList()) {
			if (metric.hasDatatype()) {
				DataType datatype = DataType.forNumber(metric.getDatatype());
				if (datatype == DataType.Template && metric.hasTemplateValue()) {
					Payload.Template template = metric.getTemplateValue();
					if (!template.hasIsDefinition() || !template.getIsDefinition()) {
						checkInstance(id, metric.getName(), template, TOPIC_PATH_NBIRTH);
					}
				}
			}
		}
	}

	public void checkDBIRTH(String clientId, PublishPacket packet) {
		final PayloadOrBuilder sparkplugPayload = Utils.getSparkplugPayload(packet);

		// check templates
		String id = groupId + "/" + edgeNodeId + "/" + deviceId;
		boolean aliasMapInitialized = false;
		for (Metric m : sparkplugPayload.getMetricsList()) {
			if (m.hasAlias()) {
				Map<Long, String> aliasMap = edgeAliasMaps.get(id);
				if (!aliasMapInitialized) {
					aliasMap = edgeAliasMaps.computeIfAbsent(id, (k) -> new HashMap<>());
					aliasMap.clear();
					aliasMapInitialized = true;
				}

				logger.debug("Creating alias: {} -> {}", m.getAlias(), m.getName());
				aliasMap.put(m.getAlias(), m.getName());
			}

			if (m.hasDatatype()) {
				DataType datatype = DataType.forNumber(m.getDatatype());
				if (datatype == DataType.Template && m.hasTemplateValue()) {
					checkInstance(id, m.getName(), m.getTemplateValue(), TOPIC_PATH_DBIRTH);
				}
			}
		}
	}
}