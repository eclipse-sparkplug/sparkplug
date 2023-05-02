<!--****************************************************************************
 * Copyright (c) 2021, 2022 Lukas Brand, Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Lukas Brand - initial implementation and documentation
 ****************************************************************************-->

<template>
    <div>
        <Navbar
            :mqttConnected="mqttClient.connected"
            :pendingNotifications="popupNotifications"
            @show-popup="showPopup = true"
        />
        <div class="m-3">
            <WebConsoleInformation/>
            <div>
                <b-card no-body>
                    <b-tabs v-model="activeTab" card>
                        <b-tab id="connection"
                               :title-link-class="mqttClient.connected ? 'bg-success text-white' : 'bg-danger text-white'"
                               @click="changeMqttConnectFun(0)"
                        >
                            <template #title>
                                <span class="mr-2">MQTT Configuration</span>
                                <span>
                    {{ mqttClient.connected ? "(connected)" : "(disconnected)" }}
                  </span>
                            </template>
                            <MqttConnect :change="tabOpen" class="mt-3" @on-connect="mqttConnected"/>
                        </b-tab>
                        <b-tab id="profile"
                               :title-link-class="
                  (sparkplugClient.hostApplication.complete && sparkplugClient.testType === 'HOSTAPPLICATION') ||
                  (sparkplugClient.eonNode.complete && sparkplugClient.testType === 'EONNODE')||
                  (sparkplugClient.testType === 'BROKER' && sparkplugClient.broker.host.length>0 && sparkplugClient.broker.port >0)
                    ? 'bg-success text-white'
                    : 'bg-danger text-white'
                "
                               @click="changeMqttConnectFun(1)"
                        >
                            <template #title>Sparkplug Conformance Profile</template>
                            <SparkplugClientConnect
                                v-model="sparkplugClient"
                                :change="tabOpen"
                                :currentTest="currentTest"
                                class="mt-3"
                            />
                        </b-tab>
                        <b-tab id="log"
                               @click="changeMqttConnectFun(2)"
                               :title-link-class="'bg-secondary text-white'"
                        >
                            <template #title>
                                <span class="mr-2">Results Overview</span>
                            </template>
                            <TckTestResultSetup
                                v-model="testResultSetup"
                                :change="tabOpen"
                                :currentTest="currentTest"
                                :filename="filename"
                                :report="report"
                                :reportCreated="reportCreated"
                                class="mt-3"
                                @reset-log="resetLogging"
                                @create-report="createReport"
                                @download-report="downloadReport"
                            />
                        </b-tab>

                    </b-tabs>
                </b-card>
            </div>
            <div v-if="activeTab === 1">
                <TckTests
                    :currentTest="this.currentTest"
                    :currentTestLogging="this.currentTestLogging"
                    :testType="this.sparkplugClient.testType"
                    class="mt-3"
                    @start-all-tests="(hostTests, eonTests, brokerTests) => startAllTestsFun(hostTests, eonTests, brokerTests)"
                    @abort-all-tests="(hostTests, eonTests, brokerTests) => abortAllTestsFun(hostTests, eonTests, brokerTests)"
                    @reset-all-tests="(hostTests, eonTests, brokerTests) => resetAllTestsFun(hostTests, eonTests, brokerTests)"
                    @start-single-test="(testParameters) => startTestFun(testParameters)"
                    @abort-single-test="(testParameters) => abortTestFun(testParameters)"
                    @reset-single-test="(testParameters) => resetTestFun(testParameters)"
                    @reset-current-test="resetCurrentTest()"
                />
            </div>

            <TckLogging :logging="this.logging" class="mt-3"/>
        </div>

        <WebConsolePopup
            v-model="popupNotifications"
            :showPopup="showPopup"
            @handle-notification="publishMessage"
            @popup-shown="showPopup = $event"
        />

        <b-button style="position: fixed; bottom: 40px; right: 40px; z-index: 9" variant="info" @click="backToTop">
            <b-icon icon="arrow-up-circle-fill"></b-icon>
        </b-button>
    </div>
</template>


<script>

let interactionListenerCreated = false;

