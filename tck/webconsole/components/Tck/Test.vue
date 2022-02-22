<!-- @author Lukas Brand -->

<template>
    <b-card border-variant="primary" bg-variant="light" class="mt-2">
            <b-container bg-variant="light" fluid="xs" border-bottom-0 small >
                <b-row>
                    <b-col sm="9" >
                      <h5 class="text-dark" v-b-toggle="'my-collapse-' + test.name + test.type" >{{test.readableName}}</h5>
                    </b-col>
                    <b-col sm="3" align="right">
                        <b-badge
                            variant="warning"
                            size="xs"
                            v-b-toggle="'my-collapse-' + test.name + test.type"
                        >
                            Show/Hide </b-badge>
                    </b-col>
                </b-row>
            </b-container>

        <b-collapse
            :id="'my-collapse-' +  test.name + test.type"
        ><hr>
        <b-container bg-variant="light"  fluid="xs" >
            <b-row>
                <b-col sm="6" >
                    <h6 class="text-primary">Description:</h6>
                    <b-card-text > {{ test.description }}</b-card-text>
                </b-col>
                <b-col sm="6">
                    <div v-if="test.requirements" class="mt-1" >
                        <h6 class="text-primary">Instructions:</h6>
                        <ol>
                        <span v-for="requirement in test.requirements" :key="requirement">
                            <li size="sm"> {{ requirement }} </li>
                        </span>
                        </ol>
                    </div>
                </b-col>
            </b-row>
            <b-row>
                <b-col sm="6"><br/>
                    <div v-if="test.parameters" class="mt-1">
                        <h6 class="text-primary">Parameters:</h6>
                        <div v-for="(parameter, parameterName) in test.parameters" :key="parameterName">
                            <b-form-group
                                :id="parameter.parameterName + '-parameter-group'"
                                :description="parameter.parameterDescription"
                                :label="parameter.parameterReadableName + ':'"
                                :label-for="parameterName + '-parameter-input'"
                                label-cols-sm="4"
                                label-cols-lg="3"
                                content-cols-sm
                                content-cols-lg="7"
                            >
                                <b-form-input
                                    :id="parameterName + '-parameter-input'"
                                    :placeholder="parameterName"
                                    :value="parameter.parameterValue"
                                    @change="update('parameters[' + parameterName + '].parameterValue', $event)"
                                ></b-form-input>
                            </b-form-group>
                        </div>
                    </div>
                </b-col>
                <b-col sm="6">
                    <div v-if="test.code" class="mt-6">
                        <h6 class="text-primary">Example Code:</h6>
                        <b-input-group>
                            <b-textarea v-model="test.code" no-resize readonly rows="5" style="white-space: pre"/>
                            <b-input-group-append>
                                <b-button
                                    id="clipboard-button"
                                    v-clipboard:copy="test.code"
                                    v-clipboard:error="(value) => onClipboardError(value)"
                                    v-clipboard:success="(value) => onClipboardCopy(value)"
                                    variant="outline-success"
                                    @click="copied = true"
                                    @mouseout="copied = false"
                                    label-cols-sm="4"
                                    label-cols-lg="3"
                                    content-cols-sm
                                    content-cols-lg="7"
                                >
                                    <b-icon icon="clipboard"/>
                                </b-button>
                            </b-input-group-append>
                        </b-input-group>
                    </div>
                </b-col>
            </b-row>
            <b-row>
                <b-col sm="12">
                <hr>
                <div v-show="test.type==='HOSTAPPLICATION'">
                    <b-button-toolbar size="m">
                            <div v-show="startH">
                                <b-button class="mr-1" variant="success"
                                          @click="$emit('start-single-test', test); testState(test)">Start Test
                                </b-button>
                                <span v-if="test.result != null">
                                    <b-button class="mr-5" variant="info"
                                         @click="$emit('reset-single-test', test); resetState(test)">Reset Test</b-button>
                                </span>
                            </div>
                            <div v-show="stopH">
                              <span v-if="test.result === null">
                                  <b-button class="ml-1" variant="danger"
                                            @click="$emit('abort-single-test', test); testState(test) ">Stop Test</b-button>
                              </span>
                                <b-button class="mr-5" variant="info"
                                          @click="$emit('reset-single-test', test); resetState(test)">Reset Test
                                </b-button>
                                <span v-if="test.result === null" class="float-right">
                                       <strong>Test is running...</strong>
                                       <b-spinner class="ml-auto" variant="info"></b-spinner>
                                </span>
                            </div>

                            <span v-if="test.result != null">
                                <span class="my-auto mr-5">overall Result: </span>
                                <b-icon v-if="test.result === true" class="my-auto h3" icon="check-circle-fill" variant="success"/>
                                <b-icon v-else-if="test.result === false" class="my-auto h3" icon="x-circle-fill" variant="danger"/>
                            </span>
                        </b-button-toolbar>
                    <div v-if="loggingSplitInLines.length > 0" class="mt-3">
                        <h6 class="text-primary">Logging:</h6>
                        <div>
                            <ul v-for="logLine in loggingSplitInLines" :key="logLine" class="list-group">
                                <li class="list-group-item border-bottom-0 small">{{ logLine }}</li>
                            </ul>

                        </div>
                    </div>
                </div>
                <div v-show="test.type==='EONNODE'">
                    <b-button-toolbar size="m">
                            <div v-show="start">
                                <b-button class="mr-1" variant="success"
                                          @click="$emit('start-single-test', test); testState(test)">Start Test
                                </b-button>
                                <span v-if="test.result != null">
                                    <b-button class="mr-5" variant="info"
                                              @click="$emit('reset-single-test', test); resetState(test)">Reset Test</b-button>
                                </span>
                            </div>
                            <div v-show="stop">
                              <span v-if="test.result === null">
                                  <b-button class="ml-1" variant="danger"
                                            @click="$emit('abort-single-test', test); testState(test) ">Stop Test</b-button>
                              </span>
                                <b-button class="mr-5" variant="info"
                                          @click="$emit('reset-single-test', test); resetState(test)">Reset Test
                                </b-button>
                                <span v-if="test.result === null" class="float-right">
                                       <strong>Test is running...</strong>
                                       <b-spinner class="ml-auto" variant="info"></b-spinner>
                                </span>
                            </div>

                            <span v-if="test.result != null">
                                <span class="my-auto mr-5">overall Result: </span>
                                <b-icon v-if="test.result === true" class="my-auto h3" icon="check-circle-fill" variant="success"/>
                                <b-icon v-else-if="test.result === false" class="my-auto h3" icon="x-circle-fill" variant="danger"/>
                            </span>
                        </b-button-toolbar>
                    <div v-if="loggingSplitInLines.length > 0" class="mt-3">
                        <h6 class="text-primary">Logging:</h6>
                        <div>
                            <ul v-for="logLine in loggingSplitInLines" :key="logLine" class="list-group">
                                <li class="list-group-item border-bottom-0 small">{{ logLine }}</li>
                            </ul>

                        </div>
                    </div>
                </div>
                </b-col>
            </b-row>
        </b-container>
        </b-collapse>
    </b-card>
