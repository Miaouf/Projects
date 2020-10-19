<template>
  <v-container fluid>
    <v-layout row wrap align-center="true" justify-center="true">
      <v-card class="mx-10" raised width="25%">
        <v-form v-model="valid">
          <v-row class="pa-0" justify="center" align="center">
            <v-col align="center" cols="5">
              <v-img src="../assets/logo.png" alt="Quick Match logo"></v-img>Inscription
            </v-col>
          </v-row>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="surname"
              :rules="surnameRules"
              :counter="20"
              label="Nom"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="first_name"
              :rules="first_nameRules"
              :counter="20"
              label="Prénom"
              required
              outlined
              filled
            ></v-text-field>
          </v-col>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="pseudo"
              :rules="pseudoRules"
              :counter="20"
              label="Pseudo"
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
          <v-col class="py-0" cols="6" md="12">
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
              v-model="phone"
              :rules="phoneRules"
              :counter="10"
              label="Numéro de téléphone (optionnel)"
              outlined
              filled
            ></v-text-field>
          </v-col>
        </v-form>
        <v-row class="pa-0" align="center" justify="center">
          <v-col class="pt-0" cols="10">
            <v-btn rounded outlined block v-on:click="login">S'inscrire</v-btn>
          </v-col>
        </v-row>
        <v-divider></v-divider>
        <v-row align="center" justify="center">
          <v-col cols="10">
            <v-btn
              v-google-signin-button="clientId"
              color="green lighten-1"
              rounded
              outlined
              block
            >S'inscrire avec Google</v-btn>
          </v-col>
        </v-row>
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
      surname: "",
      first_name: "",
      pseudo: "",
      password: "",
      email: "",
      phone: "",
      avatar: "",
      surnameRules: [
        v => !!v || 'Nom requis',
        v => v.length >= 2 || 'Nom trop court',
        v => /^[a-zA-Z \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) || 'Nom invalide'
      ],
      first_nameRules: [
        v => !!v || 'Prénom requis',
        v => v.length >= 2 || 'Prénom trop court',
        v => /^[a-zA-Z \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) || 'Prénom invalide'
      ],
      pseudoRules: [
        v => !!v || 'Pseudo requis',
        v => v.length >= 2 || 'Pseudo trop court',
        v => /^[a-zA-Z0-9 _\-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) || 'Pseudo invalide (lettres, chiffres, espace, "_" et "-" seulement)'
      ],
      passwordRules: [
        v => !!v || 'Mot de passe requis',
        v => v.length >= 8 || 'Mot de passe trop court'
      ],
      emailRules: [
        v => !!v || 'E-mail requis',
        v => /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/.test(v) || 'E-mail invalide'
      ],
      phoneRules: [
        v => v.length == 0 || /^(0[1-8])(([0-9]{2})){4}$/.test(v) || 'Numéro de téléphone invalide (exemple: 0600000000) ou laisser vide'
      ],
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
              "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Players/ma" +
                store.getters.email
            )
            .then(response => {
                if (Object.keys(response.data).length <= 1) {
                    router.push("/createAccount");
                }else{
                    store.dispatch("hasAccount");
                    store.dispatch("setID");
                    router.push("/"); // redirection vers la page d'accueil
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
        creationError: function(err) {
          if (err.detail.includes("pseudo")) {
              alert("Pseudo déjà existant ! Veuillez en saisir un nouveau.");
              return;
          }
          if (err.detail.includes("mail_address")) {
              alert("Email déjà existante ! Veuillez en saisir une nouvelle.");
              return;
          }
          if (err.detail.includes("phone_number")) {
              alert("Numéro de téléphone déjà existant ! Veuillez en saisir un nouveau.");
              return;
          }
          alert("La création du compte a échoué, veuillez réessayer ultérieurement. \n\n ERR: CANNOT_CREATE_ACCOUNT_WITHOUT_GOOGLE")
      },
      hash: function() {
          var newPassword = sha512(this.password);
          return newPassword;
      },
      checkEmail: function() {
          return /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/.test(this.email);
      },
      checkPseudo: function() {
          return /^[a-zA-Z0-9 _-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.pseudo);
      },
      checkSurname: function() {
          return /^[a-zA-Z -éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.surname);
      },
      checkFirstname: function() {
          return /^[a-zA-Z -éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(this.first_name);
      },
      checkPhone: function() {
          return (/^(0[1-8])(([0-9]{2})){4}$/.test(this.phone) || this.phone === "");
      },
      checkSyntaxes: function() {
        if (! this.checkEmail()) {
          alert("L'adresse mail entrée est invalide, merci de respecter la syntaxe.");
          return false;
        }
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
        if (! this.checkPhone()) {
          alert("Numéro de téléphone invalide (exemple: 0600000000) ou laisser vide");
          return false;
        }
        return true;
      },
      checkEntries: function() {
          if(this.surname.length < 2 || this.surname.length > 20) {
              alert("Le nom doit faire entre 2 et 20 caractères.");
              return false;
          }
          if(this.first_name.length < 2 || this.first_name.length > 20) {
              alert("Le prénom doit faire entre 2 et 20 caractères.");
              return false;
          }
          if(this.pseudo.length < 2 || this.pseudo.length > 20) {
              alert("Le pseudo doit faire entre 2 et 20 caractères.");
              return false;
          }
          if(this.password.length < 8 || this.password.length > 50) {
              alert("Le mot de passe doit faire entre 8 et 50 caractères.");
              return false;
          }
          if(this.email.length < 6 || this.email.length > 50) {
              alert("L'adresse mail doit faire entre 6 et 50 caractères.");
              return false;
          }
          if (! this.checkSyntaxes()) {
            return false;
          }
      },
        login: async function() {
            let apiRep = null;
            let tmp_password = this.hash();
            var tmp_phone = this.phone;
            if (this.checkEntries() == false) {
                return;
            }else{
            if (this.phone === "") {
                tmp_phone = null;
            }
                apiRep = await axios.post(
                    "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/",
                    {
                        pseudo: this.pseudo,
                        surname: this.surname,
                        first_name: this.first_name,
                        mdp: tmp_password,
                        phone_number: tmp_phone,
                        mail_address: this.email,
                        is_valid: false
                    }
                );
                if (apiRep.data.name != "error") {
                    store.dispatch("setEmail",this.email);
                    store.dispatch("hasAccount");
                    this.updatePassword();
                    store.dispatch("setID").then(response => {
                        router.push("/verifyAccount");
                    });
                } else {
                    this.creationError(apiRep.data);
                }
                this.phone = "";
            }
        },
        updatePassword : async function() {
            let player = await axios.get(
                "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
                  this.email,
                { ResponseType: "json" }
            );
            var newPassword = sha512(this.password + player.data.id);
            let apiRep = null;
            apiRep = await axios.put(
                "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + player.data.id,
                {
                    mdp: newPassword
                }
            );
            if (apiRep.data.name != "error") {
                // update ok
            }else{
              alert("Si vous voyez ce message, merci de contacter urgemment le support. \n\n ERR: CANNOT_UPDATE_PASSWORD");
              router.push("/");
            }
        },
  },
  created: async function() {
      alert("Pour des raisons de sécurité, nous vous recommandons la connexion via un compte Google.");
  }
};
</script>
