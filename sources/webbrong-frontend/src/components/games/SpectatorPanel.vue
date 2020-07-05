<template>
  <div>
    <h3>
      Spectators:
    </h3>
    <div v-for="(entry) in spectators" v-bind:key=entry>
      <span> Username: {{ entry }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SpectatorPanel',
  data() {
    return {
    };
  },
  props: {
    spectators: Array,
  },
};
</script>

<style scoped>

</style>
