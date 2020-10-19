<template>
  <div>
    <div v-if="isSignedIn">
      <br />
      <br />
      <v-card class="mx-auto" max-width="800" tile>
        <v-list-item three-line>
          <v-list-item-content>
            <v-list-item-title class="display-2">{{ playerToShow.pseudo }}</v-list-item-title>
            <v-list-item-subtitle>{{ playerToShow.bio }}</v-list-item-subtitle>
          </v-list-item-content>

          <v-list-item-avatar size="160">
            <img id="avatar" />
          </v-list-item-avatar>
        </v-list-item>

        <v-card-actions>
          <v-btn text color="deep-purple accent-4" :to="'/EditProfile'">
            Editer mon profil
            <v-icon>mdi-pencil-outline</v-icon>
          </v-btn>
          <v-row justify="end" class="mx-2">
            <v-dialog v-model="dialog" persistent max-width="600px">
              <template v-slot:activator="{ on }">
                <v-btn v-on="on">Modifier avatar</v-btn>
              </template>
              <v-card>
                <v-card-text>
                  <v-container>
                    <v-col>
                      <v-text-field v-model="playerToShow.avatar" label="URL de l'image" required></v-text-field>
                    </v-col>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="dialog = false">Fermer</v-btn>
                  <v-btn
                    color="blue darken-1"
                    text
                    @click="dialog = false"
                    v-on:click="updatePic"
                  >Sauvegarder</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-row>
        </v-card-actions>
      </v-card>
      <br />
      <v-card class="mx-auto" max-width="800" tile>
        <v-list-item two-line>
          <v-list-item-content>
            <v-list-item-title class="font-weight-bold">Nom</v-list-item-title>
            <v-list-item-subtitle class="headline">{{ playerToShow.surname }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>

        <v-list-item two-line>
          <v-list-item-content>
            <v-list-item-title class="font-weight-bold">Prénom</v-list-item-title>
            <v-list-item-subtitle class="headline">
              {{
              playerToShow.first_name
              }}
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>

        <v-list-item two-line>
          <v-list-item-content>
            <v-list-item-title class="font-weight-bold">Adresse mail</v-list-item-title>
            <v-list-item-subtitle class="headline">
              {{
              playerToShow.mail_address
              }}
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>

        <v-list-item two-line>
          <v-list-item-content>
            <v-list-item-title class="font-weight-bold">Numéro de téléphone</v-list-item-title>
            <v-list-item-subtitle class="headline">
              {{
              playerToShow.phone_number
              }}
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-card>
    </div>
    <NotConnected v-else></NotConnected>
  </div>
</template>

<script>
import axios from "axios";
import NotConnected from "./NotConnected";
import store from "../store";

export default {
  components: {
    NotConnected
  },
  data() {
    return {
      playerToShow: {},
      dialog: false
    };
  },

  methods: {
    updatePic: async function() {
      let apiRep = null;
      apiRep = await axios.put(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + this.id,
        {
          avatar: this.playerToShow.avatar
        }
      );
      if (apiRep.data.name != "error") {
        this.$router.go();
      } else {
        // gestion d'erreur
        alert("Une erreur est survenue, vous allez être redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: CANNOT_UPDATE_USER_PROFILE_PICTURE");
        router.push("/");
      }
    }
  },

  async created() {
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
