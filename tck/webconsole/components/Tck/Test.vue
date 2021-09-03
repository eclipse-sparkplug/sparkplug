<!-- @author Lukas Brand -->

<template>
  <b-card :title="test.readableName" border-variant="primary" class="my-1">
    <div class="mt-3">
      <h5>Description:</h5>
      <b-card-text>
        {{ test.description }}
      </b-card-text>
    </div>

    <div v-if="test.requirements" class="mt-3">
      <h5>Requirements:</h5>
      <ul v-for="requirement in test.requirements" :key="requirement">
        <li>
          {{ requirement }}
        </li>
      </ul>
    </div>

    <div v-if="test.parameters" class="mt-3">
      <h5>Parameters:</h5>
      <div v-for="(parameter, parameterName) in test.parameters" :key="parameterName">
        <b-form-group
          :id="parameter.parameterName + '-parameter-group'"
          :description="parameter.parameterDescription"
          :label="parameter.parameterReadableName + ':'"
          :label-for="parameterName + '-parameter-input'"
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

    <div v-if="test.code" class="mt-3">
      <h5>Example Code:</h5>
      <b-input-group>
        <b-textarea readonly no-resize rows="5" style="white-space: pre" v-model="test.code" />
        <b-input-group-append>
          <b-button
            id="clipboard-button"
            variant="outline-success"
            @click="copied = true"
            @mouseout="copied = false"
            v-clipboard:copy="test.code"
            v-clipboard:success="(value) => onClipboardCopy(value)"
            v-clipboard:error="(value) => onClipboardError(value)"
          >
            <b-icon icon="clipboard" />
          </b-button>
        </b-input-group-append>
      </b-input-group>
    </div>

    <div class="mt-3">
      <b-button-toolbar>
        <b-button-group class="mr-5">
          <b-button variant="success" @click="$emit('start-single-test', test)">Start Test</b-button>
          <b-button variant="danger" @click="$emit('abort-single-test', test)">Abort Test</b-button>
        </b-button-group>
        <b-button class="mr-5" variant="info" @click="$emit('reset-single-test', test)">Reset Test</b-button>

        <span class="my-auto mr-1">Result: </span>
        <b-icon class="my-auto h3" v-if="test.result === null" icon="circle-fill" variant="secondary" />
        <b-icon class="my-auto h3" v-else-if="test.result === true" icon="check-circle-fill" variant="success" />
        <b-icon class="my-auto h3" v-else-if="test.result === false" icon="x-circle-fill" variant="danger" />
      </b-button-toolbar>
    </div>

    <div class="mt-3" v-if="test.logging.length > 0">
      <h5>Logging:</h5>
      <div>
        <ul class="list-group" v-for="logMessage in test.logging" :key="logMessage.id">
          <li class="list-group-item">{{ logMessage.logValue }}</li>
        </ul>
      </div>
    </div>
  </b-card>
</template>

<script>
import { cloneDeep, tap, set } from "lodash";

export default {
  model: {
    prop: "test",
    event: "on-updated",
  },

  props: {
    test: {
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
        default: () => {},
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
  },
};
</script>
