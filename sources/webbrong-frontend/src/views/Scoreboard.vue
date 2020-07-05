<template>
  <div class="scoreboard">
    <h2 class="rankingH">RANKING PONG- TOP 100</h2>
    <button id="refreshButton" @click="fetchData"> refresh</button>
    <Table id="pongTable" :colTitles="pongHeader" :colData="pongData"/>
    <h2 class="rankingH">RANKING BREAKOUT- TOP 100</h2>
    <Table :colTitles="breakoutHeader" :colData="breakoutData"/>
  </div>
</template>

<script>
import axios from 'axios';
import Table from '@/components/scoreboard/Table.vue';

export default {
  name: 'Dashboard',
  components: {
    Table,
  },
  data() {
    return {
      pongHeader: [],
      pongData: [],
      breakoutHeader: [],
      breakoutData: [],
    };
  },
  methods: {
    fetchData() {
      axios
        .get('http://localhost:8080/data/scoreboard/PONG')
        .then((dashBoardData) => {
          this.pongHeader = dashBoardData.data.headers;
          this.pongData = dashBoardData.data.data;
        });


      axios
        .get('http://localhost:8080/data/scoreboard/BREAKOUT')
        .then((dashBoardData) => {
          this.breakoutHeader = dashBoardData.data.headers;
          this.breakoutData = dashBoardData.data.data;
        });
    },
  },
  created() {
    this.fetchData();
  },
};
</script>

<style scoped>
  .scoreboard {
    display: grid;
    grid-template-columns: repeat(12, 1fr);
  }

  Table {
    grid-column: 3/11;
  }

  #refreshButton {
    grid-column: 9;
  }
  .rankingH{
    grid-column: 1/13;
    margin-bottom: 50px;
    text-decoration: underline;
  }

  #pongTable{
    margin-bottom: 100px;
  }
</style>
