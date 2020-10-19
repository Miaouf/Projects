<template>
  <v-container fluid>
    <v-row justify="center" align="center">
      <v-col cols="1">
        <v-img src="../assets/logo.png" alt="Quick Match logo"></v-img>
      </v-col>
    </v-row>
    <v-layout row wrap align-center="true" justify-center="true">
      <v-card class="mx-10" raised width="25%">
        <v-form v-on:submit.prevent="createAccount">
          <v-layout justify-center="true">
            <v-card-title align="center">Création de compte</v-card-title>
          </v-layout>
          <v-col class="py-0" cols="4" md="12">
            <v-text-field
              v-model="pseudo"
              :rules="pseudoRules"
              :counter="20"
              label="Pseudonyme"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="surname"
              label="Nom"
              :rules="surnameRules"
              type="surname"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="firstName"
              label="Prénom"
              :rules="firstNameRules"
              type="firstName"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
        </v-form>
        <v-row class="pa-0" align="center" justify="center">
          <v-col class="pt-0" cols="10">
            <v-btn v-on:click="createAccount()" rounded outlined block>Créer !</v-btn>
          </v-col>
        </v-row>
      </v-card>
    </v-layout>
  </v-container>
</template>

<script>
import store from "../store";
import axios from "axios";
import router from "../router";

export default {
  data() {
    return {
      surname: "",
      firstName: "",
      pseudo: "",
      mailAddress: "",
      phone_number: null,
      mdp: "stocked by Google",
      response: "",
      avatar: "",
      pseudoRules: [
        v => !!v || "Pseudo requis",
        v => v.length >= 2 || "verifyAccountPseudo trop court",
        v =>
          /^[a-zA-Z0-9 _\-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          'Pseudo invalide (lettres, chiffres, espace, "_" et "-" seulement)'
      ],
      surnameRules: [
        v => !!v || "Nom requis",
        v =>
          /^[a-zA-Z \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          'Nom invalide (lettres (avec ou sans accent), espace, et "-" seulement)'
      ],
      firstNameRules: [
        v => !!v || "Prénom requis",
        v =>
          /^[a-zA-Z\- éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          'Prénom invalide (lettres (avec ou sans accent), espace, et "-" seulement)'
      ]
    };
  },
  methods: {
      checkPseudo: function() {
          return /^[a-zA-Z0-9 _-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.pseudo);
      },
      checkSurname: function() {
          return /^[a-zA-Z -éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.surname);
      },
      checkFirstname: function() {
          return /^[a-zA-Z -éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.firstName);
      },
      checkSyntaxes: function() {
        if (! this.checkPseudo()) {
          alert("Le pseudo entré est invalide (lettres, chiffres, espace, '_' et '-' seulement).");
          return false;
        }
        if (! this.checkSurname()) {
          alert("Le nom entré est invalide (lettres, espaces et '-' seulement).");
          return false;
        }
        if (! this.checkFirstname()) {
          alert("Le prénom entré est invalide (lettres, espaces et '-' seulement).");
          return false;
        }
        return true;
      },
      creationError: function(err) {
        if (err.detail.includes("pseudo")) {
            alert("Pseudo déjà existant ! Veuillez en saisir un nouveau.");
            return;
        }else{
          alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CREATION_GOOGLE_ACCOUNT_ERROR");
          router.push("/");
        }
    },
    checkEntries: function() {
        if(this.surname.length < 2 || this.surname.length > 20) {
            alert("Le nom doit faire entre 2 et 20 caractères.");
            return false;
        }
        if(this.firstName.length < 2 || this.firstName.length > 20) {
            alert("Le prénom doit faire entre 2 et 20 caractères.");
            return false;
        }
        if(this.pseudo.length < 2 || this.pseudo.length > 20) {
            alert("Le pseudo doit faire entre 2 et 20 caractères.");
            return false;
        }
        if (! this.checkSyntaxes()) {
          return false;
        }
    },
    validateAccount: async function(id) {
        let apiRep = null;
        apiRep = await axios.put(
            "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + id,
            {
                is_valid: true
            }
        );
        if (apiRep.data.name != "error") {
            // validation ok
        }else{
          alert("Si vous voyez ce message, merci de contacter le support urgemment.  \n\n ERR: CANNOT_UPDATE_VALID_FIELD");
        }
    },
    createAccount: async function() {
        if (this.checkEntries() == false) {
            return;
        }
        let apiRep = null;
        apiRep = await axios.post(
            "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/",
            {
              pseudo: this.pseudo,
              surname: this.surname,
              first_name: this.firstName,
              mdp: this.mdp,
              phone_number: this.phone_number,
              mail_address: this.mailAddress,
              avatar: this.avatar,
              is_valid: true
          });
          if (apiRep.data.name != "error") {
              this.validateAccount(apiRep.data.id);
              store.dispatch("hasAccount");
              store.dispatch("setID");
              store.dispatch("setIsValidGoogle").then(response => {
                  router.push("/");
              });
          } else {
            this.creationError(apiRep.data);
          }
        //await axios.delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/MailInvitationClub/" + this.mailAddress);
    }
  },
  created: function() {
    store.dispatch("isSignedIn");
    if (! store.getters.email == "none") {
      alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CREATION_ACCOUNT_EMAIL_NONE");
      router.push("/");
    }else{
      this.surname = store.getters.surname;
      this.firstName = store.getters.givenName;
      this.mailAddress = store.getters.email;
      this.avatar = store.getters.imageUrl;
    }
  }
};
</script>
<style></style>
