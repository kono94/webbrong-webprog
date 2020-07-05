<template>
  <div class="home">
    <h1 id="welcomeText">
      Willkommen bei Webbrong!
    </h1>
    <img id="logo" alt="Vue logo" src="../assets/landscape3.jpg">
    <div id="three-item-wrap">
      <div id="first">
        <img alt="Vue logo" src="../assets/pongIcon.png">
        <p>
          Spiele Pong und zeige deine Skills.
          Lorem Ipsum has been the industry's standard dummy tex
        </p>
      </div>
      <div id="second">
        <img alt="Vue logo" src="../assets/breakoutIcon.png">
        <p>
          Keine Lust mehr auf Pong? Dann spiel doch einfach Breakout!
          Lorem Ipsum has been the industry's standard dummy tex
        </p>
      </div>
      <div id="third">
        <img alt="Vue logo" src="../assets/rankingIcon.png">
        <p>
          Zeige wie gut du bist und messe dich mit anderen Spielern.
        </p>
      </div>

    </div>

    <div class="rage">
      <img id="rageQuit-image" alt="RageQuit Gif" src="../assets/rageQuit.gif">
      <p id="ragequit-text">
        Schon seit Anbeginn der Zeit...
        Lorem Ipsum is simply dummy text of the printing and typesetting industry.
        Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a
        galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also
        the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the
        release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software
        like Aldus PageMaker including versions of Lorem Ipsum.
      </p>
    </div>

  </div>
</template>

<script>

export default {
  name: 'home',
  components: {
  },
};
</script>

<style scoped>
  #welcomeText {
    grid-column: 3/11;
    margin-bottom: 80px;
  }

  #three-item-wrap{
    grid-column: 3/11;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-column-gap: 70px;
    margin-bottom: 200px;
  }
  #three-item-wrap div {
    align-content: center;
    grid-template-columns: 1fr;
    grid-row-gap: 20px;
  }

  #three-item-wrap img {
    width: 50%;
  }

  #three-item-wrap p{
    margin-top:30px;
    text-align: justify;
  }
  #logo {
    object-fit: cover;
    grid-column: 1/13;
    width: 100%;
    height:300px;
    display: flex;
    margin: 0 auto 100px;
  }

  .rage{
    display: grid;
    grid-column: 3/11;
    grid-template-columns: repeat(12, 1fr);
  }
  #rageQuit-image {
    grid-column: 1/5;
    width: 100%;
  }

  #ragequit-text {
    text-align: justify;
    grid-column: 6/13;
    margin: 0;
  }

  .fullSize {
    grid-column: 1/13;
    background-color: var(--primary);
  }

  @media (max-width: 800px) {
    #rageQuit-image {
      grid-column: 1/7;
      margin-bottom: 30px;
    }

    #ragequit-text {
      grid-column: 1/7;
    }

    #welcomeText {
      grid-column: 2/6;
    }

    #logo {
      grid-column: 1/7;
      height:200px;
    }

    #three-item-wrap{
      grid-column: 2/6;
      grid-template-columns: repeat(1, 1fr);
      grid-column-gap: 70px;
    }

    #three-item-wrap img{
      height: 100px;
      width: 100px;
    }

    #three-item-wrap p{
      width: 200px;
      margin: 20px auto 60px;
    }

    #three-item-wrap{
      margin-bottom: 100px;
    }

    .rage{
      grid-column: 2/6;
      grid-template-columns: repeat(6, 1fr);
    }
  }

</style>