export default {
    data: function () {
        return {
            filename: "",
            report: "",
            reportCreated: "",

            /**
             * Controls visibility of the user interactions popup.
             */
            showPopup: false,

            /**
             * Controls collapse state of MQTT connect and client connect.
             */
            tabOpen: true,

            /**
             * Value for the current open window. 0 for MQTT connect. 1 for client connect.
             */
            activeTab: 0,

            /**
             * The MQTT client which is used for all backend communication.
             */
            mqttClient: {
                connected: false,
            },

            currentTest: null,
            currentTestLogging: null,
            reportSummary: "",

            /**
             * All information about the possible sparkplug client.
             * Complete value informes about all necessary values are being set.
             * Values for both eon node and host application.
             * testType denotes the current used client.
             * @type {Object} sparkplugClient
             * @type {Boolean}sparkplugClient.complete
             * @type {String} sparkplugClient.testType
             * @type {Object} sparkplugClient.eonNode
             * @type {Boolean} sparkplugClient.eonNode.complete
             * @type {String} sparkplugClient.eonNode.groupId
             * @type {String} sparkplugClient.eonNode.edgeNodeId
             * @type {Object} sparkplugClient.hostApplication
             * @type {Boolean} sparkplugClient.hostApplication.complete
             * @type {String} sparkplugClient.hostApplication.hostId
             */
            sparkplugClient: {
                testType: "HOSTAPPLICATION",
                hostApplication: {
                    complete: false,
                    hostId: "",
                },
                eonNode: {
                    complete: false,
                    groupId: "",
                    edgeNodeId: "",
                },
                broker: {
                    complete: false,
                    host: "localhost",
                    port: 1883,
                },
            },

            testResultSetup: {},
            /**
             * List of all received logging information about the tests.
             */
            logging: [],

            /**
             * List of received pending user interactions.
             */
            popupNotifications: [],

        };
    },

    methods: {
        /**********************
         * Test functions
         **********************/

        /**
         * Starts all tests sequentially.
         */
        startAllTestsFun: function (hostTests, eonTests, brokerTests) {
            if (this.mqttClient.connected === false) {
                alert("You need to connect to the broker first.");
                return;
            }
            if (this.sparkplugClient.testType === "HOSTAPPLICATION") {
                for (const hostTest of Object.values(hostTests)) {
                    this.startTestFun(hostTest);
                }

            } else if (this.sparkplugClient.testType === "EONNODE") {
                for (const eonTest of Object.values(eonTests)) {
                    this.startTestFun(eonTest);
                }
            } else if (this.sparkplugClient.testType === "BROKER") {
                for (const brokerTest of Object.values(brokerTests)) {
                    this.startTestFun(brokerTest);
                }
            } else {
                alert("Client type does not exist");
            }
        },

        /**
         * Aborts all tests.
         */
        abortAllTestsFun: function (hostTests, eonTests, brokerTests) {
            if (this.mqttClient.connected === false) {
                alert("You need to connect to the broker first.");
                return;
            }
            if (this.sparkplugClient.testType === "HOSTAPPLICATION") {
                for (const hostTest of Object.values(hostTests)) {
                    this.abortTestFun(hostTest);
                }
            } else if (this.sparkplugClient.testType === "EONNODE") {
                for (const eonTest of Object.values(eonTests)) {
                    this.abortTestFun(eonTest);
                }
            } else if (this.sparkplugClient.testType === "BROKER") {
                for (const test of Object.values(brokerTests)) {
                    this.abortTestFun(test);
                }
            } else {
                alert("Client type does not exist");
            }
        },

        /**
         * Resets all tests.
         */
        resetAllTestsFun: function (hostTests, eonTests, brokerTests) {
        },

        /**
         * Start a single test.
         */
        startTestFun: function (testParameter) {
            if (this.mqttClient.connected === false) {
                alert("You need to connect to the broker first.");
                return;
            }
            if (this.currentTest !== null) {
                alert("You need to finish or end the current test in order to start a new test.");
                return;
            }

            console.log("index startTest:", testParameter);
            this.currentTest = testParameter.name;

            if (this.sparkplugClient.testType === "HOSTAPPLICATION") {
                const profile = "host";
                const testType = testParameter.name;
                if (testType === "SessionEstablishmentTest") {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    }
                    const testParameters = this.sparkplugClient.hostApplication.hostId;
                    this.createTestRequest(profile, testType, testParameters);
                } else if (testType === "SessionTerminationTest") {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + testParameter.parameters["client_id"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else if (["SendCommandTest", "ReceiveDataTest", "EdgeSessionTerminationTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["group_id"].parameterValue) {
                        alert("The Edge Node Group ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["edge_node_id"].parameterValue) {
                        alert("The Edge Node Edge Node ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["device_id"].parameterValue) {
                        alert("The Edge Node Device ID parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + testParameter.parameters["group_id"].parameterValue +
                        " " + testParameter.parameters["edge_node_id"].parameterValue +
                        " " + testParameter.parameters["device_id"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else if (["MessageOrderingTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["group_id"].parameterValue) {
                        alert("The Edge Node Group ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["edge_node_id"].parameterValue) {
                        alert("The Edge Node Edge Node ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["device_id"].parameterValue) {
                        alert("The Edge Node Device ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["reorder_timeout"].parameterValue) {
                        alert("The Host Application Reorder Timeout parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + testParameter.parameters["group_id"].parameterValue +
                        " " + testParameter.parameters["edge_node_id"].parameterValue +
                        " " + testParameter.parameters["device_id"].parameterValue +
                        " " + testParameter.parameters["reorder_timeout"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else if ([ "MultipleBrokerTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["broker_uri"].parameterValue) {
                        alert("The Broker URI parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + testParameter.parameters["broker_uri"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else {
                    alert("Test does not exist");
                }
            }

            if (this.sparkplugClient.testType === "EONNODE") {
                const profile = "edge";
                const testType = testParameter.name;
                if (["SessionEstablishmentTest", "SessionTerminationTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.groupId) {
                        alert("The Edge Node Group ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.edgeNodeId) {
                        alert("The Edge Node Edge Node ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["device_ids"].parameterValue) {
                        alert("The Edge Node Device IDs parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + this.sparkplugClient.eonNode.groupId +
                        " " + this.sparkplugClient.eonNode.edgeNodeId +
                        " " + testParameter.parameters["device_ids"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else if (["SendDataTest", "SendComplexDataTest", "PrimaryHostTest", "ReceiveCommandTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.groupId) {
                        alert("The Edge Node Group ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.edgeNodeId) {
                        alert("The Edge Node Edge Node ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["device_id"].parameterValue) {
                        alert("The Edge Node Device IDs parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + this.sparkplugClient.eonNode.groupId +
                        " " + this.sparkplugClient.eonNode.edgeNodeId +
                        " " + testParameter.parameters["device_id"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else if ([ "MultipleBrokerTest"].includes(testType)) {
                    if (!this.sparkplugClient.hostApplication.hostId) {
                        alert("The Host Application ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.groupId) {
                        alert("The Edge Node Group ID parameter must be set before executing this test");
                        return;
                    } else if (!this.sparkplugClient.eonNode.edgeNodeId) {
                        alert("The Edge Node Edge Node ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["device_id"].parameterValue) {
                        alert("The Edge Node Device ID parameter must be set before executing this test");
                        return;
                    } else if (!testParameter.parameters["broker_uri"].parameterValue) {
                        alert("The Broker URI parameter must be set before executing this test");
                        return;
                    }
                    const testParameters =
                        this.sparkplugClient.hostApplication.hostId +
                        " " + this.sparkplugClient.eonNode.groupId +
                        " " + this.sparkplugClient.eonNode.edgeNodeId +
                        " " + testParameter.parameters["device_id"].parameterValue +
                        " " + testParameter.parameters["broker_uri"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else {
                    alert("Test does not exist");
                }
            }

            if (this.sparkplugClient.testType === "BROKER") {
                const profile = "broker";
                const testType = testParameter.name;
                if (testType === "CompliantBrokerTest") {
                    const testParameters =
                        this.sparkplugClient.broker.host +
                        " " + this.sparkplugClient.broker.port;
                    this.createTestRequest(profile, testType, testParameters);
                } else if (testType === "AwareBrokerTest") {
                    const testParameters =
                        this.sparkplugClient.broker.host +
                        " " + this.sparkplugClient.broker.port +
                        " " + testParameter.parameters["group_id"].parameterValue +
                        " " + testParameter.parameters["edge_id"].parameterValue;
                    this.createTestRequest(profile, testType, testParameters);
                } else {
                    alert("Test:'" + testType + "' does not exist");
                }
            }
        },

        /**
         * Abort a single test.
         */
        abortTestFun: function (testParameter) {
            if (this.mqttClient.connected === false) {
                alert("You need to connect to the broker first.");
                return;
            }
            console.log("index abortTest:", testParameter);
            this.endTestRequest();
            this.popupNotifications = [];
        },

        /**
         * Reset a single test.
         */
        resetTestFun: function (testParameter) {
            if (this.mqttClient.connected === false) {
                alert("You need to connect to the broker first.");
                return;
            }

            this.abortTestFun(testParameter);
            this.resetCurrentTest();
        },

        /**********************
         * Underlying functions
         **********************/

        /**
         * Create subscription to log topic for the tests.
         */
        createResultLogTopic: function () {
            const resultTopic = "SPARKPLUG_TCK/RESULT";
            const logTopic = "SPARKPLUG_TCK/LOG";
            const reportTopic = "SPARKPLUG_TCK/REPORT";
            const downloadTopic = "SPARKPLUG_TCK/REPORT_DOWNLOAD";
            console.log("index: createLogback - subscribe to: ", resultTopic, logTopic, reportTopic, downloadTopic);
            this.mqttClient.subscribe([resultTopic, logTopic, reportTopic, downloadTopic], (error) => {
                if (error) {
                    console.log("index: Subscribe error", error);
                }
            });
            /*
            * read payload of summary and write into vue variable
             */
            this.mqttClient.on("message", (topic, message) => {
                if (topic === resultTopic || topic === logTopic ) {
                    if (this.logging.length > 100) {
                        this.logging.shift()
                    }
                    const logMessage = {
                        logLevel: "INFO",
                        logValue: message.toString(),
                        id: this.logging.length,
                    };

                    console.log("index: message on Logging:", logMessage);
                    this.logging.push(logMessage);
                    this.currentTestLogging = logMessage;
                } else if (topic ===  reportTopic ) {
                    this.reportCreated= "created";
                    console.log("index: message on Create report:", this.report);
                } else  if (topic ===  downloadTopic ) {
                    console.log("index: message on Download report for "+ this.report + " with size:", message.length);
                    this.reportSummary = message.toString();
                }
            });
        },

        /**
         * Create a listener for test interactions which creates a popup.
         */
        createInteractionListener: function () {
            if (interactionListenerCreated) {
                return;
            }
            interactionListenerCreated = true;
            console.log("index: createInteractionListener");

            const promptTopic = "SPARKPLUG_TCK/CONSOLE_PROMPT";

            this.mqttClient.subscribe(promptTopic, (error) => {
                if (error) {
                    console.log("Subscribe error", error);
                }
            });

            this.mqttClient.on("message", (topic, message) => {
                if (topic === promptTopic) {
                    console.log("popup notification:", message.toString());
                    this.popupNotifications.push(message.toString());
                }
            });
        },

        /**
         * Reset current test. Nulls currentTest & currentTestLogging.
         */
        resetCurrentTest: function () {
            this.currentTest = null;
            this.currentTestLogging = null;
        },

        resetLogging: function () {
            this.logging = [];
        },

        downloadReport: function () {
            this.report = "Summary-"+this.filename+".html"
            let element = document.createElement('a');
            element.setAttribute('href', 'data:text;charset=utf-8,' + encodeURIComponent(this.reportSummary));
            element.setAttribute('download', this.report);
            element.style.display = 'none';
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
            console.log("notification:", "download Report from " + this.filename);
        },


        /**********************
         * MQTT functions
         **********************/

        /**
         * Handles a successful MQTT connection.
         */
        mqttConnected: function (mqttClient, filename, UTCwindow) {
            this.mqttClient = mqttClient;
            this.filename = filename;
            this.UTCwindow = UTCwindow;
            this.createInteractionListener();
            this.createResultLogTopic();
            this.createResultLog();
            this.setTCKParms();
            this.activeTab = 1;
        },

        createResultLog: function () {
            let topic = "SPARKPLUG_TCK/RESULT_CONFIG";
            let payload = "NEW_RESULT-LOG " + this.filename;
            this.reportCreated ="";
            console.log("index: configure Result Log - create new file:", this.filename);
            this.mqttClient.publish(topic, payload, {qos: 1, retain: false}, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        createReport: function () {
            let topic = "SPARKPLUG_TCK/REPORT";
            let payload = "NEW_REPORT " + this.filename;
            console.log("index: create TCK Report - from: ", this.filename);
            this.mqttClient.publish(topic, payload, {qos: 1, retain: false}, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        setTCKParms: function () {
            let topic = "SPARKPLUG_TCK/CONFIG";
            let payload = "UTCwindow " + this.UTCwindow;
            console.log("index: setTCKParms - UTCwindow: ", this.UTCwindow);
            this.mqttClient.publish(topic, payload, {qos: 1, retain: true}, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        /**
         * Create an MQTT publish to start/request a test.
         */
        createTestRequest: function (profile, testType, testParameters) {
            let topic = "SPARKPLUG_TCK/TEST_CONTROL";
            let payload = "NEW_TEST " + profile + " " + testType + " " + testParameters;
            console.log("index: createTest:", payload);
            this.mqttClient.publish(topic, payload, 1, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        /**
         * Create an MQTT publish to end a test.
         */
        endTestRequest: function () {
            let topic = "SPARKPLUG_TCK/TEST_CONTROL";
            let payload = "END_TEST";

            this.mqttClient.publish(topic, payload, 1, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        /**
         * Generic method to create an MQTT publish.
         */
        publishMessage: function (topic, message) {
            const qos = 1;

            this.mqttClient.publish(topic, message, qos, (error) => {
                if (error) {
                    console.log("Publish error", error);
                }
            });
        },

        /**********************
         * UI functions
         **********************/

        /**
         * Method scrolls user back to the top.
         */
        backToTop: function () {
            document.body.scrollTop = 0;
            document.documentElement.scrollTop = 0;
        },

        /**
         * Opens or closes the MQTT connect or client connect tab if clicked.
         */
        changeMqttConnectFun: function (clickedTab) {
            if (this.activeTab === clickedTab) {
                this.tabOpen = !this.tabOpen;
            } else {
                this.tabOpen = true;
            }
        },
    },
};
</script>
