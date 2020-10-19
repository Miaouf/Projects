<template>
  <v-app>

    <Appbar></Appbar>

    <v-content>
      <router-view></router-view>
    </v-content>

    <Footer></Footer>

  </v-app>
</template>

<script>
  import Appbar from "./components/Appbar"
  import Footer from "./components/Footer"

  export default {
    components: {
        Appbar, Footer
    }
  };

</script>

<style>

</style>
