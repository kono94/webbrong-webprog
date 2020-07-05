<template>
  <div>
    <h2>
      Room-ID: {{roomID}} connected as: {{username}} {{isAdmin ? "(Admin)" : ""}}
    </h2>
    <canvas id="game-canvas" ref="game" :width="playground.width" :height="playground.height"
            style="border: 1px solid black;"
            v-on="this.isCustomPaddle ? { mousedown: handleStart,
               mousemove: handleMove,
               mouseup: handleEnd,} : {}"
            @touchstart="handleTouchStart"
            @touchmove="handleTouchMove"
            @touchend="handleTouchEnd"
    ></canvas>
    <button id="disconnect-btn" @click="$parent.disconnect()">disconnect</button>
    <button id="start-match-btn" @click="startMatch" v-if="isAdmin">Start Match</button>
    <br>
    <span id="ingame-message">{{ ingameMessage}}</span>
    <div id="panels">
      <StatsPanel id="stats" :players="players"/>
      <SpectatorPanel id="spectators" :spectators="spectators"/>
    </div>
  </div>
</template>

<script>
import { Direction } from '../../constants';
import gamesConfig from '../../gamesConfig';
import StatsPanel from './StatsPanel.vue';
import SpectatorPanel from './SpectatorPanel.vue';

export default {
  name: 'GameContainer',
  components: { StatsPanel, SpectatorPanel },
  props: {
    game: Object,
  },
  data() {
    return {
      context: {},
      playground: {
        width: 100,
        height: 100,
      },
      customPaddle: {},
      players: {},
      socket: this.$store.state[this.game.storeKey].socket,
      roomID: this.$store.state[this.game.storeKey].roomID,
      isCustomPaddle: this.$store.state[this.game.storeKey].isCustomPaddle,
      isSpectating: this.$store.state[this.game.storeKey].isSpectating,
      username: this.$store.state[this.game.storeKey].username,
      spectators: [],
      isAdmin: false,
      canvas: null,
      started: false,
      lastTouch: { x: 0, y: 0 },
      location: null,
      ingameMessage: '',
      allowedDrawingArea: null,
    };
  },
  created() {
    this.socket.onmessage = (payload) => {
      const data = JSON.parse(payload.data);
      switch (data.event) {
        case 'GAME_TICK':
          this.processTick(data);
          break;
        case 'ROOM_ENTRY_ERROR_MESSAGE':
          this.$emit('roomEntryError', data.message);
          break;
        case 'INGAME_MESSAGE':
          this.ingameMessage = data.message;
          setTimeout(() => {
            this.ingameMessage = '';
          }, 5000);
          break;
        default:
          console.error('no matching event type found');
      }
    };
  },
  mounted() {
    window.addEventListener('keydown', this.pressedMethod);
    window.addEventListener('keyup', this.releasedMethod);
    this.context = this.$refs.game.getContext('2d');
    this.canvas = this.$refs.game;
    this.$refs.game.style.width = '100%';
    this.$refs.game.style.height = '100%';

    if (this.isCustomPaddle) {
      this.initDrawing();
    }
  },
  methods: {
    processTick(data) {
      this.players = data.players;
      this.playground.width = data.playground.width;
      this.playground.height = data.playground.height;

      this.spectators = data.spectators;
      this.isAdmin = data.admin === this.username;
      // clear the whole canvas
      this.context.clearRect(0, 0, this.context.canvas.width, this.context.canvas.height);

      this.context.lineWidth = 1;
      // draw the ball
      this.context.fillStyle = data.ball.color;
      this.context.beginPath();
      this.context.arc(data.ball.position.x, data.ball.position.y, data.ball.position.radius, 0, 2 * Math.PI);
      this.context.stroke();
      this.context.fill();

      if (this.customPaddle.startX) {
        this.context.lineWidth = 20;
        this.context.strokeStyle = 'black';
        this.context.beginPath();
        this.context.moveTo(this.customPaddle.startX, this.customPaddle.startY);
        this.context.lineTo(this.customPaddle.endX, this.customPaddle.endY);
        this.context.stroke();
        this.context.closePath();
      }

      // draw players
      Object.keys(data.players).forEach((username) => {
        const player = data.players[username];
        this.context.fillStyle = player.paddle.color;
        this.context.beginPath();
        this.context.moveTo(player.paddle.pTopLeft.x, player.paddle.pTopLeft.y);
        this.context.lineTo(player.paddle.pTopRight.x, player.paddle.pTopRight.y);
        this.context.lineTo(player.paddle.pBottomRight.x, player.paddle.pBottomRight.y);
        this.context.lineTo(player.paddle.pBottomLeft.x, player.paddle.pBottomLeft.y);
        this.context.lineTo(player.paddle.pTopLeft.x, player.paddle.pTopLeft.y);
        this.context.closePath();
        this.context.fill();

        if (username === this.username) {
          this.context.strokeStyle = player.paddle.color;
          this.context.lineWidth = 20;
          this.context.beginPath();
          switch (player.position) {
            case Direction.TOP:
              this.context.moveTo(0, 0);
              this.context.lineTo(this.playground.width, 0);
              break;
            case Direction.LEFT:
              this.context.moveTo(0, 0);
              this.context.lineTo(0, this.playground.height);
              break;
            case Direction.RIGHT:
              this.context.moveTo(this.playground.width, 0);
              this.context.lineTo(this.playground.width, this.playground.height);
              break;
            case Direction.BOTTOM:
              this.context.moveTo(0, this.playground.height);
              this.context.lineTo(this.playground.width, this.playground.height);
              break;
            default:
              break;
          }
          this.context.stroke();
          this.context.closePath();

          this.allowedDrawingArea = player.allowedDrawingArea;
          if (this.allowedDrawingArea) {
            this.context.lineWidth = 3;
            this.context.strokeStyle = 'grey';
            this.context.beginPath();
            this.context.moveTo(this.allowedDrawingArea.pTopLeft.x, this.allowedDrawingArea.pTopLeft.y);
            this.context.lineTo(this.allowedDrawingArea.pTopRight.x, this.allowedDrawingArea.pTopRight.y);
            this.context.lineTo(this.allowedDrawingArea.pBottomRight.x, this.allowedDrawingArea.pBottomRight.y);
            this.context.lineTo(this.allowedDrawingArea.pBottomLeft.x, this.allowedDrawingArea.pBottomLeft.y);
            this.context.lineTo(this.allowedDrawingArea.pTopLeft.x, this.allowedDrawingArea.pTopLeft.y);
            this.context.stroke();
            this.context.closePath();
          }
        }

        if (this.game === gamesConfig.BREAKOUT) {
          data.obstacles.forEach((obstacle) => {
            if (obstacle.hitPoints > 0) {
              this.context.fillStyle = obstacle.color;
              this.context.strokeStyle = 'black';
              this.context.lineWidth = 2;
              this.context.beginPath();
              this.context.moveTo(obstacle.pTopLeft.x, obstacle.pTopLeft.y);
              this.context.lineTo(obstacle.pTopRight.x, obstacle.pTopRight.y);
              this.context.lineTo(obstacle.pBottomRight.x, obstacle.pBottomRight.y);
              this.context.lineTo(obstacle.pBottomLeft.x, obstacle.pBottomLeft.y);
              this.context.lineTo(obstacle.pTopLeft.x, obstacle.pTopLeft.y);
              this.context.stroke();
              this.context.closePath();
              this.context.fill();
            }
          });
        }
      });
    },
    pressedMethod(event) {
      switch (event.keyCode) {
        case 87:
          this.startMove(Direction.TOP);
          break;
        case 68:
          this.startMove(Direction.RIGHT);
          break;
        case 83:
          this.startMove(Direction.BOTTOM);
          break;
        case 65:
          this.startMove(Direction.LEFT);
          break;
        default:
      }
    },
    releasedMethod(event) {
      switch (event.keyCode) {
        case 87:
          this.stopMove(Direction.TOP);
          break;
        case 68:
          this.stopMove(Direction.RIGHT);
          break;
        case 83:
          this.stopMove(Direction.BOTTOM);
          break;
        case 65:
          this.stopMove(Direction.LEFT);
          break;
        default:
      }
    },
    startMove(direction) {
      if (!this.isCustomPaddle) {
        this.socket.send(
          JSON.stringify({ event: 'PADDLE_MOVEMENT', direction, isMoving: true }),
        );
      }
    },
    stopMove(direction) {
      if (!this.isCustomPaddle) {
        this.socket.send(
          JSON.stringify({ event: 'PADDLE_MOVEMENT', direction, isMoving: false }),
        );
      }
    },
    isInAllowedArea(pos) {
      const top = this.allowedDrawingArea.pTopLeft.y;
      const bottom = this.allowedDrawingArea.pBottomLeft.y;
      const right = this.allowedDrawingArea.pTopRight.x;
      const left = this.allowedDrawingArea.pTopLeft.x;
      return pos.x > left && pos.x < right && pos.y > top && pos.y < bottom;
    },
    getMousePos(mouseEvent) {
      const xRatio = this.playground.width / this.canvas.clientWidth;
      const yRatio = this.playground.height / this.canvas.clientHeight;
      this.lastTouch.x = mouseEvent.layerX;
      this.lastTouch.y = mouseEvent.layerY;
      return {
        x: mouseEvent.layerX * xRatio,
        y: mouseEvent.layerY * yRatio,
      };
    },
    handleStart(ev) {
      const pos = this.getMousePos(ev);
      if (!this.isInAllowedArea(pos)) {
        return;
      }
      this.customPaddle.startX = pos.x;
      this.customPaddle.startY = pos.y;
      this.customPaddle.endX = pos.x;
      this.customPaddle.endY = pos.y;
      this.started = true;
      this.socket.send(
        JSON.stringify({ event: 'RESET_CUSTOM_PADDLE' }),
      );
    },
    handleMove(ev) {
      if (this.started) {
        const pos = this.getMousePos(ev);
        if (!this.isInAllowedArea(pos)) {
          return;
        }
        this.customPaddle.endX = pos.x;
        this.customPaddle.endY = pos.y;
      }
    },
    handleEnd(ev) {
      if (this.started) {
        const pos = this.getMousePos(ev);
        if (!this.isInAllowedArea(pos)) {
          return;
        }
        this.customPaddle.endX = pos.x;
        this.customPaddle.endY = pos.y;
        this.started = false;
        this.sendCustomPaddle();
      }
    },
    handleTouchEnd(e) {
      e.preventDefault();
      if (this.isCustomPaddle) {
        this.handleEnd({
          layerX: this.lastTouch.x,
          layerY: this.lastTouch.y,
        });
      } else {
        console.log('stop');
        this.stopMove(Direction.TOP);
        this.stopMove(Direction.LEFT);
        this.stopMove(Direction.RIGHT);
        this.stopMove(Direction.BOTTOM);
      }
    },
    handleTouchMove(e) {
      e.preventDefault();
      const touch = e.touches[0];
      const rect = this.canvas.getBoundingClientRect();
      if (this.isCustomPaddle) {
        this.handleMove({
          layerX: touch.clientX - rect.left,
          layerY: touch.clientY - rect.top,
        });
      } else if (this.players && this.players[this.username]) {
        const me = this.players[this.username];
        if (me.position === Direction.LEFT || me.position === Direction.RIGHT) {
          const yRatio = this.playground.height / this.canvas.clientHeight;
          const y = (touch.clientY - rect.top) * yRatio;
          if (y > (me.paddle.pTopLeft.y + me.paddle.pBottomLeft.y) / 2) {
            this.stopMove(Direction.TOP);
            this.startMove(Direction.BOTTOM);
          } else {
            this.stopMove(Direction.BOTTOM);
            this.startMove(Direction.TOP);
          }
        } else if (me.position === Direction.TOP || me.position === Direction.BOTTOM) {
          const xRatio = this.playground.width / this.canvas.clientWidth;
          const x = (touch.clientX - rect.left) * xRatio;
          if (x > (me.paddle.pTopLeft.x + me.paddle.pTopRight.x) / 2) {
            this.stopMove(Direction.LEFT);
            this.startMove(Direction.RIGHT);
          } else {
            this.stopMove(Direction.RIGHT);
            this.startMove(Direction.LEFT);
          }
        }
      }
    },
    handleTouchStart(e) {
      e.preventDefault();
      const touch = e.touches[0];
      const rect = this.canvas.getBoundingClientRect();
      if (this.isCustomPaddle) {
        this.handleStart({
          layerX: touch.clientX - rect.left,
          layerY: touch.clientY - rect.top,
        });
      }
    },
    initDrawing() {
      window.addEventListener('mouseup', () => {
        if (this.started) {
          this.sendCustomPaddle();
          this.started = false;
        }
      }, false);
      window.addEventListener('touchend', () => {
        if (this.started) {
          this.started = false;
          this.sendCustomPaddle();
        }
      }, false);
    },
    sendCustomPaddle() {
      this.socket.send(
        JSON.stringify({
          event: 'NEW_CUSTOM_PADDLE',
          x1: this.customPaddle.startX,
          y1: this.customPaddle.startY,
          x2: this.customPaddle.endX,
          y2: this.customPaddle.endY,
        }),
      );
      this.customPaddle = {};
    },
    startMatch() {
      this.socket.send(JSON.stringify({ event: 'START_MATCH' }));
    },
  },
  beforeDestroy() {
    // IMPORTANT! remove eventListener each time the component gets remove from the virtual DOM
    window.removeEventListener('keydown', this.pressedMethod);
    window.removeEventListener('keyup', this.releasedMethod);
  },
};

</script>

<style scoped>
  #panels{
    margin-top: 40px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-column-gap: 20px;
  }
  #stats{
    grid-column: 1/2;
  }

  #spectators{
    grid-column:  2/3;
  }
  #ingame-message{
    color: red;
  }
</style>
