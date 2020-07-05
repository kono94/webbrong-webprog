import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import Scoreboard from './views/Scoreboard.vue';
import Ping from './views/Pong.vue';
import Breakout from './views/Breakout.vue';

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/ping',
      name: 'ping',
      component: Ping,
    },
    {
      path: '/breakout',
      name: 'breakout',
      component: Breakout,
    },
    {
      path: '/scoreboard',
      name: 'scoreboard',
      component: Scoreboard,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
    },
  ],
});
