<template>
  <div>
    <Navbar />
    <b-alert
      :show="successCountDown"
      dismissible
      variant="success"
      @dismissed="successCountDown = 0"
      >Webconsole connected</b-alert
    >
    <b-alert
      :show="failureCountDown"
      dismissible
      variant="danger"
      @dismissed="failureCountDown = 0"
      >Webconsole connected</b-alert
    >
    <b-card title="Configuration" @submit="createConnection">
      <b-form>
        <b-form-group label="Broker Url:" label-for="input-1" description="">
          <b-form-input
            id="input-1"
            v-model="connection.host"
            type="text"
            placeholder="Enter broker url"
            required
            >{{ connection.host }}</b-form-input
          >
        </b-form-group>

        <b-form-group label="Broker Port:" label-for="input-1" description="">
          <b-form-input
            id="input-1"
            v-model="connection.port"
            type="number"
            placeholder="Enter broker port"
            required
            >{{ connection.port }}</b-form-input
          >
        </b-form-group>

        <b-form-group
          label="Broker Endpoint:"
          label-for="input-1"
          description=""
        >
          <b-form-input
            id="input-1"
            v-model="connection.endpoint"
            type="text"
            placeholder="Enter broker endpoint"
          ></b-form-input>
        </b-form-group>

        <b-form-group label="Client Id:" label-for="input-1" description="">
          <b-form-input
            id="input-1"
            v-model="connection.clientId"
            type="text"
            placeholder="Enter client id"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          label="Client Username:"
          label-for="input-1"
          description=""
        >
          <b-form-input
            id="input-1"
            v-model="connection.username"
            type="text"
            placeholder="Enter client username"
          ></b-form-input>
        </b-form-group>

        <b-form-group
          label="Client Password:"
          label-for="input-1"
          description=""
        >
          <b-form-input
            id="input-1"
            v-model="connection.password"
            type="text"
            placeholder="Enter client password"
          ></b-form-input>
        </b-form-group>

        <b-button type="submit" variant="primary" @click="createConnection">
          TestConnection
        </b-button>

        <b-button type="secondary" @click="defaultConnection">
          Default Values
        </b-button>
      </b-form>
    </b-card>
  </div>
</template>


<script>
import mqtt from "mqtt";
import { v4 as uuidv4 } from "uuid";

export default {
  data() {
    return {
      mqttClient: {
        connected: false,
      },
      connection: {
        host: "",
        port: "",
        endpoint: "",
        cleanSession: true,
        connectTimeout: 4000,
        reconnectPeriod: 4000,
        clientId: "",
        username: "",
        password: "",
      },
      successCountDown: 0,
      failureCountDown: 0,
    };
  },

  computed: {
    connection() {
      return this.$store.state.connection.connection;
    },
  },

  methods: {
    defaultConnection(event) {
      event.preventDefault();
      this.connection.host = "broker.hivemq.com";
      this.connection.port = 8000;
      this.connection.endpoint = "/mqtt";
      this.connection.clientId = "tck-web-console-client-" + uuidv4();
    },

    createConnection(event) {
      event.preventDefault();

      const { host, port, endpoint, ...options } = this.connection;
      const connectUrl = `ws://${host}:${port}${endpoint}`;
      try {
        if (this.mqttClient.connected) {
          this.mqttClient.end();
          this.mqttClient = {
            connected: false,
          };
        }
        this.mqttClient = mqtt.connect(connectUrl, options);
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
        this.receiveNews = this.receiveNews.concat(message);
        console.log(`Received message ${message} from topic ${topic}`);
      });
    },

    destroyConnection() {
      if (this.mqttClient.connected) {
        try {
          this.mqttClient.end();
          this.mqttClient = {
            connected: false,
          };
          console.log("Successfully disconnected!");
        } catch (error) {
          console.log("Disconnect failed", error.toString());
        }
      }
    },
  },
};
</script>