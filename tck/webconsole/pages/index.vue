<!-- @author Lukas Brand -->

<template>
  <div>
    <Navbar
      :mqttConnected="mqttClient.connected"
      :pendingNotifications="popupNotifications"
      @show-popup="showPopup = true"
    />
    <div class="m-3">
      <WebConsoleInformation />
      <div>
        <b-tabs content-class="mt-1" v-model="activeTab" pills justified>
          <b-tab
            @click="changeMqttConnectFun(0)"
            :title-link-class="mqttClient.connected ? 'bg-success text-white' : 'bg-danger text-white'"
          >
            <template #title>
              <span class="mr-2">MQTT Configuration</span>
              <span>
                {{ mqttClient.connected ? "(connected)" : "(disconnected)" }}
              </span>
            </template>
            <MqttConnect :change="tabOpen" @on-connect="mqttConnected" class="mt-3" />
          </b-tab>
          <b-tab
            @click="changeMqttConnectFun(1)"
            :title-link-class="
              (sparkplugClient.hostApplication.complete && sparkplugClient.clientType === 'HOSTAPPLICATION') ||
              (sparkplugClient.eonNode.complete && sparkplugClient.clientType === 'EONNODE')
                ? 'bg-success text-white'
                : 'bg-danger text-white'
            "
          >
            <template #title> Sparkplug Client Configuration </template>
            <SparkplugClientConnect :change="tabOpen" v-model="sparkplugClient" class="mt-3" />
          </b-tab>
        </b-tabs>
      </div>

      <TckTests
        @start-all-tests="(hostTests, eonTests) => startAllTestsFun(hostTests, eonTests)"
        @abort-all-tests="(hostTests, eonTests) => abortAllTestsFun(hostTests, eonTests)"
        @reset-all-tests="(hostTests, eonTests) => resetAllTestsFun(hostTests, eonTests)"
        @start-single-test="(testParameters) => startTestFun(testParameters)"
        @abort-single-test="(testParameters) => abortTestFun(testParameters)"
        @reset-single-test="(testParameters) => resetTestFun(testParameters)"
        @reset-current-test="resetCurrentTest()"
        :clientType="this.sparkplugClient.clientType"
        :currentTest="this.currentTest"
        :currentTestLogging="this.currentTestLogging"
        class="mt-3"
      />

      <TckLogging :logging="this.logging" class="mt-3" />
    </div>

    <WebConsolePopup
      v-model="popupNotifications"
      @handle-notification="publishMessage"
      :showPopup="showPopup"
      @popup-shown="showPopup = $event"
    />

    <b-button variant="info" style="position: fixed; bottom: 40px; right: 40px; z-index: 9" @click="backToTop">
      <b-icon icon="arrow-up-circle-fill"></b-icon>
    </b-button>
  </div>
</template>



<script>
let loggingCreated = false;
let interactionListenerCreated = false;

export default {
  data: function () {
    return {
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

      /**
       * All information about the possible sparkplug client.
       * Complete value informes about all necessary values are being set.
       * Values for both eon node and host application.
       * ClientType denotes the current used client.
       * @type {Object} sparkplugClient
       * @type {Boolean}sparkplugClient.complete
       * @type {String} sparkplugClient.clientType
       * @type {Object} sparkplugClient.eonNode
       * @type {String} sparkplugClient.eonNode.groupId
       * @type {String} sparkplugClient.eonNode.edgeNodeId
       * @type {Object} sparkplugClient.hostApplication
       * @type {String} sparkplugClient.hostApplication.hostId
       */
      sparkplugClient: {
        complete: false,
        clientType: "HOSTAPPLICATION",
        hostApplication: {
          hostId: "",
        },
        eonNode: {
          groupId: "",
          edgeNodeId: "",
        },
      },

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
    startAllTestsFun: function (hostTests, eonTests) {
      if (this.mqttClient.connected === false) {
        alert("You need to connect to the broker first.");
        return;
      }
      if (this.sparkplugClient.clientType === "HOSTAPPLICATION") {
        for (const hostTest of Object.values(hostTests)) {
          this.startTestFun(hostTest);
        }
      } else if (this.sparkplugClient.clientType === "EONNODE") {
        for (const eonTest of Object.values(eonTests)) {
          this.startTestFun(eonTest);
        }
      } else {
        alert("Client type does not exist");
      }
    },

    /**
     * Aborts all tests.
     */
    abortAllTestsFun: function (hostTests, eonTests) {
      if (this.mqttClient.connected === false) {
        alert("You need to connect to the broker first.");
        return;
      }
      if (this.sparkplugClient.clientType === "HOSTAPPLICATION") {
        for (const hostTest of Object.values(hostTests)) {
          this.abortTestFun(hostTest);
        }
      } else if (this.sparkplugClient.clientType === "EONNODE") {
        for (const eonTest of Object.values(eonTests)) {
          this.abortTestFun(eonTest);
        }
      } else {
        alert("Client type does not exist");
      }
    },

    /**
     * Resets all tests.
     */
    resetAllTestsFun: function (hostTests, eonTests) {},

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

      if (this.sparkplugClient.clientType === "HOSTAPPLICATION") {
        const profile = "host";
        const testType = testParameter.name;
        if (testType === "SessionEstablishmentTest") {
          const testParameters = this.sparkplugClient.hostApplication.hostId;
          this.createTestRequest(profile, testType, testParameters);
        } else if (testType === "SendCommandTest") {
          const testParameters =
            this.sparkplugClient.hostApplication.hostId +
            " " +
            testParameter.parameters["edge_node_id"].parameterValue +
            " " +
            testParameter.parameters["device_id"].parameterValue;
          this.createTestRequest(profile, testType, testParameters);
        } else {
          alert("Test does not exist");
        }
      } else if (this.sparkplugClient.clientType === "EONNODE") {
      } else {
        alert("Client type does not exist");
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
     * Create logging for the tests.
     */
    createLogback: function () {
      if (loggingCreated) {
        return;
      }
      loggingCreated = true;
      console.log("index: createLogback");

      const resultTopic = "SPARKPLUG_TCK/RESULT";

      this.mqttClient.subscribe(resultTopic, (error) => {
        if (error) {
          console.log("Subscribe error", error);
        }
      });

      this.mqttClient.on("message", (topic, message) => {
        if (topic === resultTopic) {
          const logMessage = {
            id: this.logging.length,
            logLevel: "INFO",
            logValue: message.toString(),
          };
          console.log("logging:", logMessage);
          this.logging.push(logMessage);

          this.currentTestLogging = logMessage;
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

    /**********************
     * MQTT functions
     **********************/

    /**
     * Handles a successful MQTT connection.
     */
    mqttConnected: function (mqttClient) {
      this.activeTab = 1;
      this.mqttClient = mqttClient;
      this.createInteractionListener();
      this.createLogback();
    },

    /**
     * Create an MQTT publish to start/request a test.
     */
    createTestRequest: function (profile, testType, testParameters) {
      let topic = "SPARKPLUG_TCK/TEST_CONTROL";
      let payload = "NEW_TEST " + profile + " " + testType + " " + testParameters;
      console.log("index createTest:", payload);
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
