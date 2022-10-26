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

import static org.eclipse.sparkplug.tck.test.common.Constants.NOT_EXECUTED;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_CONFIG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_RESULTS_CONFIG_TOPIC;
import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_RESULTS_TOPIC;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.AdminService;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;

@SpecVersion(
		spec = "sparkplug",
		version = "3.0.0-SNAPSHOT")
public class Results implements MqttCallbackExtended {
	private static final Logger logger = LoggerFactory.getLogger("Sparkplug");
	protected static final String SPARKPLUG_TCKRESULTS_LOG = "SparkplugTCKresults.log";

	private final @NotNull AdminService adminService = Services.adminService();
	private final @NotNull ManagedExtensionExecutorService executorService = Services.extensionExecutorService();

	// Configuration
	private String serverUrl = "tcp://localhost:1883";
	private String clientId = "Sparkplug TCK Results Collector";
	//private String username = "admin";
	//private String password = "changeme";
	private String filename = SPARKPLUG_TCKRESULTS_LOG;

	private MqttTopic log_topic = null;
	private MqttClient client = null;

	public class Config {
		public long UTCwindow = 60000L;
	}

	private Config config = new Config();

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
		listener.initialize(args);
	}

	public void initialize(String[] args) {

		if (client != null && client.isConnected()) {
			return;
		}
		logger.info("{} initializing", clientId);

		executorService.schedule(() -> {
			// check if broker is ready
			if (adminService.getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY) {
				connectMQTT();
			} else {
				// schedule next check
				initialize(new String[] {});
			}
		}, 1, TimeUnit.SECONDS);
	}

	private void connectMQTT() {
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
			// client.setTimeToWait(10000); // short timeout on failure to connect
			client.setCallback(this);
			client.connect(options);
			log_topic = client.getTopic(TCK_LOG_TOPIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		logger.debug(clientId + ": connected");

		try {
			client.subscribe(TCK_RESULTS_CONFIG_TOPIC, 2);
			client.subscribe(TCK_RESULTS_TOPIC, 2);
			client.subscribe(TCK_CONFIG_TOPIC, 2);
			logger.debug(clientId + ": subscribed");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.debug(clientId + " connection lost - will auto-reconnect");
	}

	public Config getConfig() {
		return config;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			if (topic.equals(TCK_CONFIG_TOPIC)) {
				String[] words = new String(message.getPayload()).split(" ");
				config.UTCwindow = Long.parseLong(words[1]);
				logger.info("{}: setting UTCwindow to " + config.UTCwindow, clientId);
			} else if (topic.equals(TCK_RESULTS_CONFIG_TOPIC)) {
				logger.debug("{}: topic: {} msg: {}", clientId, topic, new String(message.getPayload())); // display log
																											// message
				checkOrCreateNewResultLog(message);
			} else if (topic.equals(TCK_RESULTS_TOPIC)) {
				try {
					logger.debug("{}: {} used as log file.", clientId, (new File(filename).getAbsolutePath()));
					FileWriter resultFileWriter = new FileWriter(filename, true);
					resultFileWriter.write(new String(message.getPayload()) + System.lineSeparator());
					resultFileWriter.close();
				} catch (IOException e) {
					logger.error("An error occurred. {} ", e.getMessage());
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

	private void checkOrCreateNewResultLog(MqttMessage message) throws IOException {
		final String cmd = "NEW_RESULT-LOG ";
		final String payload = new String(message.getPayload());
		final int index = payload.toUpperCase().indexOf(cmd);

		if (index >= 0 && payload.length() >= cmd.length()) {
			final String newFilename = payload.substring(cmd.length());
			logger.info("{}: Setting new result log file: {} ", clientId, newFilename);
			if (!filename.equals(newFilename)) {
				File testFile = new File(newFilename);
				if (testFile.canWrite() || testFile.createNewFile()) {
					filename = newFilename;
					logger.debug(" {}: New log file created: {} ", clientId, testFile.getAbsolutePath());
				} else {
					logger.error(" {}: New log file: {} has no write access, use old setting: {} ", clientId,
							newFilename, filename);
				}
			}
			logger.debug("{}: Set new result log file: {} ", clientId, filename);
		}
	}

	public static StringBuilder getSummary(final @NotNull Map<String, String> results) {
		final StringBuilder summary = new StringBuilder();

		String overall = results.entrySet().isEmpty() ? Constants.EMPTY : Constants.NOT_EXECUTED;
		boolean incomplete = false;
		for (final Map.Entry<String, String> reportResult : results.entrySet()) {
			if (reportResult.getValue().equals(NOT_EXECUTED)) {
				if (reportResult.getKey().startsWith("Monitor:") || reportResult.getKey().startsWith("MQTTListener")) {
					continue;
				}
				incomplete = true;
			}

			summary.append(reportResult.getKey()).append(": ").append(reportResult.getValue()).append(";")
					.append(System.lineSeparator());

			if (!overall.equals(Constants.FAIL)) { // don't overwrite an overall fail status
				if (reportResult.getValue().startsWith(Constants.PASS)) {
					overall = Constants.PASS;
				} else if (reportResult.getValue().startsWith(Constants.FAIL)) {
					overall = Constants.FAIL;
				}
			}
		}

		if (incomplete) {
			overall += " but INCOMPLETE";
		}
		summary.append("OVERALL: ").append(overall).append(";").append(System.lineSeparator());
		return summary;
	}
}