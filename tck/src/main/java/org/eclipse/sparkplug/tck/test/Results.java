/********************************************************************************
 * Copyright (c) 2014, 2022 Cirrus Link Solutions and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Cirrus Link Solutions - initial implementation
 *   Ian Craggs - add checks for Sparkplug TCK
 ********************************************************************************/

package org.eclipse.sparkplug.tck.test;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.MqttException;

import static org.eclipse.sparkplug.tck.test.common.Utils.setResult;
import static org.eclipse.sparkplug.tck.test.common.Requirements.*;
import static org.eclipse.sparkplug.tck.test.common.TopicConstants.*;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.*;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.eclipse.sparkplug.tck.test.common.TopicConstants.TCK_LOG_TOPIC;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class Results implements MqttCallbackExtended {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");

	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "Sparkplug TCK Results Collector";
	private String username = "admin";
	private String password = "changeme";
	private String filename = "SparkplugTCKresults.txt";

	private MqttTopic log_topic = null;
	private MqttClient client = null;

	private String primary_host_application_id = null;

	public void log(String message) {
		try {
			MqttMessage mqttmessage = new MqttMessage((clientId + ": " + message).getBytes());
			log_topic.publish(mqttmessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Results listener = new Results();
		listener.run(args);
	}

	public void run(String[] args) {
		if (client != null) {
			return;
		}

		logger.info("*** " + clientId + " ***");
		try {
			// Connect to the MQTT Server
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(30);
			// options.setUserName(username);
			// options.setPassword(password.toCharArray());
			client = new MqttClient(serverUrl, clientId);
			//client.setTimeToWait(10000); // short timeout on failure to connect
			client.setCallback(this);
			log_topic = client.getTopic(TCK_LOG_TOPIC);
			client.connect(options);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		logger.info(clientId + ": connected");

		try {
			client.subscribe(new String[] { TCK_RESULTS_TOPIC }, new int[] { 2 });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.debug(clientId + " connection lost - will auto-reconnect");
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if (topic.equals(TCK_RESULTS_TOPIC)) {
				try {
					File myObj = new File(filename);
					if (myObj.createNewFile()) {
						logger.info(clientId + " File created: " + myObj.getName());
					} else {
						// System.out.println("File already exists.");
					}
					FileWriter myWriter = new FileWriter(filename, true);
					myWriter.write(new String(message.getPayload()) + System.lineSeparator());
					myWriter.close();
				} catch (IOException e) {
					logger.error("An error occurred.");
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// System.out.println("Published message: " + token);
	}
}