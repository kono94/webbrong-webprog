<template>
  <div class="game-wrapper">
    <PreGameLobby  id="lobby" :image="image" :headerText="headerText" :gameConfig="game" :errorMessage="errorMessage" v-if="!connected"/>
    <GameContainer id="game"  @roomEntryError="roomEntryError" :game="game" v-else/>
  </div>
</template>

<script>
import GameContainer from '@/components/games/GameContainer.vue';
import PreGameLobby from '@/components/games/PreGameLobby.vue';
import { CONNECT_TO_GAMESOCKET, DISCONNECT_FROM_GAMESOCKET } from '@/mutation-types';

export default {
  name: 'Game',
  components: {
    GameContainer,
    PreGameLobby,
  },
  props: {
    image: String,
    headerText: String,
    game: Object,
  },
  data() {
    return {
      errorMessage: '',
    };
  },
  computed: {
    connected() {
      return this.$store.state[this.game.storeKey].socket !== null;
    },
  },
  methods: {
    connect(roomID, username, customPaddle, isSpectating) {
      this.$store.commit({
        type: CONNECT_TO_GAMESOCKET,
        game: this.game,
        roomID,
        username,
        customPaddle,
        isSpectating,
      });
    },
    disconnect() {
      this.$store.commit({
        type: DISCONNECT_FROM_GAMESOCKET,
        game: this.game,
      });
    },
    roomEntryError(event) {
      this.errorMessage = event;
    },
  },
  created() {

  },
};
</script>

<style scoped>
  #game {
    grid-column: 3/11;
  }
</style>
