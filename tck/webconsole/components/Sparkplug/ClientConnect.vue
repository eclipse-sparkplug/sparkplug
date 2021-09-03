<!-- @author Lukas Brand -->

<template>
  <div>
    <b-collapse v-model="change" id="collapse-2" class="mt-2">
      <b-card title="Sparkplug Client Configuration" border-variant="primary">
        <b-form>
          <b-form-group label="Sparkplug Client Type:" description="Choose the type of Client you want to test.">
            <b-form-radio-group
              id="radio-group-2"
              :disabled="currentTest !== null"
              :checked="local.clientType"
              @change="update('clientType', $event)"
              name="clientType"
            >
              <b-form-radio value="HOSTAPPLICATION">Host Application</b-form-radio>
              <b-form-radio value="EONNODE">Edge of Network Node</b-form-radio>
            </b-form-radio-group>
          </b-form-group>
          <div v-if="local.clientType === 'HOSTAPPLICATION'">
            <SparkplugHostApplication
              :hostApplication="local.hostApplication"
              @on-updated="update('hostApplication', $event)"
            />
          </div>
          <div v-else-if="local.clientType === 'EONNODE'">
            <SparkplugEoNNode :eonNode="local.eonNode" @on-updated="update('eonNode', $event)" />
          </div>
          <div v-else></div>
        </b-form>
      </b-card>
    </b-collapse>
  </div>
</template>

<script>
import { cloneDeep, tap, set } from "lodash";

export default {
  model: {
    prop: "sparkplugClient",
    event: "on-updated",
  },

  props: {
    /**
     * Changes the collapse state of the client connect ui.
     * @type {Boolean}
     */
    change: {
      type: Boolean,
      required: true,
      default: false,
    },

    currentTest: {
      validator: (prop) => typeof prop === "string" || prop === null,
      required: true,
    },

    /**
     * All information about the possible sparkplug client.
     * Values for both eon node and host application.
     * ClientType denotes the current used client.
     * @type {Object} sparkplugClient
     * @type {String} sparkplugClient.clientType
     * @type {Object} sparkplugClient.eonNode
     * @type {Boolean} sparkplugClient.eonNode.complete
     * @type {String} sparkplugClient.eonNode.groupId
     * @type {String} sparkplugClient.eonNode.edgeNodeId
     * @type {Object} sparkplugClient.hostApplication
     * @type {Boolean} sparkplugClient.hostApplication.complete
     * @type {String} sparkplugClient.hostApplication.hostId
     */
    sparkplugClient: {
      clientType: {
        type: String,
        required: true,
        default: "HOSTAPPLICATION",
      },
      eonNode: {
        complete: {
          type: Boolean,
          required: true,
          default: false,
        },
        groupId: {
          type: String,
          required: true,
          default: "",
        },
        edgeNodeId: {
          type: String,
          required: true,
          default: "",
        },
      },
      hostApplication: {
        complete: {
          type: Boolean,
          required: true,
          default: false,
        },
        hostId: {
          type: String,
          required: true,
          default: "",
        },
      },
    },
  },

  computed: {
    /**
     * Passthrough variable to ensure information even if property is not existent.
     * @type {Object}
     * @return {String} Sparkplug client
     */
    local: function () {
      return this.sparkplugClient
        ? this.sparkplugClient
        : {
            complete: false,
            clientType: "HOSTAPPLICATION",
            hostApplication: {
              hostId: "",
            },
            eonNode: {
              groupId: "",
              edgeNodeId: "",
            },
          };
    },
  },

  methods: {
    /**
     * Generic method to update a certain value of an existing object.
     * @param {String} key - Key which points to part of the object which should be replaced.
     * @param {Any} value - New value which replaces the old key based value.
     * @emits SparkplugClientConnect#on-updated
     */
    update(key, value) {
      const emitValue = tap(cloneDeep(this.local), (v) => set(v, key, value));
      this.$emit("on-updated", emitValue);
    },
  },
};
</script>