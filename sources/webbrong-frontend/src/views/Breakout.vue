<template>
  <div class="breakout">
    <Game id="game" :image="image" :headerText="headerText" :game="game"/>
  </div>
</template>

<script>
import GamesConfig from '../gamesConfig';
import Game from '../components/games/GameWrapper.vue';

export default {
  name: 'Breakout',
  components: {
    Game,
  },
  data() {
    return {
      image: 'breakoutIcon.png',
      headerText: 'Breakout',
      game: GamesConfig.BREAKOUT,
    };
  },
};
</script>

<style scoped>
  #game {
    grid-column: 3/11;
  }
  @media (max-width: 800px) {
    #game {
      grid-column: 2/6;
    }
  }
</style>
