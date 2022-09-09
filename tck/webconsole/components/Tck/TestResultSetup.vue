<template>
    <div>
        <b-card border-variant="primary">
            <b-form>
                <b-container fluid>
                    <b-row class="mt-3">
                        <b-col sm="4"><label size="sm">Result logfile:</label></b-col>
                        <b-col sm="8">
                            <b-form-input size="sm"
                                          :disabled="true"
                                          :placeholder="$attrs.filename"></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row class="mt-3">
                        <b-col sm="4"><label size="sm">Empty log console (not the File):</label></b-col>
                        <b-col sm="6">
                            <b-button size="sm" variant="danger" @click="cleanLogging"> Clear Console</b-button>
                        </b-col>
                    </b-row>
                </b-container>
            </b-form>
        </b-card>
    </div>
</template>


<script>

export default {
    methods: {
        cleanLogging(event) {
            this.$emit("reset-log");
        },
    },
};
</script>
