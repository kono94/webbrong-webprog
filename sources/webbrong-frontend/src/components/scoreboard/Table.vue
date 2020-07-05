<template>
  <table id="dashboard" class="table">
    <thead class="thead-dark">
    <tr>
      <th><span class="columnTitle">Rank</span></th>
      <th v-for="(colTitle, index) in colTitles"
          v-bind:key=index
          @click='changeSorting(index)'
          v-bind:class="{ active: $store.state.scoreboard.sortingColumnIndex === index }">

        <span class="columnTitle">{{colTitle}}</span>
        <font-awesome-icon :icon="defaultSortIcon" v-if="$store.state.scoreboard.sortingColumnIndex !== index"/>
        <font-awesome-icon :icon="directionIcon" v-else/>
      </th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(entry, index) in colData" v-bind:key=index>
      <td> {{index}}</td>
      <td v-for="(e, i) in Object.keys(entry)" v-bind:key=i>
        {{entry[e]}}
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faSortUp, faSortDown, faSort } from '@fortawesome/free-solid-svg-icons';
import { SET_SCORBOARD_SORTING } from '../../mutation-types';

export default {
  name: 'Table',
  data() {
    return {
      defaultSortIcon: faSort,
    };
  },
  computed: {
    directionIcon() {
      return this.$store.state.scoreboard.asc ? faSortUp : faSortDown;
    },
  },
  props: {
    colTitles: Array,
    colData: Array,

  },
  components: {
    FontAwesomeIcon,
  },
  created() {
    this.sortTable();
  },
  methods: {
    changeSorting(index) {
      const scoreBoardStore = this.$store.state.scoreboard;
      let orderAsc;
      if (index === scoreBoardStore.sortingColumnIndex) {
        // swap ordering if double clicking on same column index
        orderAsc = !scoreBoardStore.asc;
      } else {
        // default behaviour is ordering ascending
        orderAsc = true;
      }
      this.$store.commit({
        type: SET_SCORBOARD_SORTING,
        index,
        asc: orderAsc,
      });

      this.sortTable();
    },
    sortTable() {
      this.colData.sort((a, b) => {
        const sortingProp = Object.keys(a)[this.$store.state.scoreboard.sortingColumnIndex];

        if (typeof a[sortingProp] === 'string') {
          if (this.$store.state.scoreboard.asc) {
            return a[sortingProp].localeCompare(b[sortingProp]);
          }
          return b[sortingProp].localeCompare(a[sortingProp]);
        }

        if (this.$store.state.scoreboard.asc) {
          return a[sortingProp] - b[sortingProp];
        }
        return b[sortingProp] - a[sortingProp];
      });
    },
  },
};
</script>

<style scoped>
  th{
    cursor: pointer;
  }

  .columnTitle{
    margin-right: 10px;
  }

  .active{
    background-color: #a6a6a6;
  }
</style>
