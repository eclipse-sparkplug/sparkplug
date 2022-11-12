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
 *    Ian Craggs - updates for usability and features
 ****************************************************************************-->

<template>
    <div>
        <h3 v-if="testType === 'HOSTAPPLICATION'">Host Application Tests</h3>
        <h3 v-else-if="testType === 'EONNODE'">Edge Node Tests</h3>
        <h3 v-else>Broker Tests</h3>
        <TckTestsInformation :testNames="getTestNames" v-model="sidebar"/>
        <div>
            <div v-if="testType === 'HOSTAPPLICATION'">
                <TckTest
                    v-for="test in hostTests"
                    :id="test.testValues.name"
                    :key="test.testValues.name"
                    :ref="test.testValues.name"
                    v-model="test.testValues"
                    @start-single-test="(testParameter) => $emit('start-single-test', testParameter)"
                    @abort-single-test="(testParameter) => $emit('abort-single-test', testParameter)"
                    @reset-single-test="(testParameter) => resetTest(testParameter)"
                />
            </div>
            <div v-else-if="testType === 'EONNODE'">
                <TckTest
                    v-for="test in eonTests"
                    :id="test.testValues.name"
                    :key="test.testValues.name"
                    :ref="test.testValues.name"
                    v-model="test.testValues"
                    @start-single-test="(testParameter) => $emit('start-single-test', testParameter)"
                    @abort-single-test="(testParameter) => $emit('abort-single-test', testParameter)"
                    @reset-single-test="(testParameter) => resetTest(testParameter)"
                />
            </div>
            <div v-else-if="testType === 'BROKER'">
                <TckTest
                    v-for="test in brokerTests"
                    :id="test.testValues.name"
                    :key="test.testValues.name"
                    :ref="test.testValues.name"
                    v-model="test.testValues"
                    @start-single-test="(testParameter) => $emit('start-single-test', testParameter)"
                    @abort-single-test="(testParameter) => $emit('abort-single-test', testParameter)"
                    @reset-single-test="(testParameter) => resetTest(testParameter)"
                />
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        /**
         * The client type denotes the current used client.
         * @type {String}
         */
        testType: {
            type: String,
            required: true,
            default: "HOSTAPPLICATION",
        },

        currentTestLogging: null,
        currentTest: null,
    },

    computed: {
        /**
         * Calculates a readable name out of the client type property.
         * @return {String} Readable name
         */
        testTypeReadableName: function () {
            if (this.testType === "HOSTAPPLICATION") {
                return "Host Application Tests";
            } else if (this.testType === "EONNODE") {
                return "EoN Node Tests";
            } else if (this.testType === "BROKER") {
                return "Sparkplug MQTT Broker Tests";
            }
        },

        /**
         * Creates a list of all test names depending on the client type.
         * @return {Object[]} List of objects with test name & readable name
         */
        getTestNames: function () {
            const testNames = [];
            if (this.testType === "HOSTAPPLICATION") {
                for (const [_, testValue] of Object.entries(this.hostTests)) {
                    const message = {
                        name: testValue.testValues.name,
                        readableName: testValue.testValues.readableName,
                    };
                    testNames.push(message);
                }
                return testNames;
            } else if (this.testType === "EONNODE") {
                for (const [_, testValue] of Object.entries(this.eonTests)) {
                    const message = {
                        name: testValue.testValues.name,
                        readableName: testValue.testValues.readableName,
                    };
                    testNames.push(message);
                }
                return testNames;
            } else if (this.testType === "BROKER") {
                for (const [_, testValue] of Object.entries(this.brokerTests)) {
                    const message = {
                        name: testValue.testValues.name,
                        readableName: testValue.testValues.readableName,
                    };
                    testNames.push(message);
                }
                return testNames;
            }
        },
    },

    watch: {
        /**
         * Updates test with logging.
         * @param {String} newValue - New current tests logging
         * @param {String} oldValue - Old current tests logging
         */
        currentTestLogging: function (newValue, oldValue) {
            if (newValue === null) return;
            let finished = false;
            if (this.testType === "HOSTAPPLICATION") {
                for (const [_, testValue] of Object.entries(this.hostTests)) {
                    if (testValue.testValues.name === this.currentTest) {
                        testValue.testValues.logging.push(newValue);
                        finished = true;
                        if (newValue.logValue.includes("OVERALL: PASS")) {
                            testValue.testValues.result = true;
                        } else if (newValue.logValue.includes("OVERALL: FAIL")) {
                            testValue.testValues.result = false;
                        } else if (newValue.logValue.includes("OVERALL: NOT EXECUTED")) {
                            testValue.testValues.result = false;
                        } else {
                            finished = false;
                        }
                    }
                }
            } else if (this.testType === "EONNODE") {
                for (const [_, testValue] of Object.entries(this.eonTests)) {
                    if (testValue.testValues.name === this.currentTest) {
                        testValue.testValues.logging.push(newValue);
                        finished = true;
                        if (newValue.logValue.includes("OVERALL: PASS")) {
                            testValue.testValues.result = true;
                        } else if (newValue.logValue.includes("OVERALL: FAIL")) {
                            testValue.testValues.result = false;
                        } else if (newValue.logValue.includes("OVERALL: NOT EXECUTED")) {
                            testValue.testValues.result = false;
                        } else {
                            finished = false;
                        }
                    }
                }
            } else if (this.testType === "BROKER") {
                for (const [_, testValue] of Object.entries(this.brokerTests)) {
                    if (testValue.testValues.name === this.currentTest) {
                        testValue.testValues.logging.push(newValue);
                        finished = true;
                        if (newValue.logValue.includes("OVERALL: PASS")) {
                            testValue.testValues.result = true;
                        } else if (newValue.logValue.includes("OVERALL: FAIL")) {
                            testValue.testValues.result = false;
                        } else if (newValue.logValue.includes("OVERALL: NOT EXECUTED")) {
                            testValue.testValues.result = false;
                        } else {
                            finished = false;
                        }
                    }
                }
            }
            if (finished === true) {
                this.$emit("reset-current-test");
            }
        },
    },

    data: function () {
        return {
            /**
             * Opens or closes the sidebar.
             */
            sidebar: false,

            /**
             * All available host application tests
             * @type {Object} hostTests
             * @type {Object} hostTests.{testName}
             * @type {Object} hostTests.{testName}.testValues
             * @type {String} hostTests.{testName}.testValues.name
             * @type {String} hostTests.{testName}.testValues.readableName
             * @type {String} hostTests.{testName}.testValues.description
             * @type {?String[]} hostTests.{testName}.testValues.requirements
             * @type {?Object} hostTests.{testName}.testValues.parameters
             * @type {Object} hostTests.{testName}.testValues.parameters.{parameterName}
             * @type {String} hostTests.{testName}.testValues.parameters.{parameterName}.parameterReadableName
             * @type {String} hostTests.{testName}.testValues.parameters.{parameterName}.parameterValue
             * @type {String} hostTests.{testName}.testValues.parameters.{parameterName}.parameterDescription
             * @type {?String} hostTests.{testName}.testValues.code
             * @type {Boolean} hostTests.{testName}.testValues.result
             * @type {Object[]} hostTests.{testName}.testValues.logging
             * @type {Object} hostTests.{testName}.testValues.logging.logMessage
             * @type {String} hostTests.{testName}.testValues.logging.logMessage.logLevel
             * @type {String} hostTests.{testName}.testValues.logging.logMessage.logValue
             */
            hostTests: {
                sessionEstablishmentTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "SessionEstablishmentTest",
                        readableName: "Session Establishment Test",
                        description: `"This is the Host Application Sparkplug session establishment test.
                        It also tests session re-establishment after an offline state message is received.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Ensure the Host Application implementation to be tested is not running.",
                            "Start this test.",
                            "Start the Host Application.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        result: null,
                        logging: [],
                    },
                },
                sessionTerminationTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "SessionTerminationTest",
                        readableName: "Session Termination Test",
                        description: `This is the Sparkplug Host Application session termination test. There are 
                        two ways of running it. Either set the MQTT Client id of an already connected Host Application,
                        or connect the Host Application while the test is running. This tests a deliberate disconnection
                        preceded by the sending of a STATE message, not abnormal termination firing a will message.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Enter the MQTT Client id if the Host Application is already running.",
                            "Start this test.",
                            "Start the Host Application, if it wasn't already.",
                            "Stop the Host Application to trigger the Sparkplug death messages.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            client_id: {
                                parameterReadableName: "MQTT ClientId",
                                parameterValue: "",
                                parameterDescription: "The MQTT Client Id of the Host Application",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                sendCommandTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "SendCommandTest",
                        readableName: "Send Command Test",
                        description:
                            `To check that a command from a Host Application under test is correct to
                            both an Edge Node (NCMD) and a Device (DCMD). You will be asked to send a
                            rebirth and a metric value update command to an Edge Node and a Device in
                            sequence. If you don't connect the Edge Node before the test runs, a 
                            simulated Edge Node will be used.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set the Group, Edge Node and Device Id of the Edge Node/Device to be used.",
                            "Start the Host Application, if it is not yet running.",
                            "Optionally start your Edge Node implementation.",
                            "Start this test.",
                            "Follow the instructions in the sequence of dialogs. Press OK to move forward.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            group_id: {
                                parameterReadableName: "Group Id",
                                parameterValue: "",
                                parameterDescription: "The Group Id of the Edge Node",
                            },
                            edge_node_id: {
                                parameterReadableName: "Edge Node Id",
                                parameterValue: "",
                                parameterDescription: "The Edge Node Id the Host Application will send messages to.",
                            },
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Device Id the Host Application will send messages to.",
                            },
                        },
                        code: "",
                        result: null,
                        logging: [],
                    },
                },
                edgeSessionTerminationTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "EdgeSessionTerminationTest",
                        readableName: "Edge Session Termination Test",
                        description:
                            `To check that the Host Application behaves correctly when death messages are received from an Edge Node.
                            This is mainly a manual check, as there is no programmatic way to query Sparkplug host applications. You
                            will be presented with a sequence of questions, to which you should respond with OK or FAIL.

                            This test use a simulated Edge Node and Device, so ensure the Group, Edge Node and Device IDs you use are
                            not being used already.
                            `,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Start the Host Application, if it is not yet running.",
                            "Set the Group, Edge Node and Device Id of the Edge Node/Device to be used.",
                            "Start this test.",
                            "Answer each of the questions, having checked the Host Application responses.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            group_id: {
                                parameterReadableName: "Group Id",
                                parameterValue: "",
                                parameterDescription: "The Group Id of the Edge Node",
                            },
                            edge_node_id: {
                                parameterReadableName: "Edge Node Id",
                                parameterValue: "",
                                parameterDescription: "The id of the Edge Node the Host Application will receive the death message from.",
                            },
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Device Id of a device connected to the Edge Node.",
                            },
                        },
                        code: "",
                        result: null,
                        logging: [],
                    },
                },
                messageOrderingTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "MessageOrderingTest",
                        readableName: "Message Ordering Test",
                        description:
                            `To check that the Host Application behaves correctly when messages are received out of order.
                            This will use a simulated Edge Node and Device to force the conditions.`,
                        requirements: [
                            "Start the Host Application, if it is not yet running.",
                            "Connect this console to the HiveMQ broker.",
                            "Set a Host Application Id that is used by an Application.",
                            "Start this test.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            group_id: {
                                parameterReadableName: "Group Id",
                                parameterValue: "",
                                parameterDescription: "The Group Id of the Edge Node",
                            },
                            edge_node_id: {
                                parameterReadableName: "Edge Node Id",
                                parameterValue: "",
                                parameterDescription: "The id of the Edge Node the Host Application will receive the death message from.",
                            },
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Device Id of a device connected to the Edge Node.",
                            },
                            reorder_timeout: {
                                parameterReadableName: "Reorder Timeout (ms)",
                                parameterValue: "5000",
                                parameterDescription: "The Reorder Timeout value in ms of the Host Application.",
                            },
                        },
                        code: "",
                        result: null,
                        logging: [],
                    },
                },
                multipleBrokerTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "MultipleBrokerTest",
                        readableName: "Multple MQTT Server (Broker) Test",
                        description:
                            `To check that the Host Application behaves correctly when multiple MQTT servers are used.
                            `,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Ensure all Host Applications are disconnected from both brokers.",
                            "Set the broker URI of the second broker.",
                            "Configure the Host Application to connect to the two brokers.",
                            "Start this test.",
                            "Start the Host Application implementation to test.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            broker_uri: {
                                parameterReadableName: "Broker URI",
                                parameterValue: "tcp://localhost:1884",
                                parameterDescription: "The connection URI of the second MQTT broker",
                            },
                        },
                        code: "",
                        result: null,
                        logging: [],
                    },
                },
            },

            /**
             * All available eon tests
             * @type {Object} eonTests
             * @type {Object} eonTests.{testName}
             * @type {Object} eonTests.{testName}.testValues
             * @type {String} eonTests.{testName}.testValues.name
             * @type {String} eonTests.{testName}.testValues.readableName
             * @type {String} eonTests.{testName}.testValues.description
             * @type {?String[]} eonTests.{testName}.testValues.requirements
             * @type {?Object} eonTests.{testName}.testValues.parameters
             * @type {Object} eonTests.{testName}.testValues.parameters.{parameterName}
             * @type {String} eonTests.{testName}.testValues.parameters.{parameterName}.parameterReadableName
             * @type {String} eonTests.{testName}.testValues.parameters.{parameterName}.parameterValue
             * @type {String} eonTests.{testName}.testValues.parameters.{parameterName}.parameterDescription
             * @type {?String} eonTests.{testName}.testValues.code
             * @type {Boolean} eonTests.{testName}.testValues.result
             * @type {Object[]} eonTests.{testName}.testValues.logging
             * @type {Object} eonTests.{testName}.testValues.logging.logMessage
             * @type {String} eonTests.{testName}.testValues.logging.logMessage.logLevel
             * @type {String} eonTests.{testName}.testValues.logging.logMessage.logValue
             */
            eonTests: {
                sessionEstablishmentTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "SessionEstablishmentTest",
                        readableName: "Session Establishment Test",
                        description: `This test checks that Edge Nodes and Devices can connect correctly to the MQTT broker.
                        It can be run in two ways, with a real Host Application or simulated. To use a real Host Application, 
                        ensure it is active before the test is started.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set the Host App, Group, Edge Node and Device ids.",
                            "Ensure the Host App is started, if you are using a real one.",
                            "Start this test.",
                            "Connect the Edge Node and Device.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_ids: {
                                parameterReadableName: "Device Ids",
                                parameterValue: "",
                                parameterDescription: "The space separated list of Ids of devices connected to the edge node.",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                sessionTerminationTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "SessionTerminationTest",
                        readableName: "Session Termination Test",
                        description: `This is the Sparkplug Edge Node session termination test.
                        It tests deliberate session termination, where NDEATH and DDEATH
                        packets are sent before disconnecting. A simulated Host Application will
                        be used if the named one is not already connected. `,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set the Device Id that is used by the configured Host, Group and Edge.",
                            "Connect the Host Application to the broker, if you are using one.",
                            "Start this test.",
                            "Connect the Edge Node and Device.",
                            "Stop the edge node and device named.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_ids: {
                                parameterReadableName: "Device Ids",
                                parameterValue: "",
                                parameterDescription: "The space separated list of Ids of devices connected to the edge node.",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                sendDataTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "SendDataTest",
                        readableName: "Send Data Test",
                        description: `This is the Edge Node Sparkplug send data test.
                            It determines the MQTT client ID of the Edge Node from the MQTT connect,
                            so you must connect the Edge Node after the test has started.
                        `,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Ensure the Edge Node is not connected to the broker.",
                            "Start this test.",
                            "Connect the Edge Node and send data from the Edge Node and Device.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Id of a device connected to the edge node",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                sendComplexDataTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "SendComplexDataTest",
                        readableName: "Send Complex Data Test",
                        description: `This is the Sparkplug test to validate the Edge Node sending complex
                        data types: DataSets, Templates and Custom Properties. It determines the MQTT client
                        ID of the Edge Node from the MQTT connect, so you must connect the Edge Node after
                        the test has started.
                        `,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Ensure the Edge Node is not connected to the broker.",
                            "Start this test.",
                            "Connect the Edge Node and send data from the Edge Node and Device.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Id of a device connected to the edge node",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                receiveCommandTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "ReceiveCommandTest",
                        readableName: "Receive Command Test",
                        description: `This is the Edge Node Sparkplug receive command test. A rebirth
                                      command will be sent to the Edge Node, and the proper rebirth
                                      sequence checked. Do not connect the Host Application and 
                                      start the Edge node only once the test has been started.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Ensure the Host Application and Edge Node are not connected.",
                            "Start this test.",
                            "Start the Edge Node.",
                            "The Edge Node and Devices should receive a rebirth command.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Id of a device connected to the edge node",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                primaryHostTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "PrimaryHostTest",
                        readableName: "Primary Host Test",
                        description: `This checks that an Edge Node which is configured to wait for a Primary Host
                                      Application behaves correctly. The test contains delays so can take 30 seconds
                                      or more to run.
                                      This test uses a simulated Host Application, so no Host Application should be
                                      connected to the broker as this will confuse the test.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Ensure no Host Application is connected to the HiveMQ broker.",
                            "Start the edge node to test.",
                            "Start this test.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Id of a device connected to the edge node",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
                multpleBrokerTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "MultipleBrokerTest",
                        readableName: "Multiple MQTT Server (Broker) Test",
                        description: `This is the Sparkplug Edge Node test. It checks that an Edge Node behaves 
                                      correctly when multiple Brokers are present. The Edge Node can be started
                                      before the test, or the test before the Edge Node, as long as no Host Application
                                      is online. A simulated Host Application will be used.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Start a second MQTT broker listening on a different port.",
                            "Ensure no Host Application is connected to either broker.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Set the broker URI of the second broker.",
                            "Start the Edge Node implementation to test.",
                            "Start this test.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Id of a device connected to the edge node",
                            },
                            broker_uri: {
                                parameterReadableName: "Broker URI",
                                parameterValue: "tcp://localhost:1884",
                                parameterDescription: "The connection URI of the second MQTT broker",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
            },
            brokerTests: {
                sparkplugCompliantBrokerTest: {
                    testValues: {
                        testType: "BROKER",
                        name: "CompliantBrokerTest",
                        readableName: "Sparkplug Compliant Test",
                        description: `Any fully MQTT 3.1.1 or 5.0 MQTT broker will meet the requirements of Sparkplug.
                                      However, not all of the features of MQTT are required. This test checks that the
                                      broker supports the features of MQTT that are required.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Ensure the correct Broker TCP/IP address is set.",
                            "Start this test.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        result: null,
                        logging: [],
                    },
                },
                sparkplugAwareBrokerTest: {
                    testValues: {
                        testType: "BROKER",
                        name: "AwareBrokerTest",
                        readableName: "Sparkplug Aware Test",
                        description: `A 'Sparkplug Aware' MQTT Server includes all of the aspects of a Sparkplug
                                     Compliant MQTT Server. In addition, it also must have the ability to store
                                     NBIRTH and DBIRTH messages of Sparkplug Edge Nodes that pass through it. This
                                     test checks that those messages are stored and updated correctly.`,
                        requirements: [
                            "Connect this console to the HiveMQ broker.",
                            "Ensure the correct Broker TCP/IP address is set.",
                            "Enter the Group and Edge Node ids to use.",
                            "Start this test.",
                            "Connect an Edge Node to trigger a birth message for the Edge and its Devices.",
                            "End the Edge Node to trigger DEATH messages.",
                            "Wait until the test is finished and check the results.",
                            "If the test does not stop automatically, press the \"Stop Test\" button."
                        ],
                        parameters: {
                            group_id: {
                                parameterReadableName: "Group Id",
                                parameterValue: "",
                                parameterDescription: "The Id of the group of the edge node",
                            },
                            edge_id: {
                                parameterReadableName: "EdgeNode Id",
                                parameterValue: "",
                                parameterDescription: "The Id of the edge node",
                            },
                        },
                        result: null,
                        logging: [],
                    },
                },
            },
        };
    },

    methods: {
        /**
         * Inverts sidebar state.
         */
        toggleSidebar: function () {
            this.sidebar = !this.sidebar;
        },

        resetTest: function (testParameter) {
            this.$emit("reset-single-test", testParameter);

            if (this.testType === "HOSTAPPLICATION") {
                for (const [_, testValue] of Object.entries(this.hostTests)) {
                    if (testValue.testValues.name === testParameter.name) {
                        testValue.testValues.logging = [];
                        testValue.testValues.result = null;
                    }
                }
            } else if (this.testType === "EONNODE") {
                for (const [_, testValue] of Object.entries(this.eonTests)) {
                    if (testValue.testValues.name === testParameter.name) {
                        testValue.testValues.logging = [];
                        testValue.testValues.result = null;
                    }
                }
            } else if (this.testType === "BROKER") {
                for (const [_, testValue] of Object.entries(this.brokerTests)) {
                    if (testValue.testValues.name === testParameter.name) {
                        testValue.testValues.logging = [];
                        testValue.testValues.result = null;
                    }
                }
            }
        },
    },
};
</script>
