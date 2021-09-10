<!-- @author Lukas Brand -->

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
    <b-collapse v-model="change" id="collapse-1" class="mt-2">
      <b-card title="Mqtt Server Configuration" @submit="createConnection">
        <b-form>
          <b-form-group label="Broker Url:" label-for="input-1" description="">
            <b-form-input v-model="connection.host" type="text" placeholder="Enter broker url" required></b-form-input>
          </b-form-group>

          <b-form-group label="Broker Port:" label-for="input-1" description="">
            <b-form-input
              v-model="connection.port"
              type="number"
              placeholder="Enter broker port"
              required
            ></b-form-input>
          </b-form-group>

          <b-form-group label="Broker Endpoint:" label-for="input-1" description="">
            <b-form-input v-model="connection.endpoint" type="text" placeholder="Enter broker endpoint"></b-form-input>
          </b-form-group>

          <b-form-group label="Client Id:" label-for="input-1" description="">
            <b-form-input v-model="connection.clientId" type="text" placeholder="Enter client id"></b-form-input>
          </b-form-group>

          <b-form-group label="Client Username:" label-for="input-1" description="">
            <b-form-input v-model="connection.username" type="text" placeholder="Enter client username"></b-form-input>
          </b-form-group>

          <b-form-group label="Client Password:" label-for="input-1" description="">
            <b-form-input v-model="connection.password" type="text" placeholder="Enter client password"></b-form-input>
          </b-form-group>

          <b-button type="submit" variant="primary" :disabled="mqttClient.connected" @click="createConnection">
            {{ mqttClient.connected ? "Connected" : "Connect" }}
          </b-button>

          <b-button v-if="mqttClient.connected" variant="danger" class="conn-btn" @click="destroyConnection">
            Disconnect
          </b-button>

          <b-button type="secondary" @click="defaultConnection"> Default Values </b-button>
        </b-form>
      </b-card>
    </b-collapse>
  </div>
</template>

<script>
import mqtt from "mqtt";
import { v4 as uuidv4 } from "uuid";

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
      /**
       * The MQTT Client used througout the whole application.
       * @type {MqttClient}
       */
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
    },

    /**
     * Creates an MQTT connection to the broker to communicate with the backend.
     * @emits MqttConnect#on-connect
     */
    createConnection(event) {
      event.preventDefault();

      const { host, port, endpoint, ...options } = this.connection;
      const connectUrl = `ws://${host}:${port}${endpoint}`;
      try {
        this.mqttClient = mqtt.connect(connectUrl, options);
        this.$emit("on-connect", this.mqttClient);
        console.log(this.mqttClient);
      } catch (error) {
        this.failureCountDown = 5;
        console.log("mqtt.connect error", error);
      }

      this.mqttClient.on("connect", () => {
        this.successCountDown = 5;
        console.log("Connection succeeded!");
      });
      this.mqttClient.on("error", (error) => {
        this.failureCountDown = 5;
        console.log("Connection failed", error);
      });
      this.mqttClient.on("message", (topic, message) => {
        console.log(`Received message ${message} from topic ${topic}`);
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
          this.$emit("on-connect", this.mqttClient);
          console.log("Successfully disconnected!");
        } catch (error) {
          console.log("Disconnect failed", error.toString());
        }
      }
    },
  },
};
</script>