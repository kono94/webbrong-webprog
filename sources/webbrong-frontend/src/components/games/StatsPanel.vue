<template>
  <div>
    <h3>
      Players:
    </h3>
    <div v-for="(key, index) in Object.keys(players)" v-bind:key=index>
      <span  v-bind:style="{ color: players[key].paddle.color}" > Username: {{ key }} - P: {{ players[key].points }} L: {{players[key].lifePoints}}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatsPanel',
  data() {
    return {
    };
  },
  props: {
    players: Object,
  },
};
</script>

<style scoped>

</style>
