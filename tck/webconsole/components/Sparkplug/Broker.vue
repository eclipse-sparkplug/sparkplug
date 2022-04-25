<!-- @author Lukas Brand -->

<template>
    <div>
        <client-only>
            <b-form>
                <b-container fluid="xs">
                    <b-row>
                        <b-col sm="3">
                            <label size="sm" description="The identifiers of the broker">Broker address:</label>
                        </b-col>
                        <b-col sm="9">
                            <b-input-group class="mb-2">
                                <b-form-input
                                    :value="local.host"
                                    @input="update('host', $event)"
                                    placeholder="host"
                                    required
                                />
                                <b-input-group-text>:</b-input-group-text>
                                <b-form-input
                                    :value="local.port"
                                    @input="update('port', $event)"
                                    placeholder="port"
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
import {cloneDeep, tap, set} from "lodash";

export default {
    props: {
        /**
         *
         */
        broker: {
            host: {
                type: String,
                required: true,
                default: "localhost",
            },
            port: {
                type: Number,
                required: true,
                default: 1883,
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
            return this.broker
                ? this.broker
                : {
                    complete: false,
                    host: "localhost",
                    port: 1883
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

            if (emitValue.host.length !== 0 && emitValue.port > 0) {
                this.$emit("on-updated", set(emitValue, "complete", true));
            } else {
                this.$emit("on-updated", set(emitValue, "complete", false));
            }
        },
    },
};
</script>
