import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import { SET_SCORBOARD_SORTING, CONNECT_TO_GAMESOCKET, DISCONNECT_FROM_GAMESOCKET } from './mutation-types';

axios.defaults.baseURL = process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : '';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    scoreboard: {
      sortingColumnIndex: 0,
      asc: true,
    },
    pong: {
      socket: null,
      roomID: null,
      isCustomPaddle: false,
      username: null,
      isSpectating: false,
    },
    breakout: {
      socket: null,
      roomID: null,
      isCustomPaddle: false,
      username: null,
      isSpectating: false,
    },
  },
  mutations: {
    [SET_SCORBOARD_SORTING](state, payload) {
      state.scoreboard.sortingColumnIndex = payload.index;
      state.scoreboard.asc = payload.asc;
    },
    [CONNECT_TO_GAMESOCKET](state, payload) {
      if (state[payload.game.storeKey].socket === null) {
        axios.get(`/eureka/gameInstanceInfo/${payload.game.appName}/${payload.roomID}`).then((resp) => {
          const url = `ws://${resp.data.ipAddr}:${resp.data.metadata['management.port']}/${payload.game.storeKey}-room/`;
          const socket = new WebSocket(`${url}${payload.roomID}:${payload.username}:${payload.customPaddle}:${payload.isSpectating}`);
          socket.onopen = () => {
            state[payload.game.storeKey].socket = socket;
            state[payload.game.storeKey].roomID = payload.roomID;
            state[payload.game.storeKey].isCustomPaddle = payload.customPaddle;
            state[payload.game.storeKey].username = payload.username;
            state[payload.game.storeKey].isSpectating = payload.isSpectating;
          };
          socket.onclose = () => {
            state[payload.game.storeKey].socket = null;
          };
        });
      }
    },
    [DISCONNECT_FROM_GAMESOCKET](state, payload) {
      if (state[payload.game.storeKey].socket !== null) {
        state[payload.game.storeKey].socket.close();
      }
    },
  },
  actions: {

  },
});
