/*******************************************************************************
 * Copyright (c) 2021 Ian Craggs
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.packets.general.UserProperties;
import java.util.List;
import java.util.Optional;
import org.eclipse.tahu.message.SparkplugBPayloadDecoder;
import org.eclipse.tahu.message.model.SparkplugBPayload;
import org.eclipse.tahu.message.model.Metric;
import org.eclipse.tahu.message.model.MetricDataType;
import java.util.ArrayList;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.nio.ByteBuffer;
import java.util.Date;

@SpecVersion(
	spec = "sparkplug", 
	version = "3.0.0-SNAPSHOT")
public class SessionEstablishment extends TCKTest {

	private static Logger logger = LoggerFactory.getLogger("Sparkplug");
	private HashMap<String, String> testResults = new HashMap<String, String>();
	String[] testIds = {
		"payloads-ndeath-will-message-qos", 
		"payloads-ndeath-seq", 
		"payloads-ndeath-will-message-retain", 
		"payloads-ndeath-will-message", 
		"edge-death-bdseq",
		"payloads-nbirth-qos", 
		"payloads-nbirth-retain", 
		"payloads-nbirth-seq", 
		"payloads-nbirth-bdseq", 
		"edge-birth-metrics",
		"payloads-nbirth-timestamp", 
		"payloads-nbirth-bdseq-inc", 
		"payloads-ndeath-bdseq", 
		"edge-subscribe-ncmd", 
		"edge-subscribe-dcmd",
		"message-flow-edge-node-birth-publish-subscribe"
	};
	private String myClientId = null;
	private String state = null;
	private TCK theTCK = null;
	private String host_application_id = null; // The primary host application id to be used
	private boolean commands_supported = true; // Are commands supported by the edge node?
	private long death_bdSeq = -1;
	private long birth_bdSeq = -1;
	private String group_id = null;
	private String edge_node_id = null;
	private boolean ncmd_found = false;
	private boolean dcmd_found = false;
	private boolean state_found = false;

	enum TestType {
		GOOD, HOST_OFFLINE
	}

	TestType test_type = TestType.GOOD;

	/*
	 * The session establishment test for an edge node
	 */

	public SessionEstablishment(TCK aTCK, String[] parms) {
		logger.info("Edge Node session establishment test");
		theTCK = aTCK;

		testResults = new HashMap<String, String>();

		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], "");
		}

		if (parms.length < 3) {
			logger.info("Parameters to edge session establishment test must be: host_application_id group_id edge_node_id");
			return;
		}

		host_application_id = parms[0];
		group_id = parms[1];
		edge_node_id = parms[2];

		logger.info("Host application id is " + host_application_id);
		logger.info("Group id is " + group_id);
		logger.info("Edge node id is " + edge_node_id);

		// if (parms.length > 1 && parms[1].equals("false")) {
		// 	commands_supported = false;
		// }

	}

	public void endTest() {
		state = null;
		myClientId = null;
		reportResults(testResults);
		for (int i = 0; i < testIds.length; ++i) {
			testResults.put(testIds[i], "");
		}
	}

	public String getName() {
		return "SessionEstablishment";
	}

	public String[] getTestIds() {
		return testIds;
	}

	public HashMap<String, String> getResults() {
		return testResults;
	}

	@SpecAssertion(
		section = Sections.PAYLOADS_B_NDEATH, 
		id = "payloads-ndeath-will-message-qos")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NDEATH, 
		id = "payloads-ndeath-seq")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NDEATH, 
		id = "payloads-ndeath-will-message-retain")
	@SpecAssertion(
		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, 
		id = "edge-death-bdseq")
	public Optional<WillPublishPacket> checkWillMessage(ConnectPacket packet) throws Exception {
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();
			
			// NDEATH message must set MQTT Will QoS to 1
			String result = "FAIL";
			if (willPublishPacket.getQos().getQosNumber() == 1) {
				result = "PASS";
			}
			testResults.put("payloads-ndeath-will-message-qos", result);

			ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
			SparkplugBPayload sparkplugPayload = decode(payload);

			List<Metric> metrics = sparkplugPayload.getMetrics();
			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					death_bdSeq = (long) m.getValue();
					break;
				}
			}

			// ensuring that death message has a bdSeq metric
			result = "FAIL";
			if (death_bdSeq != -1) {
				result = "PASS";
			}
			testResults.put("edge-death-bdseq", result);
			
			// death message should not include a sequence number
			result = "FAIL";
			if (sparkplugPayload.getSeq() == -1) {
				result = "PASS";
			}
			testResults.put("payloads-ndeath-seq", result);

			// retained flag must be false
			result = "FAIL";
			if (!willPublishPacket.getRetain()) {
				result = "PASS";
			}
			testResults.put("payloads-ndeath-will-message-retain", result);

		}
		return willPublishPacketOptional;
	}

	@SpecAssertion(
		section = Sections.PAYLOADS_B_NDEATH, 
		id = "payloads-ndeath-will-message")
	public void connect(String clientId, ConnectPacket packet) {
		logger.info("Edge session establishment test - connect");

		String result = "FAIL";
		Optional<WillPublishPacket> willPublishPacketOptional = null;
		try {
			willPublishPacketOptional = checkWillMessage(packet);
			if (willPublishPacketOptional.isPresent()) {
				result = "PASS";
			}
			testResults.put("payloads-ndeath-will-message", result);
		} catch (Exception e) {
			logger.info("Exception", e);
		}
	}
	
	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		// TODO Auto-generated method stub
		
	}
	
	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("Edge session establishment test - subscribe");

		String topic = "";

		List<Subscription> subscriptions = packet.getSubscriptions();
		for (Subscription s : subscriptions) {
			topic = s.getTopicFilter();
			if (topic.startsWith("spBv1.0/" + group_id + "/NCMD/" + edge_node_id)) {
				ncmd_found = true;
			} else if (topic.startsWith("spBv1.0/" + group_id + "/DCMD/" + edge_node_id)) {
				dcmd_found = true;
			} else if (topic.startsWith("STATE/" + host_application_id)) {
				state_found = true;
			}
		}
		// logger.info("--------");

		// if (myClientId.equals(clientId)) {
		// String result = "FAIL";
		// try {
		// if (!state.equals("CONNECTED"))
		// throw new Exception("State should be connected");
		// if
		// (!packet.getSubscriptions().get(0).getTopicFilter().equals("STATE/"+host_application_id))
		// throw new Exception("Topic string wrong");
		// TODO: what else do we need to check?
		// result = "PASS";
		// state = "SUBSCRIBED";
		// } catch (Exception e) {
		// result = "FAIL "+e.getMessage();
		// }
		// testResults.put("message-flow-edge-node-birth-publish-subscribe", result);

		// A retained message should have been set on the STATE/host_application_id
		// topic to indicate the
		// status of the primary host. The edge node's behavior will vary depending on
		// the result.

		// }
	}

	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-bdseq")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-qos")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-retain")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-seq")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-timestamp")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NBIRTH, 
		id = "payloads-nbirth-bdseq-inc")
	@SpecAssertion(
		section = Sections.PAYLOADS_B_NDEATH, 
		id = "payloads-ndeath-bdseq")
	@SpecAssertion(
		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, 
		id = "edge-birth-metrics")
	@SpecAssertion(
		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, 
		id = "message-flow-edge-node-birth-publish-subscribe")
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Edge session establishment test - publish");

		Date received_birth = new Date();
		long millis_received_birth = received_birth.getTime();
		long millis_past_five_min = millis_received_birth - (5 * 60 * 1000);

		// edge node must subscribe to state before publishing nbirth
		String result = "FAIL";
		if (state_found == true) {
			result = "PASS";
		}
		testResults.put("message-flow-edge-node-birth-publish-subscribe",result);
		
		ByteBuffer payload = packet.getPayload().orElseGet(null);
		SparkplugBPayload sparkplugPayload = decode(payload);

		// qos should be 0
		result = "FAIL";
		if (packet.getQos().getQosNumber() == 0) {
			result = "PASS";
		}
		testResults.put("payloads-nbirth-qos", result);

		// retain should be false
		result = "FAIL";
		if (!packet.getRetain()) {
			result = "PASS";
		}
		testResults.put("payloads-nbirth-retain", result);

		// sequence number should be 0
		result = "FAIL";
		if (sparkplugPayload.getSeq() == 0) {
			result = "PASS";
		}
		testResults.put("payloads-nbirth-seq", result);

		// making sure that the payload timestamp is greater than (recieved_bith_time - 5 min) and less than the received_birth_time
		result = "FAIL";
		Date ts = sparkplugPayload.getTimestamp();
		if (ts != null) {
			long millis_payload = ts.getTime();
			if (millis_payload > millis_past_five_min && millis_payload < (millis_received_birth)) {
				result = "PASS";
			}
		}
		testResults.put("payloads-nbirth-timestamp", result);

		boolean rebirth_found = false;
		boolean bdSeq_found = false;

		MetricDataType datatype = null;
		boolean rebirth_val = true;

		boolean bad_metric_found = false;

		if (sparkplugPayload != null) {
			List<Metric> metrics = sparkplugPayload.getMetrics();
			for (Metric m : metrics) {
				logger.info(m.getName());
				// if bad_metric_found becomes true because of one of the metrics,
				// why check the rest of the metrics if the test result is already fail?
				if (bad_metric_found == false) {
					bad_metric_found = is_metric_bad(m);
				}
				if (m.getName().equals("bdSeq")) {
					bdSeq_found = true;
					birth_bdSeq = (long) m.getValue();
				} else if (m.getName().equals("Node Control/Rebirth")) {
					rebirth_found = true;
					datatype = m.getDataType();
					rebirth_val = (boolean) m.getValue();
				} else if (bdSeq_found == true && rebirth_found == true && bad_metric_found == true) {
					// if bdseq and rebirth were already found, and if we already found a bad
					// metric,
					// then we have the info we need and we can break out of this loop.
					// but, if bad_metric_found is still false, then we need to stay in the loop to
					// make sure that none of the other metrics are bad
					break;
				}
			}
		}

		// every nbirth must include a bdSeq
		result = "FAIL";
		if (birth_bdSeq != -1) {
			result = "PASS";
		}
		testResults.put("payloads-nbirth-bdseq", result);

		// the birth bdSeq "must match the bdseq number provided in the MQTT CONNECT packets Will Message payload"
		result = "FAIL";
		if (birth_bdSeq != -1 && death_bdSeq != -1 && birth_bdSeq == death_bdSeq) {
			result = "PASS";
		}
		testResults.put("payloads-ndeath-bdseq", result);

		// nbirth message must include 'node control/rebirth' metric
		result = "FAIL";
		if (rebirth_found == true && datatype == MetricDataType.Boolean && rebirth_val == false) {
			result = "PASS";
		}
		testResults.put("payloads-nbirth-bdseq-inc", result);

		// at a minimum, each metric must include the metric name, datatype, and current value
		result = "FAIL";
		if (bad_metric_found == false) {
			result = "PASS";
		}
		testResults.put("edge-birth-metrics", result);

		// testResults.put("edge-bdseq",result);
		// logger.info("client id is " + clientId);
		// if (myClientId.equals(clientId)) {
		// String result = "FAIL";
		// try {
		// if (!state.equals("SUBSCRIBED"))
		// throw new Exception("State should be subscribed");

		// String payload = null;
		// ByteBuffer bpayload = packet.getPayload().orElseGet(null);
		// if (bpayload != null) {
		// payload = StandardCharsets.UTF_8.decode(bpayload).toString();
		// }
		// if (!payload.equals("ONLINE"))
		// throw new Exception("Payload should be ONLINE");

		// TODO: what else do we need to check?
		// result = "PASS";
		// state = "PUBLISHED";
		// } catch (Exception e) {
		// result = "FAIL " + e.getMessage();
		// }
		// testResults.put("primary-application-state-publish", result);
		// }

		check_subscribe_topics();

		theTCK.endTest();

	}

	public SparkplugBPayload decode(ByteBuffer payload) {
		byte[] bytes = new byte[payload.remaining()];
		payload.get(bytes);
		SparkplugBPayloadDecoder decoder = new SparkplugBPayloadDecoder();
		SparkplugBPayload sparkplugPayload = null;
		try {
			sparkplugPayload = decoder.buildFromByteArray(bytes);
		} catch (Exception e) {
			logger.info("Exception", e);
			return sparkplugPayload;
		}
		return sparkplugPayload;
	}

	public boolean is_metric_bad(Metric m) {
		if (m.hasName() == false) {
			return true;
		} else if (m.getDataType() == null) {
			return true;
		} else if (m.getValue() == null) {
			return true;
		} else {
			return false;
		}
	}

	@SpecAssertion(
		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, 
		id = "edge-subscribe-ncmd")
	@SpecAssertion(
		section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, 
		id = "edge-subscribe-dcmd")
	public void check_subscribe_topics() {
		
		// making sure edge node subscribes to ncmd and dcmd
		
		String result = "FAIL";
		if (ncmd_found == true) {
			result = "PASS";
		}
		testResults.put("edge-subscribe-ncmd", result);

		result = "FAIL";
		if (dcmd_found == true) {
			result = "PASS";
		}
		testResults.put("edge-subscribe-dcmd", result);

	}
}
