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
