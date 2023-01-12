/********************************************************************************
 * Copyright (c) 2014-2022 Cirrus Link Solutions and others
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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.services.ManagedExtensionExecutorService;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.AdminService;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.sparkplug.tck.test.common.Constants;
import org.eclipse.sparkplug.tck.test.report.ReportSummaryWriter;
import org.jboss.test.audit.annotations.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.eclipse.sparkplug.tck.test.common.Constants.*;

@SpecVersion(
        spec = "sparkplug",
        version = "3.0.0")
public class Results implements MqttCallbackExtended {
    protected static final String SPARKPLUG_TCKRESULTS_LOG = "SparkplugTCKresults.log";
    private static final Logger LOGGER = LoggerFactory.getLogger("Sparkplug");
    private final static String SERVER_URL = "tcp://localhost:1883";
    private final static String CLIENT_ID = "Sparkplug TCK Results Collector";
    private final @NotNull AdminService adminService = Services.adminService();
    private final @NotNull ManagedExtensionExecutorService executorService = Services.extensionExecutorService();
    private final Config config = new Config();
    private String filename;
    private MqttTopic logTopic;
    private MqttClient client;

    public Results(String filename, MqttTopic logTopic, MqttClient client) {
        this.filename = filename;
        this.logTopic = logTopic;
        this.client = client;
    }

    public static void main(String[] args) {
        Results listener = new Results(SPARKPLUG_TCKRESULTS_LOG, null, null);
        listener.initialize(args);
    }

    public static StringBuilder getSingleTestSummary(final @NotNull Map<String, String> results) {
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

    public void log(String message) {
        try {
            MqttMessage mqttmessage = new MqttMessage((CLIENT_ID + ": " + message).getBytes());
            logTopic.publish(mqttmessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize(String[] args) {

        if (client != null && client.isConnected()) {
            return;
        }
        LOGGER.info("{} initializing", CLIENT_ID);

        executorService.schedule(() -> {
            // check if broker is ready
            if (adminService.getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY) {
                connectMQTT();
            } else {
                // schedule next check
                initialize(new String[]{});
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
            client = new MqttClient(SERVER_URL, CLIENT_ID);
            // client.setTimeToWait(10000); // short timeout on failure to connect
            client.setCallback(this);
            client.connect(options);
            logTopic = client.getTopic(TCK_LOG_TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        LOGGER.debug(CLIENT_ID + ": connected");

        try {
            client.subscribe(TCK_RESULTS_CONFIG_TOPIC, 2);
            client.subscribe(TCK_RESULTS_TOPIC, 2);
            client.subscribe(TCK_CONFIG_TOPIC, 2);
            client.subscribe(TCK_REPORT_TOPIC, 2);
            LOGGER.debug(CLIENT_ID + ": subscribed");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        LOGGER.debug(CLIENT_ID + " connection lost - will auto-reconnect");
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            switch (topic) {
                case TCK_CONFIG_TOPIC:
                    String[] words = new String(message.getPayload()).split(" ");
                    config.UTCwindow = Long.parseLong(words[1]);
                    LOGGER.info("{}: setting UTCwindow to " + config.UTCwindow, CLIENT_ID);
                    break;
                case TCK_RESULTS_CONFIG_TOPIC:
                    LOGGER.debug("{}: topic: {} msg: {}", CLIENT_ID, topic, new String(message.getPayload()));
                    // display log message
                    checkOrCreateNewResultLog(message);
                    break;
                case TCK_RESULTS_TOPIC:
                    try {
                        LOGGER.debug("{}: {} attach Logfile.", CLIENT_ID, (new File(filename).getAbsolutePath()));
                        FileWriter resultFileWriter = new FileWriter(filename, true);
                        resultFileWriter.write(new String(message.getPayload()) + System.lineSeparator());
                        resultFileWriter.close();
                    } catch (IOException e) {
                        LOGGER.error("An error occurred. {} ", e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case TCK_REPORT_TOPIC:
                    try {
                        String reportFile = "Summary-" + filename + ".html";
                        LOGGER.debug("{}: create new Report {} from TCK log.", CLIENT_ID, (new File(reportFile).getAbsolutePath()));
                        String downloadPath = createNewReport(message);
                        LOGGER.info("{}: new Report written into: {} ", CLIENT_ID, downloadPath);
                    } catch (IOException e) {
                        LOGGER.error("An error occurred. {} ", e.getMessage());
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        LOGGER.trace("Published message: {} ", token);
    }

    private void checkOrCreateNewResultLog(MqttMessage message) throws IOException {
        final String cmd = "NEW_RESULT-LOG ";
        final String payload = new String(message.getPayload());
        final int index = payload.toUpperCase().indexOf(cmd);

        if (index >= 0 && payload.length() >= cmd.length()) {
            final String newFilename = payload.substring(cmd.length());
            LOGGER.info("{}: Setting new result log file: {} ", CLIENT_ID, newFilename);
            if (!filename.equals(newFilename)) {
                final File testFile = new File(newFilename);
                if (testFile.canWrite() || testFile.createNewFile()) {
                    (new File(filename)).delete();
                    filename = newFilename;
                    LOGGER.debug(" {}: New log file created: {} ", CLIENT_ID, testFile.getAbsolutePath());
                } else {
                    LOGGER.error(" {}: New log file: {} has no write access, use old setting: {} ", CLIENT_ID,
                            newFilename, filename);
                }
            }
            LOGGER.debug("{}: Set new result log file: {} ", CLIENT_ID, filename);
        }
    }

    private String createNewReport(MqttMessage message) throws IOException {
        final String cmd = "NEW_REPORT ";
        String payload = new String(message.getPayload());
        final int index = payload.toUpperCase().indexOf(cmd);

        if (index >= 0 && payload.length() >= cmd.length()) {
            final String logFileName = payload.substring(cmd.length());
            LOGGER.info("{}: Writing new report file from : {} ", CLIENT_ID, logFileName);
            final ReportSummaryWriter reportSummaryWriter = new ReportSummaryWriter(logFileName);
            payload = reportSummaryWriter.writeReport();
            //publish the data to the DOWNLOAD TOPIC
            LOGGER.info(" {}: The report is created at: {}", CLIENT_ID, payload);
            reportSummaryWriter.publishDownloadSummary();
        } else {
            LOGGER.debug(" {}: The report cannot created: Payload - {}", CLIENT_ID, payload);
        }
        return payload;
    }

    public static class Config {
        public long UTCwindow = 60000L;
    }
}
