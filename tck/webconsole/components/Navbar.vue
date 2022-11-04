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
  <b-navbar :sticky="true" fixed="top" toggleable="lg" type="light" variant="light">
    <b-navbar-brand to="/">
      <SparkplugLogo width="27.04" height="40" />
      <a href="https://github.com/eclipse/sparkplug">Eclipse&trade; Sparkplug&trade;</a> TCK Console
    </b-navbar-brand>

    <div>
      <b-button v-if="pendingNotifications.length > 0" variant="danger" @click="$emit('show-popup')">
        <b-icon-exclamation-diamond />
        Pending actions: {{ pendingNotifications.length }}
      </b-button>
    </div>

    <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
    <b-collapse id="nav-collapse" is-nav>
      <div>
        <span class="mr-auto">MQTT Client Connected:</span>
        <b-icon v-if="mqttConnected" icon="check-circle-fill" variant="success" class="mr-3" />
        <b-icon v-else icon="x-circle-fill" variant="danger" class="mr-3" />
      </div>
    </b-collapse>
  </b-navbar>
</template>

<script>
export default {
  props: {
    /**
     * Shows if the MQTT client is connected or not.
     * @type {Boolean}
     */
    mqttConnected: {
      type: Boolean,
      required: true,
      default: false,
    },

    /**
     * List of notifications which were not processed by the user.
     * @type {String[]}
     */
    pendingNotifications: {
      type: Array,
      required: true,
      default: () => [],
    },
  },
};
</script>