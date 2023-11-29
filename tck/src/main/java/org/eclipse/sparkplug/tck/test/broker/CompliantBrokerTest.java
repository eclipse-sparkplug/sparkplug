/*******************************************************************************
 * Copyright (c) 2021, 2023 Anja Helmbrecht-Schaar
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Anja Helmbrecht-Schaar - initial implementation and documentation
 *******************************************************************************/

package org.eclipse.sparkplug.tck.test.broker;

import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_QOS0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_QOS1;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.CONFORMANCE_MQTT_WILL_MESSAGES;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_QOS0;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_QOS1;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_RETAINED;
import static org.eclipse.sparkplug.tck.test.common.Requirements.ID_CONFORMANCE_MQTT_WILL_MESSAGES;
import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.eclipse.sparkplug.tck.test.TCK;
import org.eclipse.sparkplug.tck.test.TCKTest;
import org.eclipse.sparkplug.tck.test.broker.test.BrokerConformanceFeatureTester;
import org.eclipse.sparkplug.tck.test.broker.test.results.ComplianceTestResult;
import org.eclipse.sparkplug.tck.test.broker.test.results.QosTestResult;
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

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.services.Services;

@SpecVersion(
		spec = "sparkplug",
		version = "4.0.0-SNAPSHOT")
public class CompliantBrokerTest extends TCKTest {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	private static final int TIME_OUT = 60;
	public static final @NotNull List<String> testIds = List.of(ID_CONFORMANCE_MQTT_QOS0, ID_CONFORMANCE_MQTT_QOS1,
			ID_CONFORMANCE_MQTT_WILL_MESSAGES, ID_CONFORMANCE_MQTT_RETAINED);
	private TCK theTCK = null;
	private @NotNull String host;
	private @NotNull String port;

	public CompliantBrokerTest(TCK aTCK, String[] params) {
		logger.info("Broker: {} Parameters: {} ", getName(), Arrays.asList(params));
		theTCK = aTCK;
		if (params.length < 2) {
			log("Not enough parameters: " + Arrays.toString(params));
			log("Parameters must be: host and port, (already set during mqtt connection establishment)");
			throw new IllegalArgumentException();
		}
		host = params[0];
		port = params[1];
		logger.info("Parameters are Broker host: {}, port: {}, ", host, port);

		Services.extensionExecutorService().schedule(new Runnable() {
			@Override
			public void run() {
				logger.info("Broker - Sparkplug Broker compliant Test - execute test");
				try {
					checkCompliance(host, Integer.parseInt(port), testResults);
				} finally {
					theTCK.endTest();
				}
			}
		}, 5, TimeUnit.SECONDS);
	}

	@SpecAssertion(
			section = Sections.CONFORMANCE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_QOS0)
	@SpecAssertion(
			section = Sections.CONFORMANCE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_QOS1)
	@SpecAssertion(
			section = Sections.CONFORMANCE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_WILL_MESSAGES)
	@SpecAssertion(
			section = Sections.CONFORMANCE_MQTT_SERVER,
			id = ID_CONFORMANCE_MQTT_RETAINED)
	public static void checkCompliance(final String host, final int port, final Map<String, String> testResults) {
		logger.info("{} - Start", Sections.CONFORMANCE_MQTT_SERVER);

		logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_QOS0);
		BrokerConformanceFeatureTester brokerConformanceFeatureTester =
				new BrokerConformanceFeatureTester(host, port, null, null, null, TIME_OUT);

		QosTestResult qos0 = brokerConformanceFeatureTester.testQos(MqttQos.AT_MOST_ONCE, 3);
		testResults.put(ID_CONFORMANCE_MQTT_QOS0, setResult(qos0.getReceivedPublishes() > 0, CONFORMANCE_MQTT_QOS0));

		logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_QOS1);
		QosTestResult qos1 = brokerConformanceFeatureTester.testQos(MqttQos.AT_LEAST_ONCE, 3);
		testResults.put(ID_CONFORMANCE_MQTT_QOS1, setResult(qos1.getReceivedPublishes() > 0, CONFORMANCE_MQTT_QOS1));

		logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_WILL_MESSAGES);
		Mqtt3ConnAck connack = brokerConformanceFeatureTester.testConnectWithWill();
		boolean valid = connack != null && !connack.getReturnCode().isError();
		testResults.put(ID_CONFORMANCE_MQTT_WILL_MESSAGES, setResult(valid, CONFORMANCE_MQTT_WILL_MESSAGES));

		logger.debug("Check Req: {} ", ID_CONFORMANCE_MQTT_RETAINED);
		ComplianceTestResult retain = brokerConformanceFeatureTester.testRetain();
		testResults.put(ID_CONFORMANCE_MQTT_RETAINED,
				setResult(retain == ComplianceTestResult.OK, CONFORMANCE_MQTT_RETAINED));
	}

	@Override
	public void endTest(Map<String, String> results) {
		testResults.putAll(results);
		Utils.setEndTest(getName(), testIds, testResults);
		reportResults(testResults);
	}

	public String getName() {
		return "Broker SparkplugCompliant";
	}

	public String[] getTestIds() {
		return testIds.toArray(new String[0]);
	}

	public Map<String, String> getResults() {
		return testResults;
	}

	@Override
	public void connect(String clientId, ConnectPacket packet) {
		// TODO Auto-generated method stub
		logger.info("Broker - Sparkplug Broker compliant Test - connect - clientId: {}", clientId);
	}

	@Override
	public void disconnect(String clientId, DisconnectPacket packet) {
		logger.info("Broker - Sparkplug Broker compliant Test - disconnect - clientId: {}", clientId);
	}

	@Override
	public void subscribe(String clientId, SubscribePacket packet) {
		logger.info("Broker - Sparkplug Broker compliant Test - subscribe - " + "clientId: {} topic: {}", clientId,
				packet.getSubscriptions().get(0));
	}

	@Override
	public void publish(String clientId, PublishPacket packet) {
		final String topic = packet.getTopic();
		logger.info("Broker - Sparkplug Broker compliant Test - publish - topic: {}", topic);
	}

}
