/*
 * Copyright © 2021 The Eclipse Foundation, Cirrus Link Solutions, and others
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.sparkplug.tck.host.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.sparkplug.tck.host.LifecycleExtension;
import org.eclipse.sparkplug.tck.host.model.LifecycleTestResult;
import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hivemq.testcontainer.core.HiveMQExtension;
import com.hivemq.testcontainer.junit5.HiveMQTestContainerExtension;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class LifecycleTest {

	private static Logger logger = LoggerFactory.getLogger(LifecycleTest.class.getName());

	private Set<String> testResultsSet = new HashSet<>();

	@RegisterExtension
	public final @NotNull HiveMQTestContainerExtension extension =
			new HiveMQTestContainerExtension().withExtension(HiveMQExtension.builder().id("extension-1")
					.name("sparkplug-extension").version("1.0.0-SNAPSHOT").mainClass(LifecycleExtension.class).build());

	@BeforeEach
	public void setup() {
		testResultsSet = new HashSet<>();
		testResultsSet.add("host-topic-phid-death-payload");
		testResultsSet.add("host-topic-phid-death-qos");
		testResultsSet.add("host-topic-phid-death-retain");
	}

	@AfterEach
	public void tearDown() {
		testResultsSet = null;
	}

	@Test
	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = "host-topic-phid-death-payload")
	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = "host-topic-phid-death-qos")
	@SpecAssertion(
			section = Sections.TOPICS_DEATH_MESSAGE_STATE,
			id = "host-topic-phid-death-retain")
	public void testHostLifecycle() {
		try {
			// Get the client which will provide results
			setupClient();

			logger.info("CONNECT on port {}", extension.getMqttPort());
			Thread.sleep(120000);
		} catch (InterruptedException ie) {
			if (testResultsSet.isEmpty()) {
				logger.info("TEST PASSED!");
			} else {
				logger.info("TEST FAILED");
				fail();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private MqttClient setupClient() throws MqttException {
		// Connect to the MQTT Server
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(30);
		options.setKeepAliveInterval(30);
		MqttClient client = new MqttClient("tcp://localhost:" + extension.getMqttPort(), "Sparkplug TCK Client");
		client.setTimeToWait(2000);
		client.connect(options);
		assertTrue(client.isConnected());

		// Set up the callback
		client.setCallback(new LocalCallback(Thread.currentThread()));
		client.subscribe("SPARKPLUG_TCK_RESULT/#");
		return client;
	}

	private class LocalCallback implements MqttCallbackExtended {

		private Thread callingThread;

		public LocalCallback(Thread callingThread) {
			this.callingThread = callingThread;
		}

		@Override
		public void connectionLost(Throwable cause) {
			logger.info("Connection Lost");
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String payloadString = new String(message.getPayload());
			logger.info("Message arrived on {} with payload: {}", topic, payloadString);
			LifecycleTestResult lifecycleTestResult = new Gson().fromJson(payloadString, LifecycleTestResult.class);

			if ("SPARKPLUG_TCK_RESULT".equals(topic) && lifecycleTestResult.getHostTopicPhidDeathPayload() != null) {
				if ("PASS".equals(lifecycleTestResult.getHostTopicPhidDeathPayload())) {
					testResultsSet.remove("host-topic-phid-death-payload");
				} else {
					callingThread.interrupt();
				}
			}

			if ("SPARKPLUG_TCK_RESULT".equals(topic) && lifecycleTestResult.getHostTopicPhidDeathQos() != null) {
				if ("PASS".equals(lifecycleTestResult.getHostTopicPhidDeathQos())) {
					testResultsSet.remove("host-topic-phid-death-qos");
				} else {
					callingThread.interrupt();
				}
			}

			if ("SPARKPLUG_TCK_RESULT".equals(topic) && lifecycleTestResult.getHostTopicPhidDeathRetain() != null) {
				if ("PASS".equals(lifecycleTestResult.getHostTopicPhidDeathRetain())) {
					testResultsSet.remove("host-topic-phid-death-retain");
				} else {
					callingThread.interrupt();
				}
			}

			if (testResultsSet.isEmpty()) {
				callingThread.interrupt();
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			logger.info("Delivery Complete");
		}

		@Override
		public void connectComplete(boolean reconnect, String serverURI) {
			logger.info("Connect complete");
		}
	}
}
