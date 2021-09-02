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
	String[] testIds = { "edge-death-qos", "edge-death-seq", "edge-death-retain", "edge-death-cert", "edge-death-bdseq",
			"edge-birth-qos", "edge-birth-retain", "edge-birth-seq", "edge-birth-bdseq", "edge-birth-metrics",
			"edge-birth-timestamp", "edge-rebirth", "edge-bdseq", "edge-subscribe-ncmd", "edge-subscribe-dcmd",
			"edge-subscribe-state", };
	private String myClientId = null;
	private String state = null;
	private TCK theTCK = null;
	private String host_application_id = null; // The primary host application id to be used
	private boolean commands_supported = true; // Are commands supported by the edge node?
	private long death_bdSeq = -1;
	private long birth_bdSeq = -1;
	private String group_id = null;
	private String edge_node_id = null;
	private ArrayList<String> subscribe_topics = new ArrayList<String>();

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
			logger.info(
					"Parameters to edge session establishment test must be: host_application_id group_id edge_node_id");
			return;
		}

		host_application_id = parms[0];
		group_id = parms[1];
		edge_node_id = parms[2];

		logger.info("Host application id is " + host_application_id);
		logger.info("Group id is " + group_id);
		logger.info("Edge node id is " + edge_node_id);

		// if (parms.length > 1 && parms[1].equals("false")) {
		// commands_supported = false;
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

	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-death-qos")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-death-seq")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-death-retain")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-death-bdseq")
	public Optional<WillPublishPacket> checkWillMessage(ConnectPacket packet) throws Exception {
		Optional<WillPublishPacket> willPublishPacketOptional = packet.getWillPublish();
		if (willPublishPacketOptional.isPresent()) {
			WillPublishPacket willPublishPacket = willPublishPacketOptional.get();

			String result = "FAIL";
			if (willPublishPacket.getQos().getQosNumber() == 1) {
				result = "PASS";
			}
			testResults.put("edge-death-qos", result);

			ByteBuffer payload = willPublishPacket.getPayload().orElseGet(null);
			SparkplugBPayload sparkplugPayload = decode(payload);

			List<Metric> metrics = sparkplugPayload.getMetrics();
			for (Metric m : metrics) {
				if (m.getName().equals("bdSeq")) {
					death_bdSeq = (long) m.getValue();
					break;
				}
			}

			result = "FAIL";
			if (death_bdSeq != -1) {
				result = "PASS";
			}
			testResults.put("edge-death-bdseq", result);

			result = "FAIL";
			if (sparkplugPayload.getSeq() == -1) {
				result = "PASS";
			}
			testResults.put("edge-death-seq", result);

			result = "FAIL";
			if (!willPublishPacket.getRetain()) {
				result = "PASS";
			}
			testResults.put("edge-death-retain", result);

		}
		return willPublishPacketOptional;
	}

	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-death-cert")
	public void connect(String clientId, ConnectPacket packet) {
		logger.info("Edge session establishment test - connect");

		String result = "FAIL";
		Optional<WillPublishPacket> willPublishPacketOptional = null;
		try {
			willPublishPacketOptional = checkWillMessage(packet);
			if (willPublishPacketOptional.isPresent()) {
				result = "PASS";
			}
			testResults.put("edge-death-cert", result);
		} catch (Exception e) {
			logger.info("Exception", e);
		}
	}

	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("Edge session establishment test - subscribe");

		String topic = "";

		List<Subscription> subscriptions = packet.getSubscriptions();
		for (Subscription s : subscriptions) {
			topic = s.getTopicFilter();
			subscribe_topics.add(topic);
			// logger.info(topic);
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

	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-bdseq")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-qos")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-retain")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-seq")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-timestamp")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-rebirth")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-bdseq")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-birth-metrics")
	public void publish(String clientId, PublishPacket packet) {
		logger.info("Edge session establishment test - publish");

		Date received_birth = new Date();
		long millis_received_birth = received_birth.getTime();
		long millis_past_five_min = millis_received_birth - (5 * 60 * 1000);

		ByteBuffer payload = packet.getPayload().orElseGet(null);
		SparkplugBPayload sparkplugPayload = decode(payload);

		String result = "FAIL";
		if (packet.getQos().getQosNumber() == 0) {
			result = "PASS";
		}
		testResults.put("edge-birth-qos", result);

		result = "FAIL";
		if (!packet.getRetain()) {
			result = "PASS";
		}
		testResults.put("edge-birth-retain", result);

		result = "FAIL";
		if (sparkplugPayload.getSeq() == 0) {
			result = "PASS";
		}
		testResults.put("edge-birth-seq", result);

		result = "FAIL";
		Date ts = sparkplugPayload.getTimestamp();
		if (ts != null) {
			long millis_payload = ts.getTime();
			if (millis_payload > millis_past_five_min && millis_payload < (millis_received_birth)) {
				result = "PASS";
			}
		}
		testResults.put("edge-birth-timestamp", result);

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

		result = "FAIL";
		if (birth_bdSeq != -1) {
			result = "PASS";
		}
		testResults.put("edge-birth-bdseq", result);

		result = "FAIL";
		if (birth_bdSeq != -1 && death_bdSeq != -1 && birth_bdSeq == death_bdSeq) {
			result = "PASS";
		}
		testResults.put("edge-bdseq", result);

		result = "FAIL";
		if (rebirth_found == true && datatype == MetricDataType.Boolean && rebirth_val == false) {
			result = "PASS";
		}
		testResults.put("edge-rebirth", result);

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

	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-subscribe-ncmd")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-subscribe-dcmd")
	@SpecAssertion(section = Sections.OPERATIONAL_BEHAVIOR_EDGE_NODE_SESSION_ESTABLISHMENT, id = "edge-subscribe-state")
	public void check_subscribe_topics() {
		boolean ncmd_found = false;
		boolean dcmd_found = false;
		boolean state_found = false;

		for (String topic : subscribe_topics) {
			if (topic.startsWith("spBv1.0/" + group_id + "/NCMD/" + edge_node_id)) {
				ncmd_found = true;
			} else if (topic.startsWith("spBv1.0/" + group_id + "/DCMD/" + edge_node_id)) {
				dcmd_found = true;
			} else if (topic.startsWith("STATE/" + host_application_id)) {
				state_found = true;
			}
		}

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

		result = "FAIL";
		if (state_found == true) {
			result = "PASS";
		}
		testResults.put("edge-subscribe-state", result);

	}
}