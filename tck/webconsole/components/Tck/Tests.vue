<!-- @author Lukas Brand -->

<template>
  <div>
    <h3>Tests</h3>
    <div>
      <TckAllTests
        @start-all-tests="$emit('start-all-tests=', hostTests, eonTests)"
        @abort-all-tests="$emit('abort-all-tests=', hostTests, eonTests)"
        @reset-all-tests="$emit('reset-all-tests=', hostTests, eonTests)"
        @showAllTests="toggleSidebar"
      />
      <TckTestsInformation :testNames="getTestNames" v-model="sidebar" />
      <h3>Individual Tests</h3>
      <div>
        <div v-if="clientType === 'HOSTAPPLICATION'">
          <TckTest
            v-for="test in hostTests"
            :key="test.testValues.name"
            :id="test.testValues.name"
            :ref="test.testValues.name"
            @start-single-test="(testParameter) => $emit('start-single-test', testParameter)"
            @abort-single-test="(testParameter) => $emit('abort-single-test', testParameter)"
            @reset-single-test="(testParameter) => $emit('reset-single-test', testParameter)"
            v-model="test.testValues"
          />
        </div>
        <div v-else-if="clientType === 'EONNODE'">
          <TckTest
            v-for="test in eonTests"
            :key="test.testValues.name"
            @start-single-test="(testParameter) => $emit('start-single-test', testParameter)"
            @abort-single-test="(testParameter) => $emit('abort-single-test', testParameter)"
            @reset-single-test="(testParameter) => $emit('reset-single-test', testParameter)"
            v-model="test.testValues"
          />
        </div>
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
    clientType: {
      type: String,
      required: true,
      default: "HOSTAPPLICATION",
    },
  },

  computed: {
    /**
     * Calculates a readable name out of the client type property.
     * @return {String} Readable name
     */
    clientTypeReadableName: function () {
      if (this.clientType === "HOSTAPPLICATION") {
        return "Host Application Tests";
      } else if (this.clientType === "EONNODE") {
        return "EoN Node Tests";
      }
    },

    /**
     * Creates a list of all test names depending on the client type.
     * @return {Object[]} List of objects with test name & readable name
     */
    getTestNames: function () {
      if (this.clientType === "HOSTAPPLICATION") {
        const testNames = [];
        for (const [_, testValue] of Object.entries(this.hostTests)) {
          const message = {
            name: testValue.testValues.name,
            readableName: testValue.testValues.readableName,
          };
          testNames.push(message);
        }
        return testNames;
      } else if (this.clientType === "EONNODE") {
        const testNames = [];
        for (const [_, testValue] of Object.entries(this.eonTests)) {
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
            name: "SessionEstablishmentTest",
            readableName: "Session Establishment Test",
            description: "This is the Host Application Sparkplug session establishment, and re-establishment test.",
            requirements: ["The Host Application under test must be connected and online prior to starting this test."],
            code: "This is code you can copy \n Multiline text \n Test Senario",
            logging: [],
          },
        },
        sendCommandTest: {
          testValues: {
            name: "SendCommandTest",
            readableName: "Send Command Test",
            description:
              "To check that a command from a Host Application under test is correct to both an edge node (NCMD) and a device (DCMD).",
            requirements: ["The Host Application under test must be connected and online prior to starting this test."],
            parameters: {
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
            result: false,
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
      eonTests: {},
    };
  },

  methods: {
    /**
     * Inverts sidebar state.
     */
    toggleSidebar: function () {
      this.sidebar = !this.sidebar;
    },
  },
};
</script>