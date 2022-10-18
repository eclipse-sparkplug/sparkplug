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
import static org.eclipse.sparkplug.tck.test.common.Constants.*;

import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.*;
import org.eclipse.sparkplug.tck.test.common.SparkplugBProto.Payload.Metric;

import org.eclipse.sparkplug.tck.sparkplug.Sections;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.admin.AdminService;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.eclipse.sparkplug.tck.test.common.Constants.TCK_LOG_TOPIC;

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
    private String username = "admin";
    private String password = "changeme";
    private String filename = SPARKPLUG_TCKRESULTS_LOG;

    private MqttTopic log_topic = null;
    private MqttClient client = null;
    
    public class Config {
    	public long UTCwindow = 60000L;
    } 
    
    private Config config = new Config();

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
        listener.initialize(args);
    }

    public void initialize(String[] args) {

        if (client != null && client.isConnected()) {
            return;
        }
        logger.info("Initialize {} ", clientId);
        
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
            //client.setTimeToWait(10000); // short timeout on failure to connect
            client.setCallback(this);
            client.connect(options);
            log_topic = client.getTopic(TCK_LOG_TOPIC);
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		logger.info(clientId + ": connected");

        try {
            client.subscribe(TCK_RESULTS_CONFIG_TOPIC, 2);
            client.subscribe(TCK_RESULTS_TOPIC, 2);
            client.subscribe(TCK_CONFIG_TOPIC, 2);
            logger.info(clientId + ": subscribed");
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
				logger.info("Results: setting UTCwindow to "+config.UTCwindow);
			} else if (topic.equals(TCK_RESULTS_CONFIG_TOPIC)) {
                logger.debug("{}: topic: {} msg: {}", clientId, topic, new String(message.getPayload())); // display log message
                checkOrCreateNewResultLog(message);
            } else if (topic.equals(TCK_RESULTS_TOPIC)) {
                try {
                    logger.debug(" {}: {} used as log file.", clientId, (new File(filename).getAbsolutePath()));
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
            logger.info(" {}: Got new result log file: {} ", clientId, newFilename);
            if (!filename.equals(newFilename)) {
                File testFile = new File(newFilename);
                if (testFile.canWrite() || testFile.createNewFile()) {
                    filename = newFilename;
                    logger.info(" {}: New log file created: {} ", clientId, testFile.getAbsolutePath());
                } else {
                    logger.error(" {}: New log file: {} has no write access, use old setting: {} ", clientId, newFilename, filename);
                }
            }
            logger.info(" {}: Set new result log file: {} ", clientId, filename);
        }
    }
}