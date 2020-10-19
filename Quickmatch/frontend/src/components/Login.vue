<template>
  <v-container fluid v-if="!isSignedIn">
    <v-row justify="center" align="center">
      <v-col cols="1">
        <v-img src="../assets/logo.png" alt="Quick Match logo"></v-img>
      </v-col>
    </v-row>
    <v-layout row wrap align-center="true" justify-center="true">
      <v-card class="mx-10" raised width="25%">
        <v-form v-model="valid">
          <v-layout justify-center="true">
            <v-card-title align="center">Connexion à Quick Match</v-card-title>
          </v-layout>
          <v-col class="py-0" cols="4" md="12">
            <v-text-field
              v-model="email"
              :rules="emailRules"
              :counter="50"
              label="E-mail"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="password"
              :rules="passwordRules"
              :counter="20"
              label="Mot de passe"
              type="password"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
        </v-form>
        <v-row class="pa-0" align="center" justify="center">
          <v-col class="pt-0" cols="10">
            <v-btn rounded outlined block v-on:click="login">Connexion</v-btn>
          </v-col>
        </v-row>
        <v-divider></v-divider>
        <v-row align="center" justify="center">
          <v-col cols="10">
            <button></button>
            <v-btn
              v-google-signin-button="clientId"
              color="green lighten-1"
              rounded
              outlined
              block
            >Se connecter avec Google</v-btn>
          </v-col>
        </v-row>
      </v-card>
      <v-card class="mx-10" raised width="25%">
        <v-layout justify-center="true">
          <v-card-title align="center">Pas encore inscrit(e) ?</v-card-title>
        </v-layout>
        <v-row class="pa-0" align="center" justify="center">
          <v-col cols="10">
            <v-btn to="/signin" rounded outlined block>Inscrivez vous !</v-btn>
          </v-col>
        </v-row>
        <!--
        <v-divider></v-divider>
        <v-layout justify-center="true">
          <v-card-title align="center">Mot de passe oublié ?</v-card-title>
        </v-layout>
        <v-row class="pa-0" align="center" justify="center">
          <v-col cols="10">
            <v-btn rounded outlined block>Changer de mot de passe</v-btn>
          </v-col>
        </v-row>
        -->
      </v-card>
    </v-layout>
  </v-container>
</template>

<script>
import GoogleSignInButton from "../main.js";
import store from "../store";
import axios from "axios";
import router from "../router";
var sha512 = require('js-sha512');

export default {
  directives: {
    GoogleSignInButton
  },
  data: () => ({
    clientId:
      "864617003210-1dr6nsvputhjv59l5b3633sslri4vdjd.apps.googleusercontent.com",
    valid: false,
    email: "",
    emailRules: [
      v => !!v || 'E-mail requis',
      v => /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/.test(v) || 'E-mail invalide'
    ],
    password: "",
    passwordRules: [
      v => !!v || "Mot de passe requis",
      v => v.length >= 8 || "Mot de passe trop court"
    ]
  }),
  methods: {
    OnGoogleAuthSuccess(googleUser) {
      store.dispatch("setGoogleUser", googleUser);
      this.findUser(); // méthode initiale
    },
    OnGoogleAuthFail(error) {
      console.log(error);
    },
    findUser() {
      axios
        .get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
            store.getters.email
        )
        .then(response => {
          if (Object.keys(response.data).length <= 1) {
            router.push("/createAccount");
          } else {
            store.dispatch("hasAccount");
            store.dispatch("setID");
            store.dispatch("setIsValidGoogle").then(response => {
                router.push("/");
            });
          }
        })
        .catch(e => {
          alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CANNOT_CHECK_USER_MAIL");
          router.push("/");
          //console.log(e);
        });
    },
    logout() {
      store.dispatch("logout");
      router.push("/disconnected");
    },
    passwordCheck(apiPassword,id) {
        var hashedPassword = sha512(this.password + id);
        return hashedPassword === apiPassword;
    },
    async login() {
      axios
        .get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
            this.email
        )
        .then(async (response) => {
            if (this.passwordCheck(response.data.mdp,response.data.id)) {
                store.dispatch("setEmail",this.email);
                store.dispatch("hasAccount");
                store.dispatch("setID");
                var player = await axios.get(
                  "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
                    this.email,
                  { ResponseType: "json" }
                );
                store.dispatch("setIsValid",player.data.is_valid);
                if(player.data.is_valid == false) {
                    this.$router.push("/verifyAccount");
                }else{
                    this.$router.push("/"); // redirection vers la page d'accueil
                }
            }else{
                alert("Email ou mot de passe invalide !");
            }
        })
        .catch(e => {
            //console.log(e);
            alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CANNOT_CHECK_USER_MAIL");
            router.push("/");
        });
    }
  },
  computed: {
    isSignedIn: function() {
      this.$store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    }
  }
};
</script>
