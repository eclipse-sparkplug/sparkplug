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
      <h3 >Logging:</h3>
      <div border-variant="primary" bg-variant="light">
           <ol v-for="logMessage in logging" :key="logMessage.id" class="list-group">

               <li v-if="logMessage.logValue.indexOf('OVERALL: PASS')>=0"
                   class="list-group-item border-bottom-0 small border-success">
                   <b>{{ logMessage.id }}: </b> {{ beauty(logMessage.logValue) }}
               </li>
               <li v-else-if="logMessage.logValue.indexOf('OVERALL: FAIL')>=0"
                   class="list-group-item border-bottom-0 small border-danger">
                   <b>{{ logMessage.id }}: </b>{{ logMessage.logValue }}
               </li>
               <li v-else
                   class="list-group-item border-bottom-0 small border-dark">
                   <b>{{ logMessage.id }}: </b>{{ logMessage.logValue }}
               </li>
           </ol>
      </div>
  </div>
</template>

<script>
export default {
    props: {
        /**
         * All incoming log messages.
         * @type{String[]}
         */
        logging: {
            type: Array,
            required: true,
            default: () => [],
        },
    },

    methods: {
        /**
         * @param {String} value
         */
        beauty: function (value) {
            return value.replaceAll(';', ';  ');
        },
    },
};
</script>
