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
        <h3 v-else-if="testType === 'EONNODE'">EoN Node Tests</h3>
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
                        description: "This is the Host Application Sparkplug session establishment, and re-establishment test.",
                        requirements: [
                            "Setup a MQTT Connection ",
                            "Set a Host Application Id that is used by an Application",
                            "Start this test.",
                            "Start the Host Application to trigger events of MQTT messages.",
                            "Wait until Tests are finished and check Results."
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
                        or connect the Host Application while the test is running.`,
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
                            "To check that a command from a Host Application under test is correct to both an edge node (NCMD) and a device (DCMD).",
                        requirements: [
                            "Start the Host Application, if it is not yet running.",
                            "Setup a MQTT Connection.",
                            "Set a Host Application Id that is used by an Application.",
                            "Start this test.",
                            "Wait until Tests are finished and check Results."
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
                receiveDataTest: {
                    testValues: {
                        testType: "HOSTAPPLICATION",
                        name: "ReceiveDataTest",
                        readableName: "Receive Data Test",
                        description:
                            "To check that a data from an edge node (NDATA) and a device (DDATA) can be received and procesed by the Host Application.",
                        requirements: [
                            "Start the Host Application, if it is not yet running.",
                            "Setup a MQTT Connection.",
                            "Set a Host Application Id that is used by an Application.",
                            "Start this test.",
                            "Wait until Tests are finished and check Results."
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
                                parameterDescription: "The Edge Node Id the Host Application will receive data from.",
                            },
                            device_id: {
                                parameterReadableName: "Device Id",
                                parameterValue: "",
                                parameterDescription: "The Device Id the Host Application will receive data from.",
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
                            "To check that the Host Application behaves correctly when death messages are received from an Edge Node.",
                        requirements: [
                            "Start the Host Application, if it is not yet running.",
                            "Connect this console to the HiveMQ broker.",
                            "Set a Host Application Id that is used by an Application.",
                            "Start this test.",
                            "Wait until Tests are finished and check Results."
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
                        description: "This is the Edge Node Sparkplug session establishment test.",
                        requirements: [
                            "Setup a MQTT Connection.",
                            "Set a Group and a Device Id that is used by an Application.",
                            "Start this test.",
                            "Start the Host Application to trigger events of MQTT messages.",
                            "Connect Edge Node and Device.",
                            "Wait until Tests are finished and check Results."
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
                        description: "This is the Edge Node Sparkplug session termination test.",
                        requirements: [
                            "Setup a MQTT Connection to the HiveMQ Sparkplug test server.",
                            "Set a Group, Edge and Device Ids to be checked.",
                            "Start this test.",
                            "Terminate the edge node and device named.",
                            "Wait until Tests are finished and check Results."
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
                        description: "This is the Edge Node Sparkplug send data test.",
                        requirements: [
                            "Setup a MQTT Connection.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Start this test.",
                            "Send some data by Edge Node and Devices.",
                            "Wait until Tests are finished and check Results."
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
                        description: "This is the Edge Node Sparkplug receive command test. (NCMD)",
                        requirements: [
                            "Setup a MQTT Connection.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Start this test.",
                            "Disconnect and Connect the Device.",
                            "The Edge Node and Devices should receive a rebirth command.",
                            "Wait until Tests are finished and check Results."
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
                payloadTest: {
                    testValues: {
                        testType: "EONNODE",
                        name: "PayloadTest",
                        readableName: "Payload Validation Test",
                        description: "This is the Edge Node Sparkplug payload validation test.",
                        requirements: [
                            "Setup a MQTT Connection.",
                            "Set Device Id that is used by the configured Group and Edge.",
                            "Start this test.",
                            "Connect the Device and send some Data",
                            "The Edge Node and Devices should publish a DATA command.",
                            "Wait until Tests are finished and check Results."
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
            },
            brokerTests: {
                sparkplugCompliantBrokerTest: {
                    testValues: {
                        testType: "BROKER",
                        name: "CompliantBrokerTest",
                        readableName: "Sparkplug Compliant Broker Test",
                        description: "This is the test, that checks requirements for a Sparkplug compliant MQTT Broker.",
                        requirements: [
                            "Start this test with the given broker host and port.",
                            "Wait until Tests are finished and check Results."
                        ],
                        result: null,
                        logging: [],
                    },
                },
                sparkplugAwareBrokerTest: {
                    testValues: {
                        testType: "BROKER",
                        name: "AwareBrokerTest",
                        readableName: "Sparkplug Aware Broker Test",
                        description: "This is the test, that checks requirements for a Sparkplug aware MQTT Broker.",
                        requirements: [
                            "Start this test with the given broker host and port.",
                            "Start connect an Edge Node to trigger a Birth Message for Edge and its Devices of the given Group.",
                            "Stop connection to trigger the DEATH Messages of the Edge Node.",
                            "Wait until Tests are finished and check Results."
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