</template>

<script>
import {cloneDeep, tap, set} from "lodash";

export default {
    data() {
        return {
            start: true,
            stop: false,
            startH: true,
            stopH: false,
            isVisible: false
        }
    },

    model: {
        prop: "test",
        event: "on-updated",
    },

    props: {
        test: {
            /**
             * This is the test type
             * @type {String}
             */
            type: {
                type: String,
                required: true,
                default: "HOSTAPPLICATION",
            },

            /**
             * This is the test name, also used as id. Defined in the backend.
             * @type {String}
             */
            name: {
                type: String,
                required: true,
                default: "unknownTest",
            },

            /**
             * A readable version of the test name.
             * @type {String}
             */
            readableName: {
                type: String,
                required: true,
                default: "unknown test",
            },


            /**
             * Tests description.
             * @type {String}
             */
            description: {
                type: String,
                required: true,
                default: "test is missing a description",
            },

            /**
             * This is a list of textual requirements which are needed for the test.
             * @type {String[]}
             */
            requirements: {
                type: Array,
                required: false,
                default: () => [],
            },

            /**
             * This is a list of parameters needed to run the test.
             * Parameter name is the identifier for the parameter.
             * Parameter readable name holds the name in a printable version.
             * Parameter value holds the user set value.
             * Parameter description informs about the usage of the parameter.
             * @type {Object} parameters
             * @type {Object} parameters.{parameterName}
             * @type {String} parameters.{parameterName}.parameterReadableName
             * @type {String} parameters.{parameterName}.parameterValue
             * @type {String} parameters.{parameterName}.parameterDescription
             *
             */
            parameters: {
                type: Object,
                required: false,
                default: () => {
                },
            },

            /**
             * This is code which can be used to fulfill the requirements. Contains whitespaces.
             * @type {String}
             */
            code: {
                type: String,
                required: false,
                default: "",
            },

            /**
             * The test result value.
             * @type {?Boolean}
             */
            result: {
                type: Boolean,
                required: false,
                default: false,
            },

            /**
             * List of logging information during the test.
             * @type {Object[]} logging
             * @type {Object} logging.logMessage
             * @type {String} logging.logMessage.logLevel
             * @type {String} logging.logMessage.logValue
             */
            logging: {
                type: Array,
                required: true,
                default: () => [],
            },
        },
    },

    computed: {
        loggingSplitInLines: function () {
            let logLines = [];
            for (const logMessage of this.test.logging) {
                const lines = logMessage.logValue.trim().split(/\r\n|\n\r|\n|\r/);
                logLines = logLines.concat(lines);
                console.log(lines);
            }
            console.log(logLines);
            logLines = logLines.filter((line) => line.trim().length != 0);
            return logLines;
        },
    },

    methods: {
        /**
         * Method triggered on clipboard copy.
         * @param {Object} value - Event information
         */
        onClipboardCopy: function (value) {
            alert("Copied text");
        },

        /**
         * Method triggered on clipboard copy failure.
         * @param {Object} value - Event information
         */
        onClipboardError: function (value) {
            alert("Failed to copy text");
        },

        /**
         * Generic method to update a certain value of an existing object.
         * @param {String} key - Key which points to part of the object which should be replaced.
         * @param {Any} value - New value which replaces the old key based value.
         * @emits TckTest#on-updated
         */
        update(key, value) {
            const emitValue = tap(cloneDeep(this.test), (v) => set(v, key, value));
            this.$emit("on-updated", emitValue);
        },

        handleVisibility(isVisible) {
            this.isVisible = isVisible
        },

        testState(test) {

            if( test.type === "HOSTAPPLICATION") {
                this.startH = !this.startH;
                this.stopH = !this.stopH;
            } else {
                this.start = !this.start;
                this.stop = !this.stop;
            }
        },
        resetState(test ) {
            if( test.type === "HOSTAPPLICATION") {
                this.startH = true;
                this.stopH = false;
            } else {
                this.start = true;
                this.stop = false;
            }
            this.test.result = null;
        }

    },
};
</script>
