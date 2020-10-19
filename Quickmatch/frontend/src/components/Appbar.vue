<template>
  <v-app-bar class="pa-0 ma-0" app color="black" dark>
    <v-btn to="/" text>
      <v-icon dark left>mdi-home-circle</v-icon>Accueil
    </v-btn>
    <v-divider vertical></v-divider>

    <v-btn v-if="isSignedIn" to="/calendar" text>
      <v-icon dark left>mdi-calendar-clock</v-icon>Agenda
    </v-btn>
    <v-divider vertical></v-divider>

    <v-btn v-if="isSignedIn" to="/stat" text>
      <v-icon dark left>mdi-poll</v-icon>Statistiques
    </v-btn>
    <v-divider vertical></v-divider>

    <v-btn v-if="isSignedIn" to="/match" text>
      <v-icon dark left>mdi-trophy-outline</v-icon>Matchs
    </v-btn>
    <v-divider vertical></v-divider>

    <v-btn v-if="isSignedIn" to="/profile" text>
      <v-icon dark left>mdi-account-outline</v-icon>Profil
    </v-btn>
    <v-divider vertical></v-divider>

    <v-btn v-if="isSignedIn" to="/club" text>
      <v-icon dark left>mdi-account-outline</v-icon>Clubs
    </v-btn>
    <v-divider vertical></v-divider>

    <v-spacer></v-spacer>

    <v-divider vertical></v-divider>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn to="/invitation" v-on="on" text>
          <v-icon>mdi-bell-ring-outline</v-icon>
        </v-btn>
      </template>
      <span>Invitations</span>
    </v-tooltip>

    <v-divider vertical></v-divider>

    <v-tooltip bottom>
      <template v-slot:activator="{ on }">
        <v-btn to="/login" v-if="!isSignedIn" v-on="on" text>
          <v-icon dark left>mdi-login</v-icon>
        </v-btn>
      </template>
      <span>Se connecter</span>
    </v-tooltip>

    <v-tooltip bottom open-on-hover>
      <template v-slot:activator="{ on }">
        <v-btn v-if="isSignedIn" v-on:click="logout()" v-on="on" text>
          <v-icon dark left>mdi-logout</v-icon>
        </v-btn>
      </template>
      <span>Se déconnecter</span>
    </v-tooltip>
  </v-app-bar>
</template>

<script>
import store from "../store";
import router from "../router";

export default {
  methods: {
    logout: function() {
      store.dispatch("logout");
      router.push("/");
    },
    login: function() {
      router.push("/login");
    }
  },
  computed: {
    isSignedIn() {
      store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    },
    id() {
      store.dispatch("id");
      return store.getters.id;
    }
  }
};
</script>
