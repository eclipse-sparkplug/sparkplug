<!-- @author Lukas Brand -->

<template>
  <b-card title="Run all tests" border-variant="primary" class="my-1">
    <div class="mt-3">
      <b-button-toolbar>
        <b-button-group class="mr-5">
          <b-button variant="success" @click="$emit('start-all-tests')">Start all Tests</b-button>
          <b-button variant="danger" @click="$emit('abort-all-tests')">Abort all Tests</b-button>
        </b-button-group>
        <b-button variant="info" class="mr-5" @click="$emit('reset-all-tests')"> Reset all Tests </b-button>
        <b-button variant="dark" @click="$emit('show-all-tests')"> Show all Tests </b-button>
        <b-progress :value="progressBarValue" :max="progressBarMax" show-progress animated class="w-100 mt-3" />
      </b-button-toolbar>
    </div>
  </b-card>
</template>

<script>
export default {
  data: function () {
    return {
      /**
       * Current value of all tests progress bar.
       */
      progressBarValue: 0,

      /**
       * Max value of all tests progress bar.
       */
      progressBarMax: 100,
    };
  },
};
</script>
