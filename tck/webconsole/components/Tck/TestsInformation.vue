<!-- @author Lukas Brand -->

<template>
  <b-sidebar
    id="Sidebar"
    :visible="showSidebar"
    @hidden="$emit('close-sidebar', false)"
    title="All Tests"
    shadow
    right
    no-close-on-route-change
    no-enforce-focus
  >
    <b-nav pills vertical v-b-scrollspy>
      <b-nav-item v-for="testName in testNames" :key="testName.name" :to="{ hash: '#' + testName.name }">
        {{ testName.readableName }}
      </b-nav-item>
    </b-nav>
  </b-sidebar>
</template>

<script>
export default {
  model: {
    prop: "showSidebar",
    event: "close-sidebar",
  },

  props: {
    /**
     * The names of tests which are shown and linked inside the sidebar.
     * @type {String[]}
     */
    testNames: {
      type: Array,
      required: true,
      default: () => [],
    },

    /**
     * Variable to show the sidebar if true.
     * @type {Boolean}
     */
    showSidebar: {
      type: Boolean,
      required: true,
      default: false,
    },
  },
};
</script>