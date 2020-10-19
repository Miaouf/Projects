<template>
  <div>
    <v-carousel cycle show-arrows hide-delimiter-background show-arrows-on-hover>
      <v-carousel-item
        v-for="(item,i) in items"
        :key="i"
        :src="item.src"
        reverse-transition="fade-transition"
        transition="fade-transition"
      >
        <v-container fill-height>
          <v-layout row wrap align-center>
            <v-flex v-if="i==0" class="text-center">
              <p class="display-4 white--text" style="text-shadow: 1px 1px 7px #16002d;">QuickMatch</p>
              <v-btn
                v-if="!isSignedIn"
                to="/login"
                large
                color="#e3e3e3"
                style="border: 1px solid #20003c;"
                class="black--text"
              >Se connecter</v-btn>
              <v-btn
                v-if="!isSignedIn"
                to="/signin"
                large
                color="#20003c"
                style="border: 1px solid grey;"
              >S'inscrire</v-btn>
            </v-flex>
            <v-flex v-if="i==1" class="text-center">
              <p
                class="display-4 white--text"
                style="text-shadow: 1px 1px 7px #16002d;"
              >Statistiques</p>
              <p
                class="display-1 white--text"
                style="text-shadow: 1px 1px 7px #16002d;"
              >Les statistiques de tous vos matchs</p>
              <v-btn
                to="/stat"
                large
                color="#20003c"
                style="border: 1px solid grey;"
                class="white--text"
              >Consulter</v-btn>
            </v-flex>

            <v-flex v-if="i==2" class="text-center">
              <p class="display-4 white--text" style="text-shadow: 1px 1px 7px #040083;">Matchs</p>
              <p
                class="display-1 white--text"
                style="text-shadow: 1px 1px 7px #040083;"
              >L'historique et les résultats de tous les matchs</p>
              <v-btn
                to="/match"
                large
                color="#20003c"
                style="border: 1px solid grey;"
                class="white--text"
              >Consulter</v-btn>
            </v-flex>
          </v-layout>
        </v-container>
      </v-carousel-item>
    </v-carousel>
    <v-flex>
      <v-jumbotron id="jumb" class="row" style="padding: 10px 5px 10px 5px; background-color: #20003c">
        <div class="col-md-12 col-sm-12 col-md-9 col-lg-9 col-xl-10">
          <p
            class="title font-weight-regular"
            style="padding: 12px 0px 0px 100px;"
          >Quick Match organise votre vie sportive.</p>
        </div>
      </v-jumbotron>
    </v-flex>

    <v-container id="propos">
      <div class="row text-center padding">
        <div class="col-12">
          <p class="headline font-weight-medium">A PROPOS DE L'APPLICATION</p>

            <div>
              Qui n'a pas toujours rêvé d'organiser toutes ses activités entre amis en un seul clic ?
              Avec QuickMatch, proposez et organisez des rencontres sportives entre vous très facilement !

              QuickMatch est très facile d'utilisation :
              <v-card
                class="ma-2"
                tile
              >
                <v-list-item two-line>
                  <v-list-item-content>
                    <v-list-item-title> <v-icon>mdi-human-handsup</v-icon> <strong>Créez des groupes</strong></v-list-item-title>
                    <v-list-item-subtitle>Rassemblez vos amis dans des groupes privés afin de pouvoir jouer entre vous !</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item two-line>
                  <v-list-item-content>
                    <v-list-item-title> <v-icon>mdi-calendar</v-icon> <strong>Proposez des horaires</strong></v-list-item-title>
                    <v-list-item-subtitle>Une personne peut proposer des horaires hebdomadaires de parties afin que tout le monde sache toutes les semaines à quelles heures se jouent les parties !</v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item two-line>
                  <v-list-item-content>
                    <v-list-item-title> <v-icon>mdi-soccer</v-icon> <strong>Jouez</strong></v-list-item-title>
                    <v-list-item-subtitle>Il vous suffit simplement d'accepter les invitations à des horaires qui vous conviennent et le tour est joué !
                      Ensuite, remplissez vos feuilles de statistiques personnalisées !
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item two-line>
                  <v-list-item-content>
                    <v-list-item-title> <v-icon>mdi-format-list-bulleted</v-icon> <strong>Consultez vos statistiques</strong></v-list-item-title>
                    <v-list-item-subtitle>Essayez d'être le meilleur en comparant les statistiques de chacun de vos match dans un historique complet regroupant toutes vos performances !
                    </v-list-item-subtitle>
                  </v-list-item-content>
                </v-list-item>
                </v-card>

          </div>Le but de cette application est de vous proposer un système d'organisation simple permettant d'organiser vos rencontres sportives de la plus facile des manières !
        </div>
      </div>
      <v-row
      class="mb-6"
      justify="center"
    >
      <v-col class="pa-md-4" lg="2" md="auto" sd="auto">
        <v-btn v-if="!isSignedIn" class="row wrap align-center" small color="primary" to="/login">Se connecter</v-btn>
      </v-col>
      <v-col class="pa-md-4" lg="2" md="auto" sd="auto">
        <v-btn v-if="!isSignedIn" class="row wrap align-center" small color="primary" to="/signin">S'inscrire</v-btn>
      </v-col>
    </v-row>
      <v-row class="mb-6" justify="center">
      <a href="https://quickmatch.fr/mobile/app-release.apk" download="quickmatch.apk">
        <v-btn outlined color="indigo">
          <v-icon left color="indigo">mdi-cellphone-android</v-icon>Télécharger l'appli Android
        </v-btn>
      </a>
      </v-row>
    </v-container>

    <hr style="width:50%; margin: auto;" color="#20003c" size="1px" />

    <v-container id="team">
      <div class="row text-center padding">
        <div class="col-12">

          <p class="headline font-weight-medium">EQUIPE</p>
          Création d'élèves de l'ENSEIRB-Matmeca, cette application gratuite a pout but d'aider les sportifs de l'INRIA et des autres universités à organiser leurs rencontres sportives.

        </div>
      </div>
    </v-container>

    <hr style="width:50%; margin: auto;" color="#20003c" size="1px" />
  </div>
</template>

<script>
import store from "../store";
export default {
  data: () => ({
    items: [
      { src: require("../assets/foot.jpg") },
      { src: require("../assets/volley.jpg") },
      { src: require("../assets/basket.jpg") }
    ],
    socials: [
      { route: "/", icon: "mdi-facebook-box" },
      { route: "/", icon: "mdi-twitter-box" },
      { route: "/", icon: "mdi-instagram" },
      { route: "/", icon: "mdi-google-plus" },
      { route: "/", icon: "mdi-play" }
    ]
  }),
  computed: {
    isSignedIn: function() {
      this.$store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    }
  }
};
</script>

<style>
.social {
  position: relative;
  left: 30%;
  right: 30%;
}
.social a {
  margin-right: 40px;
  float: left;
}
</style>
