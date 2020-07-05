<template>
  <div class="preGameLobby">
    <h2>
      {{headerText}}
    </h2>
    <img id="game-img" alt="Vue logo" :src="require('../../assets/' + image)">

    <div id="inputs">
      <label id="room-label" for="room-input">Room-ID:</label><input id="room-input" v-model="room"/>
      <label id="username-label" for="username-input">Username:</label><input id="username-input" v-model="username"/>
      <label id="custom-paddle-label" for="custom-paddle-input">Custom Paddle:</label><input type="checkbox" id="custom-paddle-input" v-model="customPaddle">
      <label id="spectate-label" for="spectate-input">Spectating:</label><input type="checkbox" id="spectate-input" v-model="isSpectating">
      <button id="connect-btn" @click="$parent.connect(room, username, customPaddle, isSpectating)">connect</button>
      <span id="error">{{errorMessage}}</span>
    </div>
  </div>
</template>

<script>

export default {
  name: 'PreGameLobby',
  props: {
    image: String,
    headerText: String,
    gameConfig: Object,
    errorMessage: String,
  },
  data() {
    return {
      room: '',
      username: '',
        customPaddle: false,
        isSpectating: false,
    };
  },
  created() {

  },
  mounted() {
  },
  methods: {},
  beforeDestroy() {
  },
};
</script>

<style scoped>
  .preGameLobby{
    grid-column: 1/13;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
  }

  h2{
    grid-column: 3/11;

  }
#game-img{
  grid-column:3/11;
  height: 200px;
  margin:0 auto 50px;
}
#inputs{
  grid-column: 4/10;
  text-align: left;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-column-gap: 20px;
  grid-row-gap: 20px;
}

#connect-btn{
  grid-column: 1/3;
  height: 50px;
}
#error{
  color:red;
}
  @media (max-width: 800px) {
    .preGameLobby{
      grid-column: 1/6;
    }
    #inputs{
      grid-column: 1/6;
    }
    #game-img{
      grid-column: 2/11;
    }
  }

</style>
