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
    <client-only>
    <b-form>
        <b-container fluid="xs">
            <b-row  >
                <b-col sm="3">
                    <label size="sm" description="The identifiers of the Edge Node">Group ID/Edge Node ID:</label>
                </b-col>
                <b-col sm="9">
                    <b-input-group class="mb-2">
                        <b-form-input
                            :value="local.groupId"
                            @input="update('groupId', $event)"
                            placeholder="group_id"
                            required
                        />
                        <b-input-group-text >/</b-input-group-text>
                        <b-form-input
                            :value="local.edgeNodeId"
                            @input="update('edgeNodeId', $event)"
                            placeholder="edge_node_id"
                            required
                        />
                    </b-input-group>
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
            complete: false,
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

      if (emitValue.groupId.length !== 0 && emitValue.edgeNodeId.length !== 0) {
        this.$emit("on-updated", set(emitValue, "complete", true));
      } else {
        this.$emit("on-updated", set(emitValue, "complete", false));
      }
    },
  },
};
</script>
