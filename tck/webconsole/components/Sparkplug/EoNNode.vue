<!-- @author Lukas Brand -->

<template>
  <div>
    <!--<client-only>-->
    <b-form>
      <b-form-group label="Edge of Network Node ID:" description="The identifiers of the Edge of Network node">
        <b-input-group class="mb-2">
          <b-form-input
            :value="local.groupId"
            @input="update('groupId', $event)"
            type="text"
            placeholder="group_id"
            required
          />
          <b-input-group-text>/</b-input-group-text>
          <b-form-input
            :value="local.edgeNodeId"
            @input="update('edgeNodeId', $event)"
            type="text"
            placeholder="edge_node_id"
            required
          />
        </b-input-group>
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
     * All necessary information to determine an eon node.
     * @type {Object} sparkplugClient.eonNode
     * @type {String} sparkplugClient.eonNode.groupId
     * @type {String} sparkplugClient.eonNode.edgeNodeId
     */
    eonNode: {
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
  },

  computed: {
    /**
     * Passthrough variable to ensure information even if property is not existent.
     * @type {Object}
     * @return {String} Eon node
     */
    local: function () {
      return this.eonNode
        ? this.eonNode
        : {
            groupId: "",
            edgeNodeId: "",
          };
    },
  },

  methods: {
    /**
     * Generic method to update a certain value of an existing object.
     * @param {String} key - Key which points to part of the object which should be replaced.
     * @param {Any} value - New value which replaces the old key based value.
     * @emits SparkplugEonNode#on-updated
     */
    update(key, value) {
      const emitValue = tap(cloneDeep(this.local), (v) => set(v, key, value));
      this.$emit("on-updated", emitValue);
    },
  },
};
</script>