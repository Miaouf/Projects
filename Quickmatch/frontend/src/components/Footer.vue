<template>
  <v-footer color="#20003c" padless>
    <v-row justify="center">

      <v-btn v-for="link in links" :key="link.text" :to="link.route" color="white" text rounded class="my-2">
        {{ link.text }}
      </v-btn>

      <v-col class="py-4 pt-1 text-center white--text" cols="12" padding-top="0px">
        {{ new Date().getFullYear() }} — <strong>Quick Match ©</strong>
      </v-col>

    </v-row>
  </v-footer>
</template>

<script>
  export default {
    data: () => ({
      links: [
        { text:'Accueil', route: '/' },
        { text:'A propos de nous', route: '/aboutus' },
        { text:'Nous contacter', route: '/contactus' }
      ],
    }),
  }
</script>
