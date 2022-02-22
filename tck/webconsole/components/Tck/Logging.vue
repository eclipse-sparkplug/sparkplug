<!-- @author Lukas Brand -->

<template>
  <div>
      <h3 >Logging:</h3>
      <div border-variant="primary" bg-variant="light">
          <ul v-for="logMessage in logging" :key="logMessage.id" class="list-group">
              <li class="list-group-item border-bottom-0 small">{{ logMessage.logValue }}</li>
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
