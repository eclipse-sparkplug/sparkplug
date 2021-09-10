<!-- @author Lukas Brand -->

<template>
  <div>
    <h3>Logging</h3>
    <div>
      <ul class="list-group" v-for="logMessage in logging" :key="logMessage.id">
        <li class="list-group-item">{{ logMessage.logValue }}</li>
      </ul>
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
};
</script>
