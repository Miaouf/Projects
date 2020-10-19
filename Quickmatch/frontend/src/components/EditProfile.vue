<template>
  <div v-if="isSignedIn">
    <br />
    <br />
    <v-card class="mx-auto" max-width="800" tile>
      <v-list-item three-line>
        <v-list-item-content>
          <v-list-item-title class="display-2">Modifie ton profil !</v-list-item-title>
          <v-list-item-subtitle>
            <v-text-field label="Bio" v-model="playerToShow.bio"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
        <v-list-item-avatar>
          <img id="avatar" />
        </v-list-item-avatar>
      </v-list-item>
    </v-card>

    <br />
    <v-card class="mx-auto" max-width="800" tile>
      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Pseudo</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <p v-if="psdoErr" style="color : red; font-size: 25px; margin:0px;">Pseudo déjà utilisé</p>
            <v-text-field v-model="playerToShow.pseudo" :rules="pseudoRules" :counter="20"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Nom</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <v-text-field v-model="playerToShow.surname" :rules="surnameRules" :counter="20"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Prénom</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <v-text-field v-model="playerToShow.first_name" :rules="first_nameRules" :counter="20"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Adresse mail</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <p v-if="mailErr" style="color : red; font-size: 25px; margin:0px;">Mail déjà utilisé</p>

            <v-text-field v-model="playerToShow.mail_address" :rules="emailRules" :counter="50"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Numéro de téléphone</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <p
              v-if="phnmErr"
              style="color : red; font-size: 25px; margin:0px;"
            >Numéro de téléphone déjà utilisé</p>

            <v-text-field v-model="playerToShow.phone_number" :rules="telRules" :counter="14"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>

        <v-card-actions>
          <v-btn
            text
            color="deep-purple accent-4"
            v-on:click="updatePlayer"
          >Enregistrer les modifications</v-btn>
        </v-card-actions>
      </v-list-item>
    </v-card>
    <br />
    <br />
  </div>
</template>


<script>
import axios from "axios";
import store from "../store";

export default {
  data: () => ({
    dialog: false,
    psdoErr: 0,
    mailErr: 0,
    phnmErr: 0,
    playerToShow: {},
    surnameRules: [
      v => !!v || "Nom requis",
      v => v.length >= 2 || "Nom trop court",
      v =>
        /^[a-zA-Z \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
        "Nom invalide"
    ],
    first_nameRules: [
      v => !!v || "Prénom requis",
      v => v.length >= 2 || "Prénom trop court",
      v =>
        /^[a-zA-Z \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
        "Prénom invalide"
    ],
    pseudoRules: [
      v => !!v || "Pseudo requis",
      v => v.length >= 2 || "Pseudo trop court",
      v =>
        /^[a-zA-Z0-9 _\-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
        'Pseudo invalide (lettres, nombres, espace, "_" et "-" seulement)'
    ],
    emailRules: [
      v => !!v || "E-mail requis",
      v =>
        /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/.test(v) ||
        "E-mail invalide"
    ],
    telRules: [
      v => !!v || "Numéro de téléphone requis",
      v =>
        /^(0[1-8])(?:[ -]?([0-9]{2})){4}$/.test(v) ||
        'Numéro de téléphone invalide (vous pouvez utilisé "-" ou des espaces comme séparateur (06-66-66-66-66)'
    ]
  }),

  methods: {
    ShowError: function(err) {
      this.psdoErr = 0;
      this.mailErr = 0;
      this.phnmErr = 0;
      if (err.detail.includes("pseudo")) {
        this.psdoErr = 1;
      }
      if (err.detail.includes("mail_address")) {
        this.mailErr = 1;
      }
      if (err.detail.includes("phone_number")) {
        this.phnmErr = 1;
      }
    },
    updatePlayer: async function() {
      let apiRep = null;
      apiRep = await axios.put(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + this.id,
        {
          bio: this.playerToShow.bio,
          surname: this.playerToShow.surname,
          first_name: this.playerToShow.first_name,
          mail_address: this.playerToShow.mail_address,
          phone_number: this.playerToShow.phone_number,
          pseudo: this.playerToShow.pseudo
        }
      );
      if (apiRep.data.name != "error") {
        this.$router.push({ path: `/profile` });
      } else {
        alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CANNOT_UPDATE_PROFILE");
        router.push("/");
        this.ShowError(apiRep.data);
      }
    },
    refreshPic: function() {
      document.getElementById("avatar").src = this.playerToShow.avatar;
    }
  },

  async created() {
    this.id = this.$route.params.id;
    const player = await axios.get(
      "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + this.id,
      {
        responseType: "json"
      }
    );
    this.playerToShow = player.data;
    document.getElementById("avatar").src = this.playerToShow.avatar;
  },

  computed: {
    isSignedIn() {
      store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    },
    id() {
      store.dispatch("setID");
      return store.getters.id;
    }
  }
};
</script>

<style>
</style>
