<!-- @author Lukas Brand -->

<template>
  <div>
    <client-only>

    <b-form>
        <b-container fluid="xs">
            <b-row  >
                <b-col sm="3">
                    <label description="The identifiers of the Host Application">Host Application ID:</label>
                </b-col>
                <b-col sm="9">
                    <b-form-input
                        :value="local.hostId"
                        @input="update('hostId', $event)"
                        type="text"
                        placeholder="scada_host_id"
                        required
                    />
                </b-col>
            </b-row>
        </b-container>
    </b-form>
    </client-only>
  </div>
</template>

<script>
import { cloneDeep, tap, set } from "lodash";

export default {
  props: {
    /**
     * All necessary information to determine a host application.
     * @type {Object} hostApplication
     * @type {String} hostApplication.hostId
     */
    hostApplication: {
      hostId: {
        type: String,
        required: true,
        default: "",
      },
    },
  },

  computed: {
    /**
     * Passthrough variable to ensure information even if property is not existent.
     * @type {Object}
     * @return {String} Host application
     */
    local: function () {
      return this.hostApplication
        ? this.hostApplication
        : {
            complete: false,
            hostId: "",
          };
    },
  },

  methods: {
    /**
     * Generic method to update a certain value of an existing object.
     * @param {String} key - Key which points to part of the object which should be replaced.
     * @param {Any} value - New value which replaces the old key based value.
     * @emits SparkplugHostApplication#on-updated
     */
    update(key, value) {
      const emitValue = tap(cloneDeep(this.local), (v) => set(v, key, value));

      if (emitValue.hostId.length !== 0) {
        this.$emit("on-updated", set(emitValue, "complete", true));
      } else {
        this.$emit("on-updated", set(emitValue, "complete", false));
      }
    },
  },
};
</script>
