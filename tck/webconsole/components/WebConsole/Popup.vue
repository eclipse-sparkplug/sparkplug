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
  <b-modal
    scrollable
    ok-title="OK"
    ok-variant="success"
    cancel-title="Fail"
    cancel-variant="danger"
    id="notificationPopup"
    ref="notificationPopup"
    title="ACTION REQUIRED"
    @ok="confirmNotification()"
    @cancel="declineNotification()"
  >
    {{ newestMessage }}
  </b-modal>
</template>

<script>
import { cloneDeep, tap } from "lodash";

export default {
  model: {
    prop: "notifications",
    event: "remove-newest-notification",
  },

  props: {
    /**
     * All notifications which are sent from the backend to the console.
     * @type {String[]}
     */
    notifications: {
      type: Array,
      required: true,
      default: () => [],
    },

    /**
     * Variable to show the popup if true.
     * @type {Boolean}
     */
    showPopup: {
      type: Boolean,
      required: true,
      default: false,
    },
  },

  computed: {
    /**
     * Helper function to get the first entry of the
     * notifications array which is shown on the popup.
     * @return {String} First notification
     */
    newestMessage: function () {
      return this.notifications.length === 0 ? "" : this.notifications[0];
    },
  },

  watch: {
    /**
     * Opens the popup if a notification was added.
     * Prevents opening if no notification is present.
     * @param {String[]} newValue - New notifications array
     * @param {String[]} oldValue - Old notifications array
     */
    notifications: function (newValue, oldValue) {
      if (newValue.length === 0) return;
      this.$refs["notificationPopup"].show();
    },

    /**
     * Opens the popup if the showPopup boolean was set to true.
     * Prevents opening if changed to false.
     * @param {boolean} newValue - New showPopup value
     * @param {boolean} oldValue - Old showPopup value
     * @emits Popup#popup-shown
     */
    showPopup: function (newValue, oldValue) {
      if (newValue === false) return;
      this.$refs["notificationPopup"].show();
      this.$emit("popup-shown", false);
    },
  },

  methods: {
    /**
     * Confirms the successful handle of a notification.
     * Removes the notification from the list.
     * Sends PASS to the backend via MQTT.
     * @emits Popup#handle-notification
     */
    confirmNotification: function () {
      this.removeNotificationFromNotifications();

      const topic = "SPARKPLUG_TCK/CONSOLE_REPLY";
      const message = "PASS";
      this.$emit("handle-notification", topic, message);
    },

    /**
     * Confirms the unsuccessful handle of a notification.
     * Removes the notification from the list.
     * Sends FAIL to the backend via MQTT.
     * @emits Popup#handle-notification
     */
    declineNotification: function () {
      this.removeNotificationFromNotifications();

      const topic = "SPARKPLUG_TCK/CONSOLE_REPLY";
      const message = "FAIL";
      this.$emit("handle-notification", topic, message);
    },

    /**
     * Creates a copy of the notifications array, removes the first element and emits it to the parent.
     * @emits Popup#remove-newest-notification
     */
    removeNotificationFromNotifications: function () {
      const emitValue = tap(cloneDeep(this.notifications), (notifications) => notifications.shift());
      this.$emit("remove-newest-notification", emitValue);
    },
  },
};
</script>
