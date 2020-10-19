<template>
  <v-container fluid>
    <v-layout row wrap align-center="true" justify-center="true">
      <v-card class="mx-10" raised width="25%">
        <v-form>
          <v-row class="pa-0" justify="center" align="center">
            <v-col align="center" cols="5">
              <v-img src="../assets/logo.png" alt="Quick Match logo"></v-img>Vérification de compte
            </v-col>
          </v-row>
          <v-col class="py-0" cols="6" md="12">
            <v-text-field
              v-model="code"
              label="Code reçu par email"
              outlined
              filled
            ></v-text-field>
          </v-col>
        </v-form>
        <v-row class="pa-0" align="center" justify="center">
            <v-col class="pt-0" cols="10">
              <v-btn rounded outlined block v-on:click="sendAgain">Renvoyer le mail !
                 </v-btn>
                  <center><font size="-5"> (2 minutes d'attente entre chaque envoi) </font></center>
            </v-col>
          <v-col class="pt-0" cols="10">
            <v-btn rounded outlined block v-on:click="verify">Vérifier !</v-btn>
          </v-col>
        </v-row>
        <v-divider></v-divider>
      </v-card>
    </v-layout>
  </v-container>
</template>

<script>
import GoogleSignInButton from "../main.js";
import store from "../store";
import axios from "axios";
import router from "../router";

  export default {
    data: () => ({
      code: ""
    }),
    methods: {
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
                store.dispatch("setIsValid",true);
            }else{
              alert("Si vous voyez ce message, merci de contacter le support urgemment.  \n\n ERR: CANNOT_UPDATE_VALID_FIELD");
            }
        },
        verify: async function() {
            let id = store.getters.id;
            if (id == this.code) {
                let rep = await this.validateAccount(id);
                router.push("/");
            }else{
                alert("Vous avez entré le mauvais code ! \n\n ERR: WRONG_CODE");
            }
        },
        sendAgain: function() {
            store.dispatch("sendAgain");
        }
  },
  created: async function() {
      store.dispatch("sendAgain");
  }
};
</script>
