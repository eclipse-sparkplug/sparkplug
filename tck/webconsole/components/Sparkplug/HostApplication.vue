<!-- @author Lukas Brand -->

<template>
  <div>
    <!--<client-only>-->
    <b-form>
      <b-form-group label="Host Application Host ID:" description="The identifiers of the Host Application">
        <b-form-input
          :value="local.hostId"
          @input="update('hostId', $event)"
          type="text"
          placeholder="scada_host_id"
          required
        />
      </b-form-group>
    </b-form>
    <!--</client-only>-->
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
      this.$emit("on-updated", emitValue);
    },
  },
};
</script>