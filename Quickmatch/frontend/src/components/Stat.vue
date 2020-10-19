<template>
  <div>
    <div v-if="isSignedIn">
      <!-- ///////////////////////////////////////////////////////////////// -->
        <div class="my_card">
          <div class="title">Statistiques globales</div>

          <hr>
          <div class="tab_head tab_glob">Nom</div>
          <div class="tab_head tab_glob">Prénom</div>
          <div class="tab_head tab_glob">Nombre de but(s)</div>
          <div class="tab_head tab_glob">Nombre de but(s) encaissé(s)</div>
          <div class="tab_head tab_glob">Nombre de match joué(s)</div>
          <div class="tab_head tab_glob">Nombre de victoire(s)</div>

          <br>
          <div class="tab_row tab_glob">{{ playerToShow.surname }}</div>
          <div class="tab_row tab_glob">{{ playerToShow.first_name }}</div>
          <div class="tab_row tab_glob">{{ playerToShow.scored_goals }}</div>
          <div class="tab_row tab_glob">{{ playerToShow.conceded_goals }}</div>
          <div class="tab_row tab_glob">{{ playerToShow.matches_played }}</div>
          <div class="tab_row tab_glob">{{ playerToShow.victories }}</div>

        </div>
      <!-- ///////////////////////////////////////////////////////////////// -->
      <div class="my_card">
        <div class="title">Statistiques de vos derniers matchs</div>
        <br>

        <div v-if="playerStatToShow.length != 0">
          <div class="tab_head tab_match">Date</div>
          <div class="tab_head tab_match">Lieux</div>
          <div class="tab_head tab_match">Nombre de but(s)</div>
          <div class="tab_head tab_match">Nombre de but(s) encaissé(s)</div>
          <div class="tab_head tab_match">Résultat du match</div>

          <div v-for="row in playerStatToShowPage" :key="row.id">
            <hr>
            <div class="tab_row tab_match">{{ new Date(row.precise_date).toLocaleDateString('fr-FR') }} ({{ new Date(row.precise_date).toLocaleTimeString('fr-FR') }})</div>
            <div class="tab_row tab_match">{{ row.location }}</div>
            <div class="tab_row tab_match">{{ row.scored_goals }}</div>
            <div class="tab_row tab_match">{{ row.conceded_goals }}</div>
            <div v-if="row.won" class="tab_row tab_match">Victoire</div>
            <div v-else class="tab_row tab_match">Défaite</div>
          </div>

          <br>
          <div class="pages">
            <div class="numeric_icon" v-on:click="page_left()"><v-icon color="black">mdi-chevron-left</v-icon></div>
            <!-- <div class="numeric_icon" v-if="this.playerStatPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
            <div style="display: inline-block;" v-for="index in this.playerStatPageMax" :key="index">
              <div v-bind:id="index" class="numeric_icon" v-on:click="page(index)">{{ index }}</div>
            </div>
            <div class="numeric_icon" v-on:click="page_right()"><v-icon color="black">mdi-chevron-right</v-icon></div>
          </div>
        </div>

        <div v-else>
          <hr><br>
          <div class="tab_nothing">Vous n'avez pas encore joué de match</div>
        </div>
      </div>
      <!-- ///////////////////////////////////////////////////////////////// -->
    </div>
    <NotConnected v-else></NotConnected>
  </div>
</template>

<style>
  .pages{
    text-align: center;
  }
  .numeric_icon{
    margin: 2px;
    display: inline-block;
    background-color: white;
    color: black;
    border-radius: 5px;
    width: 30px;
    height: 30px;
  }
  .my_card{
    margin-top: 1%;
    margin-left: 2%;
    margin-right: 2%;
    margin-bottom: 1%;
    background-color: black;
    border-radius: 5px;
    box-shadow: 0px 4px 8px #AAA;
  }
  .title{
    text-align: center;
    color: white;
    font-size: 200%;
    margin: 5px;
  }
  .tab_nothing{
    text-align: center;
    font-weight: bold;
    color: white;
  }
  .tab_head, .tab_row{
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-top: 5px;
    margin-left: 5px;
  }
  .tab_head{
    font-weight: bold;
    color: white;
  }
  .tab_row tab_glob{
    color: #CCC;
    font-size: 120%;
  }
  .tab_glob, .tab_glob{
    width: 16%;
  }
  .tab_match, .tab_match{
    width: 19%;
  }
</style>

<script>
import store from "../store";
import axios from "axios";
import router from "../router";
import NotConnected from "./NotConnected";

export default {
  components: {
    NotConnected
  },
  data() {
    return {
      playerToShow: {},
      //////////////////////////////////////////////////////////////////////////
      playerStatToShow: {},
      playerStatNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      playerStatPage: 1,
      playerStatPageMax: 1,
      playerStatNbRowPerPage: 5,
      playerStatNbRow: 0,
      playerStatToShowPage: [],
    };
  },
  async created() {
    const player = await axios
      .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/id" + this.id, {responseType: "json"})
      .catch(e => {
        if (this.isSignedIn()){
          alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page. \n\n ERR: CANNOT_GET_PLAYER_INFOS");
          this.$router.go();
        }
      });
    this.playerToShow = player.data;
    const playerStat = await axios
      .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/stat" + this.id, {responseType: "json"})
      .catch(e => {
        if (this.isSignedIn()){
          alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page. \n\n ERR: CANNOT_GET_PLAYER_STAT");
          this.$router.go();
        }
      });
    this.playerStatToShow = playerStat.data.rows;
    this.playerStatNbRow = playerStat.data.rowCount;
    this.playerStatPageMax = Math.floor((playerStat.data.rowCount -1) / this.playerStatNbRowPerPage) + 1;
    for (let i = 0; i < Math.min(this.playerStatNbRowPerPage, this.playerStatNbRow); i++){
      this.playerStatToShowPage.push(this.playerStatToShow[i]);
    }
    document.getElementById("1").style.backgroundColor = "orange";
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
  },
  methods: {
    page_left(){
      if (this.playerStatPage != 1){
        this.page(this.playerStatPage - 1);
      }
    },
    page_right(){
      if (this.playerStatPage != this.playerStatPageMax){
        this.page(this.playerStatPage + 1);
      }
    },
    page(n){
      document.getElementById(this.playerStatPage).style.backgroundColor = "white";
      this.playerStatPage = n;
      document.getElementById(this.playerStatPage).style.backgroundColor = "orange";
      let len = this.playerStatToShowPage.length
      for (let i = 0; i < len; i++){
        this.playerStatToShowPage.pop();
      }
      for (let i = (this.playerStatPage - 1) * this.playerStatNbRowPerPage; i < Math.min(this.playerStatNbRow, this.playerStatPage * this.playerStatNbRowPerPage); i++){
        this.playerStatToShowPage.push(this.playerStatToShow[i]);
      }
    }
  },
};
</script>
