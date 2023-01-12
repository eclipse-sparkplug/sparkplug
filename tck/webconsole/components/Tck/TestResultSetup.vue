<!--****************************************************************************
 * Copyright (c) 2021, 2022 Anja Helmbrecht-Schaar, Ian Craggs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Anja Helmbrecht-Schaar - initial implementation and documentation
 *    Ian Craggs - updates for usability and features
 ****************************************************************************-->

<template>
    <div>
        <b-card border-variant="primary">
            <b-form>
                <b-container fluid>
                    <b-row class="mt-3">
                        <b-col sm="2"><label size="sm">Create Report Summary from TCK tests</label></b-col>
                        <b-col sm="4">
                            <b-form-input size="sm"
                                          :disabled="true"
                                          :placeholder="$attrs.filename"></b-form-input>
                            <b-badge v-if="$attrs.reportCreated.length > 0">Summary created</b-badge>
                            <!--
                            <b-form-input size="sm"
                                          :disabled="true"
                                          :placeholder="$attrs.reportCreated"></b-form-input>
                                          -->
                        </b-col>
                        <b-col sm="6">
                            <b-button size="sm" variant="success" @click="createReport">Create</b-button>

                            <b-button v-if="$attrs.reportCreated.length > 0"
                                      size="sm"
                                      variant="info" @click="downloadReport">Download</b-button>
                        </b-col>
                    </b-row>
                </b-container>
            </b-form>
        </b-card>
        <br>
        <b-card border-variant="primary">
            <b-form>
                <b-container fluid>
                    <b-row class="mt-3">
                        <b-col sm="2"><label size="sm">Empty log console:</label></b-col>
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
    props: {
    },
    methods: {
        cleanLogging(event) {
            this.$emit("reset-log");
        },
        createReport(event) {
            this.$emit("create-report");
        },
        downloadReport(event) {
            this.$emit("download-report");
        },

    },
};
</script>
