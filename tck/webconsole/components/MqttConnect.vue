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
    <b-alert
      :show="successCountDown"
      dismissible
      class="position-fixed fixed-top m-0 rounded-0"
      style="z-index: 2000"
      variant="success"
      @dismissed="successCountDown = 0"
      >Webconsole connected</b-alert
    >
    <b-alert
      :show="failureCountDown"
      dismissible
      class="position-fixed fixed-top m-0 rounded-0"
      style="z-index: 2000"
      variant="danger"
      @dismissed="failureCountDown = 0"
      >Webconsole disconnected</b-alert
    >
    <b-collapse v-model="change" id="collapse-1" class="mt-3">
      <b-card title="MQTT Server Configuration" @submit="createConnection" border-variant="primary">
        <b-form>
          <b-container fluid>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Broker Url:</label>
                  </b-col>
                  <b-col sm="9">
                      <b-form-input v-model="connection.host" size="sm"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter broker url" required></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Broker Port:</label>
                  </b-col>
                  <b-col sm="9">
                      <b-form-input v-model="connection.port" size="sm" type="number"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter broker port" required></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Broker Endpoint:</label>
                  </b-col>
                  <b-col sm="9">
                      <b-form-input v-model="connection.endpoint" size="sm"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter broker endpoint"></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Client Id:</label>
                  </b-col>
                  <b-col sm="9">
                      <b-form-input v-model="connection.clientId" size="sm"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter client id"></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Client Username:</label>
                  </b-col>
                  <b-col sm="6">
                      <b-form-input v-model="connection.username" size="sm"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter client username"></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="3">
                      <label>Password:</label>
                  </b-col>
                  <b-col sm="6">
                      <b-form-input v-model="connection.password" size="sm"
                                    :disabled="mqttClient.connected"
                                    placeholder="Enter client password"></b-form-input>
                  </b-col>
              </b-row>
              <b-row class="mt-3">
                  <b-col sm="9">
                      <b-button type="submit" variant="primary" :disabled="mqttClient.connected"
                                @click="createConnection">
                          {{ mqttClient.connected ? "Connected" : "Connect" }}
                      </b-button>

                      <b-button v-if="mqttClient.connected" variant="danger" class="conn-btn"
                                @click="destroyConnection">
                          Disconnect
                      </b-button>

                      <b-button type="secondary" :disabled="mqttClient.connected" @click="defaultConnection"> Set
                          Default Values
                      </b-button>
                  </b-col>
              </b-row>
          </b-container>
        </b-form>
      </b-card>
    </b-collapse>
    <b-collapse v-model="change" id="collapse-1" class="mt-3">
      <b-card title="Other settings" @submit="createConnection" border-variant="primary">
        <b-row class="mt-3">
          <b-col sm="3">
            <label>Result logfile:</label>
          </b-col>
          <b-col sm="9">
            <b-form-input v-model="filename" size="sm"
                :disabled="mqttClient.connected"
                placeholder="Enter the result log file name"></b-form-input>
          </b-col>
        </b-row>
        <b-row class="mt-3">
          <b-col sm="3">
            <label>UTC check window (ms):</label>
          </b-col>
          <b-col sm="9">
            <b-form-input v-model="UTCwindow" size="sm"
                :disabled="mqttClient.connected"
                placeholder=defaultUTCwindow></b-form-input>
          </b-col>
        </b-row>
      </b-card>
    </b-collapse>
  </div>
</template>

<script>
import mqtt from "mqtt";
import {v4 as uuidv4} from "uuid";
import {cloneDeep, set, tap} from "lodash";

let defaultLogFileName = "SparkplugTCKResults.log";
let defaultUTCwindow = "60000";
let defaultReportFileName = "Summary-"+defaultLogFileName+".html"
export default {
    props: {
        /**
         * Open or Collapses the MQTT Configuration Menu.
         * @type {Boolean}
         */
        change: {
            type: Boolean,
            required: true,
            default: true,
        },
    },

    data: function () {
        return {
            filename: defaultLogFileName,
            UTCwindow: defaultUTCwindow,
            report: defaultReportFileName,
            /**
             ** The MQTT Client used throughout the whole application.
             ** @type {MqttClient}
             **/
            mqttClient: {
                connected: false,
            },

            /**
             * The default connection settings used for the MQTT connection.
             * @type {Object} connection
             * @type {String} connection.host
             * @type {String} connection.port
             * @type {String} connection.endpoint
             * @type {Boolean} connection.cleanSession
             * @type {Integer} connection.connectionTimeout
             * @type {Integer} connection.reconnectPeriod
             * @type {String} connection.clientId
             * @type {?String} connection.username
             * @type {?String} connection.password
             */
            connection: {
                host: "",
                port: "",
                endpoint: "",
                cleanSession: true,
                connectTimeout: 4000,
                reconnectPeriod: 4000,
                clientId: "",
                username: null,
                password: null,
            },
      successCountDown: 0,
      failureCountDown: 0,
    };
  },

  methods: {
    /**
     * Sets the default values for the MQTT connection.
     */
    defaultConnection(event) {
        event.preventDefault();
        this.connection.host = "localhost";
        this.connection.port = 8000;
        this.connection.endpoint = "/mqtt";
        this.connection.clientId = "tck-web-console-" + uuidv4();
        this.filename = defaultLogFileName;
        this.UTCwindow = defaultUTCwindow;
        this.report = defaultReportFileName;
    },

    /**
     * Creates an MQTT connection to the broker to communicate with the backend.
     * @emits MqttConnect#on-connect
     */
    createConnection(event) {
        event.preventDefault();

        if (this.filename === '') {
            this.filename = defaultLogFileName
        }
        if (this.UTCwindow === '') {
            this.UTCwindow = defaultUTCwindow
        }
        this.report = "Summary-"+this.filename+".html";
        const {host, port, endpoint, ...options} = this.connection;
        console.log(this.connection);

        const connectUrl = `ws://${host}:${port}${endpoint}`;
        try {
            this.mqttClient = mqtt.connect(connectUrl, options);
            this.$emit("on-connect", this.mqttClient, this.filename, this.UTCwindow, this.report);
            console.log(this.mqttClient);
            console.log(this.filename, this.report);
        } catch (error) {
            this.failureCountDown = 5;
            console.log("mqttConnect: error", error);
        }

        this.mqttClient.on("connect", () => {
            this.successCountDown = 5;
            console.log("mqttConnect: Connection succeeded!");
        });
        this.mqttClient.on("error", (error) => {
            this.failureCountDown = 5;
            console.log("mqttConnect: Connection failed", error);
        });

        this.mqttClient.on("message", (topic, message) => {
            if (topic.endsWith("DOWNLOAD")) {
                console.log(`mqttConnect: Got message at topic ${topic} payload size ${message.length}`);
            } else {
                console.log(`mqttConnect: Got message at topic ${topic} and message ${message}`);
            }
        });
    },

    /**
     * Ends an MQTT connection.
     * @emits MqttConnect#on-connect
     */
    destroyConnection() {
      if (this.mqttClient.connected) {
        try {
          this.mqttClient.end();
          this.mqttClient = {
            connected: false,
          };
          this.failureCountDown = 5;
            //this.$emit("on-connect", this.mqttClient);
          console.log("Successfully disconnected!");
        } catch (error) {
          console.log("Disconnect failed", error.toString());
        }
      }
    },
  },
};
</script>
