<template>
  <div id="app" class="skeleton">
    <div class="header">
      <div id="nav">
        <router-link to="/">Home</router-link>
        <router-link to="/ping">Pong</router-link>
        <router-link to="/breakout">Breakout</router-link>
        <router-link to="/scoreboard">Scoreboard</router-link>
        <router-link to="/about">About</router-link>

      </div>
    </div>
    <router-view class="content"/>
    <Footer/>
  </div>
</template>
<script>
// @ is an alias to /src
import Footer from '@/components/skeleton/Footer.vue';

export default {
  name: 'app',
  components: {
    Footer,
  },
};
</script>

<style>
  :root {
    --primary: #dae2e2;
    --secondary: #d41c23;
    --background: #f7fbf9;
    --dark: #333;
    --light: #fff;
    --shadow: 0 1px 5px rgba(104, 104, 104, 0.8);
  }

  html {
    box-sizing: border-box;
    font-family: Arial, Helvetica, sans-serif;
    color: var(--dark);
  }

  html, body {
    background: var(--secondary);
    margin: 0px;
    line-height: 1.4;
  }

  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }

  #nav {
    display:grid;
    grid-template-columns: repeat(5,1fr);
    grid-gap: 20px;
    grid-column: 2;
  }

  #nav a {
    background: var(--primary);
    display: block;
    text-decoration: none;
    padding: 0.5rem 2rem;
    text-align: center;
    color: var(--dark);
    font-size: 1.1rem;
    box-shadow: var(--shadow);
    font-weight: bold;
    border-radius: 6em / 9em;
  }

  #nav a.router-link-exact-active {
    background: var(--secondary);
    color: var(--primary);
    text-shadow: 1px 1px black;
  }

  #nav .avatar {
    position: absolute;
    right: 0px;
  }

  .skeleton {
    height: 100%;
    display: grid;
    grid-template-columns: 1fr;
  }

  .header {
    padding-top: 50px;
    padding-bottom: 50px;
    background-color: var(--background);
    box-shadow: 0px 2px 5px 2px #888;
    z-index: 3;
    display: grid;
    grid-template-columns: 10% auto 10%;
  }

  .content {
    background-color: var(--background);
    padding-bottom: 200px;
    padding-top: 80px;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
  }

  @media (max-width: 800px) {
    #nav {
      grid-template-columns: 1fr;
    }

    .content{
      grid-template-columns: repeat(6, 1fr);
    }
  }

</style>
